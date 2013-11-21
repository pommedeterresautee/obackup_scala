package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{Toast, EditText}
import com.pommedeterresautee.twoborange3.R
import com.pommedeterresautee.twoborange3.Preference.BackupPref
import com.pommedeterresautee.twoborange3.Common.{RxThread, ScriptManager}
import rx.lang.scala.Notification.{OnError, OnCompleted, OnNext}
import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.RxThread._


class TerminalFragment extends Fragment {

  var unsub:Subscription = _

  def Terminal {}

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.shell_styled_view, container, true)
    v.findViewById(R.id.terminal_view).asInstanceOf[EditText].setText("Terminal")
    v
  }

  override def onStart(){
    super.onStart()
    BackupPref.register(getActivity)
    BackupPref.saveData("toto")
    BackupPref.readData
   new RxThread(ScriptManager.getLastVersion)
    unsub = ScriptManager.getLastVersion
      .setupThread
      .subscribe(n => n match {
      case OnNext(v) => Toast.makeText(getActivity, v, Toast.LENGTH_LONG).show()
      case OnCompleted(c) => //Never called
      case OnError(err) =>
    })
  }

  override def onDetach() {
    super.onDetach()
    unsub.unsubscribe()}
}
