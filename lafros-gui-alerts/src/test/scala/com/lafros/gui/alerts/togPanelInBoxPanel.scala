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
import scala.swing.{BoxPanel, Label, Orientation, Separator}
/**
 * @author Rob Dickens */
object togPanelInBoxPanel extends BoxPanel(Orientation.Vertical) {
  contents += new Label("before")
  contents += new TogPanel {
    alwaysVisibleComponent = Some(new Label("always visible"))
    concealableComponent = Some(new Label("concealable"))
    //
    // for some reason, the default value is CENTER_ALIGNMENT (while that of Labels
    // is LEFT_ALIGNMENT) - we require LEFT_ALIGNMENT too in order to prevent the
    // labels being indented
    xLayoutAlignment = java.awt.Component.LEFT_ALIGNMENT
  }
  contents += new Label("after")
  preferredSize = (500, 500)
}
