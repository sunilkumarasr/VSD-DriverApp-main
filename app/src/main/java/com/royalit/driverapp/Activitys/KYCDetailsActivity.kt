package com.royalit.driverapp.Activitys

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.royalit.driverapp.Config.Preferences
import com.royalit.driverapp.Config.ViewController
import com.royalit.driverapp.DataManager
import com.royalit.driverapp.ProfileResponse
import com.royalit.driverapp.R
import com.royalit.driverapp.UserData
import com.royalit.driverapp.databinding.ActivityAboutUsBinding
import com.royalit.driverapp.databinding.ActivityKycdetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KYCDetailsActivity : AppCompatActivity() {

    val binding: ActivityKycdetailsBinding by lazy {
        ActivityKycdetailsBinding.inflate(layoutInflater)
    }
    lateinit var user: UserData
    var veihicleType="";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "KYC Details"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            //kycDetailsApi()
        }
        getProfileDetails()

        binding.clickA.setOnClickListener {
            if(!binding.lnrImgAadhar.isVisible)
            binding.lnrImgAadhar.visibility= View.VISIBLE
            else
            binding.lnrImgAadhar.visibility= View.GONE
        }
        binding.clickP.setOnClickListener {
            if(!binding.lnrImgPan.isVisible)
            binding.lnrImgPan.visibility= View.VISIBLE
            else
            binding.lnrImgPan.visibility= View.GONE
        }
        binding.clickD.setOnClickListener {
            if(!binding.lnrImgD.isVisible)
            binding.lnrImgD.visibility= View.VISIBLE
            else
            binding.lnrImgD.visibility= View.GONE
        }

        binding.clickR.setOnClickListener {
            if(!binding.lnrImgRc.isVisible)
            binding.lnrImgRc.visibility= View.VISIBLE
            else
            binding.lnrImgRc.visibility= View.GONE
        }


        binding.imgEditVehicle.setOnClickListener {
            EditVehicleDialog()
        }

    }
    fun getProfileDetails(){
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.getProfile(object: Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {

                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: ProfileResponse? = response.body()
                        if(model!!.user_data!=null&&model!!.user_data.size>0)
                        {
                            user=model!!.user_data.get(0)
                            user.let {

                                veihicleType= user.vehicleType.toString()
                                if(user.vehicleType.equals("1")) {
                                    binding.txtVehicleType.setText("Cycle")
                                    binding.imgVType.setImageResource(R.drawable.cycle_color_ic)
                                }
                                else if(user.vehicleType.equals("2")) {
                                    binding.txtVehicleType.setText("Scooter")
                                    binding.imgVType.setImageResource(R.drawable.scooter_color_ic)

                                }
                                else if(user.vehicleType.equals("3")) {
                                    binding.txtVehicleType.setText("Bike")
                                    binding.imgVType.setImageResource(R.drawable.bike_color_ic)

                                }

                                Glide.with(applicationContext)
                                    .load(it.aadharFront)
                                    .into(binding.imgAF!!)

                                Glide.with(applicationContext)
                                    .load(it.aadharBack)
                                    .into(binding.imgAB!!)

                                Glide.with(applicationContext)
                                    .load(it.pancard)
                                    .into(binding.imgP!!)

                                Glide.with(applicationContext)
                                    .load(it.drivingLicense)
                                    .into(binding.imgD!!)

                                Glide.with(applicationContext)
                                    .load(it.vehicleRc)
                                    .into(binding.imgRc)


                            }
                        }


                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, it)
        }
    }
    private fun EditVehicleDialog() {
        var isType=veihicleType
        val bottomSheetDialog = BottomSheetDialog(this@KYCDetailsActivity)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_editvehicle, null)
        bottomSheetDialog.setContentView(view)
        val linearCancel = view.findViewById<TextView>(R.id.linearCancel)
        val linearUpload = view.findViewById<TextView>(R.id.linearUpload)
        val lnr_cycle = view.findViewById<View>(R.id.lnr_cycle)
        val lnr_scooter = view.findViewById<View>(R.id.lnr_scooter)
        val lnr_bike = view.findViewById<View>(R.id.lnr_bike)
        val radio_one = view.findViewById<RadioButton>(R.id.radio_one)
        val radio_two = view.findViewById<RadioButton>(R.id.radio_two)
        val radio_three = view.findViewById<RadioButton>(R.id.radio_three)
        if(user.vehicleType.equals("1")) {
            radio_one.isChecked=true

        }
        else if(user.vehicleType.equals("2")) {

            radio_two.isChecked=true

        }
        else if(user.vehicleType.equals("3")) {

            radio_three.isChecked=true

        }
        lnr_cycle.setOnClickListener {
            isType="1"
            radio_one.isChecked=true
        }
        lnr_scooter.setOnClickListener {
            isType="2"
            radio_two.isChecked=true
        }
         lnr_bike.setOnClickListener {
             isType="3"
             radio_three.isChecked=true
        }

        linearCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        linearUpload.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }


}