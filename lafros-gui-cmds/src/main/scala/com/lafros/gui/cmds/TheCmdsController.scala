/**
 * Copyright 2009 Latterfrosken Software Development Limited
 *
 * This file is part of Lafros GUI-Cmds.
 *
 * Lafros GUI-Cmds is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * Lafros GUI-Cmds is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with Lafros GUI-Cmds. If not, see <http://www.gnu.org/licenses/>. */
package com.lafros.gui.cmds
import scala.swing.Component
import java.awt.KeyboardFocusManager
/**
 * for controlling the package's behaviour.  Only one instance, as returned by
 * <tt>TheCmdsController.instance</tt> the first time it is called, is allowed.
 * @author Rob Dickens */
class TheCmdsController private () {
  println("Using Lafros GUI-Cmds, Copyright 2009 Latterfrosken Software Development Limited, under the GPLv3 licence.")
  /**
   * determines the way the app indicates it is busy or that a command succeeded
   * or failed. */
  def app: Option[CmdsApp] = TheCmdsController.app
  /**
   * sets this property. */
  def app_=(arg: Option[CmdsApp]) = TheCmdsController.app = arg
  /**
   * Dialogue boxes will be centred on this (rather than on the screen). */
  def rootComponent: Component = TheCmdsController.rootComponent
  /**
   * sets this property. */
  def rootComponent_=(arg: Component) = TheCmdsController.rootComponent = arg
  /**
   * to be called before the controller is reused - for example, in an applet
   * container which reuses the same classloader for each applet. Here, two applets
   * which both use the Cmds library may only be run sequentially, and
   * <tt>reset</tt> should be called when the first is <tt>destroy</tt>ed. */
  def reset() {
    seqBgExerHelper.reset()
    TheCmdsController.busy = false
    rootComponent = null
    app = None
  }
}
/**
 * companion. */
object TheCmdsController {
  private val lock = new AnyRef
  private var theCmdsController: TheCmdsController = _
  private var app: Option[CmdsApp] = None
  private var key: AnyRef = _
  private var seqBgCmdPrefix: String = _
  private[cmds] var rootComponent: Component = _
  private[cmds] var origFocusOwner: Option[java.awt.Component] = None
  private[cmds] def focusOwner =
    KeyboardFocusManager.getCurrentKeyboardFocusManager.getFocusOwner match {
      case null => None
      case x => Some(x)
    }
  /**
   * increment when setBusy(true), decrement when setBusy(false). Hence, only */
  private var busy = false
  /**
   * <tt>null</tt> will be returned to all but the first caller. Thread-safe. */
  def instance: TheCmdsController = lock.synchronized {
    if (theCmdsController == null) instance(lock)
    else null
  }
  /**
   * <tt>null</tt> will be returned if <tt>key</tt> is incorrect - the correct one
   * being established by the first caller. Thread-safe. */
  def instance(key: AnyRef) = lock.synchronized {
    if (theCmdsController == null) {
      theCmdsController = new TheCmdsController
      this.key = key
      theCmdsController
    }
    else if (key eq this.key) theCmdsController
    else null
  }
  private[cmds] def clear() = app.foreach(_.clear())
  private[cmds] def setBusy(b: Boolean) {
    if (busy != b) {
      busy = b
      app.foreach(_.setBusy(b))
    }
  }
  /**
   * trying a SeqBgCmd Cmd */
  private[cmds] def trying(msg: String) {
    seqBgCmdPrefix = msg +"... "
    app.foreach(_.trying(seqBgCmdPrefix))
  }
  private[cmds] def succeeded(_msg: Option[String], isTheSeqBgCmd: Boolean) {
    if (isTheSeqBgCmd) seqBgCmdPrefix = null
    val msg = if (seqBgCmdPrefix == null) _msg match {
      case None => null
      case Some(x) => x
    }
    else _msg match {
      case None => seqBgCmdPrefix
      case Some(x) => seqBgCmdPrefix + x
    }
    app.foreach { app =>
      msg match {
        case null => if (isTheSeqBgCmd) app.clear()
        app.succeeded()
        case _ => app.succeeded(msg)
      }
    }
  }
  /**
   * delegates to CmdApp's failed(..).*/
  private[cmds] def failed(ex: Exception, isTheSeqBgCmd: Boolean) {
    if (isTheSeqBgCmd) seqBgCmdPrefix = null
    val msg = {
      val suffix =
        if (ex.getClass == classOf[RuntimeException]) ex.getMessage
        else ex.toString
      seqBgCmdPrefix match {
        case null => suffix
        case _ => seqBgCmdPrefix + suffix
      }
    }
    app.foreach(_.failed(msg, ex))
  }
  /**
   * convenience for invoking corr'ding SwingUtilities method. */
  private[cmds] def invokeAndWait(f: => Unit) =
    javax.swing.SwingUtilities.invokeAndWait(new Runnable {
      def run() = f
    })
}
