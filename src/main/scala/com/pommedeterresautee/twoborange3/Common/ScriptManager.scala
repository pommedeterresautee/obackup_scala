package com.pommedeterresautee.twoborange3.Common

import scala.io.Source
import java.io.{FileOutputStream, File}
import java.net.URL

object ScriptManager {

  private val urlVersion = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/version")

  private val lastScript = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/onandroid")

  def getLastVersion : String = {
    val io = Source.fromURL(urlVersion)
    val result = io.getLines().mkString
    io.close()
    result
  }

  def saveLastScript(fileToSave: File) {
    val is = lastScript.openStream()
    val fos = new FileOutputStream(fileToSave)
    val dataToWrite = new Array[Byte](1024)
    Iterator
      .continually(is.read(dataToWrite))
      .takeWhile(_ != -1)
      .foreach(read => fos.write(dataToWrite, 0, read))
    fos.close()
    is.close()
  }

//  def getScriptPath: File = {
//
//  }
//
//  def isScriptInstalled() = ???
//
//  def getCurrentScriptVersion(): String = ???

}
