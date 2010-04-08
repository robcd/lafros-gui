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
import org.scalatest.matchers.ShouldMatchers
/**
 * @author Rob Dickens */
class MonFieldTest extends FunSuite with ShouldMatchers {
  val mf = new MonField
  test("setting text should set value (to text)") {
    val text = "text"
    mf.text = text
    mf.text should equal (text)
    mf.value should equal (text)
  }
  test("setting value should set text to value.toString") {
    mf.value = 1
    mf.text should equal ("1")
  }
  test("default toAlert should return current alert") {
    mf.valueToAlert() should equal (mf.alert)
  }
  test("changing valueToAlert should also apply it") {
    import Alert._
    val warnIfNotPositive = (value: Any) => value match {
      case i: Int if i > 0 => NoAlert
      case i: Int if i == 0 => NonIntrusive
      case _ => Intrusive
    }
    mf.value should equal (1)
    mf.alert should equal (NoAlert)
    mf.valueToAlert = warnIfNotPositive
    mf.alert should equal (NoAlert)
    mf.value = -1
    mf.alert should equal (Intrusive)
    mf.value = 0
    mf.alert should equal (NonIntrusive)
    mf.text = "1"
    mf.alert should equal (Intrusive)
    mf.value = 1 
    mf.alert should equal (NoAlert)
  }
}
