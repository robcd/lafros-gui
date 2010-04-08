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
import scala.swing.event.Event
/**
 * @author Rob Dickens */
private[cmds] abstract class AbTogExer(setCmd: Cmd, resetCmd: Cmd,
  val tog: Tog) extends AbExer with Tog.Exer {

  def cmd = if (resetCmd == null || !tog.isSet) setCmd else resetCmd
}

private[cmds] class TogExerImp(setCmd: Cmd, resetCmd: Cmd, tog: Tog)
              extends AbTogExer(setCmd, resetCmd, tog) {

  def this(togCmd: Tog.Cmd) = this(togCmd, null, togCmd.tog)

  def executeCmdSafely(ev: Option[Event]) = AbExer.executeThisCmd(cmd, ev) match {
    case None => // execution cancelled by user
    case Some(msg) => TheCmdsController.succeeded(msg, false)
    tog.toggle()
  }
}
/**
 * Exer that runs in the background. */
private[cmds] class SeqBgTogExer(setCmd: Cmd, resetCmd: Cmd, tog: Tog)
              extends AbTogExer(setCmd, resetCmd, tog) with SeqBgExer {

  def this(togCmd: Tog.Cmd) = this(togCmd, null, togCmd.tog)

  def executeCmdSafely(ev: Option[Event]) = cmd match {
    case seqBgCmd: SeqBgCmd => seqBgExerHelper.executeCmd(this, ev)
    case cmd => AbExer.executeThisCmd(cmd, ev) match {
      case None => // execution cancelled by user
      case Some(msg) => TheCmdsController.succeeded(msg, false)
      tog.toggle()
    }
  }

  def after(ex: Option[Exception]) = try {
    cmd.asInstanceOf[SeqBgCmd].after(ex)
  }
  finally {
    if (!ex.isDefined) tog.toggle()
  }
}
