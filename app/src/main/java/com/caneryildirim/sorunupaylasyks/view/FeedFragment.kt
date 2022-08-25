package com.caneryildirim.sorunupaylasyks.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerSoruAdapter
import com.caneryildirim.sorunupaylasyks.databinding.FragmentFeedBinding
import com.caneryildirim.sorunupaylasyks.util.Ders
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.viewModel.FeedFragmentViewModel


class FeedFragment : Fragment(),RecyclerSoruAdapter.Delete {
    private var _binding:FragmentFeedBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:FeedFragmentViewModel
    private var adapterSoru:RecyclerSoruAdapter?=null
    private var soruList:ArrayList<Soru>?= arrayListOf()
    private var dersName:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding=FragmentFeedBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        dersName="null"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(FeedFragmentViewModel::class.java)

        //Toolbardaki İconların Görünürlük ayarı
        val activity=activity as MainActivity
        activity.toolbarIconVisibility(true,true,true)

        dersName="null"
        arguments?.let {
            dersName=FeedFragmentArgs.fromBundle(it).dersName
            if (dersName!="null"){
                viewModel.refreshData(dersName!!)
            }else{
                viewModel.refreshData("Tüm Dersler")
            }

        }


        binding.recyclerFeedFragment.layoutManager=LinearLayoutManager(this.requireContext())
        adapterSoru= RecyclerSoruAdapter(soruList!!,"47384374837",this)
        binding.recyclerFeedFragment.adapter=adapterSoru

        observeLiveData()


        binding.swipeFeedFragment.setColorSchemeColors(resources.getColor(R.color.purple_500))
        binding.swipeFeedFragment.setOnRefreshListener {
            binding.progressBarFeedFragment.visibility=View.GONE
            binding.textViewErrorFeedFragment.visibility=View.GONE
            binding.recyclerFeedFragment.visibility=View.GONE
            binding.swipeFeedFragment.isRefreshing=false
            if (dersName!="null"){
                viewModel.refreshData(dersName!!)
            }else{
                viewModel.refreshData("Tüm Dersler")
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

    override fun onItemClick(position: Int) {

    }

    override fun sikayetItem(position: Int) {

    }

    override fun usteCikar(position: Int) {

    }


}