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
import scala.swing.{Frame, Menu, MenuItem, RootPanel}
import scala.swing.event.WindowClosing
import javax.swing.{SwingUtilities, WindowConstants}
import gui.cmds.Trig
/**
 * to be extended by a singleton object, conventionally called <tt>app</tt>, to be
 * supplied by all apps. All methods (except <tt>main</tt>) will be called from an
 * <tt>java.awt.EventQueue</tt> dispatch thread.
 *
 * @author Rob Dickens */
abstract class App {
  println("Using Lafros GUI-App, Copyright 2009 Latterfrosken Software Development Limited, under the GPLv3 licence.")
  /**
   * alias for <tt>AppContext</tt> - see <tt>init</tt>. */
  type Context = AppContext
  /**
   * called in applications. */
  def main(args: Array[String]) { // nb called after instant'n
    App.invokeAndWait {
      val quitCmd = new QuitCmd(this)
      val frame = new Frame {
        peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
        reactions += { // Reaction.invoke(quitCmd.exer) would force early Exer inst'n
          case WindowClosing(_) => quitCmd.exer.executeCmd()
        }
      }
      init(new ApplicationAppContext(frame, args, quitCmd));
      //
      // add quit menu-item
      (((try {
        frame.menuBar // as of Scala 2.7.4, throws NPE if menu bar not set!
      }
      catch {
        case ex: NullPointerException => null
      }) match {
        case null => Seq[Menu]()
        case menuBar => menuBar.menus
      }) firstOption) foreach(_.contents += new MenuItem(quitCmd.action))
      //
      displayApplication(frame)
    }
    App.invokeAndWait {
      start()
    }
  }
  /**
   * called in applets. */
  //private[app] def initApplet(arg: RootPanel) {
  private[app] def initApplet(context: Context) {
    init(context)
  }
  /**
   * called first. */
  def init(context: Context)
  /**
   * called after <tt>init</tt>, in applications. The default
   * implementation does a <tt>frame.pack()</tt> followed by a
   * <tt>frame.visible = true</tt>. */
  def displayApplication(frame: Frame) {
    frame.pack()
    frame.visible = true
  }
  /**
   * called after <tt>init</tt> (or <tt>displayApplication</tt>, in 
   * applications). The default implementation does nothing. */
  def start() {}
  /**
   * called in applets. The default implementation does nothing. */
  def stopApplet() {}
  /**
   * called in applets. The default implementation does nothing. */
  def restartApplet() {}
  /**
   * called when the application exits or the applet is destroyed. The default
   * implementation does nothing. */
  def terminate() {}
}
/**
 * companion. */
object App {
  /**
   * convenience, for invoking the corresponding <tt>javax.swing.SwingUtilities</tt> method. */
  def invokeAndWait(f: => Unit) = SwingUtilities.invokeAndWait(newRunnable(f))
  /**
   * convenience - see above. */
  def invokeLater(f: => Unit) = SwingUtilities.invokeLater(newRunnable(f))

  private def newRunnable(f: => Unit) = new Runnable {
    def run() = f
  }
}
