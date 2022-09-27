package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.view.FeedFragmentDirections
import com.caneryildirim.sorunupaylasyks.view.SingActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal

class MainViewModel:ViewModel() {
    private val auth=Firebase.auth
    private val db=Firebase.firestore
    private var registiration: ListenerRegistration?=null
    var notificationNumber=MutableLiveData<Int>(0)



    fun oneSignal(context: Context){
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(context)
        OneSignal.setAppId(Singleton.ONESIGNAL_APP_ID)
    }



    fun adminPid(){
        if (auth.currentUser!!.uid=="6A9F9r4CAhM18qHT1JgiuthRGZm2"){
            val pId=OneSignal.getDeviceState()?.userId.toString()
            val pIdMap= hashMapOf<String,Any>()
            pIdMap.put("pId",pId)
            db.collection("Admin").document(auth.currentUser!!.uid).set(pIdMap).addOnSuccessListener {

            }
        }
    }

    fun getNotNumber(){
        registiration=db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
            .whereNotEqualTo("okundu",true)
            .addSnapshotListener { value, error ->
                if (value!=null){
                    notificationNumber.value=value.size()
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        registiration?.remove()
    }
}