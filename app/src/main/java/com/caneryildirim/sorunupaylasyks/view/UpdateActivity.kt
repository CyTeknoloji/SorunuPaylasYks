package com.caneryildirim.sorunupaylasyks.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.sorunupaylasyks.databinding.ActivityUpdateBinding
import com.caneryildirim.sorunupaylasyks.viewModel.UpdateActivityViewModel

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdateBinding
    private lateinit var viewModel: UpdateActivityViewModel


    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        viewModel=ViewModelProvider(this).get(UpdateActivityViewModel::class.java)
    }

    fun yukle(view: View){

    }

    fun cameraSelected(view: View){

    }

    fun gallerySelected(view: View){

    }

}