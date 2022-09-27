package com.caneryildirim.sorunupaylasyks.view


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.sorunupaylasyks.databinding.FragmentUploadBinding
import com.caneryildirim.sorunupaylasyks.singleton.Singleton
import com.caneryildirim.sorunupaylasyks.viewModel.UploadViewModel
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class UploadFragment : Fragment() {
    private var _binding:FragmentUploadBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: UploadViewModel

    private lateinit var permiisonLauncher: ActivityResultLauncher<String>
    private lateinit var pickPhotoLauncher:ActivityResultLauncher<Intent>
    private lateinit var pickGalleryLauncher:ActivityResultLauncher<Intent>
    private lateinit var cropActivityGalleryResultLauncher: ActivityResultLauncher<Uri?>

    private lateinit var dersKonularList:List<List<String>>
    private var dersPosition:Int?=null
    private lateinit var konuPosition:String

    private var selectedImage:Uri?=null
    lateinit var currentPhotoPath: String
    private var requestFrom: String?=null

    private val cropActivityGalleryContract=object : ActivityResultContract<Uri?, Uri?>(){
        override fun createIntent(context: Context, input: Uri?): Intent {
            return CropImage.activity(input)
                .setCropMenuCropButtonTitle("Kırp")
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding=FragmentUploadBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(UploadViewModel::class.java)
        observeLiveData()


        val dersList=viewModel.dersler()
        val dersAdaptor= ArrayAdapter(this.requireContext(),android.R.layout.simple_list_item_1,android.R.id.text1,dersList)
        binding.spinnerDers.adapter=dersAdaptor

        dersKonularList=viewModel.konular()

        spinnerSet()
        registerLauncher()

        binding.cameraSelected.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),Manifest.permission.CAMERA)){
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

        binding.gallerySelected.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this.requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Görsel yüklenmesi için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                        requestFrom="Gallery"
                        permiisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    requestFrom="Gallery"
                    permiisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickGalleryLauncher.launch(intentGallery)

            }
        }

        binding.buttonYukle.setOnClickListener {
            if(binding.spinnerDers.selectedItemPosition==0){
                Toast.makeText(this.requireContext(),"Ders seçmeniz gerekli",Toast.LENGTH_LONG).show()
            }else if(binding.spinnerKonu.selectedItemPosition==0){
                Toast.makeText(this.requireContext(),"Konu seçmeniz gerekli",Toast.LENGTH_LONG).show()
            }else if (selectedImage==null){
                Toast.makeText(this.requireContext(),"Soru görselinizi seçmeniz gerekli",Toast.LENGTH_LONG).show()
            }else if (binding.editTextAciklama.text.toString().isEmpty()){
                Toast.makeText(this.requireContext(),"Soru açıklaması yazmanız gerekli",Toast.LENGTH_LONG).show()
            }else{
                val selectedAciklama=binding.editTextAciklama.text.toString()
                val selectedDers=dersList[binding.spinnerDers.selectedItemPosition]
                val selectedKonu=konuPosition
                viewModel.yukle(it,this.requireContext(),requireActivity(),selectedAciklama,selectedDers,selectedKonu,selectedImage)
            }

        }



    }


    private fun observeLiveData() {
        viewModel.uploadLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                binding.progressBarUpdate.visibility=View.VISIBLE
                binding.buttonYukle.isEnabled=false
                binding.cameraSelected.isEnabled=false
                binding.gallerySelected.isEnabled=false
                val activity=activity as MainActivity
                Singleton.whereFragment ="UploadFragment"
            }else{
                binding.progressBarUpdate.visibility=View.GONE
                binding.buttonYukle.isEnabled=true
                binding.cameraSelected.isEnabled=true
                binding.gallerySelected.isEnabled=true
                val activity=activity as MainActivity
                Singleton.whereFragment ="null"
            }
        })

    }

    private fun spinnerSet() {
        binding.spinnerDers.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                dersPosition=p2
                var adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[0])
                when(p2){
                    0->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[0])
                        binding.spinnerKonu.adapter=adapter
                    }
                    1->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[1])
                        binding.spinnerKonu.adapter=adapter
                    }
                    2->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[2])
                        binding.spinnerKonu.adapter=adapter
                    }
                    3->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[3])
                        binding.spinnerKonu.adapter=adapter
                    }
                    4->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[4])
                        binding.spinnerKonu.adapter=adapter
                    }
                    5->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[5])
                        binding.spinnerKonu.adapter=adapter
                    }
                    6->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[6])
                        binding.spinnerKonu.adapter=adapter
                    }
                    7->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[7])
                        binding.spinnerKonu.adapter=adapter
                    }
                    8->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[8])
                        binding.spinnerKonu.adapter=adapter
                    }
                    9->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[9])
                        binding.spinnerKonu.adapter=adapter
                    }
                    10->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[10])
                        binding.spinnerKonu.adapter=adapter
                    }
                    11->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[11])
                        binding.spinnerKonu.adapter=adapter
                    }
                    12->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[12])
                        binding.spinnerKonu.adapter=adapter
                    }
                    13->{
                        adapter=ArrayAdapter(this@UploadFragment.requireContext(),android.R.layout.simple_list_item_1,dersKonularList[13])
                        binding.spinnerKonu.adapter=adapter
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.spinnerKonu.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (dersPosition!=null){
                    when(dersPosition){
                        1->konuPosition=dersKonularList[1].get(p2)
                        2->konuPosition=dersKonularList[2].get(p2)
                        3->konuPosition=dersKonularList[3].get(p2)
                        4->konuPosition=dersKonularList[4].get(p2)
                        5->konuPosition=dersKonularList[5].get(p2)
                        6->konuPosition=dersKonularList[6].get(p2)
                        7->konuPosition=dersKonularList[7].get(p2)
                        8->konuPosition=dersKonularList[8].get(p2)
                        9->konuPosition=dersKonularList[9].get(p2)
                        10->konuPosition=dersKonularList[10].get(p2)
                        11->konuPosition=dersKonularList[11].get(p2)
                        12->konuPosition=dersKonularList[12].get(p2)
                        13->konuPosition=dersKonularList[13].get(p2)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    private fun registerLauncher(){
        cropActivityGalleryResultLauncher=registerForActivityResult(cropActivityGalleryContract){uri->
            uri.let {
                binding.imageViewUpload.setImageURI(it)
                selectedImage=it
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

        pickGalleryLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
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
                Toast.makeText(this.requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
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
                        this.requireContext(),
                        "com.caneryildirim.sorunupaylasyks.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    pickPhotoLauncher.launch(takePictureIntent)


                }

        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.imageViewUpload.width
        val targetH: Int = binding.imageViewUpload.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->


        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


}