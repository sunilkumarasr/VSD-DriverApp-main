package com.royalit.driverapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Utils {



    companion object{

        val RUPEE_SYMBOL="₹"
        fun showMessage(msg: String?, ctx: Context)
        {
            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()
        }

        fun checkConnectivity(ctx:Context):Boolean
        {
           val connectivityManager= ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Returns a Network object corresponding to
                // the currently active default data network.
                val network = connectivityManager.activeNetwork ?: return false

                // Representation of the capabilities of an active network.
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    // Indicates this network uses a Wi-Fi transport,
                    // or WiFi has network connectivity
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                    // Indicates this network uses a Cellular transport. or
                    // Cellular has network connectivity
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                    // else return false
                    else -> false
                }
            } else {
                // if the android version is below M
                @Suppress("DEPRECATION") val networkInfo =
                    connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }


        }


      /* fun getJsonObject(strRes:Any): JsonObject?
        {
           return Gson().toJsonTree(strRes).getAsJsonObject()
        }*/

        val firstApiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val firstApiFormat2 = SimpleDateFormat("EEE, dd MMM")
        val firstApiFormat3 = SimpleDateFormat("hh:mm a")
        val firstApiFormat4 = SimpleDateFormat("dd MMM")


        val firstCreatedDaeFormat = SimpleDateFormat("yyyy-MM-dd")
        val firstCreatedDaeFormat2 = SimpleDateFormat("EEE, dd MMM yyyy")

        fun getDateInMonthFormat(date:String):String
        {
            return firstApiFormat2.format( firstApiFormat.parse(date))
        }
        fun getDateInTimeFormat(date:String):String
        {
            return firstApiFormat3.format( firstApiFormat.parse(date))
        }
        fun getCreatedDateFormat(date:String):String
        {
            return firstCreatedDaeFormat2.format( firstCreatedDaeFormat.parse(date))
        }

        fun getTodayThMonth():String
        {

            val cal: Calendar = Calendar.getInstance()
            cal.setTime(Date())

            val day: Int = cal.get(Calendar.DATE)
            if(!((day>10) && (day<19))){
                when (day % 10) {
                     1-> SimpleDateFormat ("d'st' 'of' MMM").format(Date().time);
                   2-> SimpleDateFormat ("d'nd' 'of' MMM").format(Date().time);
                    3->SimpleDateFormat ("d'rd' 'of' MMM").format(Date().time);
                   else ->SimpleDateFormat ("d'th' 'of' MMM").format(Date().time);
                    }

              }

                    return firstApiFormat4.format( Date().time)
        }
        fun checkNull(s:String?):String
        {
            if(s==null||s=="null")
                return ""
            return s
        }
    }

}
//keystore alias => royal
//keystore password => royal@123