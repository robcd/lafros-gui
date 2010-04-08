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

import java.awt.Frame
//import javax.swing.*;

import com.lafros.gui.alerts.{AlertsApp, Beep, Loop, TheAlertsController}
/**
 * ~.app.TheAlertsController wrapper - only to be referenced if alerts package
 * actually present.
 *
 * @author Rob Dickens */
private[app] object onlyInitIfAlertsPresent {
  val controllerKey = this
  private val theAlertsController = TheAlertsController.instance(controllerKey)
  private val alertsApp = new AlertsApp() {
    def setAlertsRaised(b: Boolean) = frame match {
      // de-iconify when alert raised (in application case)
      case None =>
      case Some(x) => if (b) x.setState(Frame.NORMAL)
    }
  }
  var frame: Option[Frame] = None

//   static void loadJUICeResources() {
//     TheAlertsController.loadJUICeResources();
//   }

  theAlertsController.app = Some(alertsApp)

  def defaultAlertSound = theAlertsController.defaultAlertSound
  def defaultAlertSound_=(arg: Beep) = theAlertsController.defaultAlertSound = arg
  def defaultAlertSound_=(arg: Loop) = theAlertsController.defaultAlertSound = arg
  def useSystemBeep = theAlertsController.useSystemBeep
  def useSystemBeep_=(b: Boolean) = theAlertsController.useSystemBeep = b
  def muted = theAlertsController.muted
  def muted_=(arg: Boolean) = theAlertsController.muted = arg
  def beep() = theAlertsController.beep()
  def reset() = theAlertsController.reset()
}
