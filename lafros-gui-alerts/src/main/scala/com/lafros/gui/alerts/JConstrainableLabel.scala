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
import java.awt.Component
import javax.swing.{JLabel, SwingUtilities}
/**
 * doesn't necessarily <tt>revalidate()</tt> itself every time the <tt>text</tt>
 * property is set - instead, the width may be fixed, by setting the
 * <tt>templateText</tt> property. This is desirable where there might be many such
 * fields, each being frequently updated, as in a real-time monitoring application.
 *
 * @author Rob Dickens */
class JConstrainableLabel extends JLabel {
  private var _templateText = ""
  private var _revalidateNext = false

  setAlignmentX(Component.CENTER_ALIGNMENT)
  /**
   * creates an instance whose <tt>templateText</tt> property will be set to
   * <tt>templateText0</tt>. */
  def this(templateText0: String) {
    this()
    templateText = templateText0
  }
  /**
   * forwards to the superclass only if <tt>templateText</tt> has changed since
   * last time, or is empty. */
  override def revalidate() =
    if (_revalidateNext) {
      super.revalidate()
      //println("super.revalidate() called")
      _revalidateNext = false
    }
    else _templateText match {
      case "" => super.revalidate()
      //println("super.revalidate() called")
      case _ =>
      //println("super.revalidate() NOT called")
    }
  /**
   * unless empty (the default value), constrains the width of the label to that
   * required to display this text, no matter what value is assigned to the
   * <tt>text</tt> property. */
  def templateText = _templateText
  /**
   * sets this property. */
  def templateText_=(s: String) {
    val savedText = getText
    if (s == savedText) setText(s +" ") // otherwise setText(s) would do nothing
    setPreferredSize(null) // allow new preferred size to be calculated
    _revalidateNext = true // tells next revalidate() to call super.revalidate()
    setText(s)
    if (s != "") setPreferredSize(getPreferredSize)
    _templateText = s
    setText(savedText)
  }
}
