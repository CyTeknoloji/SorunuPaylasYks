package com.caneryildirim.sorunupaylasyks.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.util.Soru

class FeedFragmentViewModel:ViewModel() {
    val loadingFeedLive=MutableLiveData<Boolean>(false)
    val errorFeedLive=MutableLiveData<Boolean>(false)
    val soruListLive=MutableLiveData<List<Soru>>()


    fun refreshData(dersName:String){
        println(dersName)


    }

    override fun onCleared() {
        super.onCleared()
    }
}