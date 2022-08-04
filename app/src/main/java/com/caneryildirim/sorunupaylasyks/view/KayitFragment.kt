package com.caneryildirim.sorunupaylasyks.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.FragmentKayitBinding
import com.caneryildirim.sorunupaylasyks.viewModel.GirisViewModel
import com.caneryildirim.sorunupaylasyks.viewModel.KayitViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class KayitFragment : Fragment() {
    private var _binding:FragmentKayitBinding?=null
    private val binding  get() = _binding!!

    private lateinit var userDisplayName:String
    private lateinit var userEmail:String
    private lateinit var userPassword:String
    private lateinit var userRepeatPassword:String

    private lateinit var viewModel: KayitViewModel
    private var userNames:ArrayList<String>?= arrayListOf()

    private lateinit var activityResult: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentKayitBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(KayitViewModel::class.java)
        viewModel.getDataUsername()
        viewModel.initGoogle(this.requireActivity())
        registerLauncher()
        observeLiveData()

        binding.kayitOlButton.setOnClickListener {
            userDisplayName=binding.userNameText.text.toString().trim()
            userEmail=binding.kayitEmailText.text.toString().trim()
            userPassword=binding.kayitPasswordText.text.toString().trim()
            userRepeatPassword=binding.kayitRepeatPasswordText.text.toString().trim()

            if (userDisplayName.isEmpty()){
                binding.userNameText.setError("Kullanıcı adınızı giriniz!")
                binding.userNameText.requestFocus()
            }else if (userEmail.isEmpty()){
                binding.kayitEmailText.setError("Eposta adresi giriniz!")
                binding.kayitEmailText.requestFocus()
            }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                binding.kayitEmailText.setError("Geçersiz Eposta formatı!")
                binding.kayitEmailText.requestFocus()
            }else if (userPassword.isEmpty()){
                binding.kayitPasswordText.setError("Şifrenizi giriniz!")
                binding.kayitPasswordText.requestFocus()
            }else if (userPassword.length<6){
                binding.kayitPasswordText.setError("Şifreniz 6 karakterden küçük olamaz")
                binding.kayitPasswordText.requestFocus()
            }else if (userPassword!=userRepeatPassword){
                binding.kayitRepeatPasswordText.setError("Şifreleriniz uyuşmuyor")
                binding.kayitRepeatPasswordText.requestFocus()
            }else if(userNames!!.contains(userDisplayName)){
                binding.userNameText.setError("Girdiğiniz kullanıcı adı mevcut!")
                binding.userNameText.requestFocus()
            }else{
                viewModel.kayitOl(this.requireContext(),it,userEmail,userPassword,userDisplayName)
            }
        }

        binding.kayitGoogle.setOnClickListener {
            val intent=viewModel.kayitGoogle()
            activityResult.launch(intent)
        }

    }



    private fun observeLiveData(){
        viewModel.loginLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarKayit.visibility=View.VISIBLE
                binding.kayitGoogle.isEnabled=false
                binding.kayitOlButton.isEnabled=false
            }else{
                binding.progressBarKayit.visibility=View.GONE
                binding.kayitGoogle.isEnabled=true
                binding.kayitOlButton.isEnabled=true
            }
        })

        viewModel.userNames.observe(viewLifecycleOwner, Observer {
            userNames?.addAll(it)

        })
    }

    private fun registerLauncher(){
        activityResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d(KayitViewModel.TAG, "firebaseAuthWithGoogle:" + account.id)
                        viewModel.firebaseAuthWithGoogle(account.idToken!!,this.requireContext(),this.requireActivity())

                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(KayitViewModel.TAG, "Google sign in failed", e)

                    }

            }
        }
    }







}