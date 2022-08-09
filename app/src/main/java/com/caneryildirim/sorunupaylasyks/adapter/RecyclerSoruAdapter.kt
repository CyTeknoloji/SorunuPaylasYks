package com.caneryildirim.sorunupaylasyks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.SoruRowBinding
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerSoruAdapter(val soruList:ArrayList<Soru>,val userUid:String,val delete:Delete):RecyclerView.Adapter<RecyclerSoruAdapter.SoruHolder>(),Filterable {
    var filterSoruList = ArrayList<Soru>()
    var farkGunYedek:Long?=null

    init {
        filterSoruList = soruList
    }

    interface Delete{
        fun onItemClick(position: Int)
        fun sikayetItem(position: Int)
        fun usteCikar(position: Int)
    }

    class SoruHolder(val binding: SoruRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoruHolder {
        val binding = SoruRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoruHolder(binding)
    }

    override fun onBindViewHolder(holder: SoruHolder, position: Int) {
        //clickler kaldı

        if (filterSoruList[position].dogruCevap!=null){
            if (filterSoruList[position].dogruCevap!!){
                holder.binding.textViewOdevCozum.visibility= View.VISIBLE
            }else{
                holder.binding.textViewOdevCozum.visibility=View.INVISIBLE
            }
        }

        holder.binding.dersOdevText.text="Ders:${filterSoruList[position].selectedDers}"
        holder.binding.konuOdevText.text="Konu:${filterSoruList[position].selectedKonu}"
        holder.binding.userNameOdevText.text=filterSoruList[position].userDisplayName

        dateSettings(position,holder)

        holder.binding.odevFeedImageview.setImageResource(R.drawable.whiteimage)
        Picasso.get().load(filterSoruList[position].downloadUrl).placeholder(R.drawable.whiteimage).into(holder.binding.odevFeedImageview)


        if (filterSoruList[position].userPhotoUrl=="null"){
            holder.binding.userProfileOdevImageview.setImageResource(R.drawable.personfeed)
        }else{
            Picasso.get().load(filterSoruList[position].userPhotoUrl).into(holder.binding.userProfileOdevImageview)
        }

        holder.binding.menuRecycler.setOnClickListener {
            if (userUid==filterSoruList[position].userUid || userUid=="P2dukbTHNMcdyP3FjDOsd7fR6PT2"){

                val popup= PopupMenu(holder.itemView.context,it)
                popup.menuInflater.inflate(R.menu.menu_delete,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_delete){
                        delete.onItemClick(position)
                        true
                    }else if (it.itemId==R.id.menu_ustecikar){
                        if (farkGunYedek?.toInt()!! <2){
                            Toast.makeText(holder.itemView.context,"En az 2 gün geçmesi gerekli",Toast.LENGTH_SHORT).show()
                        }else{
                            delete.usteCikar(position)
                        }
                        true
                    }else{
                        false
                    }
                }

            }else{
                val popup=PopupMenu(holder.itemView.context,it)
                popup.menuInflater.inflate(R.menu.menu_block,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_block){
                        delete.sikayetItem(position)
                        true
                    }else{
                        false
                    }

                }
            }

        }



    }

    override fun getItemCount(): Int {
        return filterSoruList.size
    }

    fun updateSoruList(newSoruList: List<Soru>) {
        soruList.clear()
        soruList.addAll(newSoruList)
        notifyDataSetChanged()
    }



    fun getTimeDate(date:Long):String{
        val netDate=Date(date)
        val sdf= SimpleDateFormat("dd/MM/yy HH:mm:ss",Locale.getDefault())
        return sdf.format(netDate)

    }

    fun dateSettings(position:Int,holder:SoruHolder){
        val dateFirst=filterSoruList[position].date.toDate().time
        val now= Timestamp.now().toDate().time
        val nowTime=getTimeDate(now)
        val firstTime=getTimeDate(dateFirst)
        val format=SimpleDateFormat("dd/MM/yy HH:mm:ss")
        val firstDate=format.parse(firstTime)
        val nowDate= format.parse(nowTime)
        val diff=nowDate.time-firstDate.time

        val farkDakika=diff/(60*1000)
        val farkSaat=farkDakika/60
        val farkGun=farkSaat/24
        farkGunYedek=farkGun
        val farkHafta=farkGun/7
        val farkAy=farkHafta/4
        val farkYil=farkAy/12

        if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()==0){
            holder.binding.tarihOdevText.text="Şimdi"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()<61){
            holder.binding.tarihOdevText.text="${farkDakika} Dk Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()<25){
            holder.binding.tarihOdevText.text="${farkSaat} Saat Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 &&farkGun.toInt()<8){
            holder.binding.tarihOdevText.text="${farkGun} Gün Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()<5){
            holder.binding.tarihOdevText.text="${farkHafta} Hafta Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()<13){
            holder.binding.tarihOdevText.text="${farkAy} Ay Önce"
        }else if (farkYil.toInt()!=0){
            holder.binding.tarihOdevText.text="${farkYil} Yıl Önce"
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterSoruList = soruList
                } else {
                    val resultList = ArrayList<Soru>()
                    for (row in soruList) {
                        if (row.selectedKonu.lowercase(Locale.getDefault()).contains(
                                charSearch.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || row.selectedDers.lowercase(Locale.getDefault()).contains(
                                charSearch.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterSoruList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterSoruList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterSoruList = results?.values as ArrayList<Soru>
                notifyDataSetChanged()
            }
        }


    }
}