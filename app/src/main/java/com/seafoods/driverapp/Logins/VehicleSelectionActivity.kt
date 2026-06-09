package com.seafoods.driverapp.Logins

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.seafoods.driverapp.Adapters.VehicleAdapter
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.OTPResponse
import com.seafoods.driverapp.VehiclesRes
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityVehicleSelectionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleSelectionActivity : AppCompatActivity() {

    lateinit var adapter: VehicleAdapter
    val binding: ActivityVehicleSelectionBinding by lazy {
        ActivityVehicleSelectionBinding.inflate(layoutInflater)
    }
    var vehicle_type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.white),
            true,
            binding.root
        )
        binding.imgBack.setOnClickListener {
            finish()
        }
        adapter = VehicleAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.recycler.adapter = adapter

        inits()
        getVehicleList()
    }


    private fun inits() {
        binding.linearContinue.setOnClickListener {
            vehicle_type = adapter.selected_id
            if (vehicle_type.isEmpty()) {
                Utils.showMessage("Please select Vehicle", applicationContext)
                return@setOnClickListener
            }
            setVehicleType()
            //startActivity(Intent(this@VehicleSelectionActivity, UploadDocActivity::class.java))
        }


    }

    fun getVehicleList() {
        val dialog = CustomDialog(applicationContext)
        // Obtain the DataManager instance
        dialog.showDialog(this@VehicleSelectionActivity, false)
        val dataManager = DataManager.getDataManager()

        // Create a callback for handling the API response
        val otpCallback = object : Callback<VehiclesRes> {
            override fun onResponse(call: Call<VehiclesRes>, response: Response<VehiclesRes>) {
                dialog.closeDialog()
                if (response.isSuccessful) {
                    val model: VehiclesRes? = response.body()

                    // Handle the response

                    //  model?.message?.let { Utils.showMessage(it,applicationContext) }

                    adapter.setData(model!!.data)
                    //startActivity(Intent(this@VehicleSelectionActivity, PersonalInformationActivity::class.java))
                    // overridePendingTransition(0, 0)
                    println("OTP Sent successfully: ${model?.message}")
                } else {
                    // Handle error
                    println("Failed to send OTP. ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VehiclesRes>, t: Throwable) {
                // Handle failure
                println("Failed to send OTP. ${t.message}")
                dialog.closeDialog()
            }
        }

        // Call the sendOtp function in DataManager
        dataManager.vehicleList(otpCallback)
    }

    fun setVehicleType() {
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.setVehicleType(object : Callback<OTPResponse> {
                override fun onResponse(
                    call: Call<OTPResponse>,
                    response: Response<OTPResponse>
                ) {

                    Log.e("response.body()", "response.body() ${response.body()}")
                    if (response.body()?.status == true) {
                        val model: OTPResponse? = response.body()

                        var flag = model!!.data.flag
                        Preferences.saveProfileStatus(applicationContext, flag)
                        ViewController.profileNavigateScreen(applicationContext)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    Log.e("response.body()", "response.body() ")

                }

                override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                    Log.e("response.body()", "response.body() ${t.printStackTrace()}")
                }

            }, it, vehicle_type)
        }
    }
}