package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerNotificationAdapter
import com.caneryildirim.sorunupaylasyks.databinding.FragmentNotificationBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.util.Notification
import com.caneryildirim.sorunupaylasyks.viewModel.NotificationViewModel


class NotificationFragment : Fragment(),RecyclerNotificationAdapter.Click {
    private var _binding:FragmentNotificationBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:NotificationViewModel

    private var adapterNotification: RecyclerNotificationAdapter?=null
    private var notificationList:ArrayList<Notification>?= arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding=FragmentNotificationBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(NotificationViewModel::class.java)

        createMenu()

        Singleton.whereFragment ="NotificationFragment"
        viewModel.getNotData(this.requireContext())

        binding.recyclerNotification.layoutManager=LinearLayoutManager(requireContext())
        adapterNotification=RecyclerNotificationAdapter(notificationList!!,this)
        binding.recyclerNotification.adapter=adapterNotification

        observeLiveData()




    }

    private fun observeLiveData() {
        viewModel.errorNotification.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarNotification.visibility=View.GONE
                binding.errorTextViewNotification.visibility=View.VISIBLE
                binding.recyclerNotification.visibility=View.GONE
            }else{
                binding.progressBarNotification.visibility=View.GONE
                binding.errorTextViewNotification.visibility=View.GONE
                binding.recyclerNotification.visibility=View.VISIBLE
            }
        })

        viewModel.loadingNotification.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarNotification.visibility=View.VISIBLE
                binding.errorTextViewNotification.visibility=View.GONE
                binding.recyclerNotification.visibility=View.GONE
            }else{
                binding.progressBarNotification.visibility=View.GONE
                binding.errorTextViewNotification.visibility=View.GONE
                binding.recyclerNotification.visibility=View.VISIBLE
            }
        })

        viewModel.notificationLiveList.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.recyclerNotification.visibility=View.VISIBLE
                adapterNotification?.updateSoruList(it)
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

                val itemMenu=menu.findItem(R.id.alt_menu)
                itemMenu.isVisible=false

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId==R.id.okunanlari_sil){
                    viewModel.deleteOkunanlar(requireContext())
                    return true
                }else if (menuItem.itemId==R.id.tumunu_sil){
                    viewModel.deleteTumu(requireContext())
                    return true
                }else{
                    return false
                }

            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



    override fun delete(position: Int) {
        viewModel.delete(position,requireContext())
    }


}