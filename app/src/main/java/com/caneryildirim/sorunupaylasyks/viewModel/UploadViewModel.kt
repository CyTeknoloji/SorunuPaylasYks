package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.onesignal.OneSignal
import java.util.*
import kotlin.collections.ArrayList

class UploadViewModel:ViewModel() {
    val auth=Firebase.auth
    val storage=Firebase.storage
    val firestore=Firebase.firestore
    val uploadLoading= MutableLiveData<Boolean>(false)

    fun yukle(view: View,context:Context,activity: Activity, selectedAciklama:String, selectedDers:String, selectedKonu:String, selectedImage:Uri?){
        //TODO("yükleme tamamlanana kadar bottomNavigation butonlarına basılamamalı")
        uploadLoading.value=true
        val uuid=UUID.randomUUID().toString()
        val imageName="${uuid}.jpg"
        val userUid=auth.currentUser!!.uid
        val userEmail=auth.currentUser!!.email
        val userDisplayName=auth.currentUser!!.displayName
        val userPhotoUrl=auth.currentUser!!.photoUrl.toString()
        val reference=storage.reference.child("images").child(imageName)
        val docRef=uuid
        val pId= OneSignal.getDeviceState()?.userId
            reference.putFile(selectedImage!!).addOnSuccessListener {
                val uploadImageReference=storage.reference.child("images").child(imageName)
                uploadImageReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    val postMap= hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("selectedAciklama",selectedAciklama)
                    postMap.put("selectedDers",selectedDers)
                    postMap.put("selectedKonu",selectedKonu)
                    postMap.put("userUid",userUid)
                    postMap.put("email",userEmail!!)
                    postMap.put("userDisplayName",userDisplayName!!)
                    postMap.put("userPhotoUrl",userPhotoUrl)
                    postMap.put("date",com.google.firebase.Timestamp.now())
                    postMap.put("docRef",docRef)
                    postMap.put("dogruCevap",false)
                    postMap.put("dogruCevapString","null")
                    postMap.put("dogruCevapImage","null")
                    pId?.let { postMap.put("pId",pId) }

                    firestore.collection("Sorular").document(docRef).set(postMap).addOnSuccessListener {
                        uploadLoading.value=false
                        Toast.makeText(context,"Sorunuz yüklendi",Toast.LENGTH_SHORT).show()
                        activity.onBackPressed()

                        //val action=UploadFragmentDirections.actionUploadFragmentToFeedFragment()
                        //Navigation.findNavController(view).navigate(action)
                    }.addOnFailureListener {
                        //firebase yüklenemedi
                        uploadLoading.value=false
                        Toast.makeText(context,"Sorunuz bir hatadan dolayı yüklenemedi. Lütfen tekrar deneyiniz",Toast.LENGTH_SHORT).show()


                    }
                }.addOnFailureListener {
                    //downloadUrl yüklenemedi
                    uploadLoading.value=false
                    Toast.makeText(context,"Sorunuz bir hatadan dolayı yüklenemedi. Lütfen tekrar deneyiniz",Toast.LENGTH_SHORT).show()


                }
            }.addOnFailureListener {
                //storage yüklemenedi
                uploadLoading.value=false
                Toast.makeText(context,"Sorunuz bir hatadan dolayı yüklenemedi. Lütfen tekrar deneyiniz",Toast.LENGTH_SHORT).show()
            }

    }

    fun dersler() :List<String> {
        val ders=ArrayList<String>()
        ders.add("Dersi Seçin")
        ders.add("Türkçe")
        ders.add("Matematik")
        ders.add("Geometri")
        ders.add("Fizik")
        ders.add("Kimya")
        ders.add("Biyoloji")
        ders.add("Edebidyat")
        ders.add("Din Kültürü ve Ahlak Bilgisi")
        ders.add("Coğrafya")
        ders.add("Tarih")
        ders.add("Yabancı Dil")
        ders.add("Felsefe")
        return ders
    }

    fun konular():List<List<String>>{
        val konuBos=ArrayList<String>()
        konuBos.add("Ders seçmeniz gerekiyor")

        val konuTurkce=ArrayList<String>()
        konuTurkce.add("Konu seçin")
        konuTurkce.add("Sözcükte Anlam")
        konuTurkce.add("Söz Yorumu")
        konuTurkce.add("Deyim ve Atasözü")
        konuTurkce.add("Cümlede Anlam")
        konuTurkce.add("Paragraf")
        konuTurkce.add("Ses Bilgisi")
        konuTurkce.add("Yazım Kuralları")
        konuTurkce.add("Noktalama İşaretleri")
        konuTurkce.add("Sözcükte Yapı/Ekler")
        konuTurkce.add("Sözcük Türleri")
        konuTurkce.add("Fiiller")
        konuTurkce.add("Sözcük Grupları")
        konuTurkce.add("Cümlenin Ögeleri")
        konuTurkce.add("Cümle Türleri")
        konuTurkce.add("Anlatım Bozukluğu")


        val konuMatematik=ArrayList<String>()
        konuMatematik.add("Konu seçin")
        konuMatematik.add("Temel Kavramlar")
        konuMatematik.add("Sayı Basamakları")
        konuMatematik.add("Bölme ve Bölünebilme")
        konuMatematik.add("EBOB – EKOK")
        konuMatematik.add("Rasyonel Sayılar")
        konuMatematik.add("Basit Eşitsizlikler")
        konuMatematik.add("Mutlak Değer")
        konuMatematik.add("Üslü Sayılar")
        konuMatematik.add("Köklü Sayılar")
        konuMatematik.add("Çarpanlara Ayırma")
        konuMatematik.add("Oran Orantı")
        konuMatematik.add("Denklem Çözme")
        konuMatematik.add("Problemler")
        konuMatematik.add("Kümeler – Kartezyen Çarpım")
        konuMatematik.add("Mantık")
        konuMatematik.add("Fonskiyonlar")
        konuMatematik.add("Polinomlar")
        konuMatematik.add("2.Dereceden Denklemler")
        konuMatematik.add("Permütasyon ve Kombinasyon")
        konuMatematik.add("Olasılık")
        konuMatematik.add("Veri – İstatistik")

        val konuGeometri=ArrayList<String>()
        konuGeometri.add("Konu seçin")
        konuGeometri.add("Temel Kavramlar")
        konuGeometri.add("Doğruda Açılar")
        konuGeometri.add("Üçgende Açılar")
        konuGeometri.add("Özel Üçgenler")
        konuGeometri.add("Açıortay")
        konuGeometri.add("Kenarortay")
        konuGeometri.add("Eşlik ve Benzerlik")
        konuGeometri.add("Üçgende Alan")
        konuGeometri.add("Üçgende Benzerlik")
        konuGeometri.add("Açı Kenar Bağıntıları")
        konuGeometri.add("Çokgenler")
        konuGeometri.add("Özel Dörtgenler")
        konuGeometri.add("Çember ve Daire")
        konuGeometri.add("Analitik Geometri")
        konuGeometri.add("Katı Cisimler")
        konuGeometri.add("Çemberin Analitiği")

        val konuFizik=ArrayList<String>()
        konuFizik.add("Konu seçin")
        konuFizik.add("Fizik Bilimine Giriş")
        konuFizik.add("Madde ve Özellikleri")
        konuFizik.add("Sıvıların Kaldırma Kuvveti")
        konuFizik.add("Basınç")
        konuFizik.add("Isı, Sıcaklık ve Genleşme")
        konuFizik.add("Hareket ve Kuvvet")
        konuFizik.add("Dinamik")
        konuFizik.add("İş, Güç ve Enerji")
        konuFizik.add("Elektrik")
        konuFizik.add("Manyetizma")
        konuFizik.add("Dalgalar")
        konuFizik.add("Optik")

        val konuKimya=ArrayList<String>()
        konuKimya.add("Konu seçin")
        konuKimya.add("Kimya Bilimi")
        konuKimya.add("Atom ve Periyodik Sistem")
        konuKimya.add("Kimyasal Türler Arası Etkileşimler")
        konuKimya.add("Maddenin Halleri")
        konuKimya.add("Doğa ve Kimya")
        konuKimya.add("Kimyanın Temel Kanunları")
        konuKimya.add("Kimyasal Hesaplamalar")
        konuKimya.add("Karışımlar")
        konuKimya.add("Asit, Baz ve Tuz")
        konuKimya.add("Kimya Her Yerde")

        val konuBiyoloji=ArrayList<String>()
        konuBiyoloji.add("Konu seçin")
        konuBiyoloji.add("Canlıların Ortak Özellikleri")
        konuBiyoloji.add("Canlıların Temel Bileşenleri")
        konuBiyoloji.add("Hücre ve Organelleri")
        konuBiyoloji.add("Hücre Zarından Madde Geçişi")
        konuBiyoloji.add("Canlıların Sınıflandırılması")
        konuBiyoloji.add("Mitoz ve Eşeysiz Üreme")
        konuBiyoloji.add("Mayoz ve Eşeyli Üreme")
        konuBiyoloji.add("Kalıtım")
        konuBiyoloji.add("Ekosistem Ekolojisi")
        konuBiyoloji.add("Güncel Çevre Sorunları")

        val konuEdebiyat=ArrayList<String>()
        konuEdebiyat.add("Konu seçin")
        konuEdebiyat.add("Anlam Bilgisi")
        konuEdebiyat.add("Dil Bilgisi")
        konuEdebiyat.add("Güzel Sanatlar ve Edebiyat")
        konuEdebiyat.add("Metinlerin Sınıflandırılması")
        konuEdebiyat.add("Şiir Bilgisi")
        konuEdebiyat.add("Edebi Sanatlar")
        konuEdebiyat.add("Türk Edebiyatı Dönemleri")
        konuEdebiyat.add("İslamiyet Öncesi Türk Edebiyatı")
        konuEdebiyat.add("Halk Edebiyatı")
        konuEdebiyat.add("Divan Edebiyatı")
        konuEdebiyat.add("Tanzimat Edebiyatı")
        konuEdebiyat.add("Servet-i Fünun Edebiyatı")
        konuEdebiyat.add("Fecr-i Ati Edebiyatı")
        konuEdebiyat.add("Milli Edebiyat")
        konuEdebiyat.add("Cumhuriyet Dönemi Edebiyatı")
        konuEdebiyat.add("Edebiyat Akımları")
        konuEdebiyat.add("Dünya Edebiyatı")

        val konuDin=ArrayList<String>()
        konuDin.add("Konu seçin")
        konuDin.add("Bilgi ve İnanç")
        konuDin.add("Din ve İslam")
        konuDin.add("İslam ve İbadet")
        konuDin.add("Ahlak ve Değerler")
        konuDin.add("Allah İnsan İlişkisi")
        konuDin.add("Hz. Muhammed (S.A.V.)")
        konuDin.add("Vahiy ve Akıl")
        konuDin.add("İslam Düşüncesinde Yorumlar, Mezhepler")
        konuDin.add("Din, Kültür ve Medeniyet")
        konuDin.add("İslam ve Bilim, Estetik, Barış")
        konuDin.add("Yaşayan Dinler")

        val konuCografya=ArrayList<String>()
        konuCografya.add("Konu seçin")
        konuCografya.add("Doğa ve İnsan")
        konuCografya.add("Dünya’nın Şekli ve Hareketleri")
        konuCografya.add("Coğrafi Konum")
        konuCografya.add("Harita Bilgisi")
        konuCografya.add("Atmosfer ve Sıcaklık")
        konuCografya.add("İklimler")
        konuCografya.add("Basınç ve Rüzgarlar")
        konuCografya.add("Nem, Yağış ve Buharlaşma")
        konuCografya.add("İç Kuvvetler / Dış Kuvvetler")
        konuCografya.add("Su – Toprak ve Bitkiler")
        konuCografya.add("Nüfus")
        konuCografya.add("Göç")
        konuCografya.add("Yerleşme")
        konuCografya.add("Türkiye’nin Yer Şekilleri")
        konuCografya.add("Ekonomik Faaliyetler")
        konuCografya.add("Bölgeler")
        konuCografya.add("Uluslararası Ulaşım Hatları")
        konuCografya.add("Çevre ve Toplum")
        konuCografya.add("Doğal Afetler")

        val konuTarih=ArrayList<String>()
        konuTarih.add("Konu seçin")
        konuTarih.add("Tarih ve Zaman")
        konuTarih.add("İnsanlığın İlk Dönemleri")
        konuTarih.add("Orta Çağ’da Dünya")
        konuTarih.add("İlk ve Orta Çağlarda Türk Dünyası")
        konuTarih.add("İslam Medeniyetinin Doğuşu")
        konuTarih.add("İlk Türk İslam Devletleri")
        konuTarih.add("Selçuklu Türkiyesi")
        konuTarih.add("Beylikten Devlete Osmanlı Siyaseti")
        konuTarih.add("Devletleşme Sürecinde Savaşçılar ve Askerler")
        konuTarih.add("Beylikten Devlete Osmanlı Medeniyeti")
        konuTarih.add("Dünya Gücü Osmanlı")
        konuTarih.add("Sultan ve Osmanlı Merkez Teşkilatı")
        konuTarih.add("Klasik Çağda Osmanlı Toplum Düzeni")
        konuTarih.add("Değişen Dünya Dengeleri Karşısında Osmanlı Siyaseti")
        konuTarih.add("Değişim Çağında Avrupa ve Osmanlı")
        konuTarih.add("Uluslararası İlişkilerde Denge Stratejisi (1774-1914)")
        konuTarih.add("Devrimler Çağında Değişen Devlet-Toplum İlişkileri")
        konuTarih.add("Sermaye ve Emek")
        konuTarih.add("XIX. ve XX. Yüzyılda Değişen Gündelik Hayat")
        konuTarih.add("XX. Yüzyıl Başlarında Osmanlı Devleti ve Dünya")
        konuTarih.add("Milli Mücadele")
        konuTarih.add("Atatürkçülük ve Türk İnkılabı")

        val konuYabanciDil=ArrayList<String>()
        konuYabanciDil.add("Konu seçin")
        konuYabanciDil.add("Kelime Bilgisi")
        konuYabanciDil.add("Dil Bilgisi")
        konuYabanciDil.add("Cloze Test")
        konuYabanciDil.add("Cümleyi Tamamlama")
        konuYabanciDil.add("İngilizce Cümlenin Türkçe Karşılığını Bulma")
        konuYabanciDil.add("Türkçe Cümlenin İngilizce Karşılığını Bulma")
        konuYabanciDil.add("Paragraf Anlamca Yakın Cümleyi Bulma")
        konuYabanciDil.add("Paragrafta Anlam Bütünlüğünü Sağlayacak Cümleyi Bulma")
        konuYabanciDil.add("Verilen Durumda Söylenecek İfadeyi Bulma")
        konuYabanciDil.add("Diyalog Tamamlama")
        konuYabanciDil.add("Anlam Bütünlüğünü Bozan Cümleyi Bulma")

        val konuFelsefe=ArrayList<String>()
        konuFelsefe.add("Konu seçin")
        konuFelsefe.add("Felsefe’nin Konusu")
        konuFelsefe.add("Bilgi Felsefesi")
        konuFelsefe.add("Varlık Felsefesi")
        konuFelsefe.add("Ahlak Felsefesi")
        konuFelsefe.add("Sanat Felsefesi")
        konuFelsefe.add("Din Felsefesi")
        konuFelsefe.add("Siyaset Felsefesi")
        konuFelsefe.add("Bilim Felsefesi")
        konuFelsefe.add("İlk Çağ Felsefesi")
        konuFelsefe.add("2. Yüzyıl ve 15. Yüzyıl Felsefeleri")
        konuFelsefe.add("15. Yüzyıl ve 17. Yüzyıl Felsefeleri")
        konuFelsefe.add("18. Yüzyıl ve 19. Yüzyıl Felsefeleri")
        konuFelsefe.add("20. Yüzyıl Felsefesi")

        val dersKonularList=ArrayList<List<String>>()
        dersKonularList.add(konuBos)
        dersKonularList.add(konuTurkce)
        dersKonularList.add(konuMatematik)
        dersKonularList.add(konuGeometri)
        dersKonularList.add(konuFizik)
        dersKonularList.add(konuKimya)
        dersKonularList.add(konuBiyoloji)
        dersKonularList.add(konuEdebiyat)
        dersKonularList.add(konuDin)
        dersKonularList.add(konuCografya)
        dersKonularList.add(konuTarih)
        dersKonularList.add(konuYabanciDil)
        dersKonularList.add(konuFelsefe)

        return dersKonularList

    }


    override fun onCleared() {
        super.onCleared()
    }
}