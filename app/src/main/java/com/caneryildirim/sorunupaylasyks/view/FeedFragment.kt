package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerSoruAdapter
import com.caneryildirim.sorunupaylasyks.databinding.FragmentFeedBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.notificationInfo
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.viewModel.FeedFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FeedFragment : Fragment(),RecyclerSoruAdapter.Delete {
    private var _binding:FragmentFeedBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:FeedFragmentViewModel
    private var adapterSoru:RecyclerSoruAdapter?=null
    private var soruList:ArrayList<Soru>?= arrayListOf()
    private var dersName:String?=null
    private lateinit var auth:FirebaseAuth
    private var infoGetData:Boolean=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding=FragmentFeedBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onPause() {
        super.onPause()
        infoGetData=false
    }

    override fun onResume() {
        super.onResume()
        if (!infoGetData){
            viewModel.refreshData(this.requireContext())
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(FeedFragmentViewModel::class.java)

        //DetailActivity den bildirim bilgisi gönderildiği zaman çalışacak
        notificationInfo?.let {
            if (it){
                val action=FeedFragmentDirections.actionFeedFragmentToNotificationFragment()
                Navigation.findNavController(view).navigate(action)
            }
        }

        viewModel.controlNotification(requireContext(),requireView())
        Singleton.whereFragment ="FeedFragment"

        viewModel.refreshData(this.requireContext())
        infoGetData=true

        auth=Firebase.auth
        val user=auth.currentUser

        binding.recyclerFeedFragment.layoutManager=LinearLayoutManager(this.requireContext())
        adapterSoru= RecyclerSoruAdapter(soruList!!,user!!.uid,this)

        binding.recyclerFeedFragment.adapter=adapterSoru

        createMenu()
        observeLiveData()
        swipeOn()


    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                val itemNotification=menu.findItem(R.id.deleteNotification)
                itemNotification.isVisible=false

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.paylas->{
                        viewModel.paylas(requireContext())
                        return true
                    }
                    R.id.puanver->{
                        viewModel.puanVer(requireContext())
                        return true
                    }
                    R.id.signout->{
                        viewModel.signOut(requireContext(),requireActivity())
                        return true
                    }
                    R.id.tumdersler_menu->{
                        dersName=null
                        viewModel.refreshData(requireContext())
                        return true
                    }
                    R.id.turkce_menu->{
                        dersName="Türkçe"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.matematik_menu->{
                        dersName="Matematik"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.geometri_menu->{
                        dersName="Geometri"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.fizik_menu->{
                        dersName="Fizik"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.kimya_menu->{
                        dersName="Kimya"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.biyoloji_menu->{
                        dersName="Biyoloji"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.edebiyat_menu->{
                        dersName="Edebiyat"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.din_menu->{
                        dersName="Din Kültürü ve Ahlak Bilgisi"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.cografya_menu->{
                        dersName="Coğrafya"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.tarih_menu->{
                        dersName="Tarih"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.yabancidil_menu->{
                        dersName="Yabancı Dil"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    R.id.felsefe_menu->{
                        dersName="Felsefe"
                        viewModel.selectedData(requireContext(),dersName!!)
                        return true
                    }
                    else->{
                        return false
                    }
                }

            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    } 

    private fun swipeOn() {
        //swipe ayarları
        binding.swipeFeedFragment.setColorSchemeColors(resources.getColor(R.color.purple_500))
        binding.swipeFeedFragment.setOnRefreshListener {
            binding.progressBarFeedFragment.visibility=View.GONE
            binding.textViewErrorFeedFragment.visibility=View.GONE
            binding.recyclerFeedFragment.visibility=View.GONE
            binding.swipeFeedFragment.isRefreshing=false
            if (dersName==null){
                viewModel.refreshData(this.requireContext())
            }else{
                viewModel.selectedData(this.requireContext(),dersName!!)
            }


        }
    }

    private fun observeLiveData() {
        viewModel.loadingFeedLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarFeedFragment.visibility=View.VISIBLE
                binding.textViewErrorFeedFragment.visibility=View.GONE
                binding.recyclerFeedFragment.visibility=View.GONE
            }else{
                binding.progressBarFeedFragment.visibility=View.GONE
                binding.textViewErrorFeedFragment.visibility=View.GONE
                binding.recyclerFeedFragment.visibility=View.VISIBLE
            }
        })

        viewModel.errorFeedLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarFeedFragment.visibility=View.GONE
                binding.textViewErrorFeedFragment.visibility=View.VISIBLE
                binding.recyclerFeedFragment.visibility=View.GONE
            }else{
                binding.progressBarFeedFragment.visibility=View.GONE
                binding.textViewErrorFeedFragment.visibility=View.GONE
                binding.recyclerFeedFragment.visibility=View.VISIBLE
            }
        })

        viewModel.soruListLive.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.recyclerFeedFragment.visibility=View.VISIBLE
                adapterSoru?.updateSoruList(it)

            }

        })

    }

    override fun onItemClick(docRef:String) {
        viewModel.onItemClick(this.requireContext(),docRef,dersName)
    }

    override fun sikayetItem(soruUid: String, userDisplayName: String, userUid: String) {
        viewModel.sikayetEt(this.requireContext(),soruUid,userDisplayName,userUid)
    }

    override fun usteCikar(docRef: String) {
        viewModel.usteCikar(this.requireContext(),docRef,dersName)
    }


}

