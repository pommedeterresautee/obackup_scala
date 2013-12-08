package com.pommedeterresautee.twoborange3.Common

import scala.io.Source
import java.io.{FileOutputStream, File}
import java.net.URL


import rx.lang.scala.{Subscription, Observer, Observable}


import scala.Predef.String
import com.stericson.RootTools.RootTools
import scala.collection.immutable._
import scala.Some
import spray.json._


object ScriptManager {

  case class Device(brandName:String, codeName: String, commercialName: String, partitionTable:Map[String, String]) extends Ordered[Device]{
    override def toString = brandName + " (model: " + commercialName + ")\n" + partitionTable.mkString(", ")
    override def compare(that: Device): Int = this.brandName compareTo that.brandName match {
        case 0 => commercialName.compareTo(that.commercialName)
        case brandCompare => brandCompare
      }
  }

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val f = jsonFormat4(Device)
  }

  private val urlVersion = "https://raw.github.com/ameer1234567890/OnlineNandroid/master/version"

  private val lastScript = "https://raw.github.com/ameer1234567890/OnlineNandroid/master/onandroid"

  private val jsonDevices = "https://raw.github.com/pommedeterresautee/onandroidparser/master/example_result/onandroid.json"

  private val partitionLayoutBase = "https://raw.github.com/ameer1234567890/OnlineNandroid/master/part_layouts/raw/partlayout4nandroid."
  
  def getAsyncPartitionLayout(deviceName:String) = getAsyncUrl(partitionLayoutBase + deviceName)

  def getAsyncLayoutTable:Observable[Seq[Device]] =
    getAsyncUrl(jsonDevices).map{
      json:Option[String] =>
      import MyJsonProtocol._
      val des = json.get.asJson
      val result = des.convertTo[Seq[Device]]
      result
    }


  def getAsyncLayoutTableCompatible:Observable[Seq[Device]] ={
    val tableToRequest = List(("cache","/cache"),("system","/system"),("userdata","/data"))
    getAsyncLayoutTable
      .zip(getDevicesPartition)
      .map{
      case (internetL, deviceL) => internetL.filter{
        internetDevice =>
          tableToRequest.map{case (key, path) => deviceL.getOrElse(path, "")}
            .equals(
              tableToRequest.map{case (key, path) => internetDevice.partitionTable.getOrElse(key, "")}
            )}}
  }

  def getAsyncLastVersion = getAsyncUrl(urlVersion)

  private def getAsyncUrl(urlString: String):Observable[Option[String]] = Observable {
    (observer: Observer[Option[String]]) => {
      val url = new URL(urlString)
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
    val is = new URL(lastScript).openStream()
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

  /**
   * Check if a device is a MTD one.
   * MTD devices don't need partition layout file.
   *
   * @return true if it is a MTD.
   */
  private def isMTDDevice: Boolean = {
    Source.fromFile("/proc/partitions").mkString("\n").contains("mtdblock1")
  }


  private def getDevicesPartition = Observable{
    import scala.collection.JavaConverters._
    RootTools
      .getMounts
      .asScala
      .map{mount =>
      val result =
      (mount.getMountPoint.getAbsolutePath, mount.getDevice.getCanonicalFile.getName)
    result}
    .toMap
  }
}
