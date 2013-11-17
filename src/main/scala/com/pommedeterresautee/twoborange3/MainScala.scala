package com.pommedeterresautee.twoborange3


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity

class MainScala extends FragmentActivity with TypedViewHolder with SideMenu {

  override def onCreate(savedInstanceState: Bundle){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
  }
}