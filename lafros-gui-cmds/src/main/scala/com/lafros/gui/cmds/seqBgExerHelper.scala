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
import java.awt.Toolkit
import java.lang.reflect.InvocationTargetException
import javax.swing.SwingUtilities
import scala.actors.Actor
import scala.swing.event.Event
import Actor._
/**
 * @author Rob Dickens */
private[cmds] object seqBgExerHelper {
  val controllerTimeout_ms = 2000
  private val controller: Actor = actor {
    val worker = new Actor { // i.e. reusable after act() returns
      def act() = react {
        case cmd: Cmd => sender ! (try {
          cmd()
        }
        catch {
          case ex: Exception => ex
        })
      }
    }
    var currentExer: Option[SeqBgExer] = None
    var resetPending = false
    def wrap(f: => Unit) {
      if (resetPending) resetPending = false
      else TheCmdsController.invokeAndWait {
        f
      }
      currentExer = None
    }
    def after(ex: Option[Exception]) {
      TheCmdsController.setBusy(true)
      try {
        currentExer.get.after(ex)
      }
      catch {
        case ex: Exception => printStackTrace(ex)
      }
      TheCmdsController.setBusy(false)
    }
    loop {
      react { // blocks until match received
        case newExer: SeqBgExer => // have worker call its executeCmd in background
          worker.start()
        worker ! newExer.cmd
        currentExer = Some(newExer)
        case ex: Exception => // as thrown by executeCmd
          wrap {
            TheCmdsController.failed(ex, true)
            after(Some(ex))
          }
        case _msg: Option[_] => // as returned by executeCmd
          val msg = _msg.asInstanceOf[Option[String]]
        wrap {
          TheCmdsController.succeeded(msg, true)
          after(None)
        }
        currentExer = None
        case "reset" => if (currentExer != None) resetPending = true
        case _ => // can a new cmd be executed?
          //println("controller: sender ! "+ (currentExer == None))
          sender ! (currentExer == None)
      }
    }
  }
  /**
   * */
  def executeCmd(exer: SeqBgExer, ev: Option[Event]) {
    controller !? (controllerTimeout_ms, self) match {
      case Some(true) =>
        //println("helper: controller says go ahead")
        exer.before(ev) match {
          case Some(true) => // call execute()
            //println("exer.before returned true (do call execute)")
            controller ! exer
          case Some(false) => // don't call execute()
            //println("exer.before returned false (don't call execute)")
            try {
              exer.after(None)
            }
          catch {
            case ex: Exception => printStackTrace(ex)
          }
          case None => // execution cancelled
            //println("exer.before returned None (execution cancelled)")
        }
      case Some(false) =>
        Toolkit.getDefaultToolkit.beep()
      case None => throw new RuntimeException("Error: actor not responding")
    }
  }

  def reset() {
    controller ! "reset"
  }

  private def printStackTrace(ex: Exception) {
    ex.printStackTrace()
    println("[caught by "+ getClass.getPackage.getName +"]")
  }
}
