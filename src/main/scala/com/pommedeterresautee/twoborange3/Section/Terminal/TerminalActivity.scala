package com.pommedeterresautee.twoborange3.Section.Terminal

import android.os.Bundle
import android.support.v4.app.FragmentActivity


import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.{SideMenu, Busybox}
import com.pommedeterresautee.twoborange3.{R, TypedViewHolder}


class TerminalActivity extends FragmentActivity with TypedViewHolder with SideMenu {

  var mUnsubscribe: Option[Subscription] = None

  override def onCreate(savedInstanceState: Bundle){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    mUnsubscribe = Busybox.startCopyAsyncBusyboxIfNeeded(this)
  }

  override def onDestroy() = {mUnsubscribe.map(_.unsubscribe()); super.onDestroy()}
}