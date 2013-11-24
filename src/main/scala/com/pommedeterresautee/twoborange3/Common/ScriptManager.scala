package com.pommedeterresautee.twoborange3.Common

import scala.io.Source
import java.io.{FileOutputStream, File}
import java.net.URL


import rx.lang.scala.{Observer, Observable}
import rx.lang.scala.subscriptions.Subscription
import android.app.{Activity, AlertDialog}
import android.view.{View, LayoutInflater}
import android.content.{DialogInterface, Context}
import android.widget._
import com.pommedeterresautee.twoborange3.R
import scala.Predef.String



object ScriptManager {

  case class Device(brandName:String, codeName: String, commercialName: String, var partitionName: Option[Seq[String]] = None) extends Ordered[Device]{
    override def toString = brandName + " (model: " + commercialName + ")\n" + partitionName.mkString(", ")

    def compare(that: Device): Int = this.brandName compareTo that.brandName match {
        case 0 => commercialName.compareTo(that.commercialName)
        case compareBrand => compareBrand
      }
  }

  private val urlVersion = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/version")

  private val lastScript = new URL("https://raw.github.com/ameer1234567890/OnlineNandroid/master/onandroid")

//  def getAsyncLayoutTable:Observable[String] = ???

  def getTestListOfDevices = Observable(Device("Samsung", "007", "comm model 1"), Device("Samsung", "008", "comm model 2"), Device("Philipps", "009", "comm model 3"))

  def getAsyncLastVersion = getAsyncUrl(urlVersion)

  private def getAsyncUrl(url: URL) = Observable {
    (observer: Observer[String]) => {
      val io = Source.fromURL(urlVersion)
      val result = io.getLines().mkString
      io.close()
      observer.onNext(result)
      observer.onCompleted()
      Subscription()
    }
  }

  def saveLastScript = Observable{
    (observer: Observer[File]) => {
    val scriptFile = FileManager.getOnAndroidScript
    val is = lastScript.openStream()
    val fos = new FileOutputStream(scriptFile)
    val dataToWrite = new Array[Byte](1024)
    Iterator
      .continually(is.read(dataToWrite))
      .takeWhile(_ != -1)
      .foreach(read => fos.write(dataToWrite, 0, read))
    fos.close()
    is.close()
//    require(true/*scriptFile.exists()*/, "Download of the script failed.")
    scriptFile.setExecutable(true)
    scriptFile.setReadable(true)
    scriptFile.setWritable(true)
      observer.onNext(scriptFile)
      observer.onCompleted()
      Subscription()
    }
  }

  /**
   * Get the version number of the local script.
   * 
   * @return the version number of the installed script
   */
  def getLocalScriptVersion: Observable[String] = Observable{
    import scala.sys.process._
    (observer: Observer[String]) => {
      val scriptFile = FileManager.getOnAndroidScript      
      if(scriptFile.exists() && scriptFile.canExecute && scriptFile.length() > 0){
        val result = (FileManager.getOnAndroidScript.getAbsolutePath + " --version").!!.split("\n")(0)
        observer.onNext(result)
      } else observer.onNext("")
      observer.onCompleted()
      Subscription()
    }
  }


  def getMainDialog(mContext: Activity, devices:List[Device]): AlertDialog.Builder ={

    val brandList = devices.map(_.brandName).distinct.sorted

    def getFilteredDeviceList(brand:String) = devices.filter(_.brandName == brand).toList.sortWith(_ > _)

    val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    val layout: ScrollView = inflater.inflate(R.layout.fragment_script_installation, null).asInstanceOf[ScrollView]

    val brandAdapter: ArrayAdapter[String] = new ArrayAdapter[String](mContext, android.R.layout.simple_spinner_dropdown_item, brandList.toArray)


    val modelAdapter: ArrayAdapter[String] = new ArrayAdapter[String](mContext, android.R.layout.simple_spinner_dropdown_item, Array[String]())

    val explanation: TextView = layout.findViewById(R.id.device_selection_explanation).asInstanceOf[TextView]

    val technicalName: TextView = layout.findViewById(R.id.device_technical_name).asInstanceOf[TextView]

    val brandSelection: Spinner = layout.findViewById(R.id.device_choice_brand_spinner).asInstanceOf[Spinner]


    val modelSelection: Spinner = layout.findViewById(R.id.device_choice_model_spinner).asInstanceOf[Spinner]


    val recoverySelection: Spinner = layout.findViewById(R.id.device_selection_backup_type_spinner).asInstanceOf[Spinner]

    explanation.setText("Complete supported list\nChoose your device model or click NOT LISTED button if you don't find it.")

    brandSelection.setAdapter(brandAdapter)


    brandSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
      def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
        import collection.JavaConversions._
        if (position == 0) {
//          refreshValidateButtonEnable(false)
        }
        modelAdapter.clear()
        modelAdapter.addAll(getFilteredDeviceList(brandList(position)).map(_.commercialName))
        modelAdapter.notifyDataSetChanged()
      }

      def onNothingSelected(parent: AdapterView[_]) {
      }
    })

    modelSelection.setAdapter(modelAdapter)

    modelSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
      def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
        val brand: String = brandList(brandSelection.getSelectedItemPosition)
        val techName: String = if (position == 0) "no selection" else getFilteredDeviceList(brand)(position).codeName
        technicalName.setText(techName + ")")
//        if (dialog != null) {
//          refreshValidateButtonEnable(position > 0)
//        }
      }

      def onNothingSelected(parent: AdapterView[_]) {
      }
    })

//    recoverySelection.setSelection(mBackupPref.getBackupTypePosition)

    new AlertDialog.Builder(mContext).setTitle(R.string.configuration).setView(layout).setCancelable(false).setPositiveButton(R.string.continue_button, new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {

      }
    })
  }

}
