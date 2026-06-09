package com.seafoods.driverapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seafoods.driverapp.Activitys.DashBoardActivity
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.LoginResponse
import com.seafoods.driverapp.Utils
import com.seafoods.driverapp.databinding.FragmentSupportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SupportFragment : Fragment() {

    private lateinit var binding: FragmentSupportBinding
    var name=""
    var email=""
    var phone=""
    var subject=""
    var message=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSupportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {

        binding.lnrSend.setOnClickListener {
            subject=binding.editSubject.text.toString()
            message=binding.editMessage.text.toString()
            if(subject.isEmpty()||message.isEmpty())
            {
                Utils.showMessage("Please fill details",requireActivity())
                return@setOnClickListener
            }
            name= Preferences.getUserName(requireActivity()).toString()
            phone= Preferences.getMobile(requireActivity()).toString()
            email= Preferences.getEMPID(requireActivity()).toString()
            support()
        }
    }
    fun support(){
        val dialog= CustomDialog(requireActivity())
        // Obtain the DataManager instance
       // dialog.showDialog(requireActivity(),false)

        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(requireActivity().applicationContext)?.let {
            dataManager.support(object: Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                  //  dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    Utils.showMessage(response.body()!!.message,requireActivity())
                    if(response.body()?.status ==true) {

                       binding.editMessage.setText("")
                       binding.editSubject.setText("")

                        (activity as DashBoardActivity).loadPosition(0)
                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    //dialog.closeDialog()
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, name,email,phone,subject,message)
        }
    }

}