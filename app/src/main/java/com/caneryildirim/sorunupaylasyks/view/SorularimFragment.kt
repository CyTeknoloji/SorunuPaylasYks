package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerSoruAdapter
import com.caneryildirim.sorunupaylasyks.databinding.FragmentSorularimBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.viewModel.SorularimViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SorularimFragment : Fragment(),RecyclerSoruAdapter.Delete {
    private var _binding: FragmentSorularimBinding?=null
    private val binding get() = _binding!!

    private lateinit var viewModel: SorularimViewModel
    private var adapterSoru: RecyclerSoruAdapter?=null
    private var soruList:ArrayList<Soru>?= arrayListOf()
    private lateinit var auth: FirebaseAuth

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

        Singleton.whereFragment ="SorularimFragment"

        viewModel.refreshData(this.requireContext())

        auth= Firebase.auth
        val user=auth.currentUser

        createMenu()

        binding.recyclerSorularimFragment.layoutManager= LinearLayoutManager(this.requireContext())
        adapterSoru= RecyclerSoruAdapter(soruList!!,user!!.uid,this)
        binding.recyclerSorularimFragment.adapter=adapterSoru

        observerLiveData()



    }

    private fun observerLiveData() {
        viewModel.loadingFeedLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarSorularimFragment.visibility=View.VISIBLE
                binding.textViewErrorSorularimFragment.visibility=View.GONE
                binding.recyclerSorularimFragment.visibility=View.GONE
            }else{
                binding.progressBarSorularimFragment.visibility=View.GONE
                binding.textViewErrorSorularimFragment.visibility=View.GONE
                binding.recyclerSorularimFragment.visibility=View.VISIBLE
            }
        })

        viewModel.errorFeedLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarSorularimFragment.visibility=View.GONE
                binding.textViewErrorSorularimFragment.visibility=View.VISIBLE
                binding.recyclerSorularimFragment.visibility=View.GONE
            }else{
                binding.progressBarSorularimFragment.visibility=View.GONE
                binding.textViewErrorSorularimFragment.visibility=View.GONE
                binding.recyclerSorularimFragment.visibility=View.VISIBLE
            }
        })

        viewModel.soruListLive.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()){
                    binding.textViewNoData.visibility=View.VISIBLE
                    binding.recyclerSorularimFragment.visibility=View.GONE
                }else{
                    binding.textViewNoData.visibility=View.GONE
                    binding.recyclerSorularimFragment.visibility=View.VISIBLE
                    adapterSoru?.updateSoruList(it)
                }


            }

        })
    }

    override fun onItemClick(docRef: String) {
        viewModel.onItemClick(this.requireContext(),docRef)
    }

    override fun sikayetItem(soruUid: String, userDisplayName: String, userUid: String) {

    }

    override fun usteCikar(docRef: String) {
        viewModel.usteCikar(this.requireContext(),docRef)
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
                if (menuItem.itemId==R.id.filterMenu){
                    Toast.makeText(requireContext(),"başarılı", Toast.LENGTH_SHORT).show()
                }else if (menuItem.itemId==R.id.alt_menu){

                }else if (menuItem.itemId==R.id.paylas){
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