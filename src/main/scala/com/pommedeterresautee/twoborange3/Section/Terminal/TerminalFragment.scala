package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.pommedeterresautee.twoborange3.{TR, TypedViewHolder, R}
import com.pommedeterresautee.twoborange3.Common.{NewLine, ShellExecutor}
import com.pommedeterresautee.twoborange3.Common.RxThread._
import rx.lang.scala.Notification.{OnError, OnNext}
import org.scaloid.common._
import android.view.View.OnClickListener
import rx.lang.scala.Subscription
import android.widget.Button


class TerminalFragment extends Fragment with TypedViewHolder{

  var layout:View = _
  def findViewById(id: Int): View = layout.findViewById(id)

  lazy implicit val context = getActivity
  var unsubscribe:Option[Subscription] = None
  var stop:Button = _
  var start:Button = _


  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    layout = inflater.inflate(R.layout.installation, container, false)
    stop = findView(TR.`button_left`)
    stop.setText("Stop")
    stop.setOnClickListener(new OnClickListener {
      def onClick(v: View): Unit = {
        unsubscribe.map(_.unsubscribe())
        start.setEnabled(true)
        
      }
    })
    start = findView(TR.`button_right`)
    start.setText("Start")
    start.setOnClickListener(new OnClickListener {
      def onClick(v: View): Unit = {
        unsubscribe = Option(startBackup)
        start.setEnabled(false)
      }
    })
    layout
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unsubscribe.map(_.unsubscribe())
  }

  def startBackup = {
    val shellView = findView(TR.`terminal_view`)
    var lastLineSize = 0
    ShellExecutor(OnandroidCommands.getCommand, useRoot = true)
      .execAsync
      .subscribe(_ match {
      case OnNext(NewLine(s, true, false)) => shellView.append(s + "\n")
        lastLineSize = shellView.getText.length()
      case OnNext(NewLine(s, false, false)) =>
        shellView.getText.replace(lastLineSize, shellView.getText.length(), s + "\n")
        shellView.setSelection(shellView.getText.length())
      case OnNext(NewLine(_, _, true)) =>  start.setEnabled(true)
      case OnError(e) =>  longToast("error:" + e.getLocalizedMessage)
        start.setEnabled(true)
    })
  }
}
