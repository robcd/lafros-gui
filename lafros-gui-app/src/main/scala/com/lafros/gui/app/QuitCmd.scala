/**
 * Copyright 2009 Latterfrosken Software Development Limited
 *
 * This file is part of Lafros GUI-App.
 *
 * Lafros GUI-App is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * Lafros GUI-App is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with Lafros GUI-App. If not, see <http://www.gnu.org/licenses/>. */
package com.lafros.gui.app

import java.awt.event.KeyEvent
import java.lang.Integer
import javax.swing.KeyStroke
import scala.swing.Action

import com.lafros.gui.cmds.{CheckFirstCmd, Cmd, Exer}
/**
 * quits the application!
 *
 * @author Rob Dickens */
private[app] class QuitCmd(
  private val app: App) extends CheckFirstCmd {
  /**
   * Setting this to <code>None</code> causes {@link #confirm()} to return
   * false (so that the user is not prompted).
   * @param text Include the '.' or '!". */
  var confirmReason: Option[String] = None

  lazy val exer = Exer(this)
  lazy val action = new Action("Quit") {
    mnemonic = KeyEvent.VK_Q
    accelerator = Some(KeyStroke.getKeyStroke("control Q"))
    def apply() = exer.executeCmd()
  }
  def apply() = {
    app.terminate()
    System.exit(0)
    None
  }
  def prompt = confirmReason match {
    case None => None
    case Some(x) => Some(x +" - Really quit?")
  }
  override def safe = false
}
