package com.caneryildirim.sorunupaylasyks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.ActivityDersBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.mInterstitialAd


class DersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDersBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        if (mInterstitialAd!=null){
            mInterstitialAd?.show(this)
            mInterstitialAd=null
        }

        //val navHostFragment=supportFragmentManager.findFragmentById(R.id.navHostFragmentDers) as NavHostFragment
        //NavigationUI.setupWithNavController(binding.navigationViewDers,navHostFragment.navController)


        //val navHostFragment=supportFragmentManager.findFragmentById(R.id.navHostFragmentDers) as NavHostFragment
        //val inflater = navHostFragment.navController.navInflater
        //val graph = inflater.inflate(R.navigation.navigation_ders)
        //navHostFragment.navController.graph = graph

        binding.toolbarDersActivity.title="SORUNU PAYLAŞ"
        binding.toolbarDersActivity.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarDersActivity)
        val toggle=ActionBarDrawerToggle(this,binding.navigationDersDrawer,binding.toolbarDersActivity,
            0,0)
        binding.navigationDersDrawer.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color=getColor(R.color.white)
        toggle.syncState()








    }

    override fun onBackPressed() {
        if (binding.navigationDersDrawer.isDrawerOpen(GravityCompat.START)){
            binding.navigationDersDrawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

}