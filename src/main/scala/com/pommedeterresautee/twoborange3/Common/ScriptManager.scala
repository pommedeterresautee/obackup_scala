package com.pommedeterresautee.twoborange3.Common

import scala.io.Source
import java.io.{FileOutputStream, File}
import java.net.URL


import rx.lang.scala.{Observer, Observable}
import rx.lang.scala.subscriptions.Subscription
import scala.Predef.String

import play.api.libs.json.Json


object ScriptManager {

  case class Device(brandName:String, codeName: String, commercialName: String, var partitionName: Option[Seq[String]] = None) extends Ordered[Device]{

    override def toString = brandName + " (model: " + commercialName + ")\n" + partitionName.mkString(", ")

    def compare(that: Device): Int = this.brandName compareTo that.brandName match {
        case 0 => commercialName.compareTo(that.commercialName)
        case compareBrand => compareBrand
      }
  }

  implicit val jsonDeviceFormat = Json.format[Device]


  private val urlVersion = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/version")

  private val lastScript = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/onandroid")

  private val jsonDevices = new URL("https://raw.github.com/pommedeterresautee/onandroidparser/master/example_result/onandroid.json")

  def getAsyncLayoutTable = getAsyncUrl(jsonDevices).map(i => i).map{s:Option[String] => Json.fromJson[Seq[Device]](Json.parse(s.get)).asOpt}

  def getAsyncLastVersion = getAsyncUrl(urlVersion)

  private def getAsyncUrl(url: URL):Observable[Option[String]] = Observable {
    (observer: Observer[Option[String]]) => {
      val urlCon = url.openConnection()
      urlCon.setConnectTimeout(5000)
      urlCon.setReadTimeout(5000)
      val io = Source.fromInputStream(urlCon.getInputStream)
      val result = Option(io.getLines().mkString.split("\n")(0))
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
//    require(scriptFile.exists(), "Download of the script failed.")
    scriptFile.setExecutable(true)
    scriptFile.setReadable(true)
    scriptFile.setWritable(true)
      observer.onNext(scriptFile)
      observer.onCompleted()
      Subscription()
    }
  }

  def deleteScript() = Observable(FileManager.getOnAndroidScript.delete())

  /**
   * Get the version number of the local script.
   * 
   * @return the version number of the installed script
   */
  def getLocalScriptVersion: Observable[Option[String]] = Observable{
    import scala.sys.process._
    (observer: Observer[Option[String]]) => {
      val scriptFile = FileManager.getOnAndroidScript      
      if(scriptFile.exists() && scriptFile.canExecute && scriptFile.length() > 0){
        val result = (FileManager.getOnAndroidScript.getAbsolutePath + " --version").!!.split("\n")(0)
        observer.onNext(Some(result))
      } else observer.onNext(None)
      observer.onCompleted()
      Subscription()
    }
  }
}
