package com.seafoods.driverapp.Logins

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.OTPResponse
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityPersonalInformationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInformationActivity : AppCompatActivity() {

    val binding: ActivityPersonalInformationBinding by lazy {
        ActivityPersonalInformationBinding.inflate(layoutInflater)
    }

    var first_name=""
    var last_name=""
    var dob=""
    var bllog_group=""
    var mobile=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)
        mobile=intent.getStringExtra("mobile").toString()

        inits()

    }

    private fun inits() {

        binding.editMobile.setText("$mobile")

        binding.editDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.imgCal.setOnClickListener {
            showDatePickerDialog()
        }
        binding.linearVerify.setOnClickListener {

            first_name=binding.editFname.text.toString().trim()
            last_name=binding.editLname.text.toString().trim()
            dob=binding.editDob.text.toString().trim()
            bllog_group=binding.editBlood.text.toString().trim()

           if(first_name.isEmpty()||last_name.isEmpty()||dob.isEmpty()||bllog_group.isEmpty())
           {
               Utils.showMessage("Please fill all details",applicationContext)
               return@setOnClickListener
           }
            updatePersonalDetails()
            //startActivity(Intent(this@PersonalInformationActivity, AddYourAddressActivity::class.java))
           // overridePendingTransition(0, 0)
        }

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date
                val selectedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                binding.editDob.setText( "$selectedDate")
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
    fun updatePersonalDetails() {
        val dialog= CustomDialog(applicationContext)
        // Obtain the DataManager instance
        dialog.showDialog(this@PersonalInformationActivity,false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: OTPResponse? = response.body()

                    // Handle the response

                    model?.message?.let { Utils.showMessage(it,applicationContext) }
                    model!!.user_data.id?.let {
                        Preferences.saveUserId(applicationContext, it)
                        Preferences.saveUserName(applicationContext, model!!.user_data.firstName!!)
                        Preferences.saveMobile(applicationContext,  model!!.user_data.phone!!)
                        Preferences.saveEMPID(applicationContext,  model!!.user_data.emp_id!!)
                    }
                    var flag=model.user_data.flag
                    Preferences.saveProfileStatus(applicationContext, flag)
                    ViewController.profileNavigateScreen(applicationContext)
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
            dataManager.updatePersonalInfo(otpCallback,first_name,last_name,dob,bllog_group,
                it
            )
        }
    }
}