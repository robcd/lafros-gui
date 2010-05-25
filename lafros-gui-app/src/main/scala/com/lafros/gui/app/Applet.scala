/**
 * Copyright 2009 Latterfrosken Software Development Limited
 *
 * This file is part of Lafros GUI-App.
 *
 * Lafros GUI-App is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * Lafros GUI-App is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with Lafros GUI-App. If not, see <http://www.gnu.org/licenses/>. */
package com.lafros.gui.app
import javax.swing.{JApplet, JMenuBar}
import scala.swing.{Component, Label, MenuBar, RootPanel, ScrollPane}
/**
 * runs the <tt>App</tt> as an applet.
 * <p>The web page in which the applet is to be
 * embedded should contain an element similar to the following one:<pre>
 * &lt;applet code="com.lafros.gui.app.Applet"
 *   width="300"
 *   height="200"
 *   archive="lafros-gui-app-1.0r1.jar"
 *   archive="lafros-gui-cmds-1.0r1.jar"
 *   archive="lafros-gui-alerts-1.0r1.jar"&gt;
 *   &lt;param name="App" value="org.myorg.myapp.app"&gt;
 * &lt;/applet&gt;</pre></p>
 * <p>Note that the Alerts .jar file need only be included if Alerts are used by
 * the app itself.</p>
 *
 * @author Rob Dickens */
class Applet extends JApplet { outer =>
  private var appCreated = false // dispatch-thread only
  private var app: App = _ // dispatch-thread only
  private var _appContext: AppletAppContext = _ // appContext already defined!
  private var startedBefore = false  // dispatch-thread only
  val rootPanel = new RootPanel {
    def peer = outer
    override def contents_=(c: Component) {
      super.contents_=(c)
      peer.validate()
    }
  }

  override def init() {
    //println("applet.init(): "+ this)
    App.invokeAndWait {
      appCreated = if (Applet.onlyTrueTheFirstTime) try {
        app = createApp
        true
      }
      catch {
        case ex: Exception =>
          displayWarning("failed to create app:<br>"+ ex.getMessage)
        false
      }
      else {
        // classloader being reused, indicating applet-page reloaded or returned to,
        // which scala.actors can't tolerate - that is, all bets are off
        displayWarning("To reload or load another instance of this applet, please do the following:"+
                       "<ol>"+
                       "<li>type 'x' into the Java console window[1];"+
                       "<li>press the browser's reload-page button."+
                       "</ol>"+
                       "1 Please consider altering your Java preferences if this is not shown.")
        false
      }
      if (appCreated) {
        _appContext = new AppletAppContext(this, args)
        wrap("init") {
          app.initApplet(_appContext)
        }
      }
    }
    //println("..applet.init()")
  }

  override def start() = App.invokeAndWait {
    if (appCreated) wrap("start") {
      if (startedBefore) app.restartApplet() else {
        app.start()
        startedBefore = true
      }
    }
  }

  override def stop() = App.invokeAndWait {
    if (appCreated) wrap("stop") {
      app.stopApplet()
    }
  }

  override def destroy() = App.invokeAndWait {
    //println("applet.destroy()..")
    if (appCreated) {
      try {
        app.terminate()
      }
      catch {
        case ex: Exception => ex.printStackTrace
      }
      if (_appContext != null) _appContext.terminate()
    }
    //println("..applet.destroy()")
  }
  private def wrap(action: String)(f: => Unit) = try {
    f
  }
  catch {
    case ex: Exception =>
      val msg = "failed to "+ action +"() applet: "+ ex
      println(msg)
      showStatus(msg)
  }

  override def setJMenuBar(arg: JMenuBar) = _appContext.menuBar = arg match {
    case null => null
    case _ => new MenuBar {
      override lazy val peer = arg
    }
  }
  // 
  private[app] def setJMenuBarPrivately(arg: JMenuBar) = super.setJMenuBar(arg)
  private def args = {
    var l: List[String] = Nil
    def appendedParam(i: Int) = getParameter("arg"+ i) match {
      case null => false
      case untrimmed => untrimmed.trim match {
        case "" => false
        case trimmed => l = (trimmed :: l)
        true
      }
    }
    {
      var i = 0
      while (appendedParam(i)) i += 1
    }
    l.toArray
  }
  private def createApp = {
    val name = {
      val p = getParameter("App")
      if (p == null) throw new RuntimeException("missing parameter: App - name of impl'n class to run as applet")
      if (p.endsWith("$")) p else p +"$"
    }
    try {
      val field = {
        val c1ass = Class.forName(name)
        c1ass.getDeclaredField("MODULE$")
      }
      field.get(null).asInstanceOf[App]
    }
    catch {
      case ex: ClassNotFoundException =>
        throw new RuntimeException("App impl'n class not found: "+ name)
      case ex: IllegalAccessException =>
        throw new RuntimeException("App impl'n class could not be instantiated: "+ ex)
      case ex: InstantiationException =>
        throw new RuntimeException("App impl'n class could not be instantiated: "+ ex)
    }
  }
  private def displayWarning(msg: String) {
    val sp = new ScrollPane
    rootPanel.contents = sp
    sp.viewportView = new Label("<html>"+ msg +"</html>")
    
  }
}

private object Applet {
  private[this] var _onlyTrueTheFirstTime = true

  def onlyTrueTheFirstTime = synchronized {
    val res = _onlyTrueTheFirstTime
    if (_onlyTrueTheFirstTime) _onlyTrueTheFirstTime = false
    res
  }
}
