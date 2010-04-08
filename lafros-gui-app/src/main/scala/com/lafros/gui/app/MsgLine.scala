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
import scala.swing.{Alignment, BorderPanel, Label, ScrollPane}
// import java.awt.*;
// import java.net.*;
import java.awt.BorderLayout
import javax.swing.{ImageIcon, JPanel, ScrollPaneConstants}
import BorderPanel.Position._
/**
 * for displaying feedback messages to the user, on a single line complete with
 * fixed horizontal scrollbar.
 *
 * @author Rob Dickens */
class MsgLine extends BorderPanel {
//   override lazy val peer = new javax.swing.JPanel(new BorderLayout) with SuperMixin {
//     override def getPreferredSize = {
//       val dim = super.getPreferredSize
//       dim.width = 0
//       dim
//     }
//   }
  private val msgLabel = new Label(" ") {
    xAlignment = Alignment.Left
  }
  private lazy val mutedIconLabel = new Label {
    icon = MsgLine.mutedIcon
  }
  private var _muted = false

  msgLabel.opaque = true
  ;{
    val scrollPane = new ScrollPane(msgLabel)
    scrollPane.peer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER)
    scrollPane.peer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS)
    layout(scrollPane) = Center
  }
  /**
   * displays <tt>_msg</tt>. <tt>null</tt> will be displayed as "null", and "" as " ". */  
  def displayMsg(_msg: String) {
    val msg = if (_msg == null) "null"
              else if (_msg.length == 0) " "
              else _msg
    msgLabel.text = msg
  }
  /**
   * clears any message currently displayed. */  
  def clear() = msgLabel.text = " "
  /**
   * displays a suitable icon when <tt>true</tt>. */
  def muted = _muted
  /**
   * sets this property. */
  def muted_=(arg: Boolean) = if (_muted != arg) {
    _muted = arg
    if (_muted) layout(mutedIconLabel) = West // show icon
    else layout -= mutedIconLabel // hide icon
    revalidate()
  }
}

private[app] object MsgLine {
  lazy val mutedIcon = {
    val url = getClass.getResource("muted.png")
    //if (url == null) throw new RuntimeException(BaseAppContext.FAILED_MSG)
    new ImageIcon(url)
  }
}
