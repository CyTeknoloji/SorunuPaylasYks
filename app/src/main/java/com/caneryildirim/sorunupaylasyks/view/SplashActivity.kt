package com.caneryildirim.sorunupaylasyks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.caneryildirim.sorunupaylasyks.databinding.ActivitySplashBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.mInterstitialAd
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()


        // Admob Id= ca-app-pub-8642310051732821/6508290767
        InterstitialAd.load(this,"ca-app-pub-8642310051732821/6508290767", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd=null


            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
            }
        })



        Handler(Looper.getMainLooper()).postDelayed({
            val intent= Intent(this,SingActivity::class.java)
            startActivity(intent)
            finish()
        },4000)


    }

    override fun onBackPressed() {

    }
}