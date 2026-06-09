package com.royalit.driverapp

import android.content.Context


class MyPref() {
    companion object{


        fun setUser(
            ctx: Context,
            user: String,
            mobile_number: String,
            full_name: String,
            email_id: String,
            profileStatus: Int
        )
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
            editor.putString("user_id",user)
            editor.putString("mobile_number",mobile_number)
            editor.putString("full_name",full_name)
            editor.putString("email_id",email_id)
            editor.putInt("ps",profileStatus)
            editor.commit()

        }
 fun setProfileStatus(
            ctx: Context,
            profileStatus: Int
        )
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
          
            editor.putInt("ps",profileStatus)
            editor.commit()

        }

        

        fun getProfileData(ctx:Context): String?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("profile","")
        }

        fun getUser(ctx:Context): String
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("user_id","").toString()
        }
        fun getMobile(ctx:Context): String?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("mobile_number","")
        } 
        
        fun getEmail(ctx:Context): String?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("email_id","")
        }
        fun getProfileStatus(ctx:Context): Int?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getInt("ps",0)
        }
        fun getName(ctx:Context): String?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("full_name","")
        }
        fun setName(ctx:Context,full_name:String)
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()

            editor.putString("full_name",full_name)
            editor.commit()
        }

        fun getOccupation(ctx:Context): String?
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("occupation","")
        }
        fun setOccupation(ctx:Context,occupation:String)
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()

            editor.putString("occupation",occupation)
            editor.commit()
        }



        fun setEmail(ctx:Context,email_id:String)
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("email_id",email_id)
            editor.commit()
        }


        fun setUserActiveStatus(ctx:Context,status:Int)
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putInt("active_status",status)

            editor.commit()

        }
        fun getUserActiveStatus(ctx:Context): Int
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getInt("active_status",1)
        }

        fun clear(ctx: Context) {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            sharedPreference.edit().clear().commit()

        }


        fun setAddress(ctx:Context,id:String,address:String,type:String)
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("address_id",id)
            editor.putString("address",address)
            editor.putString("address_type",type)
            editor.commit()
        }


        fun getAddressId(ctx:Context): String
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("address_id","").toString()
        }
        fun getAddress(ctx:Context): String
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("address","").toString()
        }fun getAddressType(ctx:Context): String
        {
            val sharedPreference =  ctx.getSharedPreferences("RC", Context.MODE_PRIVATE)
            return sharedPreference.getString("address_type","").toString()
        }
    }
}