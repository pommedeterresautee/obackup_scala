package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{Toast, EditText, TextView}
import android.content.Context
import org.scaloid.common._
import com.pommedeterresautee.twoborange3.R
import com.pommedeterresautee.twoborange3.Preference.BackupPref


class TerminalFragment extends Fragment {

  def Terminal {}

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.shell_styled_view, container, true)
    v.findViewById(R.id.terminal_view).asInstanceOf[EditText].setText("essai")
    v
  }

  override def onStart(){
    super.onStart()
    BackupPref.register(getActivity)
    BackupPref.saveData("toto")
    BackupPref.readData
    Toast.makeText(getActivity, BackupPref.readData.getOrElse(123).toString, Toast.LENGTH_LONG).show()
  }
}
