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
import java.applet.Applet
import java.awt.Toolkit
import java.net.URL
/**
 * superclass of the various types of alert sound.
 * @author Rob Dickens */
sealed abstract class AlertSound 
/**
 * the current default alert sound. */
case object DefaultAlertSound extends AlertSound {
  override def toString = "default: "+ intruders.defaultAlertSound.toString
}
/**
 * short alert sound, as opposed to a <tt>Loop</tt>. */
abstract class Beep extends AlertSound {
  /**
   * produces the alert sound. */
  def beep()
}
/**
 * continuous alert sound, as opposed to a <tt>Beep</tt>.
 * @param url location of the sound file. */
case class Loop(
  val url: URL) extends AlertSound with HasAudioUrl {
  /**
   * starts the alert sound. */
  def start() = ac.loop()
  /**
   * stops the alert sound. */
  def stop() = ac.stop()
  override def toString = "loop"
}
/**
 * mixed into alert sounds required to declare the location of the sound file
 * used. */
trait HasAudioUrl {
  /**
   * location of the sound file. */
  def url: URL
  /**
   * the corresponding <tt>java.applet.AudioClip</tt>. */
  val ac = {
    /* throw exception if URL does not point to a valid sound file */ {
      val connection = url.openConnection() // throws IOException
      // the next line is required in order to get intelligent exceptions
      connection.getContent // throws IOException
      // the following is required in case the required file was found, but isn't actually a sound file
      val contentType = connection.getContentType
      if (!contentType.startsWith("audio/"))
        throw new RuntimeException("specified file has incorrect content type: "+ contentType)
    }
    Applet.newAudioClip(url)
  }
}
/**
 * as produced by <tt>java.awt.Toolkit.beep()</tt>. */
case object SystemBeep extends Beep {
  def beep() = Toolkit.getDefaultToolkit.beep()
  override def toString = "system beep"
}
/**
 * <tt>Beep</tt> created using the specified sound file.
 * @param url location of the sound file. */
case class CustomBeep(
  val url: URL) extends Beep with HasAudioUrl {
  def beep() = ac.play()
  override def toString = "custom beep"
}
/**
 * companion. */
object AlertSound {
  /**
   * function of type, <tt>AlertSound => Unit</tt>, as may be passed to
   * <tt>useBeepFor</tt> or <tt>useLoopFor</tt>. */
  type UsesAlertSound = AlertSound => Unit
  /**
   * names of built-in alert sounds, as may be passed to <tt>builtInOneAsBeep</tt>
   * or <tt>builtInOneAsLoop</tt>. */
  lazy val builtInOnes =
    List("Futuristic.wav", "HighPitched.wav", "Thunderbirds.wav",
         "Toot.wav", "UhOh.au")
  /**
   * returns the specified built-in alert sound as a <tt>Beep</tt>. */
  def builtInOneAsBeep(name: String) = {
    val url = classOf[AlertSound].getResource(name)
    beepMap.get(url) match {
      case None => val beep = new CustomBeep(url)
      beepMap += (url -> beep)
      beep
      case Some(x) => x
    }
  }
  /**
   * returns the specified built-in alert sound as a <tt>Loop</tt>. */
  def builtInOneAsLoop(name: String) = {
    val url = classOf[AlertSound].getResource(name)
    loopMap.get(url) match {
      case None => val loop = new Loop(url)
      loopMap += (url -> loop)
      loop
      case Some(x) => x
    }
  }
  private[alerts] def initialDefaultBuiltInOne: AlertSound =
    builtInOneAsBeep(builtInOnes(2))
  import scala.collection.jcl.WeakHashMap
  private lazy val beepMap = new WeakHashMap[URL, Beep]
  private lazy val loopMap = new WeakHashMap[URL, Loop]
  import com.lafros.gui.cmds.SeqBgCmd
  private var url: URL = _
  private var requireBeep: Boolean = _
  private var useAlertSound: UsesAlertSound = _
  private lazy val seqBgCmd = new SeqBgCmd {
    var alertSound: AlertSound = _

    override def before() = {
      val cachedInstance =
        if (requireBeep) beepMap.get(url)
        else loopMap.get(url)
      cachedInstance match {
        case None => Some("fetching sound")
        case Some(x) => alertSound = x
        None
      }
    }
    def apply() = {
      alertSound =
        if (requireBeep) new CustomBeep(url)
        else new Loop(url)
      None
    }
    override def after(ex: Option[Exception]) {
      try {
        if (ex == None) {
          if (requireBeep) beepMap += (url -> alertSound.asInstanceOf[Beep])
          else loopMap += (url -> alertSound.asInstanceOf[Loop])
          useAlertSound(alertSound)
        }
      }
      finally {
        alertSound = null
        useAlertSound = null
        url = null
      }
    }
  }
  import com.lafros.gui.cmds.Exer
  private lazy val _exer = Exer(seqBgCmd)
  /**
   * means of obtaining an alert sound in the background (so as not to detain the
   * Swing dispatch-thread). If not found in the cache, the alert sound will be
   * created in the background, using the sound file specified by <tt>url</tt>,
   * before being passed to <tt>f</tt>. */
  def useBeepFor(url: URL)(f: UsesAlertSound) = usesAlertSoundFor(url, true, f)
  /**
   * as for <tt>useBeepFor</tt>, but substituting <tt>Loop</tt> for <tt>Beep</tt>. */
  def useLoopFor(url: URL)(f: UsesAlertSound) = usesAlertSoundFor(url, false, f)

  private def usesAlertSoundFor(url: URL, asBeep: Boolean, f: UsesAlertSound) {
    this.url = url
    this.requireBeep = asBeep
    this.useAlertSound = f
    _exer.executeCmd()
  }
}
