package com.caneryildirim.sorunupaylasyks.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UpdateActivityViewModel:ViewModel() {
    private  var auth=Firebase.auth
    private  var firestore=Firebase.firestore
    private  var storage=Firebase.storage

    private val ders=ArrayList<String>()
    private val turkce=ArrayList<String>()
    private val matematik=ArrayList<String>()
    private val fizik=ArrayList<String>()
    private val kimya=ArrayList<String>()
    private val biyoloji=ArrayList<String>()
    private val din=ArrayList<String>()
    private val cografya=ArrayList<String>()
    private val tarih=ArrayList<String>()
    private val yabanciDil=ArrayList<String>()
    private val felsefe=ArrayList<String>()



    fun konuLists(){



    }

    override fun onCleared() {
        super.onCleared()
    }
}