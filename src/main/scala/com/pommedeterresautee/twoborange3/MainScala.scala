package com.pommedeterresautee.twoborange3


import android.app.Activity
import android.os.Bundle

class MainScala extends Activity with TypedViewHolder with SideMenu {

  override def onCreate(savedInstanceState: Bundle){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
  }
}