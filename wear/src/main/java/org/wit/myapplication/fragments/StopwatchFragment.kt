package org.wit.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.wit.myapplication.R

class StopwatchFragment : Fragment() {

    //stopwatch
    var millsecondTime: Long? = null
    var startTime: Long? =null
    var TimeBuff: Long? = null
    var UpdateTime: Long? = 0
    var seconds: Int? = null
    var minutes: Int? = null
    var milliseconds: Int? =null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false)
    }

}
