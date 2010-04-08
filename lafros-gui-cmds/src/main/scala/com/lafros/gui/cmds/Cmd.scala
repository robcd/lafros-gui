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
import java.awt.KeyboardFocusManager
import java.awt.event.{ActionListener, ComponentAdapter, ComponentEvent, FocusAdapter, FocusEvent}
import javax.swing.JOptionPane
import scala.swing.{Dialog, event, PasswordField, Swing}
import event.{ActionEvent, EditDone, Event}
/**
 * Assuming that GUI apps may be considered as being composed of commands,
 * <tt>Cmd</tt> represents the app-specific part of such a command. Impl'ns must
 * supply a <tt>def apply()</tt> which returns either
 * <tt>Some(userFeedbackString)</tt> or <tt>None</tt>. This will be called when the
 * command is executed: if it returns, the command will be deemed to have
 * succeeded; otherwise, if an <tt>Exception</tt> was thrown, this will be caught,
 * and the command deemed to have failed.
 * @see CmdsApp
 * @author Rob Dickens */
trait Cmd extends Function0[Cmd.Result]
/**
 * companion. */
object Cmd {
  /**
   * alias for <tt>Option[String]</tt>. */
  type Result = Option[String]
  /**
   * alias for <tt>PartialFunction[Event, Result]</tt>. */
  type Reaction = PartialFunction[Event, Result]
  /**
   * allows a <tt>Cmd</tt> to be created directly from a block, using <tt>Cmd
   * {...}</tt>, whose <tt>toString</tt> will return <tt>""</tt>. */
  def apply(arg: => Result): Cmd = apply("")(arg)
  /**
   * allows a <tt>Cmd</tt> to be created directly from a block, using <tt>Cmd(text)
   * {...}</tt>, whose <tt>toString</tt> will return <tt>text</tt>. */
  def apply(text: String)(arg: => Result) = new Cmd {
    def apply() = arg
    override def toString = text
  }
}
/**
 * <tt>Cmd</tt> which does nothing, and whose <tt>toString</tt> returns
 * <tt>""</tt>. */
object NoCmd extends Cmd {
  def apply() = None
  override def toString = ""
  /**
   * <tt>Cmd.Reaction</tt> which does nothing and returns <tt>None</tt>. */
  object Reaction extends Cmd.Reaction {
    def apply(ev: Event) = None
    def isDefinedAt(ev: Event) = ev match {
      case ActionEvent(_) | EditDone(_) => true
      case _ => false
    }
  }
}
/**
 * one whose <tt>apply</tt> method is to be called in the background. If another
 * <tt>SeqBgCmd</tt> is already in progress, execution is cancelled, and the user
 * alerted. Note that, since <tt>apply</tt> is to be called from a thread other
 * than the GUI dispatch one, it may not interact with the GUI. */
trait SeqBgCmd extends Cmd {
  /**
   * called before <tt>apply</tt>, in the foreground. Any <tt>Exception</tt>
   * thrown will be caught, and the command will be deemed to have failed.
   * @return a message indicating what <tt>apply</tt> will attempt to do, or
   * <tt>None</tt> if <tt>apply</tt> should not be called on this occassion, in
   * which case <tt>after</tt> will be called immediately. The default
   * impl'n returns <tt>Some(toString)</tt>. */
  def before(): Option[String] = Some(toString)
  /**
   * called after <tt>apply</tt>, in the foreground, or immediately after
   * <tt>before</tt> if <tt>apply</tt> was not called. The default impl'n
   * does nothing. Any <tt>Exception</tt> thrown will be caught, leaving the
   * success or failure of the command unaffected.
   * @param ex <tt>Exception</tt> thrown by <tt>apply</tt> */
  def after(ex: Option[Exception]) {}
}
/**
 * one optionally prompting the user to confirm that execution should proceed. */
trait CheckFirstCmd extends Cmd {
  /**
   * message with which to prompt user.
   * @return user will not be prompted if None */
  def prompt: Option[String]
  /**
   * determines the default action if the user hits return. The default
   * impl'n returns false - execution will proceed if the user hits
   * return. */
  def safe = false
}
/**
 * companion. */
private[cmds] object CheckFirstCmd {
  def proceed(cmd: CheckFirstCmd) = cmd.prompt match {
    case None => true
    case _ => val res = try {
      // part 1/2 of attempt to restore focus after dialogue dismissed
      TheCmdsController.origFocusOwner = TheCmdsController.focusOwner
      //
      val options = List("Yes", "No") 
      Dialog.showOptions(TheCmdsController.rootComponent, // parent
                         cmd.prompt.get, // text
                         "Please confirm...", // title
                         Dialog.Options.YesNo, // the options available
                         Dialog.Message.Question, // what the above text represents
                         Swing.EmptyIcon, // icon
                         options, // text for each option
			 if (cmd.safe) 1 else 0) // option selected initially
    }
    finally {
      // part 2/2 of attempt to restore focus after dialogue dismissed
      TheCmdsController.origFocusOwner match {
        case None =>
        case Some(x) =>
          if (!x.requestFocusInWindow()) x.requestFocus()
        // tidy up if not going to re-use
        if (!cmd.isInstanceOf[PwdProtectedCmd])
          TheCmdsController.origFocusOwner = None
      }
    }
    import Dialog.Result._
    res match {
      case Yes => true
      case No | Closed => false
    }
  }
}
/**
 * one optionally requiring a password. */
trait PwdProtectedCmd extends Cmd {
  private[this] var _pwdCorrect = false
  /**
   * message displayed if the password supplied was incorrect. */
  lazy val sorry = "Sorry - password incorrect."
  /**
   * whether or not one is required this time. The default impl'n returns
   * <tt>true</tt> */
  def pwdRequired = true
  private def setPwd(pwd: String) = _pwdCorrect = pwdCorrect(pwd)
  /**
   * called after the password has been entered. */
  def pwdCorrect(pwd: String): Boolean
}
/**
 * companion. */
private[cmds] object PwdProtectedCmd {
  def proceed(cmd: PwdProtectedCmd) = if (cmd.pwdRequired) {
    val textf = new PasswordField
    textf.columns = 10
    textf.echoChar = '*'
    val optionPane = new JOptionPane(textf.peer,
			             JOptionPane.QUESTION_MESSAGE,
			             JOptionPane.OK_CANCEL_OPTION)
    textf.reactions += {
      case EditDone(`textf`) =>
	optionPane.setValue(new java.lang.Integer(JOptionPane.OK_OPTION))
    }
    // part 1/2 of attempt to restore focus after dialogue dismissed
    if (!cmd.isInstanceOf[CheckFirstCmd])
      TheCmdsController.origFocusOwner = TheCmdsController.focusOwner;
    {
      val dialog = optionPane.createDialog(TheCmdsController.rootComponent.peer,
					   "Please enter password...")
      //println("dialog.addComponentListener()...")
      dialog.addComponentListener(new ComponentAdapter() {
	override def componentShown(ev: ComponentEvent) {
	  //println("dialog.requestFocus()...")
	  dialog.requestFocus()
	} 
      })
      //println("dialog.addFocusListener()...")
      dialog.addFocusListener(new FocusAdapter() {
	val fm = KeyboardFocusManager.getCurrentKeyboardFocusManager
	override def focusGained(ev: FocusEvent) {
	  //println("focusNextComponent()...")
	  fm.focusNextComponent()
	}
      })
      dialog.setVisible(true)
    }
    // part 2/2 of attempt to restore focus after dialogue dismissed
    TheCmdsController.origFocusOwner match {
      case None =>
        case Some(x) =>
          if (!x.requestFocusInWindow()) x.requestFocus
      // tidy up
      TheCmdsController.origFocusOwner = None
    }
    val okSelected = {
      val selectedValue = optionPane.getValue
      (selectedValue != null) &&
      (selectedValue.isInstanceOf[java.lang.Integer]) &&
      (selectedValue.asInstanceOf[java.lang.Integer].intValue == JOptionPane.OK_OPTION)
    }
    if (okSelected) {
      val text = new String(textf.password)
      if (text.length > 0) {
        if (cmd.pwdCorrect(text)) true else throw new RuntimeException(cmd.sorry)
      } else false
    }
    else false
  }
  else true
}
/**
 * one that will be notified of the GUI <tt>scala.swing.event.Event</tt> that
 * triggered the execution. */
trait EventDependentCmd extends Cmd {
  /**
   * called if execution was triggered via the GUI, before <tt>apply</tt>, and
   * before any other methods of any other <tt>Cmd</tt> sub-traits. */
  def setEvent(ev: Event)
}
