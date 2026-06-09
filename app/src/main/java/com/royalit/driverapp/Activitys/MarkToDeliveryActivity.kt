package com.royalit.driverapp.Activitys

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royalit.driverapp.Config.ViewController
import com.royalit.driverapp.R
import com.royalit.driverapp.databinding.ActivityMarkToDeliveryBinding
import com.royalit.driverapp.databinding.ActivityOrderDetailsBinding

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