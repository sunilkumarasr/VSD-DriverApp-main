package com.seafoods.driverapp.Logins

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.LoginResponse
import com.seafoods.driverapp.NetWorkConection
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    var mobile=""

    val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.loginBg), false,binding.root)

        inits()
    }

    private fun inits() {

        binding.linearLogin.setOnClickListener {

            val intent=Intent(this@RegisterActivity, LoginActivity::class.java)
             startActivity(intent)
            overridePendingTransition(0, 0)

        }
        binding.linearVerify.setOnClickListener {
            mobile=   binding.editMobile.text.toString().trim()
            if(mobile.isEmpty()||mobile.length<10)
            {
                Utils.showMessage("Please enter valid mobile number",applicationContext)
                return@setOnClickListener
            }
            if(!NetWorkConection.isNEtworkConnected(this@RegisterActivity))
            {
                Utils.showMessage("Please check Network connection",applicationContext)
                return@setOnClickListener
            }
            LoginService()

        }

    }
    fun LoginService() {
        val dialog= CustomDialog(applicationContext)
        // Obtain the DataManager instance
        dialog.showDialog(this@RegisterActivity,false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: LoginResponse? = response.body()

                    // Handle the response

                    model?.message?.let { Utils.showMessage(it,applicationContext) }

                    if(model?.status == true)
                    {
                        model.otp?.let {

                            val intent=Intent(this@RegisterActivity, OTPActivity::class.java)
                            intent.putExtra("otp",it)
                            intent.putExtra("mobile",mobile)

                            startActivity(intent)
                            finish()
                        }
                    }
                    println("OTP Sent successfully: ${model?.message}")
                } else {
                    // Handle error
                    println("Failed to send OTP. ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle failure
                println("Failed to send OTP. ${t.message}")
                dialog.closeDialog()
            }
        }

        // Call the sendOtp function in DataManager
        dataManager.login(otpCallback,mobile)
    }
}