package com.seafoods.driverapp.Adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seafoods.driverapp.OrderClickListener
import com.seafoods.driverapp.Orders
import com.seafoods.driverapp.R

class ActiveOrderAdapter: RecyclerView.Adapter<ActiveOrderAdapter.OrderViewHolder>() {
    var list=ArrayList<Orders>()
    lateinit var orderClickListener: OrderClickListener
    var selected_id=""
   fun setData(lists:ArrayList<Orders>)
    {
        list.clear()

        list.addAll(lists)
        notifyDataSetChanged()
   }
    fun setClickListner(orderClickListener:OrderClickListener)
    {
       this.orderClickListener=orderClickListener
   }
    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val txt_name=view.findViewById<TextView>(R.id.txt_name)
        val txt_order_id=view.findViewById<TextView>(R.id.txt_order_id)
        val txt_address=view.findViewById<TextView>(R.id.txt_address)
        val lnr_navigation=view.findViewById<LinearLayout>(R.id.lnr_navigation)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
val view=LayoutInflater.from(parent.context).inflate(R.layout.layout_orders,parent,false)
        return OrderViewHolder(view)

    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        var order=list.get(position)
        holder.txt_name.text=order.fullName
        holder.txt_order_id.text=order.orderId
        holder.txt_address.text=formAddress(order)
        holder.lnr_navigation.visibility=View.VISIBLE
        holder.lnr_navigation.setOnClickListener {
            val builder = Uri.Builder()
            builder.scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendPath("dir")
                .appendPath("")
                .appendQueryParameter("api", "1")
                //.appendQueryParameter("origin", pickLatLng!!.latitude.toString() + "," + pickLatLng!!.longitude)
                .appendQueryParameter("destination", order.latitude + "," + order!!.longitude)
            val url = builder.build().toString()
            Log.d("Directions", url)
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            holder.txt_name.context. startActivity(i)
        }

        holder.itemView.setOnClickListener {

            orderClickListener.onclick(list.get(position))
        }
    }
    fun formAddress(data: Orders):String
    {
        var adrs="${data.houseNo},${data.floor},${data.landmark}\n${data.cityTown},${data.state},${data.country},${data.zipCode}"
        adrs=adrs.replace(",,",",")
        return adrs
    }

}