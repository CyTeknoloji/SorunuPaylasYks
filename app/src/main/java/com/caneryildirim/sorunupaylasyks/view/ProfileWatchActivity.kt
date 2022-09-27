package com.caneryildirim.sorunupaylasyks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerSoruAdapter
import com.caneryildirim.sorunupaylasyks.databinding.ActivityProfileWatchBinding
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoProfil
import com.caneryildirim.sorunupaylasyks.viewModel.ProfileWatchViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileWatchActivity : AppCompatActivity(),RecyclerSoruAdapter.Delete {
    private lateinit var binding:ActivityProfileWatchBinding
    private lateinit var viewModel:ProfileWatchViewModel
    private var adapterSoru:RecyclerSoruAdapter?=null
    private var soruList:ArrayList<Soru>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileWatchBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        viewModel=ViewModelProvider(this).get(ProfileWatchViewModel::class.java)

        binding.toolbarProfileWatch.title="Profil Detayları"
        binding.toolbarProfileWatch.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarProfileWatch)
        binding.toolbarProfileWatch.setNavigationOnClickListener {
            onBackPressed()
        }

        val intent=intent
        val userUid=intent.getStringExtra("userUid")

        observeLiveData()

        viewModel.getData(userUid)

        val auth=Firebase.auth
        val user=auth.currentUser

        binding.recyclerProfileWatch.layoutManager= LinearLayoutManager(this)
        adapterSoru= RecyclerSoruAdapter(soruList!!,user!!.uid,this)
        binding.recyclerProfileWatch.adapter=adapterSoru

    }

    private fun observeLiveData() {
        viewModel.soruListLive.observe(this, Observer {
            it?.let {
                binding.recyclerProfileWatch.visibility=View.VISIBLE
                adapterSoru?.updateSoruList(it)
                binding.userNameProfileWatch.text=it[0].userDisplayName
                binding.userPhotoProfileWatch.downloadUrlPicassoProfil(it[0].userPhotoUrl)
            }
        })

        viewModel.loadingFeedLive.observe(this, Observer {
            if (it){
                binding.progressProfileWatch.visibility= View.VISIBLE
                binding.textErrorProfileWatch.visibility=View.GONE
                binding.recyclerProfileWatch.visibility=View.GONE
            }else{
                binding.progressProfileWatch.visibility= View.GONE
                binding.textErrorProfileWatch.visibility=View.GONE
                binding.recyclerProfileWatch.visibility=View.VISIBLE
            }
        })

        viewModel.errorFeedLive.observe(this, Observer {
            if (it){
                binding.progressProfileWatch.visibility= View.GONE
                binding.textErrorProfileWatch.visibility=View.VISIBLE
                binding.recyclerProfileWatch.visibility=View.GONE
            }else{
                binding.progressProfileWatch.visibility= View.GONE
                binding.textErrorProfileWatch.visibility=View.GONE
                binding.recyclerProfileWatch.visibility=View.VISIBLE
            }
        })
    }

    override fun onItemClick(docRef: String) {

    }

    override fun sikayetItem(soruUid: String, userDisplayName: String, userUid: String) {
        viewModel.sikayetEt(this,soruUid,userDisplayName,userUid)
    }

    override fun usteCikar(docRef: String) {

    }

}