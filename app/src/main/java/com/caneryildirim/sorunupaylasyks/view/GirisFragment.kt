package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.FragmentGirisBinding
import com.caneryildirim.sorunupaylasyks.viewModel.GirisViewModel


class GirisFragment : Fragment() {
    private var _binding:FragmentGirisBinding?=null
    private val binding get() = _binding!!

    private lateinit var userEmail:String
    private lateinit var userPassword:String

    private lateinit var viewModel: GirisViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentGirisBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(GirisViewModel::class.java)
        observeLoading()
        viewModel.currentUser(this.requireContext())

        binding.girisButton.setOnClickListener {

            userEmail=binding.loginEmailText.text.toString().trim()
            userPassword=binding.loginPasswordText.text.toString().trim()

            if (userEmail.isEmpty()){
                binding.loginEmailText.setError("Eposta adresinizi giriniz!")
                binding.loginEmailText.requestFocus()
            }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                binding.loginEmailText.setError("Düzgün formatta Epostanızı giriniz!")
                binding.loginEmailText.requestFocus()
            }else if (userPassword.isEmpty()){
                binding.loginPasswordText.setError("Şifrenizi giriniz!")
                binding.loginPasswordText.requestFocus()
            }else if (userPassword.length<6){
                binding.loginPasswordText.setError("Şifreniz 6 karakterden az olamaz!")
                binding.loginPasswordText.requestFocus()
            }else{
                viewModel.giris(userEmail,userPassword,this.requireContext())
            }


        }

        binding.kayitButton.setOnClickListener {
            viewModel.kayitOl(it)
        }

        binding.lossPassButton.setOnClickListener {
            userEmail=binding.loginEmailText.text.toString().trim()
            if(userEmail.isEmpty()){
                binding.loginEmailText.setError("Eposta adresinizi giriniz!")
                binding.loginEmailText.requestFocus()
            }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                binding.loginEmailText.setError("Geçersiz Eposta formatı!")
                binding.loginEmailText.requestFocus()
            }else{
                viewModel.lossPass(this.requireContext(),userEmail)
            }
        }

    }

    fun observeLoading(){
        viewModel.loginLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarGirisFragment.visibility=View.VISIBLE
                binding.girisButton.isEnabled=false
                binding.kayitButton.isEnabled=false
                binding.lossPassButton.isEnabled=false
            }else{
                binding.progressBarGirisFragment.visibility=View.GONE
                binding.girisButton.isEnabled=true
                binding.kayitButton.isEnabled=true
                binding.lossPassButton.isEnabled=true
            }
        })

        viewModel.finishActivity.observe(viewLifecycleOwner, Observer {
            if (it){
                activity?.finish()
            }
        })


    }




}