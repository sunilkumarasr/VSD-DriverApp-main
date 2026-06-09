package com.seafoods.driverapp.Logins

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.R
import com.seafoods.driverapp.databinding.ActivityDocsBinding

class DocsActivity : AppCompatActivity() {

    val binding: ActivityDocsBinding by lazy {
        ActivityDocsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white), true,binding.root)

        inits()

        var flag= Preferences.getProfileStatus(applicationContext)
        if(flag==5) {
            binding.relativeVehicle.isEnabled = false
            binding.txtVehicleDetails.setTextColor(resources.getColor(R.color.gray))
        }
    }

    private fun inits() {
        binding.relativeVehicle.setOnClickListener {
            startActivity(Intent(this@DocsActivity, VehicleSelectionActivity::class.java))
        }
        binding.relativeDocuments.setOnClickListener {
            startActivity(Intent(this@DocsActivity, UploadDocActivity::class.java))
        }
        binding.linearSublit.setOnClickListener {

        }

    }

}