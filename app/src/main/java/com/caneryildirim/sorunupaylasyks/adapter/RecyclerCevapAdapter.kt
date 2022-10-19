package com.caneryildirim.sorunupaylasyks.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.databinding.CevapDetailRowBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton.ADMIN_UID
import com.caneryildirim.sorunupaylasyks.util.Cevap
import com.caneryildirim.sorunupaylasyks.view.ProfileWatchActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerCevapAdapter(val delete: Delete, val cevapArrayList: ArrayList<Cevap>, val userUid:String):RecyclerView.Adapter<RecyclerCevapAdapter.CevapHolder>() {

    interface Delete{
        fun onItemDelete(position: Int,docUuid:String,dogruCevap:Boolean)
        fun sikayetItem(position: Int)
        fun duzenleItem(position: Int)
        fun guncelleItem(position: Int)
        fun onItemClick(position: Int)
    }

    class CevapHolder(val binding:CevapDetailRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CevapHolder {
        val binding=CevapDetailRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CevapHolder(binding)
    }

    override fun onBindViewHolder(holder: CevapHolder, position: Int) {
        if (userUid== cevapArrayList[position].userUidSoru && cevapArrayList[position].dogruCevap==false && cevapArrayList[position].userUid!=userUid){
            if (cevapArrayList[position].yanlisCevap==false || cevapArrayList[position].yanlisCevap==null){
                holder.binding.dogruCevapmiSorText.visibility=View.VISIBLE
                holder.binding.evetDogruText.visibility=View.VISIBLE
                holder.binding.hayirYanlisText.visibility= View.VISIBLE
            }else{
                holder.binding.dogruCevapmiSorText.visibility=View.GONE
                holder.binding.evetDogruText.visibility=View.GONE
                holder.binding.hayirYanlisText.visibility=View.GONE
            }
        }else{
            holder.binding.dogruCevapmiSorText.visibility=View.GONE
            holder.binding.evetDogruText.visibility=View.GONE
            holder.binding.hayirYanlisText.visibility=View.GONE
        }

        holder.binding.evetDogruText.setOnClickListener {
            holder.binding.dogruCevap.visibility=View.VISIBLE
            holder.binding.dogruCevapText.visibility=View.VISIBLE
            holder.binding.dogruCevapmiSorText.visibility=View.GONE
            holder.binding.evetDogruText.visibility=View.GONE
            holder.binding.hayirYanlisText.visibility=View.GONE
            delete.guncelleItem(position)
        }
        holder.binding.hayirYanlisText.setOnClickListener {
            holder.binding.dogruCevapmiSorText.visibility=View.GONE
            holder.binding.evetDogruText.visibility=View.GONE
            holder.binding.hayirYanlisText.visibility=View.GONE
            val db= Firebase.firestore
            val referenceCevap=db.collection("Cevap").document(cevapArrayList[position].soruUid).collection("Cevaplar").document(cevapArrayList[position].docUuid)
            referenceCevap.update("yanlisCevap",true).addOnSuccessListener {}
        }

        if (cevapArrayList[position].dogruCevap){
            holder.binding.dogruCevap.visibility=View.VISIBLE
            holder.binding.dogruCevapText.visibility=View.VISIBLE
        }else{
            holder.binding.dogruCevap.visibility=View.GONE
            holder.binding.dogruCevapText.visibility=View.GONE
        }

        dateSettings(position,holder)
        holder.binding.textAciklamaDetailCevap.text=cevapArrayList[position].selectedAciklama
        holder.binding.textUserNameDetailCevap.text=cevapArrayList[position].userName

        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context, ProfileWatchActivity::class.java)
            intent.putExtra("userUid",cevapArrayList[position].userUid)
            intent.putExtra("userName",cevapArrayList[position].userName)
            intent.putExtra("userPhotoUrl",cevapArrayList[position].userPhotoUrl)
            holder.itemView.context.startActivity(intent)
        }

        if (cevapArrayList[position].downloadUrl=="null"){
            holder.binding.imageCevapDetailCevap.visibility=View.GONE
            holder.binding.imageCevapDetailCevap.isClickable=false
        }else{
            holder.binding.imageCevapDetailCevap.visibility=View.VISIBLE
            holder.binding.imageCevapDetailCevap.isClickable=true
            holder.binding.imageCevapDetailCevap.setImageResource(R.drawable.cevapmedia)
        }

        holder.binding.menuRecyclerCevap.setOnClickListener {
            if (userUid==cevapArrayList[position].userUid || userUid==ADMIN_UID){
                val popup= PopupMenu(holder.itemView.context,it)
                popup.menuInflater.inflate(R.menu.menu_cevap_delete,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_cevap_delete){
                        delete.onItemDelete(position,cevapArrayList[position].docUuid,cevapArrayList[position].dogruCevap)
                        true
                    }else{
                        false
                    }
                }
            }else if(userUid==cevapArrayList[position].userUidSoru){
                val popup= PopupMenu(holder.itemView.context,it)
                popup.menuInflater.inflate(R.menu.menu_sorusahibi,popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    if (it.itemId==R.id.menu_blocksahip){
                        delete.sikayetItem(position)
                        true
                    }else if (it.itemId==R.id.menu_dogrucevapisaret){
                        holder.binding.dogruCevap.visibility=View.VISIBLE
                        holder.binding.dogruCevapText.visibility=View.VISIBLE
                        holder.binding.dogruCevapmiSorText.visibility=View.GONE
                        holder.binding.evetDogruText.visibility=View.GONE
                        holder.binding.hayirYanlisText.visibility=View.GONE
                        delete.guncelleItem(position)
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
        holder.binding.imageCevapDetailCevap.setOnClickListener {
            delete.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return cevapArrayList.size
    }

    fun updateCevapList(newCevapList: List<Cevap>) {
        cevapArrayList.clear()
        cevapArrayList.addAll(newCevapList)
        notifyDataSetChanged()
    }

    fun getTimeDate(date:Long):String{
        val netDate= Date(date)
        val sdf= SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        return sdf.format(netDate)

    }

    fun dateSettings(position:Int,holder: RecyclerCevapAdapter.CevapHolder){
        val dateFirst=cevapArrayList[position].date.toDate().time
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
        val farkHafta=farkGun/7
        val farkAy=farkHafta/4
        val farkYil=farkAy/12

        if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()==0){
            holder.binding.tarihCevapText.text="Şimdi"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()<61){
            holder.binding.tarihCevapText.text="${farkDakika} Dk Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()<25){
            holder.binding.tarihCevapText.text="${farkSaat} Saat Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 &&farkGun.toInt()<8){
            holder.binding.tarihCevapText.text="${farkGun} Gün Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()<5){
            holder.binding.tarihCevapText.text="${farkHafta} Hafta Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()<13){
            holder.binding.tarihCevapText.text="${farkAy} Ay Önce"
        }else if (farkYil.toInt()!=0){
            holder.binding.tarihCevapText.text="${farkYil} Yıl Önce"
        }


    }

}