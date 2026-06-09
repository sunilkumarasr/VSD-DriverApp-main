package com.seafoods.driverapp.Activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.DriverStatus
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirverstatusVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dirverstatus_verification)
        val imgBack=findViewById<View>(R.id.imgBack)
        imgBack.setOnClickListener {
            finish()
        }
        checkDriverStatus()
    }
    fun checkDriverStatus() {
        Preferences.getUserID(applicationContext)?.let {

            val dialog = CustomDialog(applicationContext)
            // Obtain the DataManager instance
            dialog.showDialog(this@DirverstatusVerificationActivity, false)
            val dataManager = DataManager.getDataManager()

            // Create a callback for handling the API response
            val otpCallback = object : Callback<DriverStatus> {
                override fun onResponse(
                    call: Call<DriverStatus>,
                    response: Response<DriverStatus>
                ) {
                    dialog.closeDialog()
                    if (response.isSuccessful) {
                        val model: DriverStatus? = response.body()

                        // Handle the response

                        model?.message?.let { Utils.showMessage(it, applicationContext) }

                        if (model!!.status == true) {
                            if(model!!.driver_status==1) {
                                Preferences.saveDriverStatus(applicationContext,1)
                                intent = Intent(applicationContext, DashBoardActivity::class.java)

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                                startActivity(intent)
                                finish()
                            }
                        }

                    } else {
                        // Handle error
                        println("Failed to send OTP. ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DriverStatus>, t: Throwable) {
                    // Handle failure
                    println("Failed to send OTP. ${t.message}")
                    dialog.closeDialog()
                }
            }

            // Call the sendOtp function in DataManager
            dataManager.checkDriverStatus(otpCallback,it)
        }
    }
}