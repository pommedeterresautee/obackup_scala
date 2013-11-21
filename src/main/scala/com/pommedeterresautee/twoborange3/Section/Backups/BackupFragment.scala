package com.pommedeterresautee.twoborange3.Section.Backups

import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import android.widget.{Toast, EditText}
import com.pommedeterresautee.twoborange3.R
import com.pommedeterresautee.twoborange3.Preference.BackupPref


class BackupFragment extends Fragment {

   override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
     val v = inflater.inflate(R.layout.shell_styled_view, container, true)
     v.findViewById(R.id.terminal_view).asInstanceOf[EditText].setText("Backup")
     v
   }

   override def onStart(){
     super.onStart()
   }
 }
