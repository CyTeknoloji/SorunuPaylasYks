package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerDersAdapter
import com.caneryildirim.sorunupaylasyks.databinding.FragmentDersFeedBinding
import com.caneryildirim.sorunupaylasyks.databinding.FragmentGirisBinding
import com.caneryildirim.sorunupaylasyks.util.Ders
import com.caneryildirim.sorunupaylasyks.viewModel.DersFeedViewModel


class DersFeedFragment : Fragment() {
    private var _binding: FragmentDersFeedBinding?=null
    private val binding get() = _binding!!

    private lateinit var viewModel:DersFeedViewModel
    private var adapterDers:RecyclerDersAdapter?=null
    private var dersList:ArrayList<Ders>?= arrayListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentDersFeedBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(DersFeedViewModel::class.java)
        viewModel.dersler()

        //Toolbardaki İconların Görünürlük ayarı
        //val activity=activity as MainActivity
        //activity.toolbarIconVisibility(false,true,true)


        binding.recyclerDers.layoutManager=LinearLayoutManager(this.requireContext())
        adapterDers= RecyclerDersAdapter(dersList!!)
        binding.recyclerDers.adapter=adapterDers

        observerLiveData()



    }

    private fun observerLiveData() {
        viewModel.dersListLive.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapterDers?.addDersList(it)
            }


        })
    }


}