package com.caneryildirim.sorunupaylasyks.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.caneryildirim.sorunupaylasyks.R
import com.squareup.picasso.Picasso



fun ImageView.downloadUrlPicassoSoru(url:String){
    Picasso.get().load(url)
        .placeholder(R.drawable.whiteimage)
        .into(this)
}

fun ImageView.downloadUrlPicassoProfil(url: String){
    Picasso.get().load(url)
        .into(this)
}


fun placeHolderProgressBar(context: Context):CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        centerRadius=40f
        strokeWidth=8f
        start()
    }
}