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

import scala.swing.{event, Publisher}
/**
 * toggle, whose state changes only after execution of the associated
 * <tt>Cmd</tt> succeeds (i.e. no exception was thrown), whereupon a
 * <tt>Tog.Event</tt> is published.
 * @author Rob Dickens */
class Tog(initialState: Boolean) extends Publisher {
  private var _isSet = initialState
  private lazy val togEvent = new Tog.Event(this)
  /**
   * creates new instance whose initial state is unset. */
  def this() = this(false)
  /**
   * the toggle's state. */
  def isSet = _isSet

  private[cmds] def toggle() {
    _isSet = !_isSet
    publish(togEvent)
  }
}
/**
 * companion. */
object Tog {
  /**
   * command representing a toggle. */
  trait Cmd extends cmds.Cmd with HasTog
  /**
   * published by <tt>Tog</tt>s, upon changing state. */
  case class Event(src: Tog) extends event.Event
  /**
   * <tt>Exer</tt> representing a toggle. Create instances using <tt>Exer(setCmd,
   * unsetCmd)</tt> or <tt>Exer(togCmd)</tt>. */
  trait Exer extends cmds.Exer with HasTog
}
/**
 * for mixing into anything representing a toggle. */
trait HasTog {
  val tog: Tog
}
