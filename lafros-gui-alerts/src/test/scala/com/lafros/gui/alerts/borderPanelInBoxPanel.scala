package com.lafros.gui.alerts
import scala.swing.{Alignment, BorderPanel, BoxPanel, Label, Orientation, Separator}
import BorderPanel.Position._

object borderPanelInBoxPanel extends BoxPanel(Orientation.Vertical) {
  val lab1 = new Label("short") {
    //horizontalAlignment = Alignment.Right
    //xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
    //xLayoutAlignment = java.awt.Component.LEFT_ALIGNMENT
    println("xLayoutAlignment: "+ xLayoutAlignment)
  }
  contents += lab1
  contents += new BorderPanel {
//     layout(new BorderPanel {
//       layout(new Label("North North")) = North
//       layout(new Label("North West")) = West
//       layout(new Label("North Center")) = Center
//       layout(new Label("North East")) = East
//       layout(new Label("North South")) = South
//     }) = North
    layout(new Label("North")) = North
    layout(new Label("West")) = West
    layout(new Label("Center")) = Center
    layout(new Label("East")) = East
    layout(new Label("South")) = South
    //xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
    xLayoutAlignment = java.awt.Component.LEFT_ALIGNMENT
    println("xLayoutAlignment: "+ xLayoutAlignment)
  }
  val lab2 = new Label("this one is very long indeed") {
    //xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
    //xLayoutAlignment = java.awt.Component.LEFT_ALIGNMENT
    println("xLayoutAlignment: "+ xLayoutAlignment)
  }
  contents += lab2
  //println("lab2: "+ lab2)
  border = new javax.swing.border.LineBorder(java.awt.Color.black)
}
