package com.pommedeterresautee.twoborange3.Common

import scala.io.Source
import java.io.{FileOutputStream, File}
import java.net.URL
import scala.concurrent._
import ExecutionContext.Implicits.global





import rx.lang.scala.{Observer, Observable, Subscription}
import rx.lang.scala.subscriptions
import rx.lang.scala.subscriptions.Subscription


object ScriptManager {

  private val urlVersion = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/version")

  private val lastScript = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/onandroid")

  def getLastVersion = Observable {
    (observer: Observer[String]) => {
      val io = Source.fromURL(urlVersion)
      val result = io.getLines().mkString
      io.close()
      observer.onNext(result)
      observer.onCompleted()
      Subscription()
    }
  }

  def saveLastScript = Observable{
    (observer: Observer[File]) => {
    val scriptFile = FileManager.getOnAndroidScript
    val is = lastScript.openStream()
    val fos = new FileOutputStream(scriptFile)
    val dataToWrite = new Array[Byte](1024)
    Iterator
      .continually(is.read(dataToWrite))
      .takeWhile(_ != -1)
      .foreach(read => fos.write(dataToWrite, 0, read))
    fos.close()
    is.close()
    require(scriptFile.exists(), "Download of the script failed.")
    scriptFile.setExecutable(true)
    scriptFile.setReadable(true)
    scriptFile.setWritable(true)
      observer.onNext(scriptFile)
      observer.onCompleted()
      Subscription()
    }
  }

  def isScriptInstalled = {
    val f = FileManager.getOnAndroidScript
      f.exists() && f.canExecute && f.length() > 0
  }

  def getCurrentScriptVersion = Observable{
    import scala.sys.process._
    (observer: Observer[String]) => {
      if(isScriptInstalled){
        val result = (FileManager.getOnAndroidScript.getAbsolutePath + " --version").!!.split("\n").take(1).mkString
        observer.onNext(result)
      } else observer.onNext("")
      observer.onCompleted()
      Subscription()
    }
  }

}
