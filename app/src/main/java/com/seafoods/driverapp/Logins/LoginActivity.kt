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
import com.seafoods.driverapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    var mobile=""
    val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.loginBg), false,binding.root)

        inits()
    }



    private fun inits() {
        binding.linearRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        binding.linearLogin.setOnClickListener {

            mobile=   binding.editPhone.text.toString().trim()
            if(mobile.isEmpty()||mobile.length<10)
            {
                Utils.showMessage("Please enter valid mobile number",applicationContext)
                return@setOnClickListener
            }
            if(!NetWorkConection.isNEtworkConnected(this@LoginActivity))
            {
                Utils.showMessage("Please check Network connection",applicationContext)
                return@setOnClickListener
            }
            LoginService()
        }

        binding.linearRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    fun LoginService() {
        val dialog= CustomDialog(applicationContext)
        // Obtain the DataManager instance
        dialog.showDialog(this@LoginActivity,false)
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

                            val intent=Intent(this@LoginActivity, OTPActivity::class.java)
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