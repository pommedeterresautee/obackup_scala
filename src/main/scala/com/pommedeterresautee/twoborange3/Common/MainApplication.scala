package com.pommedeterresautee.twoborange3.Common
import android.app._
import com.pommedeterresautee.twoborange3.Preference.BackupPref

class MainApplication extends Application {

  override def onCreate() = {
    super.onCreate()
    BackupPref.register(this.getApplicationContext)
    FileManager.registerContext(this.getApplicationContext)
  }
}
