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

import com.lafros.gui.cmds.{Exer, TheCmdsController, Tog, Trig}
import scala.swing.{AbstractButton, Action, Component, Container, MenuBar, RootPanel}
/**
 * app context, as passed to the app's <tt>init</tt> method.
 *
 * @author Rob Dickens */
trait AppContext {
  /**
   * corresponds to that of <tt>scala.swing.Frame</tt> in applications. */
  def title: String
  /**
   * sets this property. */
  def title_=(arg: String)
  /**
   * corresponds to those passed to the applications's <tt>main</tt> method, or to
   * those named <tt>arg0</tt>, <tt>arg1</tt>, etc. in the case of an applet. */
  def args: Array[String]
  /**
   * default value is <tt>null</tt>. */
  def menuBar: MenuBar
  /**
   * sets this property. */
  def menuBar_=(arg: MenuBar)
  /**
   * sole element of the <tt>javax.swing.RootPaneContainer</tt>'s content pane. */
  def rootPanel: RootPanel
  /**
   * corresponds to the <tt>contents</tt> of <tt>rootPanel</tt>, whose default
   * value is <tt>null</tt>. */
  def mainComponent: Component
  /**
   * sets this property. */
  def mainComponent_=(arg: Component)
  /**
   * should be set if the user is to be prompted before exiting the application. */
  def confirmQuitReason: Option[String]
  /**
   * sets this property. */
  def confirmQuitReason_=(arg: Option[String])
  /**
   * indicates whether the app is running as an application or applet. */
  def isApplet: Boolean
  /**
   * for toggling the state of <tt>com.lafros.gui.alerts.TheAlertsController</tt>'s
   * <tt>muted</tt> property (provided the Alerts library is available). */
  def muteExer: Tog.Exer
  /**
   * for <tt>muteExer</tt>. */
  def muteTrigProps: Trig.Props
  /**
   * for invoking <tt>com.lafros.gui.alerts.TheAlertsController</tt>'s
   * <tt>beep</tt> method (provided the Alerts library is available). */
  def testBeepExer: Exer
  /**
   * invokes <tt>testBeepExer</tt>. */
  def testBeepAction: Action
  /**
   * for toggling the state of <tt>com.lafros.gui.alerts.TheAlertsController</tt>'s
   * <tt>useSystemBeep</tt> property (provided the Alerts library is available). */
  def useSystemBeepExer: Tog.Exer
  /**
   * for <tt>useSystemBeepExer</tt>. */
  def useSystemBeepTrigProps: Trig.Props
  /**
   * for enabling audible feedback if execution of any
   * <tt>com.lafros.gui.cmds.Cmd</tt> throws an exception. */
  def enableSoundEffectsExer: Tog.Exer
  /**
   * for <tt>enableSoundEffectsExer</tt>. */
  def enableSoundEffectsTrigProps: Trig.Props
  /**
   * for receiving messages from any <tt>com.lafros.gui.cmds.Cmd</tt>s the app is
   * using. The default value is <tt>null</tt>. */
  def msgLine: MsgLine
  /**
   * sets this property. */
  def msgLine_=(arg: MsgLine)
  /**
   * the app's Cmds controller. */
  def cmdsController: TheCmdsController
  /**
   * key to the app's Alerts controller, or <tt>None</tt> if the Alerts library is
   * not available. */
  def alertsControllerKey: Option[AnyRef]
}
