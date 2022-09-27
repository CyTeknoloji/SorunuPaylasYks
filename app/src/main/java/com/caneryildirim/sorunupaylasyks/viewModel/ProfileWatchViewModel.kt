package com.caneryildirim.sorunupaylasyks.viewModel

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.onesignal.OneSignal
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class ProfileWatchViewModel:ViewModel() {
    val db=Firebase.firestore
    val soruArrayList=ArrayList<Soru>()
    val loadingFeedLive= MutableLiveData<Boolean>(false)
    val errorFeedLive= MutableLiveData<Boolean>(false)
    val soruListLive= MutableLiveData<List<Soru>>()


    fun getData(userUidFromIntent:String?){

        if (userUidFromIntent!=null){
            val reference=db.collection("Sorular").whereEqualTo("userUid",userUidFromIntent).orderBy("date", Query.Direction.DESCENDING)
            reference.get().addOnSuccessListener {
                if (it!=null){
                    if (!it.isEmpty){
                        val documents= it.documents
                        soruArrayList.clear()
                        for (document in documents){
                            val downloadUrl=document.get("downloadUrl") as String
                            val email=document.get("email") as String
                            val userDisplayName=document.get("userDisplayName") as String
                            val selectedAciklama=document.get("selectedAciklama") as String
                            val selectedDers=document.get("selectedDers") as String
                            val selectedKonu=document.get("selectedKonu") as String
                            val userUid=document.get("userUid") as String
                            val userPhotoUrl=document.get("userPhotoUrl") as String
                            val docRef=document.get("docRef") as String
                            val pId=document.get("pId") as String?
                            val dogruCevap=document.get("dogruCevap") as Boolean?
                            val dogruCevapString=document.get("dogruCevapString") as String?
                            val dogruCevapImage=document.get("dogruCevapImage") as String?
                            val date=document.get("date") as Timestamp

                            val soru=Soru(downloadUrl,email,selectedAciklama,selectedDers,selectedKonu,userUid,userDisplayName,userPhotoUrl,docRef,pId,dogruCevap,dogruCevapString,dogruCevapImage,date)
                            soruArrayList.add(soru)
                        }
                        soruListLive.value=soruArrayList
                        loadingFeedLive.value=false
                        errorFeedLive.value=false
                    }else{
                        //emty
                        soruArrayList.clear()
                        soruListLive.value=soruArrayList
                        loadingFeedLive.value=false
                        errorFeedLive.value=false
                    }
                }else{
                    //null
                    loadingFeedLive.value=false
                }
            }.addOnFailureListener {
                loadingFeedLive.value=false
                errorFeedLive.value=true
            }
        }else{
            //userUidFromIntent null
            loadingFeedLive.value=false
            errorFeedLive.value=true
        }

    }

    fun sikayetEt(context: Context,soruUid:String,userDisplayName:String,userUid:String){
        val alert= AlertDialog.Builder(context)
        alert.setTitle("Soruyu Şikayet Et")
        alert.setMessage("Emin misin?")
        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(context,"Vazgeçildi",Toast.LENGTH_SHORT).show()
        }
        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
            val uuidBildirim= UUID.randomUUID().toString()
            val bildirimMap= hashMapOf<String,Any>()
            bildirimMap.put("soruUid",soruUid)
            bildirimMap.put("userName","${userDisplayName} sorusu şikayet edildi")
            bildirimMap.put("userUid",userUid)
            bildirimMap.put("docUid",soruUid)
            bildirimMap.put("okundu",false)
            bildirimMap.put("date",Timestamp.now())
            bildirimMap.put("docBildirim",uuidBildirim)

            db.collection("Users").document("6A9F9r4CAhM18qHT1JgiuthRGZm2").collection("Bildirimler").document(uuidBildirim).set(bildirimMap)
                .addOnSuccessListener {
                    Toast.makeText(context,"Şikayet edildi",Toast.LENGTH_SHORT).show()
                    db.collection("Admin").document("6A9F9r4CAhM18qHT1JgiuthRGZm2")
                        .get().addOnSuccessListener {
                            if (it!=null){
                                val pidAdmin=it.get("pId") as String?
                                try {
                                    OneSignal.postNotification("{'contents': {'en':'Soruya Şikayet Geldi'}, 'include_player_ids': ['" + pidAdmin + "']}",null)

                                }catch (e: JSONException){
                                    e.printStackTrace()
                                }
                            }
                        }
                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }
        alert.show()
    }




    override fun onCleared() {
        super.onCleared()
    }
}