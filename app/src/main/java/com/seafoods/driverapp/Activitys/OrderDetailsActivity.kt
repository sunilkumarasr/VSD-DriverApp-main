package com.seafoods.driverapp.Activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.R
import com.seafoods.driverapp.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {

    val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.white),
            true,binding.root
        )

        inits()

    }

    private fun inits() {

        binding.imgBack.setOnClickListener {
            finish()
        }


        binding.linearSubmit.setOnClickListener {
            startActivity(Intent(this@OrderDetailsActivity, OnGoingDeliveryActivity::class.java))
        }

    }

}