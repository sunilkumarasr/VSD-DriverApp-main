package com.seafoods.driverapp.Activitys

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.Config.ViewController
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.Fragments.DeliveriesFragment
import com.seafoods.driverapp.Fragments.HomeFragment
import com.seafoods.driverapp.Fragments.ProfileFragment
import com.seafoods.driverapp.Fragments.SupportFragment
import com.seafoods.driverapp.Logins.LoginActivity
import com.seafoods.driverapp.MyPref
import com.seafoods.driverapp.MainResponse
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.ActivityDashBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardActivity : AppCompatActivity() {

    val binding: ActivityDashBoardBinding by lazy {
        ActivityDashBoardBinding.inflate(layoutInflater)
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        statusBarColorSet("0")

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.VANILLA_ICE_CREAM)
        {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

            // 2. Handle Window Insets to prevent content overlap
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply insets as padding to the root view.
                // This will push all content within binding.root away from the system bars.
                view.setPadding(insets.left, insets.left, insets.right, insets.bottom)

                // If specific views still overlap or need different behavior (e.g., a Toolbar
                // intended to sit behind a transparent status bar), you'll need to apply
                // padding or margins more selectively to those specific views or their containers.
                // For instance, to only pad the top of your contentFrame and bottom of navigationView:
                // binding.contentFrame.setPadding(insets.left, insets.top, insets.right, binding.contentFrame.paddingBottom)
                // binding.navigationView.setPadding(binding.navigationView.paddingLeft, binding.navigationView.paddingTop, binding.navigationView.paddingRight, insets.bottom)


                WindowInsetsCompat.CONSUMED
            }

        }

        //login
        //Preferences.saveStringValue(applicationContext, Preferences.LOGINCHECK, "Login")

        getDriverStatus()
        inits()

    }

    private fun statusBarColorSet(status: String) {
        if (status.equals("0")){
            ViewController.changeStatusBarColor(
                this,
                ContextCompat.getColor(this, R.color.loginBg),
                false,binding.root
            )
        }else{
            ViewController.changeStatusBarColor(
                this,
                ContextCompat.getColor(this, R.color.white),
                true,binding.root
            )
        }

    }

    fun getDriverStatus(){
        var user_id=  MyPref.getUser(applicationContext)
       
        // Obtain the DataManager instance
        //  dialog.showDialog(requireActivity(),false)
        
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(applicationContext)?.let {
            dataManager.getDriverStatus(object: Callback<MainResponse> {

                override fun onResponse(
                    call: Call<MainResponse>,
                    response: Response<MainResponse>
                ) {
                    //  dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.Status ==true) {
                        
                    }else
                    {
                        MyPref.clear(applicationContext)
                        Preferences.deleteSharedPreferences(applicationContext)
                        finish()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Utils.showMessage(response.body()?.Message,applicationContext)

                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                    // dialog.closeDialog()
                  
                }

            }, it)
        }
    }
    private fun inits() {

        //BottomNavigationView
        loadFragment(HomeFragment())
        bottomNavigationView = binding.navigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id. navigationHome-> {
                    statusBarColorSet("0")
                    binding.txtTitle.visibility = View.GONE
                    loadFragment(HomeFragment())
                    true
                }
                R.id. navigationDeliveries-> {
                    statusBarColorSet("1")
                    binding.txtTitle.visibility = View.VISIBLE
                    binding.txtTitle.setText("All Deliveries")
                    loadFragment(DeliveriesFragment())
                    true
                }
                R.id. navigationSupport-> {
                    statusBarColorSet("1")
                    binding.txtTitle.visibility = View.VISIBLE
                    binding.txtTitle.setText("Support")
                    loadFragment(SupportFragment())
                    true
                }
                R.id.navigationProfile -> {
                    statusBarColorSet("1")
                    binding.txtTitle.visibility = View.VISIBLE
                    binding.txtTitle.setText("Profile")
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

     fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun loadPosition(i: Int) {

// Get the ID of the menu item you want to select
// For example, to select the item at the second position (index 1)
        val menuItemId = bottomNavigationView.menu.getItem(i).itemId

// Set the selected item
        bottomNavigationView.selectedItemId = menuItemId


    }

}