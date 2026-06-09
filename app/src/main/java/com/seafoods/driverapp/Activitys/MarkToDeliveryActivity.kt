package com.seafoods.driverapp.Activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.R
import com.seafoods.driverapp.databinding.ActivityMarkToDeliveryBinding

class MarkToDeliveryActivity : AppCompatActivity() {

    val binding: ActivityMarkToDeliveryBinding by lazy {
        ActivityMarkToDeliveryBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.loginBg),
            false,binding.root
        )

        inits()

    }

    private fun inits() {

        binding.imgBack.setOnClickListener {
            finish()
        }


        binding.linearSubmit.setOnClickListener {
            startActivity(Intent(this@MarkToDeliveryActivity, DeliveryCompletedActivity::class.java))
        }

    }

}