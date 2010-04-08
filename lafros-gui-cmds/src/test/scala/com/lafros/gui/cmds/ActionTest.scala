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
import scala.swing.{AbstractButton, Action, Button, CheckBox, event, ToggleButton}
import event.ActionEvent
/**
 * @author Rob Dickens */
class ActionTest extends FunSuite with ShouldMatchers {
  val cmdReaction: Cmd.Reaction = {
    case _ => None
  }
  val cmd = Cmd {
    None
  }
  val trig = new Button with Trig
  val trigProps = new Trig.Props
  val text0 = "text0"
  val text1 = "text1"
  test("Trig.Props actions should not remove cmdReaction") {
    trig.cmdReaction = cmdReaction
    trig.action = trigProps
    trig.cmdReaction should equal (cmdReaction)
  }
  test("Trig.Props actions should not remove cmd") {
    trig.action = Action.NoAction
    trig.cmd = cmd
    trig.action = trigProps
    trig.cmd should equal (cmd)
  }
  test("Trig's text0, text1 should be set from Trig.Props") {
    trig.action = Action.NoAction
    trig.text should equal ("")
    trig.text0 should equal ("")
    trig.text1 should equal ("")
    trigProps.title0 = text0
    trigProps.title1 = text1
    trig.action = trigProps
    trig.text should equal ("")
    trig.text0 should equal (text0)
    trig.text1 should equal (text1)
  }
  test("Trig's text0, text1 should change if Trig.Props change") {
    trigProps.title0 = ""
    trigProps.title1 = ""
    trig.text0 should equal ("")
    trig.text1 should equal ("")
  }
  test("non Trig.Props actions should remove cmdReaction, cmd, exer") {
    trig.action = Action("") {}
    trig.cmdReaction should equal (NoCmd.Reaction)
    trig.cmd should equal (NoCmd)
    trig.exer should equal (NoExer)
  }
}
