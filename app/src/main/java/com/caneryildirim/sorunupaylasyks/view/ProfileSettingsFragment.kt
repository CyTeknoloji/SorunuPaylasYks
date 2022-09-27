package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.FragmentGirisBinding
import com.caneryildirim.sorunupaylasyks.databinding.FragmentProfileSettingsBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoProfil
import com.caneryildirim.sorunupaylasyks.viewModel.ProfileSettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileSettingsFragment : Fragment() {
    private var _binding: FragmentProfileSettingsBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:ProfileSettingsViewModel
    private var userNames:ArrayList<String>?= arrayListOf()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentProfileSettingsBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(ProfileSettingsViewModel::class.java)
        observeLiveData()

        viewModel.getDataUsername()
        createMenu()

        Singleton.whereFragment ="ProfileSettingsFragment"
        //TODO("profili silerken database deki Usernames deki verileri de sil")
        //TODO("ayrıca profil fotoğrafını da sil")
        val auth=Firebase.auth

        binding.userProfileImageview.downloadUrlPicassoProfil(auth.currentUser!!.photoUrl.toString())
        binding.emailProfileText.text=auth.currentUser!!.email
        binding.editTextUserName.setText(auth.currentUser!!.displayName)

        binding.editTextUserName.setOnClickListener {
            binding.editTextUserName.isCursorVisible=true
        }

        binding.buttonProfile.setOnClickListener {
            if (userNames!!.contains(binding.editTextUserName.text.toString())){
                binding.editTextUserName.setError("Girdiğiniz kullanıcı adı mevcut!")
                binding.editTextUserName.requestFocus()
            }else{
                viewModel.updateUserName(requireContext(),binding.editTextUserName.text.toString())
            }
        }

        binding.buttonPassReset.setOnClickListener {
            viewModel.passReset(requireContext())
        }

        binding.deleteAuth.setOnClickListener {
            viewModel.deleteAuth(requireContext())
        }

    }

    private fun observeLiveData() {
        viewModel.userNames.observe(viewLifecycleOwner, Observer {
            userNames?.addAll(it)
        })
        viewModel.loadingUserName.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.buttonProfile.isEnabled=false
                binding.deleteAuth.isEnabled=false
                binding.buttonPassReset.isEnabled=false
                binding.progressBarProfile.isVisible=true
            }else{
                binding.buttonProfile.isEnabled=true
                binding.deleteAuth.isEnabled=true
                binding.buttonPassReset.isEnabled=true
                binding.progressBarProfile.isVisible=false
            }
        })
    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                val itemFilter=menu.findItem(R.id.filterMenu)
                itemFilter.isVisible=false

                val itemNotification=menu.findItem(R.id.deleteNotification)
                itemNotification.isVisible=false

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId==R.id.paylas){
                    viewModel.paylas(requireContext())
                }else if (menuItem.itemId==R.id.puanver){
                    viewModel.puanVer(requireContext())
                }else if (menuItem.itemId==R.id.signout){
                    viewModel.signOut(requireContext(),requireActivity())
                }
                return true
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


}