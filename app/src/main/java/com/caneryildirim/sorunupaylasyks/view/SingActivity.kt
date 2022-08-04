package com.caneryildirim.sorunupaylasyks.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.caneryildirim.sorunupaylasyks.databinding.ActivitySingBinding


class SingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySingBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

    }

}