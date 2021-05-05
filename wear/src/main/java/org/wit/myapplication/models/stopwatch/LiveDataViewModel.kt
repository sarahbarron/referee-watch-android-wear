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

    val teamAtotalGoals: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
    val teamAtotalPoints: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
    val teamBtotalGoals: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
    val teamBtotalPoints: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
    val teamAtotal: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
    val teamBtotal: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }

    val mSelectedTopNav: MutableLiveData<Int>by lazy{
        MutableLiveData<Int>()
    }
}



