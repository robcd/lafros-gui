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
import lafros.maven.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import scala.swing.ToggleButton
/**
 * @author Rob Dickens */
class ToggleButtonTrigTest extends FunSuite with ShouldMatchers {
  val trig = new ToggleButton with Trig
  val cmdString = "cmd"
  val prefix = "prefix-"
  val cmd = new Cmd {
    def apply() = None
    override def toString = cmdString
  }
  test("Cmd") {
    trig.text should equal ("")
    trig.cmd = cmd
    trig.text should equal (cmdString)
    trig.exerToText = (exer: Exer) => prefix + exer.cmd.toString
    trig.text should equal (cmdString)
    trig.cmd = NoCmd
    trig.text should equal (cmdString)
    trig.text = ""
    trig.cmd = cmd
    trig.text should equal (prefix + cmdString)
    trig.selected should be (false)
    trig.doClick()
    trig.text should equal (prefix + cmdString)
    trig.selected should be (false)
  }
  val text0 = "text0"
  val text1 = "text1"
  val text2 = "text2"
  test("Cmd, Cmd") {
    val setCmdString = "set-cmd"
    val setCmd = new Cmd {
      def apply() = None
      override def toString = setCmdString
    }
    val resetCmdString = "reset-cmd"
    val resetCmd = new Cmd {
      def apply() = None
      override def toString = resetCmdString
    }
    val exer = Exer(setCmd, resetCmd)
    trig.text = ""
    trig.exer = exer
    trig.text should equal ("")
    exer.tog.isSet should be (false)
    trig.doClick()
    exer.tog.isSet should be (true)
    trig.selected should be (true)
    trig.text should equal ("")
    trig.text0 = text0
    trig.text should equal ("")
    trig.text1 = text1
    trig.text should equal ("")
    trig.doClick()
    exer.tog.isSet should be (false)
    trig.selected should be (false)
    trig.text should equal ("")
    trig.text = text2
    trig.doClick()
    trig.text should equal (text2)
    trig.exer = NoExer
    trig.text should equal (text2)
    trig.exer = exer
    trig.text should equal (text2)
    trig.doClick()
    trig.text should equal (text2)
  }
  test("Tog.Cmd") {
    val togCmdString = "tog-cmd"
    val togCmd = new Tog.Cmd {
      def apply() = None
      val tog = new Tog
      override def toString = togCmdString
    }
    trig.cmd = togCmd
    trig.text should equal (text2)
    trig.selected should be (false)
    togCmd.tog.isSet should be (false)
    trig.doClick()
    trig.selected should be (true)
    togCmd.tog.isSet should be (true)
    trig.text should equal (text2)
    trig.text = ""
    trig.doClick()
    trig.selected should be (false)
    togCmd.tog.isSet should be (false)
    trig.text should equal (prefix + togCmdString)
    trig.doClick()
    trig.selected should be (true)
    togCmd.tog.isSet should be (true)
    trig.text should equal (prefix + togCmdString)
  }
}
