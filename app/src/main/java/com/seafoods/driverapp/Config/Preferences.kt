package com.seafoods.driverapp.Config

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object Preferences {

    private const val PREFERENCE_NAME = "DriverApp"

    const val LOGINCHECK = "LOGINCHECK"
    const val userId = "userId"
    const val DRIVER_STATUS = "driver_status"
    const val PROFILE_STATUS = "profile_status"
    const val MOBILE = "mobile"
    const val EMP_ID = "emp_id"
    const val name = "name"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun saveFloatValue(context: Context, key: String, value: Float) {
        with(getSharedPreferences(context).edit()) {
            putFloat(key, value)
            apply()
        }
    }

    fun loadFloatValue(context: Context, key: String, defValue: Float): Float {
        return getSharedPreferences(context).getFloat(key, defValue)
    }

    fun saveUserId(context: Context,  value: String) {
        with(getSharedPreferences(context).edit()) {
            putString(userId, value)
            apply()
        }
    }
    fun saveUserName(context: Context,  value: String) {
        with(getSharedPreferences(context).edit()) {
            putString(name, value)
            apply()
        }
    }
    fun saveProfileStatus(context: Context,  value: Int) {
        with(getSharedPreferences(context).edit()) {
            putInt(PROFILE_STATUS, value)
            apply()
        }
    }
    fun saveMobile(context: Context,  value: String) {
        with(getSharedPreferences(context).edit()) {
            putString(MOBILE, value)
            apply()
        }
    }

    fun getUserID(context: Context): String? {
        return getSharedPreferences(context).getString(userId, "")
    }
    fun saveDriverStatus(context: Context,  value: Int) {
        with(getSharedPreferences(context).edit()) {
            putInt(DRIVER_STATUS, value)
            apply()
        }
    }
    fun getDriverStatus(context: Context): Int {
        return getSharedPreferences(context).getInt(DRIVER_STATUS, 0)
    }
    fun getUserName(context: Context): String? {
        return getSharedPreferences(context).getString(name, "")
    }

    fun getProfileStatus(context: Context): Int {
        //return 6;
        return getSharedPreferences(context).getInt(PROFILE_STATUS, -1)
    }
    fun getMobile(context: Context): String? {
        return getSharedPreferences(context).getString(MOBILE,"")
    }

    fun saveLongValue(context: Context, key: String, value: Long) {
        with(getSharedPreferences(context).edit()) {
            putLong(key, value)
            apply()
        }
    }

    fun loadLongValue(context: Context, key: String, defValue: Long): Long {
        return getSharedPreferences(context).getLong(key, defValue)
    }

    fun saveIntegerValue(context: Context, key: String, value: Int) {
        with(getSharedPreferences(context).edit()) {
            putInt(key, value)
            apply()
        }
    }

    fun loadIntegerValue(context: Context, key: String, defValue: Int): Int {
        return getSharedPreferences(context).getInt(key, defValue)
    }

    fun saveBooleanValue(context: Context, key: String, value: Boolean) {
        with(getSharedPreferences(context).edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun deleteSharedPreferences(context: Context) {
        with(getSharedPreferences(context).edit()) {
            clear()
            commit() // Note: `commit()` is synchronous; you may want to use `apply()` for async behavior
        }
    }

    fun saveContactAsynParser(context: Context, key: String, value: String) {
        with(getSharedPreferences(context).edit()) {
            val json = Gson().toJson(value)
            putString(key, json)
            apply()
        }
    }

    fun loadContactAsynParser(context: Context, key: String, defValue: String): String? {
        return getSharedPreferences(context).getString(key, defValue)
    }

    fun saveEMPID(context: Context, empId: String) {
        with(getSharedPreferences(context).edit()) {
            putString(EMP_ID, empId)
            apply()
        }

    }

    fun getEMPID(context: Context): String? {
        return getSharedPreferences(context).getString(EMP_ID, "")
    }

}