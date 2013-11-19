package com.pommedeterresautee.twoborange3

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{EditText, TextView}
import android.content.Context
import org.scaloid.common._


class Terminal extends Fragment {

  def Terminal {}

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.shell_styled_view, container, true)
    v.findViewById(R.id.terminal_view).asInstanceOf[EditText].setText("essai")
    v
  }

  override def onStart(){
    super.onStart()
  }
}
