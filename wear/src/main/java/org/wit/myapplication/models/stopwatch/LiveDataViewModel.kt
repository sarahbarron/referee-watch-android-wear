package org.wit.myapplication.models.stopwatch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LiveDataViewModel: ViewModel() {
    val seconds : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val time : MutableLiveData<String>by lazy{
        MutableLiveData<String>()
    }
}