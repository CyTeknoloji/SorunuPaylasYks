package com.caneryildirim.sorunupaylasyks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.sorunupaylasyks.databinding.DersRowBinding
import com.caneryildirim.sorunupaylasyks.util.Ders

class RecyclerDersAdapter(val dersList:ArrayList<Ders>):RecyclerView.Adapter<RecyclerDersAdapter.DersHolder>() {

    class DersHolder(val binding:DersRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DersHolder {
        val binding=DersRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DersHolder(binding)
    }

    override fun onBindViewHolder(holder: DersHolder, position: Int) {
        holder.binding.imageViewDersRow.setImageResource(dersList[position].dersImage)
        holder.binding.textViewDersRow.text=dersList[position].dersName
    }

    override fun getItemCount(): Int {
        return dersList.size
    }
}