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

// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.*;

// import com.lafros.juice.cmds.*;
import scala.swing.{Frame, MenuBar}
import scala.swing.event.WindowClosing
/**
 * app context in the case of an application.
 *
 * @author Rob Dickens */
private[app] class ApplicationAppContext(
  private val frame: Frame,
  val args: Array[String],
  private val quitCmd: QuitCmd) extends BaseAppContext(frame) {

  // AppContext impl'n...
  def title = frame.title
  def title_=(arg: String) = frame.title = arg
  def menuBar = frame.menuBar
  def menuBar_=(arg: MenuBar) = frame.menuBar = arg
  def isApplet = false
  def confirmQuitReason = quitCmd.confirmReason
  def confirmQuitReason_=(arg: Option[String]) = quitCmd.confirmReason = arg
  // ...AppContext impl'n
}
