package com.pommedeterresautee.twoborange3.Common

import org.scaloid.common._
import de.keyboardsurfer.android.widget.crouton.{Style, Crouton}
import android.app.Activity

object MyScalaHelpers {

  @inline def CAlert(message: String)(implicit context: Activity) {
    MyCrouton(message, context, Style.ALERT)
  }

  @inline def CConfirm(message: String)(implicit context: Activity) {
    MyCrouton(message, context, Style.CONFIRM)
  }

  @inline def CInfo(message: String)(implicit context: Activity) {
    MyCrouton(message, context, Style.INFO)
  }

  @inline private def MyCrouton(message: String, context: Activity, s: Style) {
    runOnUiThread(Crouton.makeText(context, message, s).show())
  }
}
