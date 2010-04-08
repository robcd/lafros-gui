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
import scala.swing.{BorderPanel, ButtonGroup, FlowPanel, Label, RadioButton}
import scala.swing.event.{ButtonClicked, Event}
import BorderPanel.Position._
/**
 * @author Rob Dickens */
object togPanel extends BorderPanel {
  val monField = new MonField {
    text = "concealable!"
  }
  val northPanel = new FlowPanel {
    def radioButton(alert: Alert) = new RadioButton(alert.toString) {
      def reaction: PartialFunction[Event, Unit] = {
        case _: ButtonClicked => monField.alert = alert
      }
      reactions += reaction
    }
    val NoneRb = radioButton(NoAlert)
    val NonIntrusiveRb = radioButton(NonIntrusive)
    val IntrusiveRb = radioButton(Intrusive)
    val bg = new ButtonGroup(NoneRb, NonIntrusiveRb, IntrusiveRb) {
      select(NoneRb)
    }
    contents += NoneRb
    contents += NonIntrusiveRb
    contents += IntrusiveRb
  }
  layout(northPanel) = North
  val centrePanel = new TogPanel {
    alwaysVisibleComponent = Some(new Label("always visible!"))
    concealableComponent = Some(monField)
    listenTo(monField)
  }
  layout(centrePanel) = Center
  preferredSize = (500, 500)
}
