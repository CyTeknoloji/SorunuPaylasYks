package com.caneryildirim.sorunupaylasyks.singleton

import android.view.MenuItem
import com.google.android.gms.ads.interstitial.InterstitialAd

object Singleton {
    var mInterstitialAd: InterstitialAd? = null
    var whereFragment:String?=""
    var notificationInfo:Boolean?=null

    const val ONESIGNAL_APP_ID="114a36aa-d0c3-44ee-8e6e-0370b4714ce3"

    const val ADMIN_UID="6A9F9r4CAhM18qHT1JgiuthRGZm2"


}