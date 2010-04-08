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
import scala.swing.{AbstractButton, Button, CheckBox, event, ToggleButton}
import event.ActionEvent
/**
 * @author Rob Dickens */
class TrigTest extends FunSuite with ShouldMatchers {
  var trig: Trig = _
  var reacted = false
  var succeededMsg: String = _
  var exCaught: Exception = _
  val cmdsApp = new CmdsApp {
    def setBusy(b: Boolean) {}
    def clear() {}
    def trying(msg: String) {}
    def succeeded() {}
    def succeeded(msg: String) {
      succeededMsg = msg
    }
    def failed(msg: String, cause: Exception) {
      exCaught = cause
    }
  }
  TheCmdsController.instance.app = Some(cmdsApp)

  test("instant'n") {
    trig = new Button with Trig
    trig.cmdReaction should equal (NoCmd.Reaction)
    trig.cmd should equal (NoCmd)
    trig.exer should equal (NoExer)
  }

  test("setting cmdReaction") {
    trig.cmdReaction = {
      case ActionEvent(_) =>
      reacted = true
      None
    }
    trig.cmd should not equal (NoCmd)
    trig.exer should not equal (NoExer)
  }

  test("triggering the cmdReaction") {
    trig.doClick()
    reacted should be (true)
  }

  test("cmdReaction returning a message") {
    val msg = "bingo"
    trig.cmdReaction = {
      case ActionEvent(_) =>
        Some(msg)
    }
    trig.doClick()
    succeededMsg should equal (msg)
  }

  test("cmdReaction throwing an Exception") {
    val exThrown = new RuntimeException("bla")
    trig.cmdReaction = {
      case ActionEvent(_) =>
        throw exThrown
        None
    }
    trig.doClick()
    exCaught eq exThrown should be (true)
  }

  val cmdString = "some-cmd"
  val msg = "bongo"
  val cmd = new Cmd {
    def apply() = Some(msg)
    override def toString = cmdString
  }

  test("setting the cmd property") {
    trig.cmd = cmd
    trig.cmdReaction should equal (NoCmd.Reaction)
    trig.exer.cmd should equal (trig.cmd)
    trig.text should equal (cmdString)
    trig.doClick()
    succeededMsg should equal (msg)
  }

  val prefix = "prefix-"

  test("replacing toText") {
    trig.text should equal (cmdString)
    trig.exerToText = (exer: Exer) => prefix + exer.cmd.toString
    // Button + Cmd = apply only if text == ""
    trig.text should equal (cmdString)
    trig.text = ""
    trig.cmd = NoCmd
    trig.text should equal (prefix)
    trig.text = ""
    trig.cmd = cmd
    trig.text should equal (prefix + cmdString)
  }

  test("setting the exer property") {
    val text = "cmd-set-via-exer"
    val cmd = new Cmd {
      def apply() = None
      override def toString = text
    }
    val exer = Exer(cmd)
    trig.exer = exer
    trig.cmdReaction should equal (NoCmd.Reaction)
    trig.exer.cmd should equal (cmd)
    trig.cmd should equal (cmd)
    trig.text should equal (prefix + cmdString)
    trig.cmd = NoCmd
    trig.text = ""
    trig.exer = exer
    trig.text should equal (prefix + text)
  }

  val setCmdText = "set"
  val resetCmdText = "reset"
  val setCmd = new Cmd {
    def apply() = None
    override def toString = setCmdText
  }
  val resetCmd = new Cmd {
    def apply() = None
    override def toString = resetCmdText
  }
  val togExer = Exer(setCmd, resetCmd)

  test("supplying exer with pair of Cmds") {
    trig.exer = togExer
    trig.cmd should equal (setCmd)
    trig.text should equal (prefix + setCmdText)
    trig.doClick()
    trig.cmd should equal (resetCmd)
    trig.text should equal (prefix + resetCmdText)
  }

  test("assigning cmd a Tog.Cmd") {
    val text0 = "do it"
    val text1 = "undo it"
    val togCmd = new Tog.Cmd {
      def apply() = None
      val tog = new Tog(false)
    }
    trig.cmd = togCmd
    trig.text0 = text0
    trig.text1 = text1
    trig.text should equal (text0)
    togCmd.tog.isSet should be (false)
    trig.exer.asInstanceOf[Tog.Exer].tog.isSet should be (false)
    trig.doClick()
    trig.text should equal (text1)
    trig.exer.asInstanceOf[Tog.Exer].tog.isSet should be (true)
    togCmd.tog.isSet should be (true)
  }

  test("new ToggleButton with Trig { exer = togExer }") {
    val s = "do bla"
    trig = new ToggleButton with Trig {
      text = s
      exer = togExer
    }
    togExer.tog.isSet should be (true)
    trig.selected should be (true)
    trig.text should equal (s)//resetCmdText)
    trig.doClick()
    togExer.tog.isSet should be (false)
    trig.selected should be (false)
    trig.text should equal (s)//setCmdText)
  }

  test("cmd = new Tog.Cmd") {
    var throwException = true
    trig.cmd = new Tog.Cmd {
      def apply() = {
        if (throwException) throw new RuntimeException
        None
      }
      val tog = new Tog(false)
    }
    trig.selected should be (false)
    trig.doClick()
    trig.selected should be (false)
    throwException = false
    trig.doClick()
    trig.selected should be (true)
  }

  val trigProps = new Trig.Props {
    enabled = false
  }
  val nonTogExer = {
    val cmd = new Cmd {
      def apply() = None
    }
    Exer(cmd)
  }
  val togCmdTogExer = {
    val togCmd = new Tog.Cmd {
      def apply() = None
      val tog = new Tog
    }
    Exer(togCmd)
  }
  val nonTogCmdTogExer = {
    val setCmd = new Cmd {
      def apply() = None
    }
    val resetCmd = new Cmd {
      def apply() = None
    }
    Exer(setCmd, resetCmd)
  }

  test("trigProps.enabled = false; *trig.action = trigProps") {
    var trig: AbstractButton = new Button with Trig {
      exer = nonTogExer
      action = trigProps
    }
    trig.enabled should be (false)
    trig = new Button with Trig {
      action = trigProps
      exer = nonTogExer
    }
    trig = new Button(trigProps) with Trig {
      exer = nonTogExer
    }
    trig.enabled should be (false)
    trig = new ToggleButton with Trig {
      exer = togCmdTogExer
      action = trigProps
    }
    trig.enabled should be (false)
    trig = new ToggleButton with Trig {
      action = trigProps
      exer = togCmdTogExer
    }
    trig.enabled should be (false)
    trig = new ToggleButton with Trig {
      exer = nonTogCmdTogExer
      action = trigProps
    }
    trig.enabled should be (false)
    trig = new ToggleButton with Trig {
      action = trigProps
      exer = nonTogCmdTogExer
    }
    trig.enabled should be (false)
  }
}
