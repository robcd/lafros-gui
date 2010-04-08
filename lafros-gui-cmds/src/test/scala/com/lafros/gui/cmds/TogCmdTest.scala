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
/**
 * @author Rob Dickens */
class TogCmdTest extends FunSuite with ShouldMatchers {

  test("toggling a Tog.Exer created from an unset Tog.Cmd") {
    doTest(false)
  }

  test("toggling a Tog.Exer created from an set Tog.Cmd") {
    doTest(true)
  }

  def doTest(initialState: Boolean) {
    val cmd = new Tog.Cmd {
      def apply() = None
      val tog = new Tog(initialState)
    }
    val exer = Exer(cmd)
    //
    exer.isInstanceOf[Tog.Exer] should be (true)
    //
    cmd.tog.isSet should be (initialState)
    exer.tog.isSet should be (initialState)
    exer.executeCmd
    cmd.tog.isSet should be (!initialState)
    exer.tog.isSet should be (!initialState)
    exer.executeCmd
    cmd.tog.isSet should be (initialState)
    exer.tog.isSet should be (initialState)
  }
}
