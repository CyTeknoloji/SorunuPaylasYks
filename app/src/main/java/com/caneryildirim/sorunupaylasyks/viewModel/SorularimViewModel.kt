package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.view.FeedFragmentDirections
import com.caneryildirim.sorunupaylasyks.view.SingActivity
import com.caneryildirim.sorunupaylasyks.view.SorularimFragmentDirections
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.onesignal.OneSignal

class SorularimViewModel:ViewModel() {
    val loadingFeedLive= MutableLiveData<Boolean>(false)
    val errorFeedLive= MutableLiveData<Boolean>(false)
    val soruListLive= MutableLiveData<List<Soru>>()
    val auth=Firebase.auth
    val db= Firebase.firestore
    val storage= Firebase.storage
    val soruArrayList=ArrayList<Soru>()

    fun controlNotification(context: Context,view: View){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(Singleton.ONESIGNAL_APP_ID)
        OneSignal.setNotificationOpenedHandler {
            val action= SorularimFragmentDirections.actionSorularimFragmentToNotificationFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun refreshData(context:Context){
        loadingFeedLive.value=true
        val reference=db.collection("Sorular").whereEqualTo("userUid",auth.currentUser!!.uid)
            .orderBy("date", Query.Direction.DESCENDING)
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
            Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
            loadingFeedLive.value=false
            errorFeedLive.value=true
        }
    }

    fun onItemClick(context: Context,docRef:String){
        val reference=storage.reference.child("images").child("${docRef}.jpg")
        reference.delete().addOnSuccessListener {
            db.collection("Sorular").document(docRef).delete().addOnSuccessListener {
                Toast.makeText(context,"Soru Silindi",Toast.LENGTH_SHORT).show()
                refreshData(context)

            }.addOnFailureListener {
                Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_SHORT).show()
        }
    }

    fun usteCikar(context: Context,docRef:String){
        db.collection("Sorular").document(docRef).update("date",Timestamp.now()).addOnSuccessListener {
            Toast.makeText(context,"Soru üste taşındı!",Toast.LENGTH_SHORT).show()
            refreshData(context)
        }.addOnFailureListener {
            Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_SHORT).show()
        }
    }

    fun signOut(context: Context,activity: Activity){
        auth.signOut()
        val intent= Intent(context, SingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        activity.finish()
    }

    fun puanVer(context: Context){
        val packageName = "com.caneryildirim.sorunupaylasyks"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageName}"))
        context.startActivity(intent)
    }

    fun paylas(context: Context){
        val shareBody="Sorunu Paylaş uygulamasını Play Store'dan yükle : https://play.google.com/store/apps/details?id=com.caneryildirim.sorunupaylasyks"
        val shareIntent= Intent(Intent.ACTION_SEND)
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody)
        context.startActivity(Intent.createChooser(shareIntent,"Paylaş"))
    }


    override fun onCleared() {
        super.onCleared()
    }
}