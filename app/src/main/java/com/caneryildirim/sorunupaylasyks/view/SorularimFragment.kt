package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.FragmentGirisBinding
import com.caneryildirim.sorunupaylasyks.databinding.FragmentSorularimBinding
import com.caneryildirim.sorunupaylasyks.viewModel.DersFeedViewModel
import com.caneryildirim.sorunupaylasyks.viewModel.SorularimViewModel


class SorularimFragment : Fragment() {
    private var _binding: FragmentSorularimBinding?=null
    private val binding get() = _binding!!

    private lateinit var viewModel: SorularimViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentSorularimBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(SorularimViewModel::class.java)
        observerLiveData()
    }

    private fun observerLiveData() {

    }


}