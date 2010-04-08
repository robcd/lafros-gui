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
import lafros.maven.scalatest.{FunSuite, GuiSupport}
/**
 * @author Rob Dickens */
class TogPanelTest extends FunSuite with GuiSupport {
  test("TogPanel should open when MonField has alert other than alerts.NoAlert") {
    displayGui(togPanel)
  }

  test("using a TogPanel in a BoxPanel") {
    displayGui(togPanelInBoxPanel)
  }
}
