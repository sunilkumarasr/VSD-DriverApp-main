package com.royalit.driverapp.Logins

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.royalit.driverapp.Config.Preferences
import com.royalit.driverapp.Config.ViewController
import com.royalit.driverapp.CustomDialog
import com.royalit.driverapp.DataManager
import com.royalit.driverapp.ImageUploadMainRes
import com.royalit.driverapp.OTPResponse
import com.royalit.driverapp.R
import com.royalit.driverapp.Utils
import com.royalit.driverapp.databinding.ActivityUploadDocBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadDocActivity : AppCompatActivity() {

    val binding: ActivityUploadDocBinding by lazy {
        ActivityUploadDocBinding.inflate(layoutInflater)
    }


    var FLAG=0//1->AdhaarFront, 2->AdhaarBack, 3->PanCardFront, 4->PanCardBack,5-> DRIVING_LICAENCE_FRON, 6->DRIVING_BACK, 7->RC_Front, 8->RC_Back
    var imgF:ImageView?=null
    var imgB:ImageView?=null
    var image_a_f_path=""
    var image_a_b_path=""

    var image_p_f_path=""
   // var image_p_b_path=""

    var image_d_f_path=""
    //var image_d_b_path=""

    var image_v_f_path=""
   // var image_v_b_path=""

    var doc_type=""

    var FLAG_IMAGE=0//2->PICK, 1->TAKE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)

        inits()

    }

    private fun inits() {

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.linearAadharCard.setOnClickListener {
            AadharCardDialog()
        }
        binding.linearPanCard.setOnClickListener {
            PanCardDialog()
        }
        binding.linearDrivingLicense.setOnClickListener {
            DrivingLicenseDialog()
        }
        binding.linearVehicleRC.setOnClickListener {
            VehicleRCDialog()
        }

        binding.linearContinue.setOnClickListener {
            Log.e("","-- $image_a_b_path 2 $image_a_f_path 3 $image_p_f_path 4 $image_v_f_path 5 $image_d_f_path ")
            if(image_a_b_path.isEmpty()||image_a_f_path.isEmpty()||image_p_f_path.isEmpty()||image_v_f_path.isEmpty()||image_d_f_path.isEmpty())
            {
                Utils.showMessage("Please add all required images",applicationContext)
                return@setOnClickListener
            }

            uploadDocDetails()
        }


    }




    private var latestTmpUri: Uri? = null // To store the URI of the image taken

    // Activity Result Launcher for Camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            // Image was captured successfully
            latestTmpUri?.let { uri: Uri ->
               // imageView.setImageURI(uri) // Display the image
                if(FLAG==1||FLAG==3||FLAG==5||FLAG==7){
                    imgF!!.setImageURI(uri)
                }else
                {
                    imgB!!.setImageURI(uri)
                }
                val file= getCompressedImageFile(uri)
                if (file != null) {
                    uploadImage(file,uri)
                }
               // uploadImage(uri)
                // You can now process the image from this URI (e.g., upload, further manipulation)
            }
        } else {
            // Image capture failed or was cancelled

        }
    }
    private fun checkCameraPermissionAndDispatchIntent() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app needs this
                // permission for a specific feature. Then, proceed to request the
                // permission again.
                Toast.makeText(this, "Camera permission is needed to take photos.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    // Activity Result Launcher for Permission Request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed to take a picture
            dispatchTakePictureIntent()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun dispatchTakePictureIntent() {
        try {
            val photoFile: File? = createImageFile()
            photoFile?.also {
                latestTmpUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider", // Must match the authority in AndroidManifest.xml
                    it
                )
                takePictureLauncher.launch(latestTmpUri!!)
            }
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }
    var hasNotificationPermissionGranted=false
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            askPermission()
                        } else {
                            // showSettingDialog()
                        }
                    }
                }
            } else {

                pickIMageIntent()
            }
        }

    private fun askPermission() {


        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Storage permission is required")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT <= 32) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    fun pickIMageIntent()
    {
        if (Build.VERSION.SDK_INT <= 32&& Build.VERSION.SDK_INT >=23) {
            if(!hasNotificationPermissionGranted) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        else{

        }


        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
    private fun AadharCardDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@UploadDocActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_aadharcard, null)
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<LinearLayout>(R.id.linearCancel)
        val linearImgPickF = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_f)
        val linearImgPickB = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_b)

        val linearImgTakeF = view.findViewById<LinearLayout>(R.id.lnr_take_photo_f)
        val linearImgTakeB = view.findViewById<LinearLayout>(R.id.lnr_take_photo_b)

         imgF=view.findViewById<ImageView>(R.id.img_f)
         imgB=view.findViewById<ImageView>(R.id.img_b)
        Glide.with(applicationContext)
            .load(image_a_f_path)
            .into(imgF!!)
        Glide.with(applicationContext)
            .load(image_a_b_path)
            .into(imgB!!)
        linearImgPickF.setOnClickListener {
            FLAG=1
            FLAG_IMAGE=2
            doc_type="aadhaar_front"
            pickIMageIntent()
        }
        linearImgPickB.setOnClickListener {
            FLAG=2
            FLAG_IMAGE=2
            doc_type="aadhaar_back"
            pickIMageIntent()
        }
        linearImgTakeF.setOnClickListener {
            FLAG=1
            FLAG_IMAGE=1
            doc_type="aadhaar_front"
            checkCameraPermissionAndDispatchIntent()
        }
        linearImgTakeB.setOnClickListener {
            FLAG=2
            FLAG_IMAGE=1
            doc_type="aadhaar_back"
            checkCameraPermissionAndDispatchIntent()
        }

        val linearUpload = view.findViewById<LinearLayout>(R.id.linearUpload)
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {

             bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
    private fun PanCardDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@UploadDocActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_pancard, null)
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<LinearLayout>(R.id.linearCancel)
        val linearUpload = view.findViewById<LinearLayout>(R.id.linearUpload)
        val linearImgPickF = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_f)
        val linearImgPickB = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_b)

        val linearImgTakeF = view.findViewById<LinearLayout>(R.id.lnr_take_photo_f)
        val linearImgTakeB = view.findViewById<LinearLayout>(R.id.lnr_take_photo_b)

        imgF=view.findViewById<ImageView>(R.id.img_f)
        imgB=view.findViewById<ImageView>(R.id.img_b)
        Glide.with(applicationContext)
            .load(image_p_f_path)
            .into(imgF!!)
        linearImgPickF.setOnClickListener {
            FLAG=3
            FLAG_IMAGE=2
            doc_type="pan_front"
            pickIMageIntent()
        }
        linearImgPickB.setOnClickListener {
            FLAG=4
            FLAG_IMAGE=2
            doc_type="pan_back"
            pickIMageIntent()
        }
        linearImgTakeF.setOnClickListener {
            FLAG=3
            FLAG_IMAGE=1
            doc_type="pan_front"
            checkCameraPermissionAndDispatchIntent()
        }
        linearImgTakeB.setOnClickListener {
            FLAG=4
            FLAG_IMAGE=1
            doc_type="pan_back"
            checkCameraPermissionAndDispatchIntent()
        }
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun DrivingLicenseDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@UploadDocActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_driving, null)
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<LinearLayout>(R.id.linearCancel)
        val linearUpload = view.findViewById<LinearLayout>(R.id.linearUpload)
        val linearImgPickF = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_f)
        val linearImgPickB = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_b)

        val linearImgTakeF = view.findViewById<LinearLayout>(R.id.lnr_take_photo_f)
        val linearImgTakeB = view.findViewById<LinearLayout>(R.id.lnr_take_photo_b)

        imgF=view.findViewById<ImageView>(R.id.img_f)
        imgB=view.findViewById<ImageView>(R.id.img_b)
        Glide.with(applicationContext)
            .load(image_d_f_path)
            .into(imgF!!)
        linearImgPickF.setOnClickListener {
            FLAG=5
            FLAG_IMAGE=2
            doc_type="driving_front"
            pickIMageIntent()
        }
        linearImgPickB.setOnClickListener {
            FLAG=6
            FLAG_IMAGE=2
            doc_type="driving_back"

            pickIMageIntent()
        }
        linearImgTakeF.setOnClickListener {
            FLAG=5
            FLAG_IMAGE=1
            doc_type="driving_front"

            checkCameraPermissionAndDispatchIntent()
        }
        linearImgTakeB.setOnClickListener {
            FLAG=6
            FLAG_IMAGE=1
            doc_type="driving_back"

            checkCameraPermissionAndDispatchIntent()
        }
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun VehicleRCDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@UploadDocActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_vehiclerc, null)
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<LinearLayout>(R.id.linearCancel)
        val linearUpload = view.findViewById<LinearLayout>(R.id.linearUpload)

        val linearImgPickF = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_f)
        val linearImgPickB = view.findViewById<LinearLayout>(R.id.lnr_pick_photo_b)

        val linearImgTakeF = view.findViewById<LinearLayout>(R.id.lnr_take_photo_f)
        val linearImgTakeB = view.findViewById<LinearLayout>(R.id.lnr_take_photo_b)

        imgF=view.findViewById<ImageView>(R.id.img_f)
        imgB=view.findViewById<ImageView>(R.id.img_b)




            Glide.with(applicationContext)
                .load(image_v_f_path)
                .into(imgF!!)
        linearImgPickF.setOnClickListener {
            FLAG=7
            FLAG_IMAGE=2
            doc_type="rc_front"

            pickIMageIntent()
        }
        linearImgPickB.setOnClickListener {
            FLAG=8
            FLAG_IMAGE=2
            doc_type="rc_back"
            pickIMageIntent()
        }
        linearImgTakeF.setOnClickListener {
            FLAG=7
            FLAG_IMAGE=1
            doc_type="rc_front"
            checkCameraPermissionAndDispatchIntent()
        }
        linearImgTakeB.setOnClickListener {
            FLAG=8
            FLAG_IMAGE=1
            doc_type="rc_back"
            checkCameraPermissionAndDispatchIntent()
        }
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

  /*  fun uploadImage(uri:Uri){
        val dialog= CustomDialog(this@UploadDocActivity)
        dialog.showDialog(this@UploadDocActivity,false)
        var user_id=   Preferences.getUserID(applicationContext)

        val dataManager = DataManager.getDataManager()
        getFileFromUri(uri)?.let {
            dataManager.fileUpload(it,object: Callback<ImageUploadMainRes> {
                override fun onResponse(
                    call: Call<ImageUploadMainRes>,
                    response: Response<ImageUploadMainRes>
                ) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true)
                    {
                        val model: ImageUploadMainRes? = response.body()

                        Log.e("image_a_f_path ","image_a_f_path $FLAG $image_a_f_path")
                        when(FLAG)
                        {
                            1->{
                                image_a_f_path= model!!.data!!.imageUrl.toString()
                                imgF!!.setImageURI(uri)
                            }
                            2->{
                                image_a_b_path= model!!.data!!.imageUrl.toString()
                                imgB!!.setImageURI(uri)
                            }


                            3->{
                                image_p_f_path= model!!.data!!.imageUrl.toString()
                            }
                            4->{
                                //image_p_b_path= model!!.data!!.imageUrl.toString()
                            }

                            5->{
                                image_d_f_path= model!!.data!!.imageUrl.toString()
                            }
                            6->{
                               // image_d_b_path= model!!.data!!.imageUrl.toString()
                            }


                            7->{
                                image_v_f_path= model!!.data!!.imageUrl.toString()
                            }
                            8->{
                                //image_v_b_path= model!!.data!!.imageUrl.toString()
                            }
                        }
                        Log.e("image_a_f_path ","image_a_f_path $FLAG $image_a_f_path")




                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<ImageUploadMainRes>, t: Throwable) {
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                    dialog.closeDialog()
                }

            },doc_type,user_id!!)
        }
    }*/
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            // Create a temporary file in your app's cache directory
            val tempFile = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            tempFile.createNewFile()
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data==null||FLAG_IMAGE==1)
            return
        val uri: Uri? = data?.getData()
        // Initialize bitmap
        // Initialize bitmap
        try {


            val returnCursor: Cursor = contentResolver.query(uri!!, null, null, null, null)!!
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor!!.moveToFirst()
            val file= getCompressedImageFile(uri)
            if (file != null) {
                uploadImage(file,uri)
            }
            if(FLAG==1||FLAG==3||FLAG==5||FLAG==7){
                imgF!!.setImageURI(uri)
            }else
            {
                imgB!!.setImageURI(uri)
            }


            returnCursor!!.close()

            /* binding.imageView?.let {
                 Glide.with(applicationContext)
                     .load(bitmap)
                     .into(it)
             };*/

            //uploadImage(uri)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Image pick error","image Pick Error ${e.printStackTrace()}")
        }
    }
    fun uploadDocDetails(){
        val dialog= CustomDialog(this@UploadDocActivity)
        dialog.showDialog(this@UploadDocActivity,false)

        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.uploadDoc(object: Callback<OTPResponse> {
                override fun onResponse(
                    call: Call<OTPResponse>,
                    response: Response<OTPResponse>
                ) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: OTPResponse? = response.body()

                        var flag=model!!.user_data.flag
                        Preferences.saveProfileStatus(applicationContext, flag)
                        ViewController.profileNavigateScreen(applicationContext)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                    dialog.closeDialog()
                }

            }, it,image_a_f_path,image_a_b_path,image_d_f_path,image_d_f_path,image_v_f_path)
        }
    }


    private fun getCompressedImageFile(uri: Uri, quality: Int = 80, maxWidth: Int = 512, maxHeight: Int = 512): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            // 1. Decode bitmap with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close() // Close and reopen to reset the stream

            // 2. Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

            // 3. Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            var bitmap = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null

            // 4. Handle EXIF Orientation (Important for camera photos)
            contentResolver.openInputStream(uri)?.use { exifStream ->
                val exif = ExifInterface(exifStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    // Add other cases like flip horizontal/vertical if needed
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }


            // 5. Create a byte array output stream for compression
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            // 6. Create a temporary file and write the compressed data
            val tempFile = File(cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            tempFile.createNewFile()
            val fileOutputStream = FileOutputStream(tempFile)
            fileOutputStream.write(byteArrayOutputStream.toByteArray())
            fileOutputStream.close()
            byteArrayOutputStream.close()

            bitmap.recycle() // Recycle bitmap to free memory

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageCompression", "Error compressing image: ${e.message}")
            null // Fallback to original method if compression fails, or handle error
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun uploadImage(file:File,uri:Uri){
        var user_id=   Preferences.getUserID(applicationContext)

        val dialog= CustomDialog(this@UploadDocActivity)
        dialog.showDialog(this@UploadDocActivity,false)
        val dataManager = DataManager.getDataManager()
        file?.let {
            dataManager.fileUpload(it,object: Callback<ImageUploadMainRes> {
                override fun onResponse(
                    call: Call<ImageUploadMainRes>,
                    response: Response<ImageUploadMainRes>
                ) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true)
                    {
                        val model: ImageUploadMainRes? = response.body()

                        when(FLAG)
                        {
                            1->{
                                image_a_f_path= model!!.data!!.imageUrl.toString()
                                imgF!!.setImageURI(uri)
                            }
                            2->{
                                image_a_b_path= model!!.data!!.imageUrl.toString()
                                imgB!!.setImageURI(uri)
                            }


                            3->{
                                image_p_f_path= model!!.data!!.imageUrl.toString()
                            }
                            4->{
                                //image_p_b_path= model!!.data!!.imageUrl.toString()
                            }

                            5->{
                                image_d_f_path= model!!.data!!.imageUrl.toString()
                            }
                            6->{
                                // image_d_b_path= model!!.data!!.imageUrl.toString()
                            }


                            7->{
                                image_v_f_path= model!!.data!!.imageUrl.toString()
                            }
                            8->{
                                //image_v_b_path= model!!.data!!.imageUrl.toString()
                            }
                        }

                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<ImageUploadMainRes>, t: Throwable) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, user_id =user_id!! )
        }
    }
}