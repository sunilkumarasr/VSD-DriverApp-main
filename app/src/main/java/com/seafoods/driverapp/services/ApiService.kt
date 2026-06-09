package com.gadiwalaUser.services



import com.seafoods.driverapp.DriverStatus
import com.seafoods.driverapp.ImageUploadMainRes
import com.seafoods.driverapp.LoginResponse
import com.seafoods.driverapp.MainResponse
import com.seafoods.driverapp.OTPResponse
import com.seafoods.driverapp.OrderResponse
import com.seafoods.driverapp.PrivacyDataMainRes
import com.seafoods.driverapp.ProfileResponse
import com.seafoods.driverapp.VehiclesRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("api/send_otp")
    fun login(@Field("api_key") api_key: String,@Field("phone") mobile_number: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/verify_otp")
    fun verifyOtp(@Field("api_key") api_key: String,@Field("phone") mobile_number: String,@Field("otp") otp: String): Call<OTPResponse>
   @FormUrlEncoded
    @POST("api/driver_admin_verified")
    fun checkDriverStatus(@Field("api_key") api_key: String,@Field("driver_id") id: String): Call<DriverStatus>
    @FormUrlEncoded
    @POST("api/pages_list")
    fun privacyTermsData(
        @Field("api_key") api_key: String,
        @Field("page_name") pagename: String,//terms-and-condition,privacy-policy,shipping-policy,about-us
    ): Call<PrivacyDataMainRes>


    @FormUrlEncoded
    @POST("api/vehicle_list")
    fun vehicleList(
        @Field("api_key") api_key: String,

    ): Call<VehiclesRes>


    @FormUrlEncoded
    @POST("api/driver_update_personal_info")
    fun updatePersonalInfo(
        @Field("api_key") api_key: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("dob") dob: String,
        @Field("blood_group") blood_group: String,

        @Field("id") id: String,
    ): Call<OTPResponse>



    @FormUrlEncoded
    @POST("api/update_vehicle_type")
    fun setVehicleType(
        @Field("api_key") api_key: String,
        @Field("id") id: String,
        @Field("vehicle_type") vehicle_type: String,

        ): Call<OTPResponse>



    @FormUrlEncoded
    @POST("api/driver_upload_document")
    fun uploadDoc(
        @Field("api_key") apiKey: String,
        @Field("id") id: String,
        @Field("aadhar_front") aadhar_front: String,
        @Field("aadhar_back") aadhar_back: String,
        @Field("pancard") pancard: String,
        @Field("driving_license") driving_license: String,
        @Field ("vehicle_rc") vehicle_rc:  String,
    ): Call<OTPResponse>

    @FormUrlEncoded
    @POST("api/driver_profile_details")
    fun getProfile(
        @Field("api_key") apiKey: String,
        @Field("driver_id") id: String,

    ): Call<ProfileResponse>

    @FormUrlEncoded
    @POST("api/upload_profile_image")
    fun uploadProfileImage(
        @Field("api_key") apiKey: String,
        @Field("id") id: String,
        @Field("profile_image") profile_image: String,

    ): Call<OTPResponse>



    @FormUrlEncoded
    @POST("api/driver_upload_personal_info")
    fun uploadProfileDetails(
        @Field("api_key") apiKey: RequestBody,
        @Field("id") id: RequestBody,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("dob") dob: String,
        @Field("blood_group") blood_group: String,

        ): Call<VehiclesRes>

    @FormUrlEncoded
    @POST("api/driver_update_address")
    fun updateAddress(
        @Field("api_key") apiKey: String,
        @Field("id") id: String,
        @Field("house_no") house_no: String,
        @Field("floor") floor: String,
        @Field("area") area: String,
        @Field("landmark") landmark: String,
        @Field("city") city: String,
        @Field("country") country: String,
        @Field("state") state: String,
        @Field("zipcode") zipcode: String,

        ): Call<OTPResponse>

    @Multipart
    @POST("api/driver_upload") // Replace "upload" with your actual API endpoint
    fun uploadImage(
        @Part("api_key") apiKey: RequestBody,
        @Part("select_type") id: RequestBody,
        @Part("user_id") user_id: RequestBody,

        @Part imageFile: MultipartBody.Part
    ): Call<ImageUploadMainRes>


    @FormUrlEncoded
    @POST("api/get_driver_orders")
    fun getOrders(
        @Field("api_key") apiKey: String,
        @Field("driver_id") id: String,


        ): Call<OrderResponse>

    @FormUrlEncoded
    @POST("api/driver_checkStatus")
    fun getDriverStatus(
        @Field("api_key") apiKey: String,
        @Field("driver_id") id: String,


        ): Call<MainResponse>

    @FormUrlEncoded
    @POST("api/get_order_details")
    fun getOrderDetails(
        @Field("api_key") apiKey: String,
        @Field("id") id: String,
        @Field("user_id") user_id: String,


        ): Call<OrderResponse>

    @FormUrlEncoded
    @POST("api/update_order_status")
    fun updateOrderStatus(
        @Field("api_key") apiKey: String,
        @Field("order_id") order_id: String,
        @Field("order_status") status: String,
        @Field("comment") comment: String,
        @Field("image0") image1: String,
        @Field("image1") image2: String,
        @Field("image[]") image: ArrayList<String>,


        ): Call<LoginResponse>
    @FormUrlEncoded
    @POST("api/get_driver_order_delivery_details")
    fun getDeliveryOrders(
        @Field("api_key") apiKey: String,
        @Field("driver_id") driver_id: String,
        @Field("order_status") status: String,

        ): Call<OrderResponse>


    @FormUrlEncoded
    @POST("api/driver_support")
    fun support(
        @Field("api_key") apiKey: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("subject") subject: String,
        @Field("message") message: String,


        ): Call<LoginResponse>
}