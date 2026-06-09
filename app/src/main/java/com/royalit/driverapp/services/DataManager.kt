package com.royalit.driverapp


import com.gadiwalaUser.services.ApiService
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.text.toLowerCase

class DataManager private constructor() {


    companion object {
        val ROOT_URL = "https://ricebazaar.co.in/admin/"
        //val ROOT_URL = "https://ritps.com/the_rice_company/"
        val RUPEE = "₹"
        val APIKEY = "the_rice_company_7s736V2J2iB549214s40i3Lz77I0297L"
        var ORDER_STATUS =""//1 Confirmed, 2 Shipped, 3 Out for Delivery, 4 Delivered, 5 Reached Customer
        private var dataManager: DataManager? = null
        @JvmStatic
        fun getDataManager(): DataManager {
            if (dataManager == null) {
                dataManager = DataManager()
            }
            return dataManager as DataManager
        }
    }

    private val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
// set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
// add your other interceptors …

        httpClient.callTimeout(5, TimeUnit.MINUTES)
        httpClient.readTimeout(5, TimeUnit.MINUTES)

// add logging as the last interceptor
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder().baseUrl(ROOT_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
    }
    fun login(cb: Callback<LoginResponse>, mobile: String) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.login(APIKEY,mobile)
        call.enqueue(cb)
    }
    fun verifyOtp(cb: Callback<OTPResponse>, mobile: String, otp: String) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.verifyOtp(APIKEY,mobile,otp)
        call.enqueue(cb)
    }
    fun checkDriverStatus(cb: Callback<DriverStatus>, driver_id: String) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.checkDriverStatus(APIKEY,driver_id)
        call.enqueue(cb)
    }
    fun vehicleList(cb: Callback<VehiclesRes>) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.vehicleList(APIKEY)
        call.enqueue(cb)
    }

    fun updatePersonalInfo(
        cb: Callback<OTPResponse>,
        first_name: String,
        last_name: String,
        dob: String,
        bllod_group: String, user_id: String,

        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.updatePersonalInfo(
            APIKEY,

            first_name,
            last_name,
            dob,
            bllod_group,

            user_id,
        )
        call.enqueue(cb)
    }


    fun uploadProfileImage(
        cb: Callback<OTPResponse>,
        user_id: String,
        profile_image: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.uploadProfileImage(
            APIKEY,
            user_id,
            profile_image ,

        )
        call.enqueue(cb)
    }


    fun setVehicleType(
        cb: Callback<OTPResponse>,
        user_id: String,
        vehicle_type: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.setVehicleType(
            APIKEY,
            user_id,
            vehicle_type ,

        )
        call.enqueue(cb)
    }



    fun addAddress(
        cb: Callback<OTPResponse>,
        id: String,
        house_no: String,
        floor: String,
        area: String,
        landmark: String,
        city: String,
        country: String,
        state: String,
        zipcode: String,

        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.updateAddress(APIKEY,
            id,house_no,floor,area,landmark,city,country,state,zipcode
        )
        call.enqueue(cb)
    }

    fun uploadDoc(
        cb: Callback<OTPResponse>,
        id: String,
        aadhar_front: String,
        aadhar_back: String,
        pancard: String,
        driving_license: String,
        vehicle_rc: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.uploadDoc(APIKEY,
            id,aadhar_front,aadhar_back,pancard,driving_license,vehicle_rc
        )
        call.enqueue(cb)
    }
    fun getProfile(
        cb: Callback<ProfileResponse>,
        id: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getProfile(APIKEY,
            id
        )
        call.enqueue(cb)
    }
    fun getOrders(
        cb: Callback<OrderResponse>,
        id: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getOrders(APIKEY,
            id
        )
        call.enqueue(cb)
    }
    fun getDeliveryOrders(
        cb: Callback<OrderResponse>,
        id: String,
        status: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getDeliveryOrders(APIKEY,
            id, status
        )
        call.enqueue(cb)
    }

    fun getOrderDetails(
        cb: Callback<OrderResponse>,
        id: String,
        user_id: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getOrderDetails(APIKEY,
            id,
            user_id,
        )
        call.enqueue(cb)
    }

    fun updateOrderStatus(
        cb: Callback<LoginResponse>,
        id: String,
        status: String,
        comment: String,
        image1: String,
        image2: String,
        image: ArrayList<String>,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.updateOrderStatus(APIKEY,
            id,status,comment,image1,image2,image
        )
        call.enqueue(cb)
    }

    fun privacyTermsData(cb: Callback<PrivacyDataMainRes>,pagename:String) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.privacyTermsData(APIKEY,pagename)
        call.enqueue(cb)
    }

    fun support(
        cb: Callback<LoginResponse>,
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.support(APIKEY,
            name,email,phone,subject,message
        )
        call.enqueue(cb)
    }

    fun fileUpload(imageFile: File, cb: Callback<ImageUploadMainRes>,doc_type:String="profile",user_id:String){

        val imagePart = imageFile.toImageRequestBody("image")
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.uploadImage(APIKEY.toTextRequestBody(),doc_type.toTextRequestBody(),user_id.toTextRequestBody(),imagePart
        )
        call.enqueue(cb)
    }
    fun String.toTextRequestBody(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }


    fun File.toImageRequestBody(partName: String): MultipartBody.Part {
        // Determine the MIME type from the file extension, or use a generic one
        val mimeType = when (this.extension.toLowerCase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            // Add more image types as needed
            else -> "image/*" // Fallback to a generic image MIME type
        }

        val requestFile = this.asRequestBody(mimeType.toMediaTypeOrNull())

        // Create the MultipartBody.Part
        // 'partName' is the name of the form field your server expects for the file
        // 'this.name' is the actual filename that will be sent
        return MultipartBody.Part.createFormData(partName, this.name, requestFile)
    }



    fun getDriverStatus(
        cb: Callback<MainResponse>,
        id: String,


        ) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getDriverStatus(APIKEY,
            id
        )
        call.enqueue(cb)
    }
}
