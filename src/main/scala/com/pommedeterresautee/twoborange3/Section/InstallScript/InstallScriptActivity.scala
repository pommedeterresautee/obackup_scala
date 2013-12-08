package com.pommedeterresautee.twoborange3.Section.InstallScript

import android.os.Bundle
import android.support.v4.app.FragmentActivity


import rx.lang.scala.Subscription
import com.pommedeterresautee.twoborange3.Common.{SideMenu, Busybox}
import com.pommedeterresautee.twoborange3.{R, TypedViewHolder}
import com.pommedeterresautee.twoborange3.Section.Terminal.TerminalFragment
import rx.lang.scala.subscriptions.CompositeSubscription


class InstallScriptActivity extends FragmentActivity with TypedViewHolder with SideMenu {

   val mUnsubscribe = CompositeSubscription()

   override def onCreate(savedInstanceState: Bundle){
     super.onCreate(savedInstanceState)
     setContentView(R.layout.main)
     getSupportFragmentManager.beginTransaction()
       .add(R.id.left_screen, new InstallScriptFragment()).commit()
     mUnsubscribe += Busybox.startCopyAsyncBusyboxIfNeeded(this)
   }

   override def onDestroy() = {
     mUnsubscribe.unsubscribe()
     super.onDestroy()}
 }