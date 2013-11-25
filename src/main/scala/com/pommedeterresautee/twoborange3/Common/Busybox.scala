package com.pommedeterresautee.twoborange3.Common

import java.io._
import android.content.Context
import rx.lang.scala._
import rx.lang.scala.concurrency.Schedulers
import rx.lang.scala.Notification.{OnError, OnCompleted, OnNext}
import org.scaloid.common._
import com.pommedeterresautee.twoborange3.R


object Busybox {
  private val BUSYBOX_FILENAME_EXTERN: String = "busybox"
  private val BUSYBOX_FILENAME_INTERN: String = "busybox-linus"
  private val BUSYBOX_SIZE: Long = 1165484l

  /**
   * Proceed to the installation of Busybox in a thread safe way.
   * @param context Activity one
   * @return a Rx Unsubscribe object
   */
  def startCopyAsyncBusyboxIfNeeded(implicit context: Context) = {
    val unsubsb = Observable{
      copyBusyBoxIfNotYetDone(context.getApplicationContext)
    }
      .subscribeOn(Schedulers.newThread)
      .observeOn(Schedulers.currentThread)
      .materialize
      .subscribe(n => n match {
      case OnNext(v) if v => longToast(R.string.busybox_installed)
      case OnCompleted(c) => //Never called
      case OnError(err) => err match {case err: Exception => longToast(context.getString(R.string.busybox_error_installation, err.getMessage))}
    })
    Some(unsubsb)
  }

  /**
   * Get the path to the BusyBox file.
   *
   * @param context
   * @return path
   */
  def getBusyBoxPath(context: Context): String = {
    if (true/*RootPref.isBusyboxInternal*/) {
      getInternalBusybox(context).getAbsolutePath
    }
    else {
      BUSYBOX_FILENAME_EXTERN
    }
  }

  private def getInternalBusybox(context: Context): File = {
    new File(context.getFilesDir, BUSYBOX_FILENAME_EXTERN)
  }

  private def copyBusyBoxIfNotYetDone(context: Context):Boolean = {
    val appContext: Context = context.getApplicationContext
    if (!checkBusyBox(appContext)) {
      copyBusyBox(appContext)
      true
    } else false
  }

  /**
   * Copy BusyBox executable to the private data folder.
   *
   * @param context
   */
  private def copyBusyBox(context: Context) = {
      val busybox: File = getInternalBusybox(context)
      if (busybox.exists) {
//        require(busybox.delete, "Impossible to delete existing busybox")
      }
      val fos: FileOutputStream = context.openFileOutput(BUSYBOX_FILENAME_EXTERN, Context.MODE_PRIVATE)
      val is: InputStream = context.getAssets.open(BUSYBOX_FILENAME_INTERN)

      val bytes = new Array[Byte](1024) //1024 bytes - Buffer size
      Iterator
        .continually (is.read(bytes))
        .takeWhile (_ != -1 )
        .foreach (read=>fos.write(bytes,0,read))
        fos.close()

      require(busybox.length() > 0l, "Failed to copy busybox to this destination: " + busybox.getAbsolutePath)

      require(busybox.setReadable(false) && busybox.setWritable(false) && busybox.setExecutable(true, true), "Failed to change properties of this file: " + busybox.getAbsolutePath)
  }

  /**
   * Check if BusyBox application has been copied
   * in the private data folder of the application.
   *
   * @param context
   * @return true if already copied.
   */
  private def checkBusyBox(context: Context): Boolean = {
    val busybox: File = getInternalBusybox(context)
    busybox.exists && busybox.length == BUSYBOX_SIZE && busybox.canExecute
  }
}
