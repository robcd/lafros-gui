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
import java.awt.{Color, Component, Container, Dimension}
import javax.swing.{Icon, JLabel}
import javax.swing.border.TitledBorder
import scala.swing.{Alignment, Label, Swing}
/**
 * titled label. Both the title and text will be centred, and the preferred
 * width set to that of the wider of the two.
 *
 * @param _border the <tt>javax.swing.border.TitledBorder</tt> to be used
 * @author Rob Dickens */
class JTitledConstrainableLabel(
  private val _border: TitledBorder) extends JConstrainableLabel {

  setHorizontalAlignment(Alignment.Center.id)
  _border.setTitleJustification(TitledBorder.CENTER)
  setBorder(_border)
  /**
   * also initialises the <tt>templateText</tt> property to <tt>templateText0</tt>. */
  def this(tb: TitledBorder, templateText0: String) {
    this(tb)
    templateText = templateText0
  }
  /**
   * a black <tt>javax.swing.border.LineBorder</tt> will be added, and the
   * <tt>title</tt> set to <tt>title0</tt>. */
  def this(title0: String) =
    this(Swing.TitledBorder(Swing.LineBorder(Color.black), title0))
  /**
   * calls <tt>this(title0)</tt>, and sets the <tt>templateText</tt> property to
   * <tt>templateText0</tt>. */
  def this(title0: String, templateText0: String) {
    this(title0)
    templateText = templateText0
  }
  /**
   * takes into account the <tt>title</tt> as well as the <tt>templateText</tt>. */
  override def getPreferredSize = {
    val d1 = super.getPreferredSize
    val d2 = _border.getMinimumSize(this)
    val w = Math.max(d1.width, d2.width)
    d1.setSize(w, d1.height)
    //println("preferred size: "+ d1.width +", "+ d1.height)
    d1
  }
  /**
   * forwards to getPreferredSize (which appears to be a requirement if the
   * container is a <tt>BoxPanel</tt>). */
  override def getMaximumSize = getPreferredSize
  /**
   * as displayed above the label. */
  def title = _border.getTitle
  /**
   * sets this property. */
  def title_=(arg: String) = if (arg != _border.getTitle) {
    // preferred size now depends on title as well as label itself;
    // if template in use, should apply that before recalculating preferred size;
    _border.setTitle(arg)
    templateText = templateText
  }
}
