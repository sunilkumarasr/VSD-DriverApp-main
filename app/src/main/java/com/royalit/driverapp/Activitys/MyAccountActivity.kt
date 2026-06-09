package com.royalit.driverapp.Activitys

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.royalit.driverapp.Config.Preferences
import com.royalit.driverapp.Config.ViewController
import com.royalit.driverapp.CustomDialog
import com.royalit.driverapp.DataManager
import com.royalit.driverapp.Logins.AddYourProfilePicActivity
import com.royalit.driverapp.OTPResponse
import com.royalit.driverapp.ProfileResponse
import com.royalit.driverapp.R
import com.royalit.driverapp.UserData
import com.royalit.driverapp.Utils
import com.royalit.driverapp.databinding.ActivityMyAccountBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

    val binding: ActivityMyAccountBinding by lazy {
        ActivityMyAccountBinding.inflate(layoutInflater)
    }
    var user_id = ""
    var selectedDate = ""
    var first_name = ""
    var last_name = ""
    var dob = ""
    var bllog_group = ""
    var mobile = ""
    var house_no = ""
    var floor = ""
    var area = ""
    var landmark = ""
    var city = ""
    var country = ""
    var state = ""
    var zipcode = ""
    lateinit var user: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.white),
            true,
            binding.root
        )
        user_id = Preferences.getUserID(applicationContext).toString()
        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "My Account"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }
        binding.imgPick.setOnClickListener {

            val intent = Intent(applicationContext, AddYourProfilePicActivity::class.java)
            intent.putExtra("isFrom", "1")
            intent.putExtra("mobile", mobile)
            startActivityForResult(intent, 200)
        }
        if (!ViewController.noInterNetConnectivity(applicationContext)) {
            ViewController.showToast(applicationContext, "Please check your connection ")
        } else {
            //myAccountApi()
        }

        binding.imgAddressEdit.setOnClickListener {
            AddressDialog()
        }

        binding.imgEditProfile.setOnClickListener {
            ProfileDialog()
        }
        getProfileDetails()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getProfileDetails()
    }

    private fun AddressDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@MyAccountActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_editaddress, null)

        var edit_house = view.findViewById<EditText>(R.id.edit_house)
        var edit_floor = view.findViewById<EditText>(R.id.edit_floor)
        var edit_area = view.findViewById<EditText>(R.id.edit_area)
        var edit_landmark = view.findViewById<EditText>(R.id.edit_landmark)
        var edit_city = view.findViewById<EditText>(R.id.edit_city)
        var edit_country = view.findViewById<EditText>(R.id.edit_country)
        var edit_state = view.findViewById<EditText>(R.id.edit_state)
        var edit_zipcode = view.findViewById<EditText>(R.id.edit_zipcode)
        if (user != null) {
            edit_house.setText("${user.houseNo}")
            edit_floor.setText("${user.floor}")
            edit_area.setText("${user.area}")
            edit_landmark.setText("${user.landmark}")
            edit_city.setText("${user.city}")
            edit_country.setText("${user.country}")
            edit_state.setText("${user.state}")
            edit_zipcode.setText("${user.zipcode}")
        }
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<TextView>(R.id.linearCancel)
        val linearUpload = view.findViewById<TextView>(R.id.linearUpload)
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {

            house_no = edit_house.text.toString().trim()
            floor = edit_floor.text.toString().trim()
            area = edit_area.text.toString().trim()
            landmark = edit_landmark.text.toString().trim()
            city = edit_city.text.toString().trim()
            country = edit_country.text.toString().trim()
            state = edit_state.text.toString().trim()
            zipcode = edit_zipcode.text.toString().trim()

            if (house_no.isEmpty() || floor.isEmpty() || area.isEmpty() || city.isEmpty()
                || country.isEmpty() || state.isEmpty() || zipcode.isEmpty()
            ) {
                Utils.showMessage("Please fill all details", applicationContext)
                return@setOnClickListener
            }
            bottomSheetDialog.dismiss()
            addAddress()


        }
        bottomSheetDialog.show()
    }

    fun addAddress() {
        val dialog = CustomDialog(applicationContext)
        // Obtain the DataManager instance
        // dialog.showDialog(this@MyAccountActivity,false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                //dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: OTPResponse? = response.body()

                    // Handle the response

                    model?.message?.let { Utils.showMessage(it, applicationContext) }


                    // startActivity(Intent(this@AddYourAddressActivity, DocsActivity::class.java))
                    overridePendingTransition(0, 0)

                    finish()
                    println("OTP Sent successfully: ${model?.message}")
                } else {
                    // Handle error
                    println("Failed to send OTP. ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                // Handle failure
                println("Failed to send OTP. ${t.message}")
                // dialog.closeDialog()
            }
        }

        // Call the sendOtp function in DataManager
        Preferences.getUserID(applicationContext)?.let {
            dataManager.addAddress(
                otpCallback,
                it, house_no, floor, area, landmark, city, country, state, zipcode
            )
        }
    }

    private fun ProfileDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@MyAccountActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_editprofile, null)
        var edit_name = view.findViewById<EditText>(R.id.edit_name)
        var edit_last_name = view.findViewById<EditText>(R.id.edit_last_name)
        var edit_dob = view.findViewById<EditText>(R.id.edit_dob)
        var edit_mobile = view.findViewById<EditText>(R.id.edit_mobile)
        var edit_blood = view.findViewById<EditText>(R.id.edit_blood)
        if (user != null) {
            edit_name.setText("${user.firstName}")
            edit_last_name.setText("${user.lastName}")
            edit_blood.setText("${user.bloodGroup}")
            edit_mobile.setText("${user.phone}")
            edit_dob.setText("${user.dob}")
        }
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<TextView>(R.id.linearCancel)
        val linearUpload = view.findViewById<TextView>(R.id.linearUpload)
        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {
            first_name = edit_name.text.toString().trim()
            last_name = edit_last_name.text.toString().trim()
            dob = edit_dob.text.toString().trim()
            bllog_group = edit_blood.text.toString().trim()

            if (first_name.isEmpty() || last_name.isEmpty() || dob.isEmpty() || bllog_group.isEmpty()) {
                Utils.showMessage("Please fill all details", applicationContext)
                return@setOnClickListener
            }
            bottomSheetDialog.dismiss()
            updatePersonalDetails()

        }
        edit_dob.setOnClickListener {
            showDatePickerDialog(edit_dob)
        }

        bottomSheetDialog.show()
    }

    private fun showDatePickerDialog(view: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date
                selectedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                view.setText("$selectedDate")
            },
            year,
            month,
            day
        )

        // Set the minimum date
        val minCalendar = Calendar.getInstance()

        minCalendar.set(2025, Calendar.JANUARY, 1) // Example: Set minimum date to January 1, 2023
        datePickerDialog.datePicker.maxDate = minCalendar.timeInMillis

        datePickerDialog.show()
    }

    fun getProfileDetails() {
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.getProfile(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {

                    Log.e("response.body()", "response.body() ${response.body()}")
                    if (response.body()?.status == true) {
                        val model: ProfileResponse? = response.body()
                        if (model!!.user_data != null && model!!.user_data.size > 0) {
                            user = model!!.user_data.get(0)
                            user.let {
                                binding.txtName.setText("${it.lastName}")
                                binding.txtMobile.setText("${it.phone}")
                                //binding.txtEmail.setText("")
                                binding.txtDob.setText("${it.dob}")
                                binding.txtBlood.setText("${it.bloodGroup}")
                                binding.txtEmail.setText("${Preferences.getEMPID(applicationContext)}")

                                mobile = it.phone.toString()
                                val hno = it.houseNo
                                    ?: "" // Use actual property name, provide default if nullable
                                val floor = it.floor
                                    ?: "" // Use actual property name, provide default if nullable
                                val area = it.area
                                    ?: "" // Use actual property name, provide default if nullable
                                val landmark = it.landmark
                                    ?: "" // Use actual property name, provide default if nullable
                                val city = it.city ?: ""
                                val state = it.state ?: ""
                                val zipCode = it.zipcode ?: ""
                                val country =
                                    it.country ?: "" // Example: if you have a country field

                                Glide.with(applicationContext)
                                    .load(it.profileImage)
                                    .into(binding.imgProfile!!)
                                // Combine them into a single string, adding separators like commas or spaces as needed
                                // Filter out empty parts to avoid extra commas or spaces
                                val fullAddress = listOfNotNull(
                                    hno,
                                    floor,
                                    area,
                                    landmark,
                                    city,
                                    state,
                                    zipCode,
                                    country
                                )
                                    .filter { it.isNotBlank() } // Ensure only non-empty parts are joined
                                    .joinToString(", ")
                                binding.txtAddress.setText("${fullAddress}")
                            }
                        }


                    }
                    Log.e("response.body()", "response.body() ")

                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("response.body()", "response.body() ${t.printStackTrace()}")
                }

            }, it)
        }
    }

    fun updatePersonalDetails() {
        val dialog = CustomDialog(applicationContext)
        // Obtain the DataManager instance
        // dialog.showDialog(this@MyAccountActivity,false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                //  dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: OTPResponse? = response.body()

                    // Handle the response

                    model?.message?.let { Utils.showMessage(it, applicationContext) }
                    model!!.user_data.id?.let {
                        Preferences.saveUserId(applicationContext, it)
                        Preferences.saveUserName(applicationContext, model!!.user_data.firstName!!)
                        Preferences.saveMobile(applicationContext, model!!.user_data.phone!!)
                        Preferences.saveEMPID(applicationContext, model!!.user_data.emp_id!!)
                    }



                    overridePendingTransition(0, 0)
                    finish()
                    println("OTP Sent successfully: ${model?.message}")
                } else {
                    // Handle error
                    println("Failed to send OTP. ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                // Handle failure
                println("Failed to send OTP. ${t.message}")
                // dialog.closeDialog()
            }
        }

        // Call the sendOtp function in DataManager
        Preferences.getUserID(applicationContext)?.let {
            dataManager.updatePersonalInfo(
                otpCallback, first_name = first_name, last_name, dob, bllog_group,
                it
            )
        }
    }
}