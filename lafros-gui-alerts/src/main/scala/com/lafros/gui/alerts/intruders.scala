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
import scala.collection.mutable
import scala.swing.Swing
import javax.swing.Timer
import Alert._
/**
 * collection of MonFields currently assigned IntrusiveAlerts.
 * @author Rob Dickens */
private[alerts] object intruders {
  private lazy val _monFields = mutable.Set.empty[MonField]
  private lazy val _beepCounts = mutable.Map.empty[Beep, Int]
  private lazy val _loopCounts = mutable.Map.empty[Loop, Int]
  private val sleepSecs = 1
  private lazy val timer = new Timer(sleepSecs*1000, Swing.ActionListener { e =>
    for (monLabel <- _monFields) monLabel.setLampOn(_lampOn)
    if (_lampOn && !_muted) {
      if (_useSystemBeep) SystemBeep.beep()
      else for (beep <- _beepCounts.keys) beep.beep()
    }
    _lampOn = !_lampOn
  })
  private var _lampOn = true
  private var defCount = 0
  private var _defaultAlertSound = AlertSound.initialDefaultBuiltInOne
  private var _useSystemBeep = false
  private var _muted = false

  def add(arg: MonField) {
    _monFields += arg
    arg.alertSound match {
      case DefaultAlertSound =>
        defCount += 1
      if (defCount == 1) {
        (_defaultAlertSound: @unchecked) match {
          case beep: Beep => addBeep(beep)
          case loop: Loop => addLoop(loop)
        }
      }
      case beep: Beep => addBeep(beep)
      case loop: Loop => addLoop(loop)
    }
    if (_monFields.size == 1) {
      TheAlertsController.setAlertsRaised(true)
      timer.start()
    }
  }
  def remove(arg: MonField) {
    _monFields -= arg
    arg.alertSound match {
      case DefaultAlertSound =>
        defCount -= 1
      if (defCount == 0) {
        (_defaultAlertSound: @unchecked) match {
          case beep: Beep => removeBeep(beep)
          case loop: Loop => removeLoop(loop)
        }
      }
      case beep: Beep => removeBeep(beep)
      case loop: Loop => removeLoop(loop)
    }
    if (_monFields.size == 0) {
      timer.stop()
      TheAlertsController.setAlertsRaised(false)
      _lampOn = true
    }
  }
  def acknowledgeAll() =
    for (monLabel <- _monFields) monLabel.alert = Acknowledged
  def defaultAlertSound = _defaultAlertSound
  def defaultAlertSound_=(arg: AlertSound) = if (_defaultAlertSound != arg)
    arg match {
      case DefaultAlertSound => // nothing to do!
      case _ => if (defCount > 0) {
        (_defaultAlertSound: @unchecked) match {
          case beep: Beep => removeBeep(beep)
          case loop: Loop => removeLoop(loop)
        }
        (arg: @unchecked) match {
          case beep: Beep => addBeep(beep)
          case loop: Loop => addLoop(loop)
        }
      }
      _defaultAlertSound = arg
    }
  def useSystemBeep = _useSystemBeep
  def useSystemBeep_=(arg: Boolean) = if (_useSystemBeep != arg) {
    startOrStopLoop(_useSystemBeep, _muted)
    _useSystemBeep = arg
  }
  def muted = _muted
  def muted_=(arg: Boolean) = if (_muted != arg) {
    startOrStopLoop(_muted, _useSystemBeep)
    _muted = arg
  }
  //
  private def startOrStopLoop(start: Boolean, startVetoed: Boolean) {
    val stopped = _muted || _useSystemBeep
    if (start) {
      if (stopped && !startVetoed) for (loop <- _loopCounts.keys) loop.start()
    }
    else {
      if (!stopped) for (loop <- _loopCounts.keys) loop.stop()
    }
  }
  private def addBeep(beep: Beep) {
    val count = if (_beepCounts.contains(beep)) _beepCounts(beep) else 0
    _beepCounts += (beep -> (count + 1))
  }
  private def removeBeep(beep: Beep) {
    val count = _beepCounts(beep)
    if (count == 1) _beepCounts -= beep
    else _beepCounts += (beep -> (count - 1))
  }
  private def addLoop(loop: Loop) {
    val count = if (_loopCounts.contains(loop)) _loopCounts(loop) else 0
    _loopCounts += (loop -> (count + 1))
    if (count == 0) startLoop(loop)
  }
  private def removeLoop(loop: Loop) {
    val count = _loopCounts(loop)
    if (count == 1) {
      _loopCounts -= loop
      stopLoop(loop)
    }
    else _loopCounts += (loop -> (count - 1))
  }
  private def startLoop(loop: Loop) = if (appropriate) loop.start()
  private def stopLoop(loop: Loop) = if (appropriate) loop.stop()
  private def appropriate = !(_muted || _useSystemBeep)
}
