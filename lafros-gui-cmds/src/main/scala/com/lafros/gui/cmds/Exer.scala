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
import java.beans.PropertyChangeEvent
import java.lang.reflect.InvocationTargetException
import scala.swing.Publisher
import scala.swing.event.{ButtonClicked, Event}
/**
 * executes <tt>Cmd</tt>s. Create instances using <tt>Exer(myCmd)</tt>.
 * @author Rob Dickens */
trait Exer {
  /**
   * executes <tt>cmd</tt>, always returning immediately. */
  def executeCmd(ev: Option[Event])
  /**
   * calls <tt>executeCmd(None)</tt>. */
  def executeCmd(): Unit = executeCmd(None)
  /**
   * the <tt>Cmd</tt> that <tt>executeCmd</tt> executes. */
  def cmd: Cmd
}
/**
 * companion. */
object Exer {
  /**
   * instantiates the appropriate <tt>Exer</tt>. */
  def apply(cmd: Cmd): Exer = cmd match {
    case seqBgCmd: SeqBgCmd => new SeqBgExerImp(seqBgCmd)
    case _ => new ExerImp(cmd)
  }
  /**
   * instantiates the appropriate <tt>Tog.Exer</tt>. */
  def apply(togCmd: Tog.Cmd): Tog.Exer = togCmd match {
    case seqBgCmd: SeqBgCmd => new SeqBgTogExer(seqBgCmd)
    case _ => new TogExerImp(togCmd)
  }
  /**
   * instantiates the appropriate unset <tt>Tog.Exer</tt>. */
  def apply(setCmd: Cmd, resetCmd: Cmd): Tog.Exer = apply(setCmd, resetCmd, false)
  /**
   * instantiates the appropriate <tt>Tog.Exer</tt> having the specified toggle state. */
  def apply(setCmd: Cmd, resetCmd: Cmd, isSet: Boolean): Tog.Exer = {
    val tog = new Tog(isSet)
    if (setCmd.isInstanceOf[SeqBgCmd] || resetCmd.isInstanceOf[SeqBgCmd])
      new SeqBgTogExer(setCmd, resetCmd, tog)
    else 
      new TogExerImp(setCmd, resetCmd, tog)
  }
}
/**
 * <tt>Exer</tt> whose <tt>executeCmd</tt> executes <tt>NoCmd</tt>. */
object NoExer extends Exer {
  def executeCmd(ev: Option[Event]) {}
  val cmd = NoCmd
}

private[cmds] abstract class AbExer extends Exer {
  def executeCmd(ev: Option[Event]) = try {
    executeCmdSafely(ev)
  }
  catch {
    case ite: InvocationTargetException =>
      val cause = ite.getCause.asInstanceOf[Exception]
    TheCmdsController.failed(cause, false)
    case th => throw th // no recovery
  }

  def executeCmdSafely(ev: Option[Event])
}

private[cmds] object AbExer {
  def executeThisCmd(cmd: Cmd, opt: Option[Event]) = {
    try {
      for (ev <- opt) cmd match {
        case edc: EventDependentCmd =>
          TheCmdsController.setBusy(true)
        edc.setEvent(ev)
        TheCmdsController.setBusy(false)
        case _ =>
      }
      if (cancel(cmd)) None
      else {
        TheCmdsController.clear()
        TheCmdsController.setBusy(true)
        val msg = try {
          cmd()
        }
        finally {
          TheCmdsController.setBusy(false)
        }
        Some(msg)
      }
    }
    catch {
      case ex: Exception => throw new InvocationTargetException(ex)
    }
  }

  def cancel(cmd: Cmd) =
    (cmd match {
      case checkFirstCmd: CheckFirstCmd 
      if CheckFirstCmd.proceed(checkFirstCmd) == false => true
      case _ => false
    }) || (cmd match {
      case pwdProtectedCmd: PwdProtectedCmd
      if PwdProtectedCmd.proceed(pwdProtectedCmd) == false => true
      case _ => false
    })
}

private[cmds] class ExerImp(val cmd: Cmd) extends AbExer {
  def executeCmdSafely(ev: Option[Event]) {
    AbExer.executeThisCmd(cmd, ev) match {
      case None => // execution cancelled by user
      case Some(msg) => TheCmdsController.succeeded(msg, false)
    }
  }
}
/**
 * Exer that runs in the background. */
private[cmds] trait SeqBgExer extends Exer {
  /**
   * called before execute().
   * @return whether or not to call execute(), or None if execution cancelled
   * @throws InvocationTargetException if any component Cmd method threw an
   * exception. */
  //@throws(classOf[InvocationTargetException])
  def before(opt: Option[Event]): Option[Boolean] = try {
    TheCmdsController.setBusy(true)
    val msg = try {
      for (ev <- opt) cmd match {
        case edc: EventDependentCmd => edc.setEvent(ev)
        case _ =>
      }
      cmd.asInstanceOf[SeqBgCmd].before()
    }
    finally {
      TheCmdsController.setBusy(false)
    }
    if (AbExer.cancel(cmd)) return None
    msg match {
      case None => TheCmdsController.clear(); Some(false)
      case Some(x) => TheCmdsController.trying(x); Some(true)
    }
  }
  catch {
    case ex: Exception => throw new InvocationTargetException(ex)
  }
  /**
   * called after execute().
   * @return whether or not to call execute(), or None if execution cancelled
   * @throws InvocationTargetException if any component Cmd method threw an
   * exception. */
  //@throws(classOf[InvocationTargetException])
  def after(ex: Option[Exception])
}

private[cmds] class SeqBgExerImp(val cmd: SeqBgCmd) extends AbExer with SeqBgExer {
  def executeCmdSafely(ev: Option[Event]) = seqBgExerHelper.executeCmd(this, ev)
  def after(ex: Option[Exception]) = cmd.after(ex)
}
