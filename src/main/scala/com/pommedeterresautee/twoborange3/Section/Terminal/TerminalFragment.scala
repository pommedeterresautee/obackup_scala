package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.pommedeterresautee.twoborange3.R
import com.pommedeterresautee.twoborange3.Common.ShellExecutor
import com.pommedeterresautee.twoborange3.Common.RxThread._
import rx.lang.scala.Notification.{OnCompleted, OnError, OnNext}
import android.widget.Toast
import android.util.Log


class TerminalFragment extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.shell_styled_view, container, false)
    v
  }

  override def onStart(): Unit = {
    super.onStart()
    ShellExecutor.execute(List("echo s", " echo toto", "ls"))
    .execAsync
    .subscribe(_ match {
      case OnNext(s) => //Toast.makeText(getActivity, "Text: " + s, Toast.LENGTH_LONG).show()
        Log.d("Rx log", s)
      case OnError(e) => Toast.makeText(getActivity, "error:" + e.getLocalizedMessage, Toast.LENGTH_LONG).show()
      case OnCompleted(e) => Toast.makeText(getActivity, "fin", Toast.LENGTH_LONG).show()
  })
}
}
