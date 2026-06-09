package com.royalit.driverapp.Activitys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.royalit.driverapp.Adapters.ProductOrderAdapter
import com.royalit.driverapp.Config.Preferences
import com.royalit.driverapp.Config.ViewController
import com.royalit.driverapp.CustomDialog
import com.royalit.driverapp.DataManager
import com.royalit.driverapp.DataManager.Companion.ORDER_STATUS
import com.royalit.driverapp.DataManager.Companion.RUPEE
import com.royalit.driverapp.ImageUploadMainRes
import com.royalit.driverapp.LoginResponse
import com.royalit.driverapp.OrderResponse
import com.royalit.driverapp.Orders
import com.royalit.driverapp.R
import com.royalit.driverapp.Utils
import com.royalit.driverapp.databinding.ActivityOnGoingDeliveryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OnGoingDeliveryActivity : AppCompatActivity() {

    val binding: ActivityOnGoingDeliveryBinding by lazy {
        ActivityOnGoingDeliveryBinding.inflate(layoutInflater)
    }

    var order_id=""
    var order_id_number=""
    var comment="Test"
    var image1=""
    var image2=""
    var status=-1
    var FLAG=0
   val imagesArray= ArrayList<String>()

    // --- Image Resizing Constants (adjust as needed) ---
    private  val TARGET_IMAGE_WIDTH_DELIVERY = 600 // Example: smaller for delivery proof
    private  val COMPRESSION_QUALITY_DELIVERY = 75
    lateinit var productOrderAdapter: ProductOrderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var window=window
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.VANILLA_ICE_CREAM)
        {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

            // 2. Handle Window Insets to prevent content overlap
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply insets as padding to the root view.
                // This will push all content within binding.root away from the system bars.
                view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

                // If specific views still overlap or need different behavior (e.g., a Toolbar
                // intended to sit behind a transparent status bar), you'll need to apply
                // padding or margins more selectively to those specific views or their containers.
                // For instance, to only pad the top of your contentFrame and bottom of navigationView:
                // binding.contentFrame.setPadding(insets.left, insets.top, insets.right, binding.contentFrame.paddingBottom)
                // binding.navigationView.setPadding(binding.navigationView.paddingLeft, binding.navigationView.paddingTop, binding.navigationView.paddingRight, insets.bottom)


                WindowInsetsCompat.CONSUMED
            }

        }
        order_id= intent.getStringExtra("order_id").toString()
        order_id_number= intent.getStringExtra("order_id_number").toString()
        FLAG= intent.getIntExtra("FLAG",0)
        if(FLAG==1)
        {
         binding.txtHeader.setText("Completed Order")
        }
        binding.txtCompletedOrderId.setText("Order ID: ${order_id}")
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.white),
            true,binding.root
        )


        inits()
        productOrderAdapter=ProductOrderAdapter()
        binding.recyclerOrders.adapter=productOrderAdapter
        getOrderDetails()
        binding.linearSubmit.setOnClickListener {
            comment=binding.editComment.text.toString().trim()
            updateOrderStatus()
        }
        binding.linearReturned.setOnClickListener {

            updateOrderStatus2()
        }

        binding.lnrTakePhoto.setOnClickListener {
            checkCameraPermissionAndDispatchIntent()
        }
        binding.txtPrice.paintFlags =
            binding.txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.deliveryChargesStrike.paintFlags =
            binding.txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


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
    var latestTmpUri:Uri? = null;
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
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            latestTmpUri?.let { uri: Uri ->
                try {
                    if(image1.isEmpty())
                        binding.imgOne.setImageURI(uri)
                    else if(image2.isEmpty())
                        binding.imgTwo.setImageURI(uri)

                    val originalBitmap = getBitmapFromUri(uri, true) // Get and orient bitmap
                    if (originalBitmap != null) {
                        // You can display the original or resized bitmap in an ImageView if you have one
                        // e.g., binding.someImageViewForPreview.setImageBitmap(originalBitmap)

                        val resizedBitmap = resizeAndCompressBitmap(
                            originalBitmap,
                            TARGET_IMAGE_WIDTH_DELIVERY,
                            COMPRESSION_QUALITY_DELIVERY
                        )

                        // Convert the resized bitmap to a file for upload
                        val imageFileToUpload = bitmapToFile(resizedBitmap, "delivery_image_${System.currentTimeMillis()}.jpg")

                        if (imageFileToUpload != null) {
                            uploadImage(imageFileToUpload) // New function to upload the File
                        } else {
                            Toast.makeText(this, "Failed to create resized image file.", Toast.LENGTH_SHORT).show()
                        }

                        // Recycle the bitmaps if they are not the same instance and not needed anymore
                        if (originalBitmap != resizedBitmap) {
                            // originalBitmap.recycle() // Be cautious as always
                        }
                        // resizedBitmap.recycle() // If bitmapToFile creates a new copy and resizedBitmap is not needed

                    } else {
                        Toast.makeText(this, "Failed to load image from camera", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Log.e("CameraError", "Error processing camera image", e)
                    Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Image capture failed or was cancelled.", Toast.LENGTH_SHORT).show()
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
    private fun inits() {

        binding.imgBack.setOnClickListener {
            finish()
        }





    }
    fun getOrderDetails(){
        val dialog= CustomDialog(this@OnGoingDeliveryActivity)
        // Obtain the DataManager instance
        //dialog.showDialog(this@OnGoingDeliveryActivity,false)
        var user_id=   Preferences.getUserID(applicationContext)

        val dataManager = DataManager.getDataManager()
        order_id_number?.let {
            dataManager.getOrderDetails(object: Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    //dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: OrderResponse? = response.body()

                     var order= model!!.orders.get(0)
                        productOrderAdapter.setData(order.products)
                        binding.txtName.text="${order.fullName}"
                        binding.txtOrderId.text="${order.orderId}"
                        binding.txtAddress.text="${formAddress(order)}"
                        binding.txtTotalAmnt.text="$RUPEE ${order.amount}"
                        binding.txtItems.text=" (${order.products.size} Items ) "
                        if(order.gst_charges!!.isEmpty())
                            order.gst_charges="0"
                        binding.txtGstCharages.text="$RUPEE  ${order.gst_charges}"

                        ORDER_STATUS= order.order_status.toString()
                        status=Integer.parseInt(ORDER_STATUS)
                        binding.lnrNav.visibility=View.GONE
                        if(ORDER_STATUS=="4")
                        {
                            binding.linearBottom.visibility= View.GONE
                            binding.lnrDelivered.visibility=View.VISIBLE
                            binding.lnrCall.visibility=View.GONE
                            binding.lnrTakePhoto.visibility=View.GONE
                            binding.lnrNav.visibility=View.GONE
                        }
                        if(status==0||status==1||status==2)
                        {
                           binding.txtOrderStatus.text="Reached Customer"
                            binding.lnrCall.visibility=View.VISIBLE
                            binding.lnrTakePhoto.visibility=View.GONE
                            binding.lnrDelivered.visibility=View.GONE
                            binding.lnrNav.visibility=View.VISIBLE
                        }else if(status==3)
                        {
                            binding.txtOrderStatus.text="Mark to Delivered"
                            binding.lnrCall.visibility=View.GONE
                            binding.lnrTakePhoto.visibility=View.VISIBLE
                            binding.lnrDelivered.visibility=View.GONE
                            binding.lnrNav.visibility=View.VISIBLE
                        }else if(status==5)
                        {
                            binding.linearBottom.visibility= View.GONE
                            binding.lnrDelivered.visibility=View.GONE
                            binding.lnrReturned.visibility=View.VISIBLE
                            binding.txtCompletedOrderIdReturned.text="Order Id: $order_id"
                            binding.lnrCall.visibility=View.GONE
                            binding.lnrTakePhoto.visibility=View.GONE
                            binding.lnrNav.visibility=View.GONE
                        }
                        binding.lnrNav.setOnClickListener {

                            val builder = Uri.Builder()
                            builder.scheme("https")
                                .authority("www.google.com")
                                .appendPath("maps")
                                .appendPath("dir")
                                .appendPath("")
                                .appendQueryParameter("api", "1")
                                //.appendQueryParameter("origin", pickLatLng!!.latitude.toString() + "," + pickLatLng!!.longitude)
                                .appendQueryParameter("destination", order.latitude + "," + order!!.longitude)
                            val url = builder.build().toString()
                            Log.d("Directions", url)
                            val i = Intent(Intent.ACTION_VIEW)
                            i.setData(Uri.parse(url))
                             startActivity(i)
                        }
                        var amount=0.0f;
                        var mrp=0.0f;
                        order.products.forEach {
                            amount=amount+((it.ourPrice!!.toFloat()) * Integer.parseInt(it.qty))
                            mrp=mrp+(it.mrpPrice!!.toFloat()) * Integer.parseInt(it.qty)
                        }



                        var deliveryCharger=0
                        var couponApplied=0

                        if(order.coupons_name!!.isNotEmpty())
                        {
                            couponApplied=Integer.parseInt(order.coupons_name!!)
                            binding.tableApplied.visibility=View.VISIBLE
                            binding.txtAmountCoupon.setText("Coupon Applied (${Utils.RUPEE_SYMBOL}${order.coupons_name})")
                        }
                        var actualpricePaid=(amount)-(((order.gst_charges!!.toFloat())+(deliveryCharger+couponApplied)))


                        binding.txtBillAmnt.text="$RUPEE $amount"
                        binding.txtPrice.text="$RUPEE $mrp"
                        binding.txtSavedPrice.text="$RUPEE ${mrp-amount+couponApplied}"
                        binding.txtDeliveryCharages.text="$RUPEE 0"

                        binding.lnrCall.setOnClickListener {
                            val phone = "${order.mobile}"
                            val intent =
                                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                            startActivity(intent)
                        }
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    //dialog.closeDialog()
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, order_id_number, user_id.toString())
        }
    }
    fun updateOrderStatus(){
        if(status==0||status==1)
        {
            status=3
        }
        else if( status==2)
        {
            status=3
        } else if( status==3)
        {
            status=4
        }
        if(status==5)
        {
            status=4
        }

        if(status==4)
            if(image1.isEmpty()||image2.isEmpty())
            {
                Utils.showMessage("Please take picture",applicationContext)
                return
            }
        //status=4
        val dialog= CustomDialog(this@OnGoingDeliveryActivity)
        // Obtain the DataManager instance
        dialog.showDialog(this@OnGoingDeliveryActivity,false)

        val dataManager = DataManager.getDataManager()
        order_id?.let {
            dataManager.updateOrderStatus(object: Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        getOrderDetails()
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, order_id,status.toString(),comment,image1,image2,imagesArray)
        }
    }
    fun updateOrderStatus2(){
        status=5
        //status=4
        val dialog= CustomDialog(this@OnGoingDeliveryActivity)
        // Obtain the DataManager instance
        dialog.showDialog(this@OnGoingDeliveryActivity,false)

        val dataManager = DataManager.getDataManager()
        order_id?.let {
            dataManager.updateOrderStatus(object: Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        getOrderDetails()
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    dialog.closeDialog()
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, order_id,status.toString(),comment,image1,image2,imagesArray)
        }
    }
    fun formAddress(data: Orders):String
    {
        var adrs="${data.houseNo},${data.floor},${data.landmark}\n${data.cityTown},${data.state},${data.country},${data.zipCode}"
        adrs=adrs.replace(",,",",")
        return adrs
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


        MaterialAlertDialogBuilder(this@OnGoingDeliveryActivity)
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

var imageName=""

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(data==null)
             return
         val uri: Uri? = data?.getData()
         // Initialize bitmap
         // Initialize bitmap
         try {
             val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
             // initialize byte stream

             binding.imgPhoto.setImageBitmap(bitmap)


             val returnCursor: Cursor = contentResolver.query(uri!!, null, null, null, null)!!
             val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
             returnCursor!!.moveToFirst()
             imageName = returnCursor!!.getString(nameIndex)
             returnCursor!!.close()

             *//* binding.imageView?.let {
                 Glide.with(applicationContext)
                     .load(bitmap)
                     .into(it)
             };*//*

            uploadImage(uri)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Image pick error","image Pick Error ${e.printStackTrace()}")
        }
    }
*/
    fun uploadImage(uri:File){
        val dataManager = DataManager.getDataManager()
        var user_id=   Preferences.getUserID(applicationContext)
        val dialog = CustomDialog(this@OnGoingDeliveryActivity) // Consider making dialog a class member if used often
        dialog.showDialog(this@OnGoingDeliveryActivity, false)

        (uri)?.let {
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

                        if(image1.isEmpty()) {

                            image1 = model!!.data!!.imageUrl.toString()
                            Glide.with(applicationContext)
                                .load(image1)
                                .into(binding.imgOne)
                            imagesArray.add(image1)
                        }
                        else {
                            image2 = model!!.data!!.imageUrl.toString()
                            Glide.with(applicationContext)
                                .load(image2)
                                .into(binding.imgTwo)
                            imagesArray.add(image1)
                        }

                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<ImageUploadMainRes>, t: Throwable) {
                    dialog.closeDialog()
                    Utils.showMessage("${t.message}",applicationContext)
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            },"delivery_order_$order_id_number",user_id.toString())
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

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri, handleOrientation: Boolean = true): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        inputStream?.use {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, options)

            contentResolver.openInputStream(uri)?.use { streamForDecode ->
                options.inSampleSize = calculateInSampleSize(options, TARGET_IMAGE_WIDTH_DELIVERY, TARGET_IMAGE_WIDTH_DELIVERY)
                options.inJustDecodeBounds = false
                var bitmap = BitmapFactory.decodeStream(streamForDecode, null, options)

                if (bitmap != null && handleOrientation) {
                    bitmap = rotateImageIfRequired(bitmap, uri)
                }
                return bitmap
            }
        }
        return null
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap {
        val inputStream: InputStream? = contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface = if (Build.VERSION.SDK_INT > 23) {
            inputStream?.let { ExifInterface(it) } ?: return img
        } else {
            selectedImage.path?.let { ExifInterface(it) } ?: return img
        }
        inputStream?.close()

        val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        // Only recycle if the original img is not the rotatedImg and you are sure the original is not needed.
        if (img != rotatedImg) {
            // img.recycle() // Be cautious with recycling if 'img' might be used elsewhere.
            // If 'img' is a temporary bitmap just for rotation, then it's safer.
        }
        return rotatedImg
    }

    private fun resizeAndCompressBitmap(bitmap: Bitmap, targetWidth: Int, quality: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val scaleFactor = if (originalWidth > targetWidth) {
            targetWidth.toFloat() / originalWidth.toFloat()
        } else {
            1.0f
        }
        val targetHeight = (originalHeight * scaleFactor).toInt()

        val resizedBitmap = if (scaleFactor != 1.0f) {
            Bitmap.createScaledBitmap(bitmap, (originalWidth * scaleFactor).toInt(), targetHeight, true)
        } else {
            bitmap
        }

        val outputStream =
            ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBitmapData = outputStream.toByteArray()
        val finalBitmap = BitmapFactory.decodeByteArray(compressedBitmapData, 0, compressedBitmapData.size)

        if (resizedBitmap != bitmap && resizedBitmap != finalBitmap) {
            resizedBitmap.recycle()
        }
        //    if (bitmap != finalBitmap && bitmap != resizedBitmap) {
        //        bitmap.recycle(); // Again, be cautious.
        //    }
        return finalBitmap
    }

    /**
     * Converts a Bitmap to a File for uploading.
     */
    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File? {
        return try {
            val file = File(cacheDir, fileName)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY_DELIVERY, fos)
            fos.flush()
            fos.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}