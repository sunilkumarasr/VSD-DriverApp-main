package com.seafoods.driverapp.Activitys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.Logins.LoginActivity
import com.seafoods.driverapp.R
import com.seafoods.driverapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }


    val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.splashStatusBar), false,binding.root)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("45", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            //token = task.result.toString()
            Log.e("FCM_TOKEN", "FCM Token: ${task.result}")
        })
        val title= intent.getStringExtra("title")
        val    notificationMessage= intent.getStringExtra("isNotification").toString()

        Log.e("notificationMessage","notificationMessage "+notificationMessage+ "title "+title)
        askNotificationPermission()
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
            }, 1500)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }


        methodRun()
        startBikeAnimation()

    }

    private fun startBikeAnimation() {

        binding.imgBike.post {

            val screenWidth = resources.displayMetrics.widthPixels.toFloat()

            binding.imgBike.translationX = screenWidth

            binding.imgBike.animate()
                .translationX(-500f)
                .setDuration(2500)
                .withEndAction {

                    binding.imgBike.alpha = 0f

                    startCycleAnimation()
                }
                .start()
        }
    }

    private fun startCycleAnimation() {

        binding.imgCycle.post {

            val screenWidth = resources.displayMetrics.widthPixels.toFloat()

            binding.imgCycle.translationX = screenWidth
            binding.imgCycle.alpha = 1f

            binding.imgCycle.animate()
                .translationX(-500f)
                .setDuration(2500)
                .start()
        }
    }


    private fun methodRun() {
        handler.postDelayed({
            val loginCheck = Preferences.getProfileStatus(applicationContext)
            if (loginCheck==-1) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }else{
                if(Preferences.getDriverStatus(applicationContext)==1)
                {
                    startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))

                    return@postDelayed
                }
                ViewController.profileNavigateScreen(applicationContext)
            }
            overridePendingTransition(0, 0)
            finish()
        }, 4000)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.e("On New INtent","OnNew Intent");
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                }, 1500)
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Handler(Looper.getMainLooper()).postDelayed({

                }, 1500)
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                 ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    200
                );
            } else {
                ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    200
                );
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Handler(Looper.getMainLooper()).postDelayed({
        }, 1500)
    }

}