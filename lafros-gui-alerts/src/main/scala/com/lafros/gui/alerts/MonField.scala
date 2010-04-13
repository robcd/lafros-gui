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
import java.awt.Color
import scala.swing.{Alignment, Component, Label, Reactions, Swing}
import scala.swing.event.MouseClicked
import Alert._
/**
 * specialised <tt>Label</tt>, having properties intended to tailor it for use as a monitor field. 
 * @author Rob Dickens */
class MonField extends Label("", Swing.EmptyIcon, Alignment.Center) {
  override lazy val peer: JConstrainableLabel =
    new JConstrainableLabel with SuperMixin
  private var _value: Any = text
  private var _alert: Alert = NoAlert
  private var _alertSound: AlertSound = DefaultAlertSound
  private var _valueToAlert: MonField.AnyToAlert = (_: Any) => _alert
  private var _normalBackground: Color = _ // i.e. that of parent

  opaque = true
  reactions += {
    case x: MouseClicked => alert = Acknowledged
  }
  /**
   * also initialises the <tt>templateText</tt> property to <tt>templateText0</tt>. */
  def this(templateText0: String) {
    this()
    templateText = templateText0
  }
  /**
   * forwards to <tt>value_=</tt>. */
  override def text_=(arg: String) = value = arg
  /**
   * overrides the superclass so as to take into account the value of
   * <tt>alert</tt>. */
  override def background = _normalBackground
  /**
   * sets this property. */
  override def background_=(arg: Color) {
    import MonField.backgrounds._
    arg match {
      case LampOff => throw new IllegalArgumentException("background may not be set to an alert colour: Color.orange or Color.red")
      case _ => if (_alert == NoAlert) super.background = arg
      _normalBackground = arg
    }
  }
  /**
   * unless empty (the default value), constrains the width of the field to that
   * required to display this text, no matter what value is assigned to the
   * <tt>text</tt> property. */
  def templateText = peer.templateText
  /**
   * sets this property. */
  def templateText_=(s: String) = peer.templateText = s
  /**
   * property whose value may be set so as to attract the user's attention. */
  def alert = _alert
  /**
   * sets this property. */
  def alert_=(arg: Alert) = if (arg != _alert) {
    if (_alert == Intrusive) removeIntruder()
    val previousValue = _alert
    _alert = arg
    arg match {
      case NoAlert => super.background = _normalBackground
      case NonIntrusive | Acknowledged =>
        super.background = MonField.backgrounds.LampOff
      case Intrusive => addIntruder()
    }
    //println("publishing AlertChanged, from "+ previousValue +" to "+ alert)
    publish(AlertChanged(this, previousValue))
  }
  /**
   * the sound to be used when <tt>alert</tt> is set to <tt>Intrusive</tt>,
   * having a default value of <tt>DefaultAlertSound</tt>. */
  def alertSound = _alertSound
  /**
   * sets this property. */
  def alertSound_=(arg: AlertSound) = if (arg != _alertSound) {
    val intrusive = _alert == Intrusive
    if (intrusive) removeIntruder()
    _alertSound = arg
    if (intrusive) addIntruder()
  }
  /**
   * function of type, <tt>Any => Alert</tt>, that will be used to
   * set the <tt>alert</tt> whenever <tt>value</tt> is set. */
  def valueToAlert = _valueToAlert
  /**
   * sets this property. */
  def valueToAlert_=(arg: MonField.AnyToAlert) = if (arg != valueToAlert) {
    _valueToAlert = arg
    alert = valueToAlert(value)
  }
  /**
   * the one being displayed. */
  def value = _value
  /**
   * also sets the <tt>text</tt> property, to <tt>arg.toString</tt>,
   * together with the </tt>alert</tt> one, to <tt>toAlert()</tt>. */
  def value_=(arg: Any) = if (arg != _value) {
    _value = arg
    super.text = arg.toString
    alert = valueToAlert(value)
  }

  private[alerts] def setLampOn(b: Boolean) {
    import MonField.backgrounds._
    super.background = if (b) LampOn else LampOff
  }

  private def addIntruder() {
    listenTo(Mouse.clicks)
    intruders.add(this)
  }
  private def removeIntruder() {
    deafTo(Mouse.clicks)
    intruders.remove(this)
  }
}
/**
 * companion.*/
object MonField {
  /**
   * alias for <tt>Any => Alert</tt>. */
  type AnyToAlert = Any => Alert

  private object backgrounds {
    val LampOn = Color.yellow
    val LampOff = Color.red
  }
}
