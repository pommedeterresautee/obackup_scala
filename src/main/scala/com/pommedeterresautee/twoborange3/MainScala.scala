package com.pommedeterresautee.twoborange3;


import org.scaloid.common._
import android.graphics.Color


class MainScala extends SActivity {

  onCreate {
    contentView = new SVerticalLayout {
      style {
        case b: SButton => b.textColor(Color.RED).onClick(longToast("Bang!"))
        case t: STextView => t textSize 10.dip
        case v => v.backgroundColor(Color.YELLOW)
      }

      STextView("I am 10 dip tall")
      STextView("I am taller than you") textSize 15.dip // overriding
      SEditText("Yellow input field")
    } padding 20.dip
  }
}