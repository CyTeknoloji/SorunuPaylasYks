package com.caneryildirim.sorunupaylasyks.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.util.Ders

class DersFeedViewModel:ViewModel() {

    val dersListLive=MutableLiveData<List<Ders>>()
    val dersList=ArrayList<Ders>()

    fun dersler(){
        val tumDersler=Ders(R.drawable.imagederstum,"Tüm Dersler")
        val turkce=Ders(R.drawable.imagedersturkce,"Türkçe")
        val matematik=Ders(R.drawable.imagedersmatematik,"Matematik")
        val fizik=Ders(R.drawable.imagedersfizik,"Fizik")
        val kimya=Ders(R.drawable.imagederskimya,"Kimya")
        val biyoloji=Ders(R.drawable.imagedersbiyoloji,"Biyoloji")
        val din=Ders(R.drawable.imagedersdin,"Din Kültürü ve Ahlak Bilgisi")
        val cografya=Ders(R.drawable.imagederscografya,"Coğrafya")
        val tarih=Ders(R.drawable.imagederstarih,"Tarih")
        val yabanciDil=Ders(R.drawable.imagedersingilizce,"Yabancı Dil")
        val felsefe=Ders(R.drawable.imagedersvatandaslik,"Felsefe")

        dersList.clear()
        dersList.add(tumDersler)
        dersList.add(turkce)
        dersList.add(matematik)
        dersList.add(fizik)
        dersList.add(kimya)
        dersList.add(biyoloji)
        dersList.add(din)
        dersList.add(cografya)
        dersList.add(tarih)
        dersList.add(yabanciDil)
        dersList.add(felsefe)

        dersListLive.value=dersList

    }

    override fun onCleared() {
        super.onCleared()
    }
}