package com.seafoods.driverapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seafoods.driverapp.Activitys.DashBoardActivity
import com.seafoods.driverapp.Activitys.OnGoingDeliveryActivity
import com.seafoods.driverapp.Adapters.ActiveOrderAdapter
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.MyPref
import com.seafoods.driverapp.OrderClickListener
import com.seafoods.driverapp.OrderResponse
import com.seafoods.driverapp.Orders
import com.seafoods.driverapp.ProfileResponse
import com.seafoods.driverapp.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), OrderClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var activeOrderAdapter: ActiveOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        binding.txtName.setText("${Preferences.getUserName(requireActivity())}")
        binding.txtEmpId.setText("${Preferences.getEMPID(requireActivity())}")

    }

    private fun init() {

        binding.linearViewAllDeliveries.setOnClickListener {
            (activity as DashBoardActivity).loadPosition(1)
        }
        activeOrderAdapter=ActiveOrderAdapter()
        activeOrderAdapter.setClickListner(this)
        binding.recyclerActiveOrders.adapter=activeOrderAdapter
       /* binding.linearViewActiveDelivery.setOnClickListener {
            startActivity(Intent(requireActivity(), OnGoingDeliveryActivity::class.java))
        }*/
        getOrders()
    }

    //
    fun getProfileDetails(){
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(requireActivity().applicationContext)?.let {
            dataManager.getProfile(object: Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {

                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: ProfileResponse? = response.body()


                    }
                    Log.e("response.body()","response.body() ")

                }
               // 9949421793 Shanker AEO

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, it)
        }
    }
    fun getOrders(){
      var user_id=  MyPref.getUser(requireActivity())
        val dialog= CustomDialog(requireActivity())
        // Obtain the DataManager instance
      //  dialog.showDialog(requireActivity(),false)
        if(activeOrderAdapter.itemCount>0)
            binding.lnrNoData.visibility=View.GONE
        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(requireActivity().applicationContext)?.let {
            dataManager.getOrders(object: Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                  //  dialog.closeDialog()
                    Log.e("response.body()","response.body() ${response.body()}")
                    if(response.body()?.status ==true) {
                        val model: OrderResponse? = response.body()
                        if(activeOrderAdapter.itemCount>0)
                            binding.lnrNoData.visibility=View.GONE
                        val count=model!!.orders.size
                        if(count>0) {
                            binding.txtOrdersCount.text = "$count Delivery Orders Found!"
                        }
                        else {
                            binding.txtOrdersCount.text = "No Orders Assigned"
                        }

                        activeOrderAdapter.setData(model!!.orders)
                        if(activeOrderAdapter.itemCount>0)
                            binding.lnrNoData.visibility=View.GONE
                    }else
                    {
                        if(activeOrderAdapter.itemCount>0)
                            binding.lnrNoData.visibility=View.GONE
                        binding.txtOrdersCount.text = "No Orders Assigned"

                    }
                    Log.e("response.body()","response.body() ")

                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                   // dialog.closeDialog()
                    if(activeOrderAdapter.itemCount>0)
                        binding.lnrNoData.visibility=View.GONE
                    Log.e("response.body()","response.body() ${t.printStackTrace()}")
                }

            }, it)
        }
    }

    override fun onclick(order: Orders) {
val intent=Intent(requireActivity(), OnGoingDeliveryActivity::class.java).apply {
    putExtra("order_id",order.orderId)
    putExtra("order_id_number",order.id)
    putExtra("FLAG",0)
}
        startActivity(intent)

    }
}