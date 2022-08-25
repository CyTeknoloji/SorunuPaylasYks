package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.view.SingActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel:ViewModel() {
    private val auth=Firebase.auth

    fun signOut(context: Context,activity:Activity){
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
        val shareIntent=Intent(Intent.ACTION_SEND)
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody)
        context.startActivity(Intent.createChooser(shareIntent,"Paylaş"))
    }


    override fun onCleared() {
        super.onCleared()
    }
}