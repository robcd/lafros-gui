/**
 * Copyright 2009 Latterfrosken Software Development Limited
 *
 * This file is part of Lafros GUI-Alerts.
 *
 * Lafros GUI-Alerts is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * Lafros GUI-Alerts is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with Lafros GUI-Alerts. If not, see <http://www.gnu.org/licenses/>. */
package com.lafros.gui.alerts
/**
 * for controlling the package's behaviour.  Only one instance, as returned by
 * <tt>TheAlertsController.instance</tt>, the first time it is called, is allowed.
 * @author Rob Dickens */
class TheAlertsController private () {
  println("Using Lafros GUI-Alerts, Copyright 2009 Latterfrosken Software Development Limited, under the GPLv3 licence.")
  /**
   * determines the way the app indicates it is busy, or that a command succeeded
   * or failed. */
  def app: Option[AlertsApp] = TheAlertsController.app
  /**
   * sets this property. */
  def app_=(arg: Option[AlertsApp]) = TheAlertsController.app = arg
  /**
   * used as the default <tt>MonField</tt> <tt>alertSound</tt>, or when this has
   * the value, <tt>DefaultAlertSound</tt>. */
  def defaultAlertSound = intruders.defaultAlertSound
  /**
   * sets this property. */
  def defaultAlertSound_=(arg: AlertSound) = intruders.defaultAlertSound = arg
  /**
   * when <tt>true</tt>, <tt>SystemBeep</tt> will be used as the alert sound by
   * all <tt>MonField</tt>s. */
  def useSystemBeep = intruders.useSystemBeep
  /**
   * sets this property. */
  def useSystemBeep_=(arg: Boolean) = intruders.useSystemBeep = arg
  /**
   * when true, all alert sounds are muted. */
  def muted = intruders.muted
  /**
   * sets this property. */
  def muted_=(arg: Boolean) = intruders.muted = arg
  /**
   * intended for test purposes - uses
   * <tt>AlertSound.builtInOnes(0)</tt>, and respects <tt>useSystemBeep</tt> and
   * <tt>muted</tt>. */
  def beep() = if (!muted) {
    if (useSystemBeep) SystemBeep.beep()
    else (defaultAlertSound: @unchecked) match {
      case beep: Beep => beep.beep()
      case loop: Loop => loop.ac.play()
    }
  }
  /**
   * to be called before the controller is reused - for example, in an applet
   * container which reuses the same classloader for each applet. Here, two applets
   * which both use the ~.gui.alerts package may only be run sequentially, and
   * <tt>reset()</tt> should be called when the first is <tt>destroy()</tt>ed. */
  def reset() = {
    intruders.acknowledgeAll()
    defaultAlertSound = AlertSound.initialDefaultBuiltInOne
    app = None
    // and leave muted and useSystemBeep settings as they
  }
}
/**
 * companion. */
object TheAlertsController {
  private val lock = new AnyRef
  private var theAlertsController: TheAlertsController = _
  private var app: Option[AlertsApp] = None
  private var key: AnyRef = _
  /**
   * <tt>null</tt> will be returned to all but the first caller. Thread-safe. */
  def instance: TheAlertsController = lock.synchronized {
    if (theAlertsController == null) instance(lock)
    else null
  }
  /**
   * <tt>null</tt> will be returned if key is incorrect - the correct one being
   * established by the first caller. Thread-safe. */
  def instance(key: AnyRef) = lock.synchronized {
    if (theAlertsController == null) {
      theAlertsController = new TheAlertsController
      this.key = key
      theAlertsController
    }
    else if (key eq this.key) theAlertsController
    else null
  }
  private[alerts] def setAlertsRaised(b: Boolean) = app match {
    case None =>
    case Some(x) => x.setAlertsRaised(b)
  } 
//   /**
//    * copied from scala.swing.Swing. */
//   import javax.swing.Icon
//   import scala.swing.Swing.EmptyIcon
//   private[alerts] def toNullIcon(i: Icon): Icon =
//     if(i == EmptyIcon) null else i
}

