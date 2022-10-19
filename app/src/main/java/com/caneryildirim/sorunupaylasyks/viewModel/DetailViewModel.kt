package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.collection.ArraySet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.CevapDetailAlertBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.ADMIN_UID
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.ONESIGNAL_APP_ID
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.notificationInfo
import com.caneryildirim.sorunupaylasyks.util.Cevap
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.util.Takipci
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoSoru
import com.caneryildirim.sorunupaylasyks.view.MainActivity
import com.caneryildirim.sorunupaylasyks.view.ProfileWatchActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.onesignal.OneSignal
import org.json.JSONException
import java.util.*

class DetailViewModel:ViewModel() {
    val db=Firebase.firestore
    val soruDataLive=MutableLiveData<Soru>()
    val cevapDataLive=MutableLiveData<List<Cevap>>()
    val loadingLive=MutableLiveData<Boolean>(false)
    val errorLive=MutableLiveData<Boolean>(false)
    val successYukle=MutableLiveData<Boolean>(false)
    val auth=Firebase.auth
    val storage=Firebase.storage
    var takipciList=ArraySet<Takipci>()
    private lateinit var referenceRegistration: ListenerRegistration
    private var cevapArrayList=ArrayList<Cevap>()
    private lateinit var soru:Soru

    fun goToProfileWatch(context: Context){
        val intent= Intent(context, ProfileWatchActivity::class.java)
        intent.putExtra("userUid",soru.userUid)
        intent.putExtra("userName",soru.userDisplayName)
        intent.putExtra("userPhotoUrl",soru.userPhotoUrl)
        context.startActivity(intent)
    }

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

                            soru= Soru(downloadUrl,email,selectedAciklama,selectedDers,selectedKonu,userUid,userDisplayName,userPhotoUrl,docRef,pId,dogruCevap,dogruCevapString,dogruCevapImage,date)
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

    fun getRecyclerData(soruUid:String){
            val reference=db.collection("Cevap").document(soruUid)
                .collection("Cevaplar")
                .orderBy("date", Query.Direction.ASCENDING)
            referenceRegistration= reference.addSnapshotListener { value, error ->
                if (value!=null){
                    if (!value.isEmpty){
                        val documents=value.documents
                        takipciList.clear()
                        cevapArrayList.clear()

                        for (doc in documents){
                            val downloadUrl=doc.get("downloadUrl") as String
                            val selectedAciklama=doc.get("selectedAciklama") as String
                            val soruUid=doc.get("soruUid") as String
                            val userName=doc.get("userName") as String
                            val userPhotoUrl=doc.get("userPhotoUrl") as String?
                            val userUid=doc.get("userUid") as String
                            val userUidSoru=doc.get("userUidSoru") as String?
                            val docUuid=doc.get("docUuid") as String
                            val dogruCevap=doc.get("dogruCevap") as Boolean
                            val yanlisCevap=doc.get("yanlisCevap") as Boolean?
                            val pIdCevap=doc.get("pIdCevap") as String?
                            val date=doc.get("date") as Timestamp
                            val cevap=Cevap(downloadUrl,selectedAciklama,soruUid,userName,userUid,userUidSoru,docUuid,dogruCevap,pIdCevap,userPhotoUrl,date,yanlisCevap)
                            cevapArrayList.add(cevap)

                            if (pIdCevap!=null && userUid!= userUid){
                                val takipci= Takipci(userUid,pIdCevap)
                                takipciList.add(takipci)
                            }
                            cevapDataLive.value=cevapArrayList
                        }
                    }else{
                        //value emty
                        cevapArrayList.clear()
                        cevapDataLive.value=cevapArrayList
                    }
                }else{
                    //value null
                    cevapArrayList.clear()
                    cevapDataLive.value=cevapArrayList

                }
            }


    }

    fun yukle(context: Context,cevapText:String?,selectedImage:Uri?){
        if (cevapText!!.isEmpty() && selectedImage==null){
            Toast.makeText(context,"Görsel ya da yorumdan oluşan cevabınız olmalı",Toast.LENGTH_SHORT).show()
        }else{
            loadingLive.value=true
            val imageUuid= UUID.randomUUID().toString()
            val soruUid= soru.docRef
            val imageName="${imageUuid}.jpg"
            val selectedAciklama=cevapText
            val date=Timestamp.now()
            val userName=auth.currentUser!!.displayName.toString()     //email yazıyor ama userName aldın
            val userPhotoUrl=auth.currentUser!!.photoUrl.toString()
            val userUid=auth.currentUser!!.uid
            val pIdCevap=OneSignal.getDeviceState()?.userId
            val reference=storage.reference.child("cevaplar").child(soruUid!!).child(imageName)

            if (selectedImage!=null){
                reference.putFile(selectedImage).addOnSuccessListener {
                    val uploadImageReference=storage.reference.child("cevaplar").child(soruUid).child(imageName)
                    uploadImageReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl=it.toString()
                        val cevapMap= hashMapOf<String,Any>()
                        cevapMap.put("downloadUrl",downloadUrl)
                        cevapMap.put("selectedAciklama",selectedAciklama)
                        cevapMap.put("date",date)
                        cevapMap.put("userName",userName)
                        cevapMap.put("userPhotoUrl",userPhotoUrl)
                        cevapMap.put("userUid",userUid)
                        cevapMap.put("userUidSoru",soru.userUid)
                        cevapMap.put("soruUid",soruUid)
                        cevapMap.put("docUuid",imageUuid)
                        cevapMap.put("dogruCevap",false)
                        cevapMap.put("yanlisCevap",false)
                        cevapMap.put("cevapYorum","null")
                        if (pIdCevap != null) {
                            cevapMap.put("pIdCevap",pIdCevap)
                        }else{
                            cevapMap.put("pIdCevap","null")
                        }

                        db.collection("Cevap")
                            .document(soruUid).collection("Cevaplar")
                            .document(imageUuid)
                            .set(cevapMap).addOnSuccessListener {
                                loadingLive.value=false
                                successYukle.value=true

                                val uuidTakipBildirim=UUID.randomUUID().toString()
                                val takipBildirimMap= hashMapOf<String,Any>()
                                takipBildirimMap.put("soruUid",soruUid)
                                takipBildirimMap.put("userName","Takip ettiğin soruya cevap geldi")
                                takipBildirimMap.put("userUid",userUid)
                                takipBildirimMap.put("docUid",imageUuid)
                                takipBildirimMap.put("date",date)
                                takipBildirimMap.put("docBildirim",uuidTakipBildirim)
                                takipBildirimMap.put("okundu",false)

                                if (takipciList.isNotEmpty()){
                                    if (takipciList.size>0){
                                        for (takipci in takipciList){
                                            if (takipci.takipciUid!=soru.userUid && takipci.takipciUid!= auth.currentUser!!.uid){
                                                db.collection("Users")
                                                    .document(takipci.takipciUid)
                                                    .collection("Bildirimler")
                                                    .document(uuidTakipBildirim)
                                                    .set(takipBildirimMap)
                                                    .addOnSuccessListener {
                                                        try {
                                                            OneSignal.postNotification("{'contents': {'en':'Takip ettiğin soruya cevap geldi'}, 'include_player_ids': ['" + takipci.takipciPid + "']}",null)
                                                        }catch (e: JSONException){
                                                            e.printStackTrace()
                                                        }
                                                    }.addOnFailureListener {
                                                        println(it.localizedMessage)
                                                    }
                                            }
                                        }
                                    }
                                }

                                if (auth.currentUser!!.uid!=soru.userUid){
                                    val uuidBildirim=UUID.randomUUID().toString()
                                    val bildirimMap= hashMapOf<String,Any>()
                                    bildirimMap.put("soruUid",soruUid)
                                    bildirimMap.put("userName","${userName} sorunu yanıtladı")
                                    bildirimMap.put("userUid",userUid)
                                    bildirimMap.put("docUid",imageUuid)
                                    bildirimMap.put("date",date)
                                    bildirimMap.put("docBildirim",uuidBildirim)
                                    bildirimMap.put("okundu",false)

                                    db.collection("Users")
                                        .document(soru.userUid)
                                        .collection("Bildirimler")
                                        .document(uuidBildirim)
                                        .set(bildirimMap)
                                        .addOnSuccessListener {
                                            if (pIdCevap!=soru.pId){
                                                try {
                                                    OneSignal.postNotification("{'contents': {'en':'Soruna Cevap Geldi'}, 'include_player_ids': ['" + soru.pId + "']}",null)
                                                }catch (e: JSONException){
                                                    e.printStackTrace()
                                                }
                                            }
                                        }

                                }

                            }.addOnFailureListener {
                                //db fail
                                Toast.makeText(context,"Cevap Yüklenemedi!",Toast.LENGTH_SHORT).show()
                                loadingLive.value=false
                            }

                    }.addOnFailureListener {
                        //downloadurl fail
                        Toast.makeText(context,"Cevap Yüklenemedi!",Toast.LENGTH_SHORT).show()
                        loadingLive.value=false
                    }
                }.addOnFailureListener {
                    //reference fail
                    Toast.makeText(context,"Cevap Yüklenemedi!",Toast.LENGTH_SHORT).show()
                    loadingLive.value=false
                }
            }else{
                //selectedImage null
                val cevapMap= hashMapOf<String,Any>()
                cevapMap.put("downloadUrl","null")
                cevapMap.put("selectedAciklama",selectedAciklama)
                cevapMap.put("date",date)
                cevapMap.put("userName",userName)
                cevapMap.put("userPhotoUrl",userPhotoUrl)
                cevapMap.put("userUid",userUid)
                cevapMap.put("userUidSoru",soru.userUid)
                cevapMap.put("soruUid",soruUid)
                cevapMap.put("docUuid",imageUuid)
                cevapMap.put("dogruCevap",false)
                cevapMap.put("yanlisCevap",false)
                cevapMap.put("cevapYorum","null")
                if (pIdCevap != null) {
                    cevapMap.put("pIdCevap",pIdCevap)
                }

                val referenceNew=db.collection("Cevap")
                    .document(soruUid).collection("Cevaplar")
                    .document(imageUuid)
                referenceNew.set(cevapMap).addOnSuccessListener {
                    loadingLive.value=false
                    successYukle.value=true

                    val uuidTakipBildirim=UUID.randomUUID().toString()
                    val takipBildirimMap= hashMapOf<String,Any>()
                    takipBildirimMap.put("soruUid",soruUid)
                    takipBildirimMap.put("userName","Takip ettiğin soruya cevap geldi")
                    takipBildirimMap.put("userUid",userUid)
                    takipBildirimMap.put("docUid",imageUuid)
                    takipBildirimMap.put("date",date)
                    takipBildirimMap.put("docBildirim",uuidTakipBildirim)
                    takipBildirimMap.put("okundu",false)

                    if (takipciList.isNotEmpty()){
                        if (takipciList.size>0){
                            for (takipci in takipciList){
                                if (takipci.takipciUid!=soru.userUid && takipci.takipciUid!= auth.currentUser!!.uid){
                                    db.collection("Users").document(takipci.takipciUid).collection("Bildirimler").document(uuidTakipBildirim).set(takipBildirimMap).addOnSuccessListener {
                                        try {
                                            OneSignal.postNotification("{'contents': {'en':'Takip ettiğin soruya cevap geldi'}, 'include_player_ids': ['" + takipci.takipciPid + "']}",null)
                                        }catch (e: JSONException){
                                            e.printStackTrace()
                                        }
                                    }.addOnFailureListener {
                                        println(it.localizedMessage)
                                    }
                                }
                            }
                        }
                    }

                    if (auth.currentUser!!.uid!=soru.userUid){
                        val uuidBildirim=UUID.randomUUID().toString()
                        val bildirimMap= hashMapOf<String,Any>()
                        bildirimMap.put("soruUid",soruUid)
                        bildirimMap.put("userName","${userName} sorunu yanıtladı")
                        bildirimMap.put("userUid",userUid)
                        bildirimMap.put("docUid",imageUuid)
                        bildirimMap.put("date",date)
                        bildirimMap.put("docBildirim",uuidBildirim)
                        bildirimMap.put("okundu",false)

                        db.collection("Users")
                            .document(soru.userUid)
                            .collection("Bildirimler")
                            .document(uuidBildirim)
                            .set(bildirimMap)
                            .addOnSuccessListener {
                                if (pIdCevap!=soru.pId){
                                    try {
                                        OneSignal.postNotification("{'contents': {'en':'Soruna Cevap Geldi'}, 'include_player_ids': ['" + soru.pId + "']}",null)

                                    }catch (e: JSONException){
                                        e.printStackTrace()
                                    }
                                }
                            }

                    }

                }.addOnFailureListener {
                    Toast.makeText(context,"Cevap Yüklenemedi!",Toast.LENGTH_SHORT).show()
                    loadingLive.value=false
                }
            }
        }
    }

    fun oneSignalListener(context:Context){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.setNotificationOpenedHandler {
            val intent= Intent(context, MainActivity::class.java)
            notificationInfo=true
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

    fun menuOption(context: Context,view:View,activity:Activity,farkGun:Long?){
            if (auth.currentUser!!.uid==soru.userUid || auth.currentUser!!.uid== ADMIN_UID){
                val popup= PopupMenu(context,view)
                popup.menuInflater.inflate(R.menu.menu_delete,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_delete){
                        val reference=storage.reference.child("images").child("${soru.docRef}.jpg")
                        reference.delete().addOnSuccessListener {
                            db.collection("Sorular").document(soru.docRef).delete().addOnSuccessListener {
                                Toast.makeText(context,"Soru Silindi", Toast.LENGTH_SHORT).show()
                                activity.finish()
                            }.addOnFailureListener {
                                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                        true
                    }else if (it.itemId==R.id.menu_ustecikar){
                        if (farkGun!!.toInt()<2){
                            Toast.makeText(context,"En az 2 gün geçmesi gerekli", Toast.LENGTH_SHORT).show()
                        }else {

                            if (soru.dogruCevap == true) {
                                Toast.makeText(
                                    context,
                                    "Çözülmüş bir soruyu üste taşıyamazsın",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                db.collection("Sorular").document(soru.docRef).update("date",Timestamp.now()).addOnSuccessListener {
                                    Toast.makeText(context,"Soru üste taşındı!",Toast.LENGTH_SHORT).show()
                                    getSoruData(soru.docRef)
                                }.addOnFailureListener {
                                    Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        true
                    }else{
                        false
                    }
                }
            }else{
                val popup= PopupMenu(context,view)
                popup.menuInflater.inflate(R.menu.menu_block,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_block){
                        val alert= AlertDialog.Builder(context)
                        alert.setTitle("Soruyu Şikayet Et")
                        alert.setMessage("Emin misin?")
                        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                            Toast.makeText(context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
                        }
                        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->

                            val soruUid= soru.docRef
                            val userName= soru.userDisplayName
                            val userUid= soru.userUid
                            val imageUuid= soru.docRef
                            val date= Timestamp.now()
                            val uuidBildirim= UUID.randomUUID().toString()
                            val bildirimMap= hashMapOf<String,Any>()
                            bildirimMap.put("soruUid",soruUid!!)
                            bildirimMap.put("userName","${userName} sorusu şikayet edildi")
                            bildirimMap.put("userUid",userUid!!)
                            bildirimMap.put("docUid",imageUuid!!)
                            bildirimMap.put("date",date)
                            bildirimMap.put("docBildirim",uuidBildirim)
                            bildirimMap.put("okundu",false)
                            db.collection("Users").document(ADMIN_UID).collection("Bildirimler").document(uuidBildirim).set(bildirimMap)
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Şikayet edildi", Toast.LENGTH_SHORT).show()
                                    db.collection("Admin").document(ADMIN_UID)
                                        .get().addOnSuccessListener {
                                            if (it!=null){
                                                val pidAdmin=it.get("pId") as String
                                                try {
                                                    OneSignal.postNotification("{'contents': {'en':'Soruya Şikayet Geldi'}, 'include_player_ids': ['" + pidAdmin + "']}",null)

                                                }catch (e: JSONException){
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                }.addOnFailureListener {
                                    Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                        }
                        alert.show()
                        true
                    }else{
                        false
                    }
                }
            }

    }

    fun onItemDelete(context: Context,docUuid:String,dogruCevap:Boolean){
        val reference=storage.reference.child("cevaplar").child(soru.docRef).child("${docUuid}.jpg")
        reference.delete().addOnSuccessListener {
            val refDb=db.collection("Cevap").document(soru.docRef).collection("Cevaplar").document(docUuid)
            refDb.delete().addOnSuccessListener {
                if (dogruCevap==true){
                    val referenceSoru=db.collection("Sorular").document(soru.docRef)
                    referenceSoru.update("dogruCevap",false).addOnSuccessListener {}
                    referenceSoru.update("dogruCevapString","null").addOnSuccessListener {}
                    referenceSoru.update("dogruCevapImage","null").addOnSuccessListener {}
                }
                Toast.makeText(context,"Cevabın silindi",Toast.LENGTH_SHORT).show()


            }.addOnFailureListener {
                Toast.makeText(context,"Cevap silinirken sorun oluştu", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            val refDb=db.collection("Cevap").document(soru.docRef).collection("Cevaplar").document(docUuid)
            refDb.delete().addOnSuccessListener {
                if (dogruCevap==true){
                    val referenceOdev=db.collection("Sorular").document(soru.docRef)
                    referenceOdev.update("dogruCevap",false).addOnSuccessListener {}
                    referenceOdev.update("dogruCevapString","null").addOnSuccessListener {}
                    referenceOdev.update("dogruCevapImage","null").addOnSuccessListener {}
                }
                Toast.makeText(context,"Cevabın silindi",Toast.LENGTH_SHORT).show()




            }.addOnFailureListener {
                Toast.makeText(context,"Cevap silinirken sorun oluştu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sikayetItem(context: Context,position:Int){
        val alert=AlertDialog.Builder(context)
        alert.setTitle("Cevabı Şikayet Et")
        alert.setMessage("Emin misin?")
        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(context,"Vazgeçildi",Toast.LENGTH_SHORT).show()
        }
        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
            val soruUid= cevapArrayList[position].soruUid
            val userName=cevapArrayList[position].userName
            val userUid=cevapArrayList[position].userUid
            val imageUuid=cevapArrayList[position].docUuid
            val date=Timestamp.now()
            val uuidBildirim= UUID.randomUUID().toString()
            val bildirimMap= hashMapOf<String,Any>()
            bildirimMap.put("soruUid",soruUid)
            bildirimMap.put("userName","${userName} cevabı şikayet edildi")
            bildirimMap.put("userUid",userUid)
            bildirimMap.put("docUid",imageUuid)
            bildirimMap.put("date",date)
            bildirimMap.put("docBildirim",uuidBildirim)
            bildirimMap.put("okundu",false)
            db.collection("Users").document("P2dukbTHNMcdyP3FjDOsd7fR6PT2").collection("Bildirimler").document(uuidBildirim).set(bildirimMap)
                .addOnSuccessListener {
                    Toast.makeText(context,"Şikayet edildi",Toast.LENGTH_SHORT).show()
                    db.collection("Admin").document("P2dukbTHNMcdyP3FjDOsd7fR6PT2")
                        .get().addOnSuccessListener {
                            if (it!=null){
                                val pidAdmin=it.get("pId") as String
                                try {
                                    OneSignal.postNotification("{'contents': {'en':'Cevaba Şikayet Geldi'}, 'include_player_ids': ['" + pidAdmin + "']}",null)

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

    fun guncelleItem(context: Context,position:Int){
        if (cevapArrayList[position].dogruCevap!=true){
            val reference=db.collection("Cevap").document(cevapArrayList[position].soruUid).collection("Cevaplar").document(cevapArrayList[position].docUuid)
            reference.update("dogruCevap",true).addOnSuccessListener {

                val soruUid= soru.docRef
                val userName=auth.currentUser!!.displayName.toString()
                val userUid=auth.currentUser!!.uid
                val date= Timestamp.now()
                val pIdCevap=OneSignal.getDeviceState()?.userId
                val imageUuid=userUid

                val uuidBildirim= UUID.randomUUID().toString()
                val bildirimMap= hashMapOf<String,Any>()
                bildirimMap.put("soruUid",soruUid)
                bildirimMap.put("userName","${userName} cevabını doğru olarak işaretledi")
                bildirimMap.put("userUid",userUid)
                bildirimMap.put("docUid",imageUuid)
                bildirimMap.put("date",date)
                bildirimMap.put("docBildirim",uuidBildirim)
                bildirimMap.put("okundu",false)
                db.collection("Users").document(cevapArrayList[position].userUid).collection("Bildirimler").document(uuidBildirim).set(bildirimMap).addOnSuccessListener {

                    try {
                        OneSignal.postNotification("{'contents': {'en':'Cevabın doğru cevap olarak işaretlendi'}, 'include_player_ids': ['" + cevapArrayList[position].pIdCevap + "']}",null)
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }

                    val referenceOdev=db.collection("Sorular").document(cevapArrayList[position].soruUid)
                    referenceOdev.update("dogruCevap",true).addOnSuccessListener {}
                    referenceOdev.update("dogruCevapString",cevapArrayList[position].selectedAciklama).addOnSuccessListener {}
                    referenceOdev.update("dogruCevapImage",cevapArrayList[position].downloadUrl).addOnSuccessListener {}

                    val uuidTakipBildirim=UUID.randomUUID().toString()
                    val takipBildirimMap= hashMapOf<String,Any>()
                    takipBildirimMap.put("soruUid",soruUid)
                    takipBildirimMap.put("userName","Takip ettiğin soru çözüldü")
                    takipBildirimMap.put("userUid",userUid)
                    takipBildirimMap.put("docUid",imageUuid)
                    takipBildirimMap.put("date",date)
                    takipBildirimMap.put("docBildirim",uuidTakipBildirim)
                    bildirimMap.put("okundu",false)

                    if (takipciList.isNotEmpty()){
                        if (takipciList.size>0){
                            for (takipci in takipciList){
                                if (takipci.takipciUid!= soru.userUid && takipci.takipciUid!=cevapArrayList[position].userUid){
                                    db.collection("Users").document(takipci.takipciUid).collection("Bildirimler").document(uuidTakipBildirim).set(takipBildirimMap).addOnSuccessListener {
                                        try {
                                            OneSignal.postNotification("{'contents': {'en':'Takip ettiğin soru çözüldü'}, 'include_player_ids': ['" + takipci.takipciPid + "']}",null)
                                        }catch (e: JSONException){
                                            e.printStackTrace()
                                        }
                                    }.addOnFailureListener {
                                        println(it.localizedMessage)
                                    }
                                }
                            }
                        }

                    }


                }.addOnFailureListener {
                    Toast.makeText(context,"Başarısız",Toast.LENGTH_SHORT).show()
                }


            }.addOnFailureListener {
                Toast.makeText(context,"Başarısız",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context,"Cevap doğru olarak seçilmiş",Toast.LENGTH_SHORT).show()
        }
    }

    fun onItemClick(context: Context,position:Int,layoutInflater: LayoutInflater){
        val bindingAlert= CevapDetailAlertBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        bindingAlert.alertImage.downloadUrlPicassoSoru(cevapArrayList[position].downloadUrl)
        val alert=AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }



    override fun onCleared() {
        super.onCleared()
        referenceRegistration.remove()
    }
}