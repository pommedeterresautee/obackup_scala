package com.pommedeterresautee.twoborange3.Section.Terminal

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{Toast, EditText}
import com.pommedeterresautee.twoborange3.R
import com.pommedeterresautee.twoborange3.Preference.BackupPref
import com.pommedeterresautee.twoborange3.Common.{FileManager, ScriptManager}
import rx.lang.scala.Notification.{OnError, OnNext}
import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.RxThread._


class TerminalFragment extends Fragment {

  var unsub: Option[Subscription] = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.shell_styled_view, container, true)
    v.findViewById(R.id.terminal_view).asInstanceOf[EditText].setText("Terminal")
    v
  }

  override def onStart() {
    super.onStart()
    BackupPref.register(getActivity)
    FileManager.registerContext(getActivity)
    BackupPref.saveData("toto")
    BackupPref.readData

    unsub = Option {
      ScriptManager.getLastVersion
        .zip(ScriptManager.getCurrentScriptVersion)
        .filter {
        case (site, internal) => site != internal
      }
        .flatMap(p => ScriptManager.saveLastScript)
        .execAsync
        .subscribe {
        _ match {
          case OnNext(v) => Toast.makeText(getActivity, "Installation done: " + v.getAbsolutePath, Toast.LENGTH_LONG).show()
          case OnError(err) => Toast.makeText(getActivity, "Failure: " + err.getMessage, Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  override def onDetach() {
    super.onDetach()
    unsub.filter(!_.isUnsubscribed).foreach(_.unsubscribe())
  }
}
