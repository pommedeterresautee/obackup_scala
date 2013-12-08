package com.pommedeterresautee.twoborange3.Common

import java.lang.String
import scala.Predef._
import java.io.File
import android.os.Environment
import android.content.{ContextWrapper, Context}

object FileManager {
  private final val ONANDROID_FILENAME: String = "onandroid"
  final val DEDUPE_FILENAME: String = "dedupe"
  final val BUSYBOX_FILENAME: String = "busybox"
  final val MKYAFFS2IMAGE_FILENAME: String = "mkyaffs2image"
  final val PATCH_FILENAME: String = "partlayout4nandroid"
  final val SYSTEM_PATH: String = "/system/"
  final val BIN_PATH: String = SYSTEM_PATH + "bin/"
  final val MKYAFFS2IMAGE_BIN_PATH: String = BIN_PATH + MKYAFFS2IMAGE_FILENAME
  final val DEDUPE_BIN_PATH: String = BIN_PATH + DEDUPE_FILENAME
  final val ONANDROID_BIN_PATH: String = BIN_PATH + ONANDROID_FILENAME
  final val ONANDROID_PATCH_BIN_PATH: String = SYSTEM_PATH + PATCH_FILENAME
  private final val CLOCKWORKMOD_PATH: String = "clockworkmod/"
  private final val CWM_BACKUP_SUBFOLDER: String = "backup/"
  private final val CWM_BACKUP_PATH: String = CLOCKWORKMOD_PATH + CWM_BACKUP_SUBFOLDER
  private final val CWM_6_DEFAULT_STORAGE_PATH: String = "/data/media/"
  private final val CWM_6_DEFAULT_PATH: String = CWM_6_DEFAULT_STORAGE_PATH + CWM_BACKUP_PATH
  private final val CWM_PATH_TO_INTERNAL_STORAGE: String = "/data/media/0/" + CWM_BACKUP_PATH
  private final val TWRP_PATH: String = "TWRP/"
  private final val TWRP_BACKUP_SUBFOLDER: String = "BACKUPS/"
  private final val TWRP_BACKUP_PATH: String = TWRP_PATH + TWRP_BACKUP_SUBFOLDER
  private final val BACKUP_FOLDER_ZIP: String = "backupZipped/"
  private final val SERIAL_NUMBER_FILE: String = "serial.temp"
  private var mDeviceSerial: String = null
  private final val TEST_CWM_FILE: String = "cwm_test_path"
  private var pathStorageForTheSession: String = null
  private var storeTEMPCWMPathFromPref: String = ""
  private var mContext: Context = _



  def registerContext(context: Context) {
    mContext = context.getApplicationContext
  }
  
  private def getContext = {
    require(mContext != null, "Context is not registered in FileManager")
    mContext
  }

  /**
   * Return the application path on the storage (SD Card / Internal Storage).
   *
   * @return path
   */
  def getExternalStorage: File = {
    var externalPath: File = getContext.getExternalFilesDir(null)

    if (externalPath == null) {
      externalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }
    if (!externalPath.exists) {
      externalPath.mkdirs
    }
    externalPath
  }

  def getInternalStorage: File =  new ContextWrapper(getContext).getFilesDir

  def getOnAndroidScript: File = new File(getInternalStorage, ONANDROID_FILENAME)


  def getBusybox: File = new File(getInternalStorage, BUSYBOX_FILENAME)

//  def isNewVersionAvailable : ((String, String)) => Boolean = (site: String, internal:String) => site != internal
}
