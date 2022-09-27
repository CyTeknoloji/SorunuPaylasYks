package com.caneryildirim.sorunupaylasyks.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.ActivityMainBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.whereFragment
import com.caneryildirim.sorunupaylasyks.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        viewModel=ViewModelProvider(this).get(MainViewModel::class.java)

        adMobView()
        uiSettings()
        observeLiveData()

        viewModel.oneSignal(this)
        viewModel.adminPid()
        viewModel.getNotNumber()

    }

    private fun adMobView() {
        if (Singleton.mInterstitialAd !=null){
            Singleton.mInterstitialAd?.show(this)
            Singleton.mInterstitialAd =null
        }
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
        val appBarConfiguration= AppBarConfiguration(setOf(R.id.feedFragment,R.id.notificationFragment,R.id.sorularimFragment,R.id.profileSettingsFragment,R.id.uploadFragment))
        setupActionBarWithNavController(navHostFragment.navController,appBarConfiguration)


    }

    private fun observeLiveData(){
        viewModel.notificationNumber.observe(this, Observer {
            //Bildirim Badge kodları
            val navBar  = findViewById<BottomNavigationView>(R.id.bottomNavMain)
            if (it!=0){
                navBar.getOrCreateBadge(R.id.notificationFragment)
            }else{
                navBar.removeBadge(R.id.notificationFragment)
            }
        })
    }


    override fun onBackPressed() {
        if (whereFragment!="UploadFragment"){
            super.onBackPressed()
        }

    }


}