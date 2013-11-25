package com.pommedeterresautee.twoborange3.Section.InstallScript

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.pommedeterresautee.twoborange3.{TR, TypedViewHolder, R}
import com.pommedeterresautee.twoborange3.Common.ScriptManager
import rx.lang.scala.Notification.{OnError, OnNext}
import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.RxThread._
import org.scaloid.common._
import com.pommedeterresautee.twoborange3.Common.MyScalaHelpers._
import rx.lang.scala.subscriptions.CompositeSubscription
import com.pommedeterresautee.twoborange3.Common.ScriptManager.Device


class InstallScriptFragment extends Fragment with TypedViewHolder {

  val mUnsubscribe = CompositeSubscription()
  var mLayout: View = _

  def findViewById(id: Int): View = mLayout.findViewById(id)

  lazy implicit val mContext = getActivity

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    mLayout = inflater.inflate(R.layout.fragment_script_installation, container, false)
    mLayout
  }

  override def onStart() {
    super.onStart()
    mUnsubscribe += refreshScriptVersion
    mUnsubscribe += setPartitionLayoutViews
  }

  def setPartitionLayoutViews() = ScriptManager.getAsyncLayoutTable
    .execAsync
    .subscribe(_ match {
    case OnNext(Some(list:Seq[Device])) => CInfo(list.head.brandName)
    case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
    case _ => CAlert(getString(R.string.connection_error_message))
  })

  def refreshScriptVersion: Subscription = ScriptManager.getAsyncLastVersion
    .zip(ScriptManager.getLocalScriptVersion)
    .execAsync
    .subscribe {
    _ match {
      case OnNext((site, internal)) =>
        findView(TR.`script_version_server`).setText(site)
        findView(TR.script_version_installed).setText(internal)
        val b = findView(TR.button_installed_onandroid_script_update)
        if(site != internal){
          b.setText(R.string.update)
          b.onClick{mUnsubscribe += updateScriptButton; ()}
        } else {
          b.setText("Remove")
          b.onClick{mUnsubscribe += removeScriptButton; ()}
        }
      case OnError(err) =>
        CAlert(getString(R.string.connection_error_message, err.getLocalizedMessage))
        findView(TR.button_installed_onandroid_script_update).setVisibility(View.GONE)
    }
  }

  def updateScriptButton() =  ScriptManager.saveLastScript.execAsync.subscribe(
    _ match {
      case OnNext(t) => CInfo(getString(R.string.installed))
        mUnsubscribe += refreshScriptVersion;
      case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
    })

  def removeScriptButton() =  ScriptManager.deleteScript.execAsync.subscribe(
    _ match {
      case OnNext(t) => CInfo("Deleted")
        mUnsubscribe += refreshScriptVersion;
      case OnError(e) => CAlert("Error:" + e.getLocalizedMessage)
    })


  override def onDetach() {
    super.onDetach()
    mUnsubscribe.unsubscribe()
  }
}
