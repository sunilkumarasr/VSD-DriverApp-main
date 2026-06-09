package com.seafoods.driverapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seafoods.driverapp.Activitys.OnGoingDeliveryActivity
import com.seafoods.driverapp.Adapters.DeliveriesOrderAdapter
import com.seafoods.driverapp.Config.Preferences
import com.seafoods.driverapp.CustomDialog
import com.seafoods.driverapp.DataManager
import com.seafoods.driverapp.OrderClickListener
import com.seafoods.driverapp.OrderResponse
import com.seafoods.driverapp.Orders
import com.seafoods.driverapp.databinding.FragmentDeliveriesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveriesFragment : Fragment(), OrderClickListener {

    private lateinit var binding: FragmentDeliveriesBinding
    var FLAG = 0
    lateinit var deliveriesOrderAdapter: DeliveriesOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeliveriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getOrders()
    }

    private fun init() {

        deliveriesOrderAdapter = DeliveriesOrderAdapter()
        deliveriesOrderAdapter.setClickListner(this)
        binding.recyclerOrders.adapter = deliveriesOrderAdapter
        binding.lnrActive.setOnClickListener {
            FLAG = 0
            binding.txtActive.isChecked = true
            binding.txtCompleted.isChecked = false
            getOrders()
        }
        binding.lnrCompleted.setOnClickListener {
            FLAG = 1
            binding.txtActive.isChecked = false
            binding.txtCompleted.isChecked = true

            getDeliveryOrders()
        }
        /* binding.linearViewDetails.setOnClickListener {
             startActivity(Intent(requireActivity(), OrderDetailsActivity::class.java))
         }*/

    }

    fun getOrders() {
        binding.lnrNoData.visibility = View.VISIBLE
        val dialog = CustomDialog(requireActivity())
        // Obtain the DataManager instance
        var user_id = Preferences.getUserID(requireActivity())
        //  dialog.showDialog(requireActivity(),false)

        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(requireActivity().applicationContext)?.let {
            if (user_id != null) {
                dataManager.getOrders(object : Callback<OrderResponse> {

                    override fun onResponse(
                        call: Call<OrderResponse>,
                        response: Response<OrderResponse>
                    ) {
                        // dialog.closeDialog()
                        Log.e("response.body()", "response.body() ${response.body()}")
                        if (response.body()?.status == true) {
                            val model: OrderResponse? = response.body()
                            deliveriesOrderAdapter.setData(model!!.orders)
                            val count = model!!.orders.size

                            if (deliveriesOrderAdapter.itemCount > 0)
                                binding.lnrNoData.visibility = View.GONE
                        }
                        Log.e("response.body()", "response.body() ")

                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        // dialog.closeDialog()
                        if (deliveriesOrderAdapter.itemCount > 0)
                            binding.lnrNoData.visibility = View.GONE
                        Log.e("response.body()", "response.body() ${t.printStackTrace()}")
                    }

                }, user_id)
            }
        }
    }

    fun getDeliveryOrders() {
        val dialog = CustomDialog(requireActivity())
        // Obtain the DataManager instance
        //dialog.showDialog(requireActivity(),false)

        val dataManager = DataManager.getDataManager()
        Preferences.getUserID(requireActivity().applicationContext)?.let {
            dataManager.getDeliveryOrders(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    // dialog.closeDialog()
                    if (deliveriesOrderAdapter.itemCount > 0)
                        binding.lnrNoData.visibility = View.GONE
                    Log.e("response.body()", "response.body() ${response.body()}")
                    if (response.body()?.status == true) {
                        val model: OrderResponse? = response.body()

                        deliveriesOrderAdapter.setData(model!!.orders)
                        if (deliveriesOrderAdapter.itemCount > 0)
                            binding.lnrNoData.visibility = View.GONE

                    }
                    Log.e("response.body()", "response.body() ")

                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    //dialog.closeDialog()
                    if (deliveriesOrderAdapter.itemCount > 0)
                        binding.lnrNoData.visibility = View.GONE
                    Log.e("response.body()", "response.body() ${t.printStackTrace()}")
                }

            }, it, "4")
        }
    }

    override fun onclick(order: Orders) {
        val intent = Intent(requireActivity(), OnGoingDeliveryActivity::class.java).apply {
            putExtra("order_id", order.orderId)
            putExtra("order_id_number", order.id)
            putExtra("FLAG", FLAG)
        }
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (binding.txtActive.isChecked)
            getOrders()
        else getDeliveryOrders()
    }
}