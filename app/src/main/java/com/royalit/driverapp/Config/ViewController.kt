package com.royalit.driverapp.Config

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.internal.ContextUtils
import com.royalit.driverapp.Activitys.DashBoardActivity
import com.royalit.driverapp.Activitys.DirverstatusVerificationActivity
import com.royalit.driverapp.Logins.AddYourAddressActivity
import com.royalit.driverapp.Logins.AddYourProfilePicActivity
import com.royalit.driverapp.Logins.DocsActivity
import com.royalit.driverapp.Logins.PersonalInformationActivity

object ViewController {

    var mProgressDialog: ProgressDialog? = null


    fun changeStatusBarColor(activity: Activity, color: Int, isLight: Boolean,view:View) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = color
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars = isLight
        var window=activity.window
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.VANILLA_ICE_CREAM)
        {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

            // 2. Handle Window Insets to prevent content overlap
            ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply insets as padding to the root view.
                // This will push all content within binding.root away from the system bars.
                view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

                // If specific views still overlap or need different behavior (e.g., a Toolbar
                // intended to sit behind a transparent status bar), you'll need to apply
                // padding or margins more selectively to those specific views or their containers.
                // For instance, to only pad the top of your contentFrame and bottom of navigationView:
                // binding.contentFrame.setPadding(insets.left, insets.top, insets.right, binding.contentFrame.paddingBottom)
                // binding.navigationView.setPadding(binding.navigationView.paddingLeft, binding.navigationView.paddingTop, binding.navigationView.paddingRight, insets.bottom)


                WindowInsetsCompat.CONSUMED
            }

        }
    }

    @SuppressLint("RestrictedApi")
    fun removeStatusBar(context: Context) {
        val window = ContextUtils.getActivity(context)?.window

        window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor =
                ContextCompat.getColor(context, android.R.color.transparent)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLoading(context: Context) {
        mProgressDialog = ProgressDialog(context).apply {
            setMessage("Loading...")
            show()
        }
    }
    fun hideLoading() {
        mProgressDialog?.dismiss()
    }


    fun noInterNetConnectivity(ctx: Context):Boolean
    {
        val connectivityManager= ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hasEditText(editText: EditText, errMsg: String??
    ): Boolean {
        if (editText.text.toString().trim { it <= ' ' }.isEmpty()) {
            editText.error = errMsg
            editText.requestFocus()//(editText, context)
            return false
        } else {
            // editText.isFocusable= false
        }
        return true
    }


    fun navigateScreen(context:Context,intent:Intent)
    {
        context.startActivity(intent)
    }

    fun profileNavigateScreen(context:Context)
    {
        var flag=Preferences.getProfileStatus(context)
        var mobile=Preferences.getMobile(context)
        var intent=Intent(context, PersonalInformationActivity::class.java)
        if(flag==1)
            intent=Intent(context, PersonalInformationActivity::class.java)
        else if(flag==2)
            intent=Intent(context, AddYourAddressActivity::class.java)
        else if(flag==3) {
            intent = Intent(context, AddYourProfilePicActivity::class.java)

            intent.putExtra("isFrom","")
        }
        else if(flag==4)
            intent=Intent(context, DocsActivity::class.java)
        else if(flag==5)
            intent=Intent(context, DocsActivity::class.java)
        else if(flag==6)
            intent=Intent(context, DirverstatusVerificationActivity::class.java)

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("mobile",mobile)
        context.startActivity(intent)


    }
}