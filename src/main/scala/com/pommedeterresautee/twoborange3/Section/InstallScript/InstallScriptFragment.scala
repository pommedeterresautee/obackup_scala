package com.pommedeterresautee.twoborange3.Section.InstallScript

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.pommedeterresautee.twoborange3.Common.ScriptManager
import rx.lang.scala.Notification.{OnError, OnNext}
import com.pommedeterresautee.twoborange3.Common.RxThread._
import org.scaloid.common._
import com.pommedeterresautee.twoborange3.Common.MyScalaHelpers._
import rx.lang.scala.subscriptions.CompositeSubscription
import rx.lang.scala.Subscription
import scala.Some
import com.pommedeterresautee.twoborange3.{TR, TypedViewHolder, R}
import android.widget.AdapterView

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

    case OnNext(Some(listOfDevices)) =>
      import scala.language.postfixOps
      val brandSpinner = findView(TR.device_choice_brand_spinner)
      val modelSpinner = findView(TR.`device_choice_model_spinner`)
      brandSpinner.setAdapter(SArrayAdapter(("Select" :: listOfDevices.map(_.brandName).distinct.sorted.toList).toArray).dropDownStyle(_.textSize(15 dip)))
      brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onNothingSelected(parent: AdapterView[_]) {}
        def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
          if(position > 0){
            modelSpinner.setAdapter(SArrayAdapter(("Select" :: listOfDevices.filter(_.brandName == brandSpinner.getSelectedItem.toString).map(_.commercialName).sorted.toList).toArray).dropDownStyle(_.textSize(15 dip)))
          } else {
            modelSpinner.setAdapter(SArrayAdapter("Select").dropDownStyle(_.textSize(15 dip)))
          }
        }
      })
      modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onNothingSelected(parent: AdapterView[_]) {}
        def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
          val techName = findView(TR.`device_technical_name`)
          if(position > 0){
            techName.setText(listOfDevices.filter{d => d.commercialName == modelSpinner.getSelectedItem.toString && d.brandName == brandSpinner.getSelectedItem.toString}.head.codeName)
          } else techName.setText("")
        }
      })
    case OnNext(None) =>CAlert(getString(R.string.connection_error_message, "No data"))
    case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
  })

  def refreshScriptVersion: Subscription = ScriptManager
    .getAsyncLastVersion
    .zip(ScriptManager.getLocalScriptVersion)
    .execAsync
    .subscribe {
    _ match {
      case OnNext((site, internal)) =>
        findView(TR.`script_version_server`).setText(site.getOrElse("?"))
        findView(TR.script_version_installed).setText(internal.getOrElse("?"))
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
        mUnsubscribe += refreshScriptVersion
      case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
    })

  def removeScriptButton() =  ScriptManager.deleteScript().execAsync.subscribe(
    _ match {
      case OnNext(t) => CInfo("Deleted")
        mUnsubscribe += refreshScriptVersion
      case OnError(e) => CAlert("Error:" + e.getLocalizedMessage)
    })


  override def onDetach() {
    super.onDetach()
    mUnsubscribe.unsubscribe()
  }
}
