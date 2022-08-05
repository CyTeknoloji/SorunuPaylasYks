package com.caneryildirim.sorunupaylasyks.view

import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.ActivityDersBinding
import com.caneryildirim.sorunupaylasyks.databinding.NavigationBaslikBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.mInterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class DersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDersBinding
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDersBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        if (mInterstitialAd!=null){
            mInterstitialAd?.show(this)
            mInterstitialAd=null
        }

        auth= Firebase.auth

        uiSettings()





    }



    private fun uiSettings() {
        //Navigation Drawer ile Component i bağlama
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostDers) as NavHostFragment
        NavigationUI.setupWithNavController(binding.navigationViewDers, navHostFragment.navController)

        //Toolbar
        binding.toolbarDersActivity.title = "SORUNU PAYLAŞ"
        binding.toolbarDersActivity.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarDersActivity)
        val toggle = ActionBarDrawerToggle(
            this, binding.navigationDersDrawer, binding.toolbarDersActivity,
            0, 0
        )
        binding.navigationDersDrawer.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = getColor(R.color.white)
        toggle.syncState()


        //Navigation Drawer Başlık
        val bindingNav = NavigationBaslikBinding.inflate(layoutInflater)
        bindingNav.textViewNavMenu.text = "${auth.currentUser!!.displayName}"
        if (auth.currentUser!!.photoUrl != null) {
            Picasso.get().load(auth.currentUser!!.photoUrl).into(bindingNav.imageViewNavMenu)
        } else {
            bindingNav.imageViewNavMenu.setImageResource(R.drawable.usernullprofileimage)
        }
        binding.navigationViewDers.addHeaderView(bindingNav.root)


        //Navigation Drawer Menü seçim işlemi **** packageName isimlerini kontrol et

        binding.navigationViewDers.setNavigationItemSelectedListener {
            if (it.itemId == R.id.puanver) {
                val packageName = "com.caneryildirim.sorunupaylasyks"
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageName}"))
                startActivity(intent)
            }else if (it.itemId==R.id.uygulamayipaylas){
                val shareBody="Sorunu Paylaş uygulamasını Play Store'dan yükle : https://play.google.com/store/apps/details?id=com.caneryildirim.sorunupaylasyks"
                val shareIntent=Intent(Intent.ACTION_SEND)
                shareIntent.type="text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody)
                startActivity(Intent.createChooser(shareIntent,"Paylaş"))
            }else if (it.itemId== R.id.signout){
                if (auth.currentUser!=null){
                    auth.signOut()
                    val intent=Intent(this, SingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }

            }else if (it.itemId==R.id.profileSettingsFragment){
                val action=DersFeedFragmentDirections.actionDersFeedFragmentToProfileSettingsFragment()
                Navigation.findNavController(it.actionView).navigate(action)       //actionview null dönderiyor
            }else if (it.itemId==R.id.sorularimFragment){
                val action=DersFeedFragmentDirections.actionDersFeedFragmentToSorularimFragment()
                Navigation.findNavController(it.actionView).navigate(action)
            }

            true
        }




    }



    override fun onBackPressed() {
        if (binding.navigationDersDrawer.isDrawerOpen(GravityCompat.START)){
            binding.navigationDersDrawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }



}