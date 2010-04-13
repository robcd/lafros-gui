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

import Alert._
import scala.swing.{BorderPanel, CheckBox, Component, Publisher, Separator}
import scala.swing.event.ButtonClicked
import BorderPanel.Position._
import java.awt.BorderLayout
import javax.swing.{JComponent, JPanel, SwingUtilities}
/**
 * toggle panel, to which may be added a component which is concealable. When
 * adding a <tt>MonField</tt> to the <tt>concealableComponent</tt>,
 * <tt>togPanel.listenTo(monField)</tt> will cause <tt>togPanel</tt> to open
 * automatically whenever <tt>monField</tt>'s <tt>alert</tt> property is anything
 * other than <tt>NoAlert</tt>.
 *
 * @author Rob Dickens */
class TogPanel extends BorderPanel {
  override lazy val peer = new JPanel(new BorderLayout) with SuperMixin {
    override def updateUI() {
      super.updateUI()
      if (!open) updateConcealableUi = true
    }
//     def setFocusable(b: Boolean) {
//       super.setFocusable(b)
//       if (togButton != null)
//         togButton.setFocusable(b)
//     }
  }
  private val alwaysVisibleContainer = new BorderPanel
  //
  private var togButton: CheckBox = _
  private var separator: Separator = _
  private var _concealableComponent, _alwaysVisibleComponent: Option[Component] = None
  private var _proppedOpen, goingFromOrToZeroAlerts, updateConcealableUi: Boolean = _
  private var alertCount: Int = _

  layout(alwaysVisibleContainer) = North
  reactions += {
    case AlertChanged(monField, previousValue) =>
      //println("TogPanel reacting to AlertChanged, from "+ previousValue +" to "+ monField.alert)
      monField.alert match {
      case NoAlert => decAlerts()
      case _ => if (previousValue == NoAlert) incAlerts()
    }
  }
  /**
   * looks out for <tt>MonField</tt>s with alerts. */
  override def listenTo(ps: Publisher*) = xTo(super.listenTo(ps: _*), incAlerts, ps: _*)
  /**
   * looks out for <tt>MonField</tt>s with alerts. */
  override def deafTo(ps: Publisher*) = xTo(super.deafTo(ps: _*), decAlerts, ps: _*)

  private def xTo(f1: => Unit, f2: => Unit, ps: Publisher*) {
    f1
    for (p <- ps) p match {
      case monField: MonField => monField.alert match {
        case NonIntrusive | Intrusive => f2
        case NoAlert =>
      }
      case _ =>
    }
  }
  /**
   * component beneath which the concealable one is displayed. */
  def alwaysVisibleComponent = _alwaysVisibleComponent
  /**
   * sets this property. */
  def alwaysVisibleComponent_=(arg: Option[Component]) {
    arg match {
      case Some(c) => alwaysVisibleContainer.layout(c) = West
      case None => _alwaysVisibleComponent match {
        case Some(c) => alwaysVisibleContainer.layout -= c
        case None => return
      }
    }
    redraw(alwaysVisibleContainer.peer)
    _alwaysVisibleComponent = arg
  }
  /**
   * supplying this component causes a check-box to appear (after the
   * <tt>alwaysVisibleComponent</tt>), selection of which causes the 
   * component to be displayed underneath. */  
  def concealableComponent = _concealableComponent
  /**
   * sets this property. */
  def concealableComponent_=(arg: Option[Component]) {
    arg match {
      case Some(c) =>
        _concealableComponent match {
          case Some(c) =>
	    if (togButton.selected) {
	      layout(c) = Center
	      redraw(peer)
	    }
          case None =>
	    togButton = new CheckBox("...") {
              reactions += {
                case ButtonClicked(_) =>
	          if (!goingFromOrToZeroAlerts) _proppedOpen = !_proppedOpen
	        if (togButton.selected) {
		  separator = new Separator
		  alwaysVisibleContainer.layout(separator) = South
		  layout(c) = Center
		  if (updateConcealableUi) {
		    SwingUtilities.updateComponentTreeUI(c.peer)
		    updateConcealableUi = false
		  }
	        }
	        else {
		  alwaysVisibleContainer.layout -= separator
		  separator = null
		  layout -= c
	        }
	        redraw(TogPanel.this.peer)
              }
            }
	  //togButton.setFocusable(isFocusable())
	  alwaysVisibleContainer.layout(togButton) = Center
	  redraw(alwaysVisibleContainer.peer)
          if (alertCount > 0) {
	    goingFromOrToZeroAlerts = true
	    togButton.doClick()
	    goingFromOrToZeroAlerts = false
          }
        }
      case None =>
        _concealableComponent match {
          case Some(c) =>
            // remove existing one together with checkbox
            alwaysVisibleContainer.layout -= togButton
          if (togButton.selected) {
	    layout -= c
	    alwaysVisibleContainer.layout -= separator
	    redraw(peer)
	    separator = null
          }
          else
	    redraw(alwaysVisibleContainer.peer)
          togButton = null
          case None => return
        }
    }
    _concealableComponent = arg
  }
  /**
   * determines whether or not the <tt>TogPanel</tt> remains <tt>open</tt> when
   * the <tt>alert</tt> property of all <tt>MonField</tt> descendants of the
   * <tt>concealableComponent</tt> has the value, <tt>Alert.NoAlert</tt>.
   * Explicitly opening or closing the <tt>TogPanel</tt> sets this property to
   * <tt>true</tt> or <tt>false</tt>, respectively. */
  def proppedOpen = _proppedOpen
  /**
   * sets this property. */
  def proppedOpen_=(b: Boolean) = if (b != _proppedOpen) {
    // if alerts then just change flag
    if (alertCount > 0) _proppedOpen = b
    else togButton.doClick() // changes flag as well
  }
  /**
   * returns <tt>true</tt> if the <tt>concealableComponent</tt> is displayed. */
  def open = if (togButton == null) false else togButton.selected
  //
  // called by MonLabel upon changing TO alert-mode
  private def incAlerts() {
    alertCount += 1
    if (alertCount == 1 &&
	togButton != null) {
      if (!togButton.selected) {
	goingFromOrToZeroAlerts = true
	togButton.doClick()
	goingFromOrToZeroAlerts = false
      }
      togButton.enabled = false
    }
  }
  //
  // called by MonLabel upon changing FROM alert-mode
  private def decAlerts() {
    alertCount -= 1
    if (alertCount == 0 &&
	togButton != null) {
      togButton.enabled = true
      if (togButton.selected &&
	  !_proppedOpen) {
	goingFromOrToZeroAlerts = true
	togButton.doClick()
	goingFromOrToZeroAlerts = false
      }
    }
  }

  private def redraw(c: JComponent) {
    if (c.isDisplayable()) {
      c.validate()
      c.revalidate()
      c.repaint()
    }
  }
}
