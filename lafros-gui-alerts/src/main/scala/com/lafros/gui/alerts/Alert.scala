/**
 * Copyright 2009 Latterfrosken Software Development Limited
 *
 * This file is part of Lafros GUI-Alerts.
 *
 * Lafros GUI-Alerts is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * Lafros GUI-Alerts is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with Lafros GUI-Alerts. If not, see <http://www.gnu.org/licenses/>. */
package com.lafros.gui.alerts
/**
 * enumerates the possible values of a <tt>MonField</tt>'s <tt>alert</tt> property.
 * @author Rob Dickens */
object Alert extends Enumeration {
  /**
   * alias for <tt>Value</tt>. */
  type Alert = Value
  /**
   * no alert. */
  val NoAlert = Value("none")
  /**
   * non-intrusive alert. */
  val NonIntrusive = Value("non-intrusive")
  /**
   * intrusive alert. */
  val Intrusive = Value("intrusive")
  /**
   * acknowledged intrusive alert - arrived at when the user clicks on the
   * <tt>MonField</tt> in question. */
  val Acknowledged = Value("acknowledged")
}
