package com.pommedeterresautee.twoborange3.Section.Terminal

import java.lang.String
import com.pommedeterresautee.twoborange3.Common.FileManager


object OnandroidCommands {
  private val BUSYBOX_PATH_FILE: String = "/data/local/tmp/onandroid.busybox"

  private def getBusyboxToUse = FileManager.getBusybox.getAbsolutePath

  def getCommand = List("echo start",
      "echo \"" + getBusyboxToUse + "\" > \"" + BUSYBOX_PATH_FILE + "\"",
    getBusyboxToUse + " ash",
    FileManager.getOnAndroidScript + " --progress-percent ")

}
