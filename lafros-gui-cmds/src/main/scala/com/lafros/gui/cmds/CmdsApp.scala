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
/**
 * determines how the app provides user feedback when commands are executed.
 * An impl'n may be passed to <tt>TheCmdsController</tt>.
 * @author Rob Dickens */
abstract class CmdsApp {
  /**
   * called just before and after calling a <tt>Cmd</tt> method. */
  def setBusy(b: Boolean)
  /**
   * instruction to clear the user feedback message line. */
  def clear()
  /**
   * called before <tt>Cmd</tt> execution, to display <tt>msg</tt>. */
  def trying(msg: String)
  /**
   * called if execution succeeded. */
  def succeeded()
  /**
   * called if execution succeeded, to display <tt>msg</tt>. */
  def succeeded(msg: String)
  /**
   * called if execution failed. */
  def failed(msg: String, cause: Exception)
}
