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
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

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

//    Future{
//      val url = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/version")
//      val urlCon = url.openConnection()
//      urlCon.setConnectTimeout(5000)
//      urlCon.setReadTimeout(5000)
//      val io = Source.fromInputStream(urlCon.getInputStream)
//      val result = Option(io.getLines().mkString.split("\n")(0))
//      io.close()
//      result
//    }.mapUi {
//      case Some(result) ⇒ longToast(s"got this: $result")
//      case None ⇒ longToast("got nothing really...")
//    } onFailureUi {
//      case t: Throwable ⇒ longToast(t.getLocalizedMessage)
//    }
  }

  def setPartitionLayoutViews() = {
    val brandSpinner = findView(TR.device_choice_brand_spinner)
    val modelSpinner = findView(TR.`device_choice_model_spinner`)
    val buttonInstall = findView(TR.`button_install_partition_layout`)
    val techName = findView(TR.`device_technical_name`)
    val filter:Boolean = findView(TR.`checkbox_filter`).isChecked
    (if(filter)
      ScriptManager.getAsyncLayoutTableCompatible
      else ScriptManager.getAsyncLayoutTable)
      .execAsync
      .subscribe(_ match {
      case OnNext(listOfDevices) =>
        import scala.language.postfixOps
        brandSpinner.setAdapter(SArrayAdapter(("Select" :: listOfDevices.map(_.brandName).distinct.sorted.toList).toArray).dropDownStyle(_.textSize(15 dip)))
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
          def onNothingSelected(parent: AdapterView[_]) {}

          //no use
          def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
            if (position > 0) {
              modelSpinner.setAdapter(SArrayAdapter(("Select" :: listOfDevices.filter(_.brandName == brandSpinner.getSelectedItem.toString).map(_.commercialName).sorted.toList).toArray).dropDownStyle(_.textSize(15 dip)))
            } else {
              modelSpinner.setAdapter(SArrayAdapter("Select").dropDownStyle(_.textSize(15 dip)))
            }
          }
        })
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
          def onNothingSelected(parent: AdapterView[_]) {}
          //no use
          def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
            if (position > 0) {
              val deviceTechName = listOfDevices.filter {
                d => d.commercialName == modelSpinner.getSelectedItem.toString && d.brandName == brandSpinner.getSelectedItem.toString
              }.head.codeName
              techName.setText(deviceTechName)
              buttonInstall.onClick {
                mUnsubscribe += ScriptManager.getAsyncPartitionLayout(deviceTechName).execAsync.subscribe(
                  _ match {
                    case OnNext(Some(t)) => CInfo(t)
                    case OnNext(None) => CAlert(getString(R.string.connection_error_message, "Server error"))
                    case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
                  })
                ()
              }
            } else {
              techName.setText("")
              buttonInstall.onClick(CAlert("Choose your device first"))
            }
          }
        })
      case OnError(e) =>
        val error = getString(R.string.connection_error_message, e.getLocalizedMessage)
        CAlert(error)
        buttonInstall.onClick(CAlert(error))
        techName.setText(error)
    })
  }

  def refreshScriptVersion: Subscription = ScriptManager
    .getAsyncLastVersion
    .zip(ScriptManager.getLocalScriptVersion)
    .execAsync
    .subscribe {
    _ match {
      case OnNext((site, internal)) =>
        findView(TR.`script_version_server`).setText(site.getOrElse("?"))
        findView(TR.script_version_installed).setText(internal.getOrElse("?"))
        val button = findView(TR.button_installed_onandroid_script_update)
        if (site != internal) {
          button.setText(R.string.update)
          button.onClick {
            mUnsubscribe += updateScriptButton; ()
          }
        } else {
          button.setText("Remove")
          button.onClick {
            mUnsubscribe += removeScriptButton; ()
          }
        }
      case OnError(err) =>
        CAlert(getString(R.string.connection_error_message, err.getLocalizedMessage))
        findView(TR.button_installed_onandroid_script_update).setVisibility(View.GONE)
    }
  }

  def updateScriptButton() = ScriptManager.saveLastScript.execAsync.subscribe(
    _ match {
      case OnNext(t) => CInfo(getString(R.string.installed))
        mUnsubscribe += refreshScriptVersion
      case OnError(e) => CAlert(getString(R.string.connection_error_message, e.getLocalizedMessage))
    })

  def removeScriptButton() = ScriptManager.deleteScript().execAsync.subscribe(
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
