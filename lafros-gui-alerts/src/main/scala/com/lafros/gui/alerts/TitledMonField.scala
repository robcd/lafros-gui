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
import scala.swing.{Alignment, Label, Swing}
import javax.swing.border.TitledBorder
import java.awt.Color
/**
 * <tt>MonField</tt> variant having title.
 * @param tb0 the <tt>javax.swing.border.TitledBorder</tt> to use
 * @author Rob Dickens */
class TitledMonField(tb0: TitledBorder) extends MonField {
  override lazy val peer: JTitledConstrainableLabel =
    new JTitledConstrainableLabel(tb0) with SuperMixin
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
   * as displayed above the field. */
  def title = peer.title
  /**
   * sets this property. */
  def title_=(s: String) = peer.title = s
}
