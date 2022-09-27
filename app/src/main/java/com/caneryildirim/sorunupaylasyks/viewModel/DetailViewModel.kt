package com.caneryildirim.sorunupaylasyks.viewModel

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.databinding.CevapDetailAlertBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.ONESIGNAL_APP_ID
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoSoru
import com.caneryildirim.sorunupaylasyks.view.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal

class DetailViewModel:ViewModel() {
    val db=Firebase.firestore
    val soruDataLive=MutableLiveData<Soru>()
    val loadingLive=MutableLiveData<Boolean>(false)
    val errorLive=MutableLiveData<Boolean>(false)


    fun getSoruData(soruUid:String?){
        if (soruUid != null) {
            loadingLive.value=true
            db.collection("Sorular").document(soruUid).get().addOnSuccessListener {
                if (it!=null){
                    if (it.exists()){
                        val data=it.data
                        if (data!=null){
                            val downloadUrl=it.get("downloadUrl") as String
                            val email=it.get("email") as String
                            val selectedAciklama=it.get("selectedAciklama") as String
                            val selectedDers=it.get("selectedDers") as String
                            val selectedKonu=it.get("selectedKonu") as String
                            val userUid=it.get("userUid") as String
                            val userDisplayName=it.get("userDisplayName") as String
                            val userPhotoUrl=it.get("userPhotoUrl") as String
                            val docRef=it.get("docRef") as String
                            val pId=it.get("pId") as String?
                            val dogruCevap=it.get("dogruCevap") as Boolean?
                            val dogruCevapString=it.get("dogruCevapString") as String?
                            val dogruCevapImage=it.get("dogruCevapImage") as String?
                            val date=it.get("date") as Timestamp

                            val soru= Soru(downloadUrl,email,selectedAciklama,selectedDers,selectedKonu,userUid,userDisplayName,userPhotoUrl,docRef,pId,dogruCevap,dogruCevapString,dogruCevapImage,date)
                            soruDataLive.value=soru
                            loadingLive.value=false
                            errorLive.value=false
                        }

                    }
                }else{
                    //null
                    loadingLive.value=false
                    errorLive.value=true
                }
            }.addOnFailureListener {

            }
        }
    }

    fun getRecyclerData(){

    }

    fun oneSignalListener(context:Context){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.setNotificationOpenedHandler {
            val intent= Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun zoomImage(context: Context,downLoadUrl:String,layoutInflater: LayoutInflater) {
        val bindingAlert= CevapDetailAlertBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        bindingAlert.alertImage.downloadUrlPicassoSoru(downLoadUrl)
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }



    override fun onCleared() {
        super.onCleared()
    }
}