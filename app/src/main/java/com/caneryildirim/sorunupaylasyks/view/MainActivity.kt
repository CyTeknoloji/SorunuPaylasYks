package com.caneryildirim.sorunupaylasyks.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.ActivityMainBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var cartBadgeTextview: TextView?=null
    private lateinit var itemSort:MenuItem
    private lateinit var itemNotification:MenuItem
    private lateinit var itemMenu:MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        viewModel=ViewModelProvider(this).get(MainViewModel::class.java)

        if (Singleton.mInterstitialAd !=null){
            Singleton.mInterstitialAd?.show(this)
            Singleton.mInterstitialAd =null
        }

        uiSettings()

    }


    private fun uiSettings() {
        //bottom nav ile nav component birleştirme
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostMain) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavMain, navHostFragment.navController)

        //Toolbar
        binding.toolbarMain.title = "SORUNU PAYLAŞ"
        binding.toolbarMain.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarMain)

        //Toolbar başlıklarını senkronize etmek için
        val appBarConfiguration= AppBarConfiguration(setOf(R.id.dersFeedFragment,R.id.feedFragment,R.id.sorularimFragment,R.id.profileSettingsFragment,R.id.uploadFragment))
        setupActionBarWithNavController(navHostFragment.navController,appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)

        itemSort = menu.findItem(R.id.search)
        itemNotification=menu.findItem(R.id.notificationMain)
        itemMenu=menu.findItem(R.id.alt_menu)

        //bildirim için eksikleri ayarla

        //Sıralama için eksikleri ayarla
        itemSort.isVisible=false


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.paylas){
            viewModel.paylas(this)
        }else if (item.itemId==R.id.puanver){
            viewModel.puanVer(this)
        }else if (item.itemId==R.id.signout){
            viewModel.signOut(this,this)
        }
        return super.onOptionsItemSelected(item)
    }

    fun toolbarIconVisibility(itemSortValue:Boolean,itemNotValue:Boolean,itemMenuValue:Boolean){
        itemSort.isVisible=itemSortValue
        itemNotification.isVisible=itemNotValue
        itemMenu.isVisible=itemMenuValue
    }



}