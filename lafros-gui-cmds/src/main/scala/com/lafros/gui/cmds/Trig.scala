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

import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import javax.swing.{AbstractButton => JAbstractButton, JToggleButton}
import scala.collection.mutable.{Buffer, ListBuffer}
import scala.swing.{AbstractButton, Action, event, Publisher, Reactions}
import event.{ActionEvent, EditDone, Event}
/**
 * <tt>AbstractButton</tt> mix-in, for use where triggering <tt>Cmd</tt> execution.
 *
 * @author Rob Dickens */
trait Trig extends AbstractButton {
  private var ev: Event = _
  private var _cmdReaction: Cmd.Reaction = NoCmd.Reaction
  private var _exer: Exer = NoExer
  private var _exerToText: Trig.ExerToString = (_.cmd.toString)
  private var _text0: String = ""
  private var _text1: String = ""
  private val selectWhenTogSet = peer.getModel match {
    case _: DeferredSelectionModel => true // pre-installed - an action was passed to
    // the constructor
    case _: JToggleButton.ToggleButtonModel =>
      peer.setModel(new DeferredSelectionModel); true
    case _ => false
  }
  private lazy val togReaction: Reactions.Reaction = {
    case Tog.Event(tog) => indicateTogState(tog.isSet); applyExerToText()
  }
  private lazy val propsReaction: Reactions.Reaction = {
    case Trig.Title0Changed(props) => text0 = props.title0
    case Trig.Title1Changed(props) => text1 = props.title1
  }

  reactions += new Reactions.Reaction {
    def apply(ev: Event) {
      Trig.this.ev = ev
      exer.executeCmd(Some(ev))
    }
    def isDefinedAt(ev: Event) = cmdReaction.isDefinedAt(ev)
  }
  action match {
    case props: Trig.Props => processProps(props)
    case _ =>
  }
  /**
   * sets this property, and, except for an <tt>arg</tt> of type
   * <tt>Trig.Props</tt>, displaces any <tt>cmd</tt>. */
  override def action_=(arg: Action) = if (arg != action) {
    if (instanceInitialised) {
      action match {
        case props: Trig.Props => reactions -= propsReaction; deafTo(props)
        case _ =>
      }
      arg match {
        case Action.NoAction =>
        case props: Trig.Props => processProps(props)
        case _ => cmdReaction = NoCmd.Reaction
      }
    } 
    else peer.getModel match {
      case _: JToggleButton.ToggleButtonModel =>
        peer.setModel(new DeferredSelectionModel)
      case _ =>
    }
    super.action_=(arg)
  }
  /**
   * sets this property. */
  override def selected_=(b: Boolean) = if (selected != b) {
    peer.getModel match {
      case model: DeferredSelectionModel => model.selected = b
      case _ => super.selected = b
    }
  }
  /**
   * similar to a <tt>Reactions.Reaction</tt>, but returns an optional feedback
   * message, and any <tt>Exception</tt> thrown will be caught. The default value
   * is <tt>NoCmd.Reaction</tt>.
   * @see Cmd.Reaction
   * @see Reactions.Reaction */
  def cmdReaction = _cmdReaction
  /**
   * sets this property, and creates a corr'ding <tt>Cmd</tt> which is assigned to
   * <tt>cmd</tt>. */
  def cmdReaction_=(arg: Cmd.Reaction) {
    if (arg eq NoCmd.Reaction) cmd = NoCmd
    if (arg != cmdReaction) {
      if (arg ne NoCmd.Reaction) cmd = Cmd("") {
        arg.apply(ev)
      }
      _cmdReaction = arg
    }
  } 
  /**
   * the <tt>Cmd</tt> whose execution is to be triggered. The default value is
   * <tt>NoCmd</tt>. */
  def cmd = _exer.cmd
  /**
   * sets this property, creates a corr'ding <tt>Exer</tt> which is assigned to
   * <tt>exer</tt>, and displaces any <tt>action</tt> (except where this is of type
   * <tt>Trig.Props</tt>). */
  def cmd_=(arg: Cmd) = if (arg != cmd)
    exer = if (arg eq NoCmd) NoExer
           else arg match {
             case togCmd: Tog.Cmd => Exer(togCmd)
             case _ => Exer(arg)
           }
  /**
   * the <tt>Exer</tt> to be triggered. */
  def exer = _exer
  /**
   * sets this property, and assigns <tt>arg.cmd</tt> to <tt>cmd</tt>. */
  def exer_=(arg: Exer) = if (arg != exer) {
    if (arg eq NoExer) exer match {
      case togExer: Tog.Exer => deafTo(togExer.tog); reactions -= togReaction
      case _ =>
    }
    else {
      // remove any existing cmdReaction or Action
      if (cmdReaction ne NoCmd.Reaction) _cmdReaction = NoCmd.Reaction
      else action match {
        case Action.NoAction =>
        case _: Trig.Props =>
        case _ => action = Action.NoAction
      }
      //
      arg match {
        case togExer: Tog.Exer => listenTo(togExer.tog)
        reactions += togReaction
        indicateTogState(togExer.tog.isSet)
        case _ =>
      }
    }
    _exer = arg
    applyExerToText()
  }
  /**
   * assigned to <tt>text</tt> when <tt>exer</tt> is an unset <tt>Tog.Exer</tt> and
   * the <tt>AbstractButton</tt> does not indicate its selected state graphically. */
  def text0 = _text0
  /**
   * sets this property. */
  def text0_=(arg: String) = if (arg != _text0) {
    _text0 = arg
    applyTextN(false)
  }
  /**
   * assigned to <tt>text</tt> when <tt>exer</tt> is a <tt>Tog.Exer</tt> that is
   * set, and the <tt>AbstractButton</tt> does not indicate its selected state
   * graphically. */
  def text1 = _text1
  /**
   * sets this property. */
  def text1_=(arg: String) = if (arg != _text1) {
    _text1 = arg
    applyTextN(true)
  }
  /**
   * function of type, <tt>Exer => String</tt>, to be applied, and its result
   * assigned to <tt>text</tt> subject to a certain condition, whenever a new value
   * is assigned to <tt>exer</tt> or <tt>exerToText</tt> itself, or a <tt>Tog.Exer</tt>
   * <tt>exer</tt> is toggled. The condition is that <tt>text == ""</tt> except
   * when <tt>exer</tt> is a <tt>Tog.Exer</tt>, in which case,
   * <ul>
   *   <li> if the <tt>AbstractButton</tt> indicates its selected state
   * graphically, the condition that <tt>cmd</tt> be a <tt>Tog.Cmd</tt> must also
   * be met;
   *   <li> if the <tt>AbstractButton</tt> does not indicate its selected state
   * graphically, the condition is that <tt>text0 == ""</tt> (when set) or <tt>text1 == ""</tt>
   * (when unset).
   * </ul>
   * The default impl'n returns the result of <tt>exer.cmd.toString</tt>.
   * */
  def exerToText = _exerToText
  /**
   * sets this property. */
  def exerToText_=(arg: Trig.ExerToString) = if (arg != _exerToText) {
    _exerToText = arg
    applyExerToText()
  }

  private def instanceInitialised = exer ne null

  private def indicateTogState(isSet: Boolean) =
    if (selectWhenTogSet) selected = isSet
    else text = if (isSet) text1 else text0

  private def applyTextN(isSet: Boolean) = exer match {
    case togExer: Tog.Exer =>
      if (togExer.tog.isSet == isSet && !selectWhenTogSet)
        text = if (isSet) text1 else text0
    case _ =>
  }

  private def applyExerToText() = if ({
    def isEmpty(s: String) = s.length == 0
    if (selectWhenTogSet) exer match {
      case togExer: Tog.Exer => cmd match {
        case togCmd: Tog.Cmd => isEmpty(text)
        case _ => false
      }
      case _ => isEmpty(text) // graphical tog + non-Tog.Exer: tog state will
      // never change, so treat as non-graphical tog
    } else exer match {
      case togExer: Tog.Exer =>
        if (togExer.tog.isSet) isEmpty(text1) else isEmpty(text0)
      case _ => isEmpty(text)
    }
  }) text = exerToText(exer)

  private def processProps(props: Trig.Props) {
    reactions += propsReaction
    listenTo(props)
    text0 = props.title0
    text1 = props.title1
  } 
}
/**
 * companion. */
object Trig {
  /**
   * alias for <tt>Exer => String</tt> */
  type ExerToString = Exer => String
  /**
   * alias for <tt>Action.NoAction.type</tt>. */
  type NoProps = Action.NoAction.type
  /**
   * trigger properties - an <tt>Action</tt> which does nothing but supply
   * properties for an <tt>Exer</tt>. This separation allows the <tt>Exer</tt> to
   * be used without instantiating the <tt>Trig.Props</tt>, or else instantiation
   * of a non-<tt>Tog.Exer</tt> <tt>Exer</tt> to be deferred until command
   * execution is actually triggered. */
  class Props extends Action("") with Publisher {
    private var _title0 = ""
    private var _title1 = ""
    //private[Trig] publisher: new Publisher
//  private var _exerToTitle: ExerToString = (_.cmd.toString)
    /**
     * does nothing! */
    def apply() {} // do nothing!
//     import Props._
    /**
     * sets the <tt>Trig</tt>'s <tt>text0</tt> property. */
    def title0 = _title0
    /**
     * sets this property. */
    def title0_=(arg: String) = if (arg != _title0) {
      _title0 = arg
      publish(Title0Changed(this))
    }
    /**
     * sets the <tt>Trig</tt>'s <tt>text1</tt> property. */
    def title1 = _title1
    /**
     * sets this property. */
    def title1_=(arg: String) = if (arg != _title1) {
      _title1 = arg
      publish(Title1Changed(this))
    }
//  def exerToTitle = _exerToTitle
//  def exerToTitle_=(arg: ExerToString) {
//    require(arg != null)
//    _exerToTitle = arg
//  }
  }

  private case class Title0Changed(src: Props) extends Event
  private case class Title1Changed(src: Props) extends Event
}
/**
 * for use with buttons which are also toggles (namely <tt>ToggleButton</tt>,
 * <tt>CheckBox</tt> and <tt>CheckMenuItem</tt>) and which are not to toggle when
 * clicked on, but rather only when explicitly requested to do so (by a
 * <tt>TogEvent</tt>). */
private class DeferredSelectionModel extends JToggleButton.ToggleButtonModel {
  /**
   * does nothing - use <tt>selected_=</tt> instead. */
  override def setSelected(b: Boolean) = {}
  /**
   * forwards to <tt>isSelected</tt>. */
  def selected = isSelected
  /**
   * sets this property. */
  def selected_=(b: Boolean) = super.setSelected(b)
}
