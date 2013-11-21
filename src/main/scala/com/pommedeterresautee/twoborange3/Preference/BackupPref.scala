package com.pommedeterresautee.twoborange3.Preference

import com.pommedeterresautee.twoborange3.R

/**
 * Implement methods to store data in Preference.
 */
object BackupPref extends BasePrefTrait {

  def saveData(s: String) = saveIt(R.string.key_test, s)
  def readData = getString(R.string.key_test)
}
