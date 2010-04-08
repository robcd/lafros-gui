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

// import java.applet.*;
import java.applet.AudioClip
import java.awt.Cursor
import java.awt.event.KeyEvent
import java.lang.Integer
// import java.net.*;
import java.util.prefs.Preferences
import javax.swing.{Action => JxSwAction, KeyStroke, SwingUtilities, UIManager}

import com.lafros.gui.cmds.{Cmd, CmdsApp, Exer, TheCmdsController, Tog, Trig}
import scala.swing.{AbstractButton, Action, ButtonGroup, Component, Frame, RadioButton, RadioMenuItem, Reactions, RootPanel}
import scala.swing.event.ButtonClicked
/**
 * app context. This provides your app with access to its environment,
 * as supplied by the JUICe application framework. The class itself is
 * abstract, and provides only that which is common to applets and applications.
 *
 * @author Rob Dickens */
private[app] abstract class BaseAppContext(
  val rootPanel: RootPanel) extends AppContext {
  private val waitCursor = new Cursor(Cursor.WAIT_CURSOR)
  private val cmdsApp = new CmdsApp {
    private var proceedCursor: Cursor = _

    def setBusy(b: Boolean) {
      if (rootPanel != null) {
        if (b) { // SET WAIT
          val currentCursor = rootPanel.cursor
          // only do so if not already a Cursor.WAIT_CURSOR
          if (currentCursor.getType != Cursor.WAIT_CURSOR) {
            proceedCursor = currentCursor
            rootPanel.cursor = waitCursor
          }
        }
        else if (this.proceedCursor != null) // SET PROCEED
          rootPanel.cursor = proceedCursor
      }
    }
    def clear() = if (haveMsgLine) _msgLine.clear()
    def trying(msg: String) = if (haveMsgLine) _msgLine.displayMsg(msg)
    def succeeded() {}
    def succeeded(msg: String) = if (haveMsgLine) _msgLine.displayMsg(msg)
    def failed(msg: String, cause: Exception) {
      if (haveMsgLine) _msgLine.displayMsg(msg)
      cause.printStackTrace()
      // note that failedAc and enableSoundEffectsExer are only created if required
      if (failedAc != null &&
          enableSoundEffectsExer.tog.isSet) failedAc.play()
      Console.err.println("[Caught by com.lafros.gui.cmds]")
    } 
  }
  private val haveAlerts = try {
      Class.forName("com.lafros.gui.alerts.AlertsApp")
      val arg = rootPanel match {
        case frame: Frame => onlyInitIfAlertsPresent.frame = Some(frame.peer)
        case _ =>
      }
      true
    }
    catch {
      case ex: ClassNotFoundException => false
    }
  private lazy val prefs = try {
      Preferences.userNodeForPackage(getClass)
    }
    catch {
      case ex: SecurityException => null
    }
  private lazy val plafButtonGroup = new ButtonGroup
  private lazy val plafMenuItemGroup = new ButtonGroup
  //
  private var failedAc: AudioClip = _
  private var _msgLine: MsgLine = _

  BaseAppContext.theCmdsController.app = Some(cmdsApp)
  //
  // AppContext impl'n...
  def mainComponent = rootPanel.contents.first
  def mainComponent_=(arg: Component) {
    rootPanel.contents = arg
    BaseAppContext.theCmdsController.rootComponent = arg
  } 
  lazy val muteExer = {
    val cmd = new Tog.Cmd {
      def apply() = {
        if (!tog.isSet && !haveAlerts)
          throw new RuntimeException(BaseAppContext.sorryNoAlerts)
        _setMuted(!tog.isSet)
        if (havePrefs) prefs.putBoolean(toString, !tog.isSet)
        None // no message required - icon (un)displayed
      }
      val tog = {
        val initialSetting = 
          haveAlerts &&
        havePrefs &&
        prefs.getBoolean(toString, false)
        if (haveAlerts) _setMuted(initialSetting)
        new Tog(initialSetting)
      }
    }
    Exer(cmd)
  }
  lazy val muteTrigProps = new Trig.Props {
    title = "Muted"
    mnemonic = KeyEvent.VK_M
    accelerator = Some(KeyStroke.getKeyStroke("control M"))
    toolTip = "mutes ~.alerts package alerts"
    enabled = haveAlerts
  }

  lazy val testBeepExer = {
    val cmd = Cmd {
      if (!haveAlerts)
        throw new RuntimeException(BaseAppContext.sorryNoAlerts)
      onlyInitIfAlertsPresent.beep()
      None
    }
    Exer(cmd)
  }
  lazy val testBeepAction = new Action("Beep Test") {
    mnemonic = KeyEvent.VK_T
    toolTip = "tests ~.alerts package alert-beep"
    enabled = haveAlerts
    def apply() = testBeepExer.executeCmd()
  }

  lazy val useSystemBeepExer = {
    val cmd = new Tog.Cmd {
      def apply() = {
        if (!tog.isSet && !haveAlerts)
          throw new RuntimeException(BaseAppContext.sorryNoAlerts)
        onlyInitIfAlertsPresent.useSystemBeep = !tog.isSet
        if (havePrefs) prefs.putBoolean(toString, !tog.isSet)
        val state = if (tog.isSet) "off" else "on"
        Some("use system beep for all intrusive alerts: "+ state)
      }
      val tog = {
        val initialSetting = haveAlerts && havePrefs && prefs.getBoolean(toString, false);
        if (haveAlerts) onlyInitIfAlertsPresent.useSystemBeep = initialSetting
        new Tog(initialSetting)
      }
    }
    Exer(cmd)
  }
  lazy val useSystemBeepTrigProps = new Trig.Props {
    title = "Use System Beep"
    mnemonic = KeyEvent.VK_B
    toolTip = "in case only this is audible!"
    enabled = haveAlerts
  }

  lazy val enableSoundEffectsExer = {
    failedAc = {
      val url = getClass.getResource("boing.wav")
      java.applet.Applet.newAudioClip(url)
    }
    assert(failedAc != null, "getResource returned null") //TODO remove
    val cmd = new Tog.Cmd {
      def apply() = {
        if (havePrefs) prefs.putBoolean(toString, !tog.isSet)
        val state = if (tog.isSet) "off" else "on"
        Some("sound-effects: "+ state)
      }
      val tog = {
        val initialSetting =
          failedAc != null &&
        havePrefs &&
        prefs.getBoolean(toString, false)
        new Tog(initialSetting)
      }
    }
    Exer(cmd)
  }
  lazy val enableSoundEffectsTrigProps = new Trig.Props {
    title = "Sound Effects"
    mnemonic = KeyEvent.VK_E
    toolTip = "audible feedback if something fails"
    enabled = failedAc != null
  }
  // ...AppContext impl'n

  def msgLine = _msgLine
  def msgLine_=(arg: MsgLine) {
    _msgLine = arg
    if (_msgLine != null)
      _msgLine.muted = (muteExer != null) && muteExer.tog.isSet
  }

  def cmdsController = BaseAppContext.theCmdsController

  lazy val alertsControllerKey =
    if (haveAlerts) Some(onlyInitIfAlertsPresent.controllerKey) else None

  private def _setMuted(b: Boolean) {
    onlyInitIfAlertsPresent.muted = b
    if (haveMsgLine) msgLine.muted = b
  }
  // called from Applet's destroy()
  def terminate() {
    BaseAppContext.theCmdsController.reset()
    if (haveAlerts) onlyInitIfAlertsPresent.reset()
  }

  private def haveMsgLine = msgLine != null
  private def havePrefs = prefs != null
}

private[app] object BaseAppContext {
  val FAILED_MSG = "[com.lafros.gui.app] failed to load resource from jarfile"
  private val sorryNoAlerts = "Sorry - alerts library required!"
//   ;{
//     val (pkgName, vSpec, vImp) = {
//       val (_pkgName, props) = {
//         val _class = getClass//classOf[BaseAppContext]
//         //(_class.getPackage.getName, getClass.getProperties(_class))
//         (_class.getPackage.getName, null.asInstanceOf[java.util.Properties])//Properties(_class))
//       }
//       if (props != null)
//         (_pkgName,
//          props.getProperty(_pkgName +".specification.version"),
//          props.getProperty(_pkgName +".version"))
//       else
//         (_pkgName, null, null)
//     }
//     if ((vSpec == null) || (vImp == null))
//       throw new RuntimeException(FAILED_MSG)
//     println(pkgName +" "+ vSpec +"r"+ vImp)
//   }

  private val theCmdsController = TheCmdsController.instance
  //
//   private lazy val failedAc = {
//     val url = getClass.getResource("boing.au")
//     if (url == null) throw new RuntimeException(FAILED_MSG)
//     Applet.newAudioClip(url)
//   }
}
