package com.pommedeterresautee.twoborange3.Section.Terminal

import android.os.Bundle
import android.support.v4.app.FragmentActivity


import com.pommedeterresautee.twoborange3.Common.SideMenu
import com.pommedeterresautee.twoborange3.{R, TypedViewHolder}


class TerminalActivity extends FragmentActivity with TypedViewHolder with SideMenu{

  override def onCreate(savedInstanceState: Bundle){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val f = new TerminalFragment()
    getSupportFragmentManager
      .beginTransaction()
      .add(R.id.left_screen, f)
      .commit()
  }

}