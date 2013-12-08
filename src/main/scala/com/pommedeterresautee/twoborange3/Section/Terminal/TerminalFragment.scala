package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.pommedeterresautee.twoborange3.{TR, TypedViewHolder, R}
import com.pommedeterresautee.twoborange3.Common.{NewLine, FileManager, ShellExecutor}
import com.pommedeterresautee.twoborange3.Common.RxThread._
import rx.lang.scala.Notification.{OnError, OnNext}
import android.widget.Toast


class TerminalFragment extends Fragment with TypedViewHolder{

  var layout:View = _
  def findViewById(id: Int): View = layout.findViewById(id)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    layout = inflater.inflate(R.layout.shell_styled_view, container, false)
    layout
  }

  override def onStart(): Unit = {
    super.onStart()
    val shellView = findView(TR.`terminal_view`)
    var lastLineSize = 0

    ShellExecutor(OnandroidCommands.getCommand, useRoot = true)
    .execAsync
    .subscribe(_ match {
      case OnNext(NewLine(s, true)) => shellView.append(s + "\n")
        lastLineSize = shellView.getText.length()
      case OnNext(NewLine(s, false)) =>
        shellView.getText.replace(lastLineSize, shellView.getText.length(), s + "\n")
        shellView.setSelection(shellView.getText.length())
      case OnError(e) => Toast.makeText(getActivity, "error:" + e.getLocalizedMessage, Toast.LENGTH_LONG).show()
  })
}
}
