package com.caneryildirim.sorunupaylasyks.view

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerCevapAdapter
import com.caneryildirim.sorunupaylasyks.adapter.RecyclerSoruAdapter
import com.caneryildirim.sorunupaylasyks.databinding.ActivityDetailBinding
import com.caneryildirim.sorunupaylasyks.databinding.CevapDetailAlertBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.util.Cevap
import com.caneryildirim.sorunupaylasyks.util.Soru
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoProfil
import com.caneryildirim.sorunupaylasyks.util.downloadUrlPicassoSoru
import com.caneryildirim.sorunupaylasyks.viewModel.DetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(),RecyclerCevapAdapter.Delete {
    private lateinit var binding:ActivityDetailBinding
    private lateinit var viewModel:DetailViewModel

    private lateinit var permiisonLauncher: ActivityResultLauncher<String>
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropActivityGalleryResultLauncher: ActivityResultLauncher<Uri?>

    private var selectedImage:Uri?=null
    lateinit var currentPhotoPath: String
    private var requestFrom: String?=null

    private lateinit var  downloadUrlString:String

    private var adapterCevap:RecyclerCevapAdapter?=null
    private var cevapList:ArrayList<Cevap>?= arrayListOf()
    private var soruUid: String?=null
    private var farkGunForViewModel:Long?=null



    private val cropActivityGalleryContract=object : ActivityResultContract<Uri?, Uri?>(){
        override fun createIntent(context: Context, input: Uri?): Intent {
            return CropImage.activity(input)
                .setCropMenuCropButtonTitle("Kırp")
                .setRequestedSize(600,600)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        adMobView()

        binding.toolbarDetailLast.title="Soru Detayları"
        binding.toolbarDetailLast.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarDetailLast)
        binding.toolbarDetailLast.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel=ViewModelProvider(this).get(DetailViewModel::class.java)

        viewModel.oneSignalListener(this)
        observeLiveData()
        registerLauncher()

        val intent=intent
        soruUid= intent.getStringExtra("soruUid")

        viewModel.getSoruData(soruUid)
        viewModel.getRecyclerData(soruUid!!)


        val auth= Firebase.auth
        val user=auth.currentUser

        binding.recyclerDetailLast.layoutManager= LinearLayoutManager(this)
        adapterCevap= RecyclerCevapAdapter(this,cevapList!!,user!!.uid)
        binding.recyclerDetailLast.adapter=adapterCevap

        binding.imageSoruDetailLast.setOnClickListener {
            viewModel.zoomImage(this,downloadUrlString,layoutInflater)
        }

        binding.menuOption.setOnClickListener {
            viewModel.menuOption(this,it,this,farkGunForViewModel)
        }

        binding.userNameTextDetailLast.setOnClickListener {
            viewModel.goToProfileWatch(this)
        }

        binding.imageUserProfileDetailLast.setOnClickListener {
            viewModel.goToProfileWatch(this)
        }

    }

    private fun adMobView() {
        if (Singleton.mInterstitialAd !=null){
            Singleton.mInterstitialAd?.show(this)
            Singleton.mInterstitialAd =null
        }
    }


    private fun observeLiveData() {
        viewModel.soruDataLive.observe(this, androidx.lifecycle.Observer {
            binding.imageSoruDetailLast.downloadUrlPicassoSoru(it.downloadUrl)
            downloadUrlString=it.downloadUrl
            binding.userNameTextDetailLast.text=it.userDisplayName
            binding.imageUserProfileDetailLast.downloadUrlPicassoProfil(it.userPhotoUrl)
            binding.textDersDetailLast.text=it.selectedDers
            binding.textKonuDetailLast.text=it.selectedKonu
            binding.textAciklamaDetailLast.text=it.selectedAciklama
            dateSettings(it.date)
        })

        viewModel.errorLive.observe(this, androidx.lifecycle.Observer {
            if (it){
                Toast.makeText(this,"Hata! Lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.loadingLive.observe(this, androidx.lifecycle.Observer {
            if (it){
                binding.progressBarDetailLast.visibility=View.VISIBLE
                binding.recyclerDetailLast.visibility=View.GONE
                binding.imageUserProfileDetailLast.visibility=View.GONE
                binding.userNameTextDetailLast.visibility=View.GONE
                binding.textTimeDetailLast.visibility=View.GONE
                binding.menuOption.visibility=View.GONE
                binding.imageSoruDetailLast.visibility=View.GONE
                binding.textDersDetailLast.visibility=View.GONE
                binding.textKonuDetailLast.visibility=View.GONE
                binding.textAciklamaDetailLast.visibility=View.GONE
                binding.cevapGonderLast.isEnabled=false
                binding.selectCameraLast.isClickable=false
                binding.selectMediaLast.isClickable=false
            }else{
                binding.progressBarDetailLast.visibility=View.GONE
                binding.recyclerDetailLast.visibility=View.VISIBLE
                binding.imageUserProfileDetailLast.visibility=View.VISIBLE
                binding.userNameTextDetailLast.visibility=View.VISIBLE
                binding.textTimeDetailLast.visibility=View.VISIBLE
                binding.menuOption.visibility=View.VISIBLE
                binding.imageSoruDetailLast.visibility=View.VISIBLE
                binding.textDersDetailLast.visibility=View.VISIBLE
                binding.textKonuDetailLast.visibility=View.VISIBLE
                binding.textAciklamaDetailLast.visibility=View.VISIBLE
                binding.cevapGonderLast.isEnabled=true
                binding.selectCameraLast.isClickable=true
                binding.selectMediaLast.isClickable=true
            }
        })

        viewModel.successYukle.observe(this, androidx.lifecycle.Observer {
            if (it){
                binding.cevapTextLast.setText("")
                binding.selectCameraLast.setImageResource(com.caneryildirim.sorunupaylasyks.R.drawable.cameraselect)
                binding.selectMediaLast.setImageResource(com.caneryildirim.sorunupaylasyks.R.drawable.mediaselected)
                Toast.makeText(this,"Cevabın Yüklendi",Toast.LENGTH_SHORT).show()
                selectedImage=null
                //viewModel.getRecyclerData(soruUid!!)
            }

        })

        viewModel.cevapDataLive.observe(this, androidx.lifecycle.Observer {
            if (it.size>0){
                binding.textCevapYokLast.visibility=View.GONE
                binding.recyclerDetailLast.visibility=View.VISIBLE
                adapterCevap?.updateCevapList(it)
            }else{
                binding.textCevapYokLast.visibility=View.VISIBLE
                binding.recyclerDetailLast.visibility=View.GONE
            }
        })
    }

    fun selectMedia(view:View){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Görsel yüklenmesi için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                    requestFrom="Gallery"
                    permiisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }else{
                requestFrom="Gallery"
                permiisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentGallery=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickGalleryLauncher.launch(intentGallery)

        }
    }

    fun selectCamera(view: View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                Snackbar.make(view,"Görsel yüklenmesi için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                    requestFrom="Camera"
                    permiisonLauncher.launch(Manifest.permission.CAMERA)
                }).show()
            }else{
                requestFrom="Camera"
                permiisonLauncher.launch(Manifest.permission.CAMERA)
            }
        }else{
            dispatchTakePictureIntent()
        }
    }

    fun yukle(view: View){
        viewModel.yukle(this,binding.cevapTextLast.text.toString().trim(),selectedImage)
    }



    private fun registerLauncher(){
        cropActivityGalleryResultLauncher=registerForActivityResult(cropActivityGalleryContract){uri->
            uri.let {
                selectedImage=it
                binding.selectMediaLast.setImageResource(com.caneryildirim.sorunupaylasyks.R.drawable.mediaselectok)
                binding.selectCameraLast.setImageResource(com.caneryildirim.sorunupaylasyks.R.drawable.cameraselectok)
            }
        }

        pickPhotoLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK){
                //setPic()
                val file= File(currentPhotoPath)
                val uri=Uri.fromFile(file)
                cropActivityGalleryResultLauncher.launch(uri)
            }
        }

        pickGalleryLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK){
                val intentData=result.data
                if (intentData!=null){
                    val uri= intentData.data
                    cropActivityGalleryResultLauncher.launch(uri)
                }
            }
        }

        permiisonLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (requestFrom!=null){
                    if (requestFrom=="Camera"){
                        dispatchTakePictureIntent()
                    }else if (requestFrom=="Gallery"){
                        val intentGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickGalleryLauncher.launch(intentGallery)
                    }
                }

            }else{
                Toast.makeText(this,"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            //alttaki resolveactivity i kaldırdın ve çalıştı başka sorunlar var oluşabilir.
            //takePictureIntent.resolveActivity(requireActivity().packageManager).also {  }
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File

                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.caneryildirim.sorunupaylasyks.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                pickPhotoLauncher.launch(takePictureIntent)


            }

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun getTimeDate(date:Long):String{
        val netDate=Date(date)
        val sdf= SimpleDateFormat("dd/MM/yy HH:mm:ss",Locale.getDefault())
        return sdf.format(netDate)

    }

    fun dateSettings(timestamp: Timestamp){
        val dateFirst=timestamp.toDate().time
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
        farkGunForViewModel=farkGun
        val farkHafta=farkGun/7
        val farkAy=farkHafta/4
        val farkYil=farkAy/12

        if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()==0){
            binding.textTimeDetailLast.text="Şimdi"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()==0 && farkDakika.toInt()<61){
            binding.textTimeDetailLast.text="${farkDakika} Dk Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 && farkGun.toInt()==0 && farkSaat.toInt()<25){
            binding.textTimeDetailLast.text="${farkSaat} Saat Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()==0 &&farkGun.toInt()<8){
            binding.textTimeDetailLast.text="${farkGun} Gün Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()==0 && farkHafta.toInt()<5){
            binding.textTimeDetailLast.text="${farkHafta} Hafta Önce"
        }else if (farkYil.toInt()==0 && farkAy.toInt()<13){
            binding.textTimeDetailLast.text="${farkAy} Ay Önce"
        }else if (farkYil.toInt()!=0){
            binding.textTimeDetailLast.text="${farkYil} Yıl Önce"
        }


    }

    override fun onItemDelete(position: Int, docUuid: String, dogruCevap: Boolean) {
        viewModel.onItemDelete(this,docUuid,dogruCevap)
    }

    override fun sikayetItem(position: Int) {
        viewModel.sikayetItem(this,position)
    }

    override fun duzenleItem(position: Int) {

    }

    override fun guncelleItem(position: Int) {
        viewModel.guncelleItem(this,position)
    }

    override fun onItemClick(position: Int) {
        viewModel.onItemClick(this,position,layoutInflater)
    }

}