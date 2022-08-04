package com.caneryildirim.sorunupaylasyks.viewModel

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.caneryildirim.sorunupaylasyks.view.DersActivity
import com.caneryildirim.sorunupaylasyks.view.GirisFragmentDirections

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GirisViewModel:ViewModel() {
    var auth= Firebase.auth
    var db=Firebase.firestore
    val loginLoading=MutableLiveData<Boolean>(false)
    val finishActivity=MutableLiveData<Boolean>(false)


    fun currentUser(context:Context){
        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent= Intent(context, DersActivity::class.java)
            context.startActivity(intent)
            finishActivity.value=true
        }
    }

    fun giris(email:String,password:String,context: Context){
        loginLoading.value=true
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                val user=auth.currentUser
                if (user!!.isEmailVerified){
                    val intent= Intent(context,DersActivity::class.java)
                    context.startActivity(intent)
                    loginLoading.value=false
                    finishActivity.value=true
                }else{
                    Toast.makeText(context,"Aktivasyon kodunuzu onaylayınız",Toast.LENGTH_SHORT).show()
                    loginLoading.value=false
                }


            }

        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            loginLoading.value=false
        }

    }

    fun kayitOl(view: View){
        val action= GirisFragmentDirections.actionGirisFragmentToKayitFragment()
        Navigation.findNavController(view).navigate(action)
    }

    fun lossPass(context: Context,email: String){
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(context,"Eposta adresinize şifre sıfırlama maili gönderildi.",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }



    override fun onCleared() {
        super.onCleared()


    }
}