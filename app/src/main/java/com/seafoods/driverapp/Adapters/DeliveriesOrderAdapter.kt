package com.seafoods.driverapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seafoods.driverapp.OrderClickListener
import com.seafoods.driverapp.Orders
import com.seafoods.driverapp.R

class DeliveriesOrderAdapter : RecyclerView.Adapter<DeliveriesOrderAdapter.OrderViewHolder>() {
    var list = ArrayList<Orders>()
    lateinit var orderClickListener: OrderClickListener
    var selected_id = ""
    fun setData(lists: ArrayList<Orders>) {
        list.clear()

        list.addAll(lists)
        notifyDataSetChanged()
    }

    fun setClickListner(orderClickListener: OrderClickListener) {
        this.orderClickListener = orderClickListener
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_name = view.findViewById<TextView>(R.id.txt_name)
        val txt_order_id = view.findViewById<TextView>(R.id.txt_order_id)
        val txt_address = view.findViewById<TextView>(R.id.txt_address)
        val linearViewDetails = view.findViewById<LinearLayout>(R.id.linearViewDetails)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_deliveries, parent, false)
        return OrderViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        var order = list.get(position)
        holder.txt_name.text = order.fullName
        holder.txt_order_id.text = order.orderId
        holder.txt_address.text = formAddress(order)



        holder.linearViewDetails.setOnClickListener {
            orderClickListener.onclick(list.get(position))
        }
    }

    fun formAddress(data: Orders): String {
        var adrs =
            "${data.houseNo},${data.floor},${data.landmark}\n${data.cityTown},${data.state},${data.country},${data.zipCode}"
        adrs = adrs.replace(",,", ",")
        return adrs
    }

}