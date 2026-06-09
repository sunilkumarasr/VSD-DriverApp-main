package com.seafoods.driverapp.Logins

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
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.ImageUploadMainRes
import com.seafoods.driverapp.OTPResponse
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityAddYourProfilePicBinding
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

class AddYourProfilePicActivity : AppCompatActivity() {

    val binding: ActivityAddYourProfilePicBinding by lazy {
        ActivityAddYourProfilePicBinding.inflate(layoutInflater)
    }

    var imageName=""
    var image_path=""
    var isFrom=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isFrom= intent.getStringExtra("isFrom").toString()

        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)
        binding.lnrTakePhoto.setOnClickListener {
            checkCameraPermissionAndDispatchIntent()

        }
        binding.lnrPickPhoto.setOnClickListener {
            pickIMageIntent()

        }
        inits()

    }


    private fun inits() {

        binding.linearVerify.setOnClickListener {
            if(image_path.isEmpty())
            {
                Utils.showMessage("Please upload image",applicationContext)
                return@setOnClickListener
            }

            UpdateProfileImage()
        }

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
    private var latestTmpUri: Uri? = null // To store the URI of the image taken

    // Activity Result Launcher for Camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            // Image was captured successfully
            latestTmpUri?.let { uri: Uri ->
                // imageView.setImageURI(uri) // Display the image

                binding.imgProfile.setImageURI(uri)
               val file= getCompressedImageFile(uri)
                if (file != null) {
                    uploadImage(file)
                }
                // You can now process the image from this URI (e.g., upload, further manipulation)
            }
        } else {
            // Image capture failed or was cancelled

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100) {
            if (data == null)
                return
            val uri: Uri? = data?.getData()
            // Initialize bitmap
            // Initialize bitmap
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                // initialize byte stream

                binding.imgProfile.setImageBitmap(bitmap)


                val returnCursor: Cursor = contentResolver.query(uri!!, null, null, null, null)!!
                val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor!!.moveToFirst()
                imageName = returnCursor!!.getString(nameIndex)
                returnCursor!!.close()

                /* binding.imageView?.let {
                Glide.with(applicationContext)
                    .load(bitmap)
                    .into(it)
            };*/

                val file= getCompressedImageFile(uri)
                if (file != null) {
                    uploadImage(file)
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Image pick error", "image Pick Error ${e.printStackTrace()}")
            }
        }
    }

    fun uploadImage(file:File){
        var user_id=   Preferences.getUserID(applicationContext)

        val dialog= CustomDialog(this@AddYourProfilePicActivity)
        dialog.showDialog(this@AddYourProfilePicActivity,false)
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

                       image_path= model!!.data!!.imageUrl.toString()

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

    fun UpdateProfileImage(){
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.uploadProfileImage(object: Callback<OTPResponse> {
                override fun onResponse(
                    call: Call<OTPResponse>,
                    response: Response<OTPResponse>
                ) {

                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: OTPResponse? = response.body()

                        var flag=model!!.user_data.flag
                        if(isFrom.isEmpty()){
                        Preferences.saveProfileStatus(applicationContext, flag)

                        ViewController.profileNavigateScreen(applicationContext)
                            }
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, it,image_path)
        }
    }

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
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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

}