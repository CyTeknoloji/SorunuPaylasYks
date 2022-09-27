package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.util.Notification
import com.caneryildirim.sorunupaylasyks.view.DetailActivity
import com.caneryildirim.sorunupaylasyks.view.NotificationFragment
import com.caneryildirim.sorunupaylasyks.view.SingActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationViewModel:ViewModel() {
    val auth=Firebase.auth
    val db=Firebase.firestore
    val notificationList=ArrayList<Notification>()
    val notificationLiveList=MutableLiveData<List<Notification>>()
    val loadingNotification=MutableLiveData<Boolean>(false)
    val errorNotification=MutableLiveData<Boolean>(false)

    fun getNotData(context: Context){
        db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
            .orderBy("date",
                Query.Direction.DESCENDING).get().addOnSuccessListener {
                if (it != null) {
                    if (!it.isEmpty){
                        notificationList.clear()
                        val documents=it.documents
                        for (document in documents){
                            val senderUsername=document.get("userName") as String
                            val senderUid=document.get("userUid") as String
                            val docBildirim=document.get("docBildirim") as String
                            val soruUid=document.get("soruUid") as String
                            val docUid=document.get("docUid") as String
                            val date=document.get("date") as Timestamp
                            val okundu=document.get("okundu") as Boolean?
                            val notification=Notification(senderUsername,senderUid,docBildirim,soruUid,docUid,date,okundu)
                            notificationList.add(notification)
                        }
                        notificationLiveList.value=notificationList
                        loadingNotification.value=false
                        errorNotification.value=false
                    }else{
                        //emtp
                        notificationList.clear()
                        notificationLiveList.value=notificationList
                        loadingNotification.value=false
                        errorNotification.value=false
                    }
                }else{
                    //null
                    loadingNotification.value=false
                }
            }.addOnFailureListener {
                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                loadingNotification.value=false
                errorNotification.value=true
            }
    }

    fun delete(position:Int,context: Context){
        val soruUid=notificationList[position].soruUid
        val refNot=db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler").document(notificationList[position].docBildirim)
        refNot.update("okundu",true).addOnSuccessListener {
                db.collection("Sorular").document(soruUid).get().addOnSuccessListener {
                    if (it.exists()){
                        getNotData(context)
                        val intent=Intent(context,DetailActivity::class.java)
                        intent.putExtra("soruUid",soruUid)
                        context.startActivity(intent)
                    }else{
                        getNotData(context)
                        refNot.delete().addOnSuccessListener {
                            Toast.makeText(context,"Soru silinmiş!",Toast.LENGTH_SHORT).show()
                        }
                    }

                }.addOnFailureListener {
                    Toast.makeText(context,"Beklenmedik bir sorun oluştu!",Toast.LENGTH_SHORT).show()
                }


        }.addOnFailureListener {
            Toast.makeText(context,"Beklenmedik bir sorun oluştu!",Toast.LENGTH_SHORT).show()
        }
    }


    fun deleteOkunanlar(context: Context){
        val alert= AlertDialog.Builder(context)
        alert.setTitle("Okunan Bildirimleri Sil")
        alert.setMessage("Emin misin?")
        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
        }
        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
            db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
                .whereEqualTo("okundu",true).get().addOnSuccessListener {
                    if (it!=null){
                        if (!it.isEmpty){
                            val documents=it.documents
                            for (document in documents){
                                val docBildirim=document.get("docBildirim") as String
                                db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
                                    .document(docBildirim).delete().addOnSuccessListener {  }
                            }
                                getNotData(context)
                        }else{
                            Toast.makeText(context,"Okunmuş bildirim yok!",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        alert.show()
    }


    fun deleteTumu(context: Context){
        val alert= AlertDialog.Builder(context)
        alert.setTitle("Tüm Bildirimleri Sil")
        alert.setMessage("Emin misin?")
        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
        }
        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
            db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
                .get().addOnSuccessListener {
                    if (it!=null){
                        if (!it.isEmpty){
                            val documents=it.documents
                            for (document in documents){
                                val docBildirim=document.get("docBildirim") as String
                                db.collection("Users").document(auth.currentUser!!.uid).collection("Bildirimler")
                                    .document(docBildirim).delete().addOnSuccessListener {  }
                            }
                            getNotData(context)
                        }else{
                            Toast.makeText(context,"Bildirim yok!",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        alert.show()
    }



    override fun onCleared() {
        super.onCleared()
    }
}