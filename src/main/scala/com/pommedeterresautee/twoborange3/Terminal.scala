package com.pommedeterresautee.twoborange3

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.TextView

class Terminal extends Fragment {

  def Terminal {}

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
   val tb = new TextView(getActivity)
    tb.setText("test")
    tb
  }
}
