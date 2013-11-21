package com.pommedeterresautee.twoborange3.Preference

import com.pommedeterresautee.twoborange3.R

object BackupPref extends BasePrefTrait {

  def saveData(s: String) = saveIt(R.string.key_test, 123)
  def readData = getInteger(R.string.key_test)
}
