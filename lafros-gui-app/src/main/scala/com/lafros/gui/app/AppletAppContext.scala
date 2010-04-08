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
// import java.util.List;
// import java.util.LinkedList;
import javax.swing.JApplet
import scala.swing.MenuBar

//import com.lafros.juice.cmds.*;
/**
 * app context in the case of an applet.
 *
 * @author Rob Dickens */
private[app] class AppletAppContext(
  private val applet: Applet,
  val args: Array[String]) extends BaseAppContext(applet.rootPanel) {
  private var _title = ""
  private var _menuBar: MenuBar = _
  def title = _title
  def title_=(arg: String) = _title = arg match {
    case null => ""
    case _ => arg
  }
  def menuBar = _menuBar
  def menuBar_=(arg: MenuBar) {
    _menuBar = arg
    applet.setJMenuBarPrivately(arg.peer) // which won't call us back!
  }
  def isApplet = true
  def confirmQuitReason = None
  def confirmQuitReason_=(arg: Option[String]) {}
}
