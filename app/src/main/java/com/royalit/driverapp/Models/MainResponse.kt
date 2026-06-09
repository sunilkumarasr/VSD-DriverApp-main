package com.royalit.driverapp

import com.google.gson.annotations.SerializedName

data class ProfileMainResponse(

    @SerializedName("Status"   ) var Status   : Boolean?            = null,
    @SerializedName("Message"  ) var Message  : String?             = null,
   // @SerializedName("Response" ) var profileModel : ArrayList<ProfileModel> = arrayListOf(),
    @SerializedName("code"     ) var code     : Int?                = null

)

data class MainResponse(

    @SerializedName("status"   ) var Status   : Boolean?            = null,
    @SerializedName("message"  ) var Message  : String?             = null,
    @SerializedName("code"     ) var code     : Int?                = null

)
data class LoginResponse (

    @SerializedName("status"  ) var status  : Boolean = false,
    @SerializedName("message" ) var message : String  = "",
    @SerializedName("otp"     ) var otp     : String?     = null

)


data class OTPResponse (

    @SerializedName("status"  ) var status  : Boolean = false,
    @SerializedName("message" ) var message : String  = "",
    @SerializedName("user_data" ) var user_data : UserData  ,
    @SerializedName("data" ) var data : UserData  ,
    @SerializedName("flag" ) var flag : Int  = -1,

)

data class ProfileResponse (

    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var user_data    : ArrayList<UserData> = arrayListOf()

)
data class DriverStatus (

    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("driver_id" ) var driver_id : String?         = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("admin_verified" ) var driver_status : Int        = 0,

)



data class PrivacyDataMainRes (

    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<PrivacyData> = arrayListOf()

)
data class PrivacyData (

    @SerializedName("id"                ) var id               : String? = null,
    @SerializedName("information_title" ) var informationTitle : String? = null,
    @SerializedName("description"       ) var description      : String? = null

)

data class VehiclesRes (

    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<Vehicles> = arrayListOf()

)

data class Vehicles (

    @SerializedName("id"            ) var id           : String? = null,
    @SerializedName("vehicle"       ) var vehicle      : String? = null,
    @SerializedName("vehicle_image" ) var vehicleImage : String? = null,
    var isSelected : Boolean= false

)
data class UserData (

    @SerializedName("id"              ) var id             : String? = null,
    @SerializedName("first_name"      ) var firstName      : String? = null,
    @SerializedName("last_name"       ) var lastName       : String? = null,
    @SerializedName("dob"             ) var dob            : String? = null,
    @SerializedName("phone"           ) var phone          : String? = null,
    @SerializedName("blood_group"     ) var bloodGroup     : String? = null,
    @SerializedName("house_no"        ) var houseNo        : String? = null,
    @SerializedName("floor"           ) var floor          : String? = null,
    @SerializedName("area"            ) var area           : String? = null,
    @SerializedName("landmark"        ) var landmark       : String? = null,
    @SerializedName("city"            ) var city           : String? = null,
    @SerializedName("country"         ) var country        : String? = null,
    @SerializedName("state"           ) var state          : String? = null,
    @SerializedName("zipcode"         ) var zipcode        : String? = null,
    @SerializedName("profile_image"   ) var profileImage   : String? = null,
    @SerializedName("vehicle_type"    ) var vehicleType    : String? = null,
    @SerializedName("aadhar_front"    ) var aadharFront    : String? = null,
    @SerializedName("aadhar_back"     ) var aadharBack     : String? = null,
    @SerializedName("pancard"         ) var pancard        : String? = null,
    @SerializedName("driving_license" ) var drivingLicense : String? = null,
    @SerializedName("vehicle_rc"      ) var vehicleRc      : String? = null,
    @SerializedName("created_at"      ) var createdAt      : String? = null,
    @SerializedName("created_by"      ) var createdBy      : String? = null,
    @SerializedName("updated_at"      ) var updatedAt      : String? = null,
    @SerializedName("updated_by"      ) var updatedBy      : String? = null,
    @SerializedName("status"          ) var status         : String? = null,
    @SerializedName("otp"             ) var otp            : String? = null,
    @SerializedName("emp_id"             ) var emp_id            : String? = null,
    @SerializedName("flag" ) var flag : Int  = -1,


    )


data class ImageUploadMainRes (

    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : ImageUploadRes?    = ImageUploadRes()

)
data class ImageUploadRes (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : String? = null,
    @SerializedName("select_type"  ) var selectType  : String? = null,
    @SerializedName("image_url"    ) var imageUrl    : String? = null,
    @SerializedName("created_date" ) var createdDate : String? = null

)



data class OrderResponse (

    @SerializedName("status"  ) var status  : Boolean?          = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("orders"  ) var orders  : ArrayList<Orders> = arrayListOf()

    )

data class Orders (

    @SerializedName("id"                  ) var id                : String?             = null,
    @SerializedName("order_id"            ) var orderId           : String?             = null,
    @SerializedName("payment_id"          ) var paymentId         : String?             = null,
    @SerializedName("customer_address_id" ) var customerAddressId : String?             = null,
    @SerializedName("amount"              ) var amount            : String?             = null,
    @SerializedName("product_id"          ) var productId         : String?             = null,
    @SerializedName("qty"                 ) var qty               : String?             = null,
    @SerializedName("slotid"              ) var slotid            : String?             = null,
    @SerializedName("cartid"              ) var cartid            : String?             = null,
    @SerializedName("created_at"          ) var createdAt         : String?             = null,
    @SerializedName("status"              ) var status            : String?             = null,
    @SerializedName("full_name"           ) var fullName          : String?             = null,
    @SerializedName("mobile"              ) var mobile            : String?             = null,
    @SerializedName("house_no"            ) var houseNo           : String?             = null,
    @SerializedName("floor"               ) var floor             : String?             = null,
    @SerializedName("landmark"            ) var landmark          : String?             = null,
    @SerializedName("city_town"           ) var cityTown          : String?             = null,
    @SerializedName("state"               ) var state             : String?             = null,
    @SerializedName("country"             ) var country           : String?             = null,
    @SerializedName("zip_code"            ) var zipCode           : String?             = null,
    @SerializedName("latitude"            ) var latitude          : String?             = null,
    @SerializedName("longitude"           ) var longitude         : String?             = null,
    @SerializedName("slot_date"           ) var slotDate          : String?             = null,
    @SerializedName("start_time"          ) var startTime         : String?             = null,
    @SerializedName("end_time"            ) var endTime           : String?             = null,
    @SerializedName("order_status"            ) var order_status           : String?             = null,
    @SerializedName("gst_charges"            ) var gst_charges           : String?             = null,
    @SerializedName("coupons_name"            ) var coupons_name           : String?             = null,
    @SerializedName("coupon_code"            ) var coupon_code           : String?             = null,
    @SerializedName("products"            ) var products          : ArrayList<Products> = arrayListOf()

)
data class Products (

    @SerializedName("id"            ) var id           : String? = null,
    @SerializedName("product_id"    ) var productId    : String? = null,
    @SerializedName("product_title" ) var productTitle : String? = null,
    @SerializedName("product_image" ) var productImage : String? = null,
    @SerializedName("mrp_price"     ) var mrpPrice     : String? = null,
    @SerializedName("market_price"  ) var marketPrice  : String? = null,
    @SerializedName("our_price"     ) var ourPrice     : String? = null,
    @SerializedName("qty"           ) var qty          : String? = null,
    @SerializedName("user_rating"   ) var userRating   : String? = null,

)

