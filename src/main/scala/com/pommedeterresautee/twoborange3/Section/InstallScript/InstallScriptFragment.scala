package com.pommedeterresautee.twoborange3.Section.InstallScript

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{Toast, EditText}
import com.pommedeterresautee.twoborange3.{TR, TypedViewHolder, R}
import com.pommedeterresautee.twoborange3.Preference.BackupPref
import com.pommedeterresautee.twoborange3.Common.{FileManager, ScriptManager}
import rx.lang.scala.Notification.{OnError, OnNext}
import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.RxThread._
import org.scaloid.common._
import scala.collection.mutable.ArrayBuffer


class InstallScriptFragment extends Fragment with TypedViewHolder {

  val mUnsubscribe = ArrayBuffer[Subscription]()
  var mLayout: View = _

  def findViewById(id: Int): View = mLayout.findViewById(id)

  lazy implicit val mContext = getActivity

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    mLayout = inflater.inflate(R.layout.fragment_script_installation, container, false)

    findView(TR.`button_installed_onandroid_script_update`).onClick{
      mUnsubscribe += ScriptManager.saveLastScript.execAsync.subscribe(
        _ match {
          case OnNext(t) => toast(getString(R.string.installed))
          case OnError(e) => toast(getString(R.string.error_message, e.getLocalizedMessage))
    })
      ()
    }
    mLayout
  }

  override def onStart() {
    super.onStart()
    BackupPref.register(getActivity)
    FileManager.registerContext(getActivity)
    BackupPref.saveData("toto")
    BackupPref.readData

    mUnsubscribe += ScriptManager.getAsyncLastVersion
      .zip(ScriptManager.getLocalScriptVersion)
      .execAsync
      .subscribe {
      _ match {
        case OnNext((site, internal)) =>{
                    findView(TR.`script_version_server`).setText(site)
                    findView(TR.script_version_installed).setText(internal)
//          findView(TR.button_installed_onandroid_script_update).setVisibility(if(site == internal) View.GONE else View.VISIBLE)
        }
        case OnError(err) => {
          longToast(getString(R.string.error_message, err.getMessage))
          findView(TR.button_installed_onandroid_script_update).setVisibility(View.GONE)
        }
      }
    }
  }

  def chooseLayout = {
    val s = spinnerDialog("Downloading", "Getting the partition layout table")
    ScriptManager.getTestListOfDevices
      .map(d => d.brandName)
      .toSeq
      .execAsync
      .subscribe {
      _ match {
        case OnNext(listOfBrand) =>
          s.dismiss()
      }
    }
  }

  override def onDetach() {
    super.onDetach()
    mUnsubscribe.filter(!_.isUnsubscribed).foreach(_.unsubscribe())
  }
}
