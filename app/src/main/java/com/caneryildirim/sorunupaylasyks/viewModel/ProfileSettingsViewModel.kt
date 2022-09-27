package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.view.SingActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileSettingsViewModel:ViewModel() {
    val auth= Firebase.auth
    val db=Firebase.firestore
    val userNameList=ArrayList<String>()
    var userNames=MutableLiveData<List<String>>()
    var loadingUserName=MutableLiveData<Boolean>(false)

    fun signOut(context: Context, activity: Activity){
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

    fun getDataUsername(){
        db.collection("Usernames").get().addOnSuccessListener {

            if (it!=null){
                if (!it.isEmpty){
                    val documents=it.documents
                    userNameList.clear()
                    for (document in documents){
                        val username=document.get("username") as String
                        userNameList.add(username)
                    }
                    userNames.value=userNameList
                }
            }
        }.addOnFailureListener {
            println(it.localizedMessage)
        }
    }

    fun updateUserName(context: Context,userName:String){
        loadingUserName.value=true
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = userName
        }
        user!!.updateProfile(profileUpdates).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,"Profil güncellendi",Toast.LENGTH_SHORT).show()
                loadingUserName.value=false
                db.collection("Sorular").whereEqualTo("userUid",auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        if (it!=null){
                            if (!it.isEmpty){
                                val documents=it.documents
                                for (document in documents){
                                    val docRef=document.get("docRef") as String
                                    val referenceNew=db.collection("Sorular").document(docRef)
                                    referenceNew.update("userDisplayName",userName).addOnSuccessListener {}
                                }
                            }
                        }
                    }.addOnFailureListener {
                        println(it.localizedMessage)
                    }

            }
        }.addOnFailureListener {
            Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_SHORT).show()
            loadingUserName.value=false
        }

    }

    fun passReset(context: Context){
        loadingUserName.value=true
        val email=auth.currentUser!!.email.toString()
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(context,"Eposta adresinize şifre sıfırlama maili gönderildi.",Toast.LENGTH_SHORT).show()
            loadingUserName.value=false
        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
            loadingUserName.value=false
        }
    }

    fun deleteAuth(context: Context){
        loadingUserName.value=true
        val alert= AlertDialog.Builder(context)
        alert.setTitle("Hesabı Sil")
        alert.setMessage("Emin misin?")
        alert.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(context,"Vazgeçildi",Toast.LENGTH_SHORT).show()
        }
        alert.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
            auth.currentUser!!.delete().addOnSuccessListener {
                loadingUserName.value=false
                Toast.makeText(context,"Hesabın Silindi",Toast.LENGTH_SHORT).show()
                val intentFinish=Intent(context,SingActivity::class.java)
                context.startActivity(intentFinish)

            }.addOnFailureListener {
                Toast.makeText(context,"Hesabı silmek için çıkış yapıp tekrar deneyin!",Toast.LENGTH_SHORT).show()
                loadingUserName.value=false
            }
        }
        alert.show()
    }

    override fun onCleared() {
        super.onCleared()
    }
}