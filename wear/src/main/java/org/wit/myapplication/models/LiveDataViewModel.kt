package org.wit.myapplication.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.myapplication.models.TeamModel

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

    val teamA: MutableLiveData<TeamModel> by lazy{
        MutableLiveData<TeamModel>()
    }
    val teamB: MutableLiveData<TeamModel> by lazy{
        MutableLiveData<TeamModel>()
    }

}
