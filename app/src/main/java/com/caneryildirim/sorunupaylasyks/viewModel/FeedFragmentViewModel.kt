package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.view.DersActivity
import com.google.firebase.Timestamp

class FeedFragmentViewModel:ViewModel() {
    val loadingFeedLive=MutableLiveData<Boolean>(false)
    val errorFeedLive=MutableLiveData<Boolean>(false)
    val soruListLive=MutableLiveData<List<Soru>>()


    fun refreshData(dersName:String,activity: DersActivity){
        activity.selectToolbarTitle(dersName)


    }

    override fun onCleared() {
        super.onCleared()
    }
}