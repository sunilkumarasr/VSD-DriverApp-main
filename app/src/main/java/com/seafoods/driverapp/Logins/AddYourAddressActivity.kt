package com.seafoods.driverapp.Logins

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.OTPResponse
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityAddYourAddressBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddYourAddressActivity : AppCompatActivity() {

    val binding: ActivityAddYourAddressBinding by lazy {
        ActivityAddYourAddressBinding.inflate(layoutInflater)
    }

    var house_no=""
    var floor=""
    var area=""
    var landmark=""
    var city=""
    var country=""
    var state=""
    var zipcode=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)

        inits()

    }

    private fun inits() {
        Log.e("Details","$house_no $floor $area $city $country $state $zipcode")
        binding.linearVerify.setOnClickListener {
            house_no=binding.editHouse.text.toString().trim()
            floor=binding.editFloor.text.toString().trim()
            area=binding.editArea.text.toString().trim()
            landmark=binding.editLandmark.text.toString().trim()
            city=binding.editCity.text.toString().trim()
            country=binding.editCountry.text.toString().trim()
            state=binding.editState.text.toString().trim()
            zipcode=binding.editZipcode.text.toString().trim()

            if(house_no.isEmpty()||floor.isEmpty()||area.isEmpty()||city.isEmpty()
                ||country.isEmpty()||state.isEmpty()||zipcode.isEmpty())
            {
                Utils.showMessage("Please fill all details",applicationContext)
                return@setOnClickListener
            }
           addAddress()
        }

    }
    fun addAddress() {
        val dialog= CustomDialog(applicationContext)
        // Obtain the DataManager instance
        dialog.showDialog(this@AddYourAddressActivity,false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: OTPResponse? = response.body()

                    // Handle the response

                    model?.message?.let { Utils.showMessage(it,applicationContext) }

                    var flag=model!!.user_data.flag!!
Preferences.saveProfileStatus(applicationContext,flag)
                    ViewController.profileNavigateScreen(applicationContext)
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
                dialog.closeDialog()
            }
        }

        // Call the sendOtp function in DataManager
        Preferences.getUserID(applicationContext)?.let {
            dataManager.addAddress(otpCallback,
                it,house_no,floor,area,landmark,city,country,state,zipcode)
        }
    }
}