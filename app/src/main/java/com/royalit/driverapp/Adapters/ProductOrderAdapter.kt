package com.royalit.driverapp.Adapters

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.driverapp.DataManager.Companion.ROOT_URL
import com.royalit.driverapp.OrderClickListener
import com.royalit.driverapp.Orders
import com.royalit.driverapp.Products
import com.royalit.driverapp.R
import com.royalit.driverapp.Vehicles

class ProductOrderAdapter: RecyclerView.Adapter<ProductOrderAdapter.ProductOrderViewHolder>() {
    var list=ArrayList<Products>()
    lateinit var orderClickListener: OrderClickListener
    var selected_id=""
   fun setData(lists:ArrayList<Products>)
    {
        list.clear()

        list.addAll(lists)
        notifyDataSetChanged()
   }
    fun setClickListner(orderClickListener:OrderClickListener)
    {
       this.orderClickListener=orderClickListener
   }
    class ProductOrderViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val product_name=view.findViewById<TextView>(R.id.product_name)
        val product_name_2=view.findViewById<TextView>(R.id.product_name_2)
        val txt_qnty=view.findViewById<TextView>(R.id.txt_qnty)
        val img=view.findViewById<ImageView>(R.id.img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderViewHolder {
val view=LayoutInflater.from(parent.context).inflate(R.layout.layout_order_product,parent,false)
        return ProductOrderViewHolder(view)

    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: ProductOrderViewHolder, position: Int) {
        holder.product_name.text=list.get(position).productTitle
        holder.txt_qnty.text=list.get(position).qty
        holder.product_name_2.text=list.get(position).productId
        Glide.with(holder.img.context).load(ROOT_URL+list.get(position).productImage).placeholder(R.drawable.item_1).into(holder.img)



    }
    fun formAddress(data: Orders):String
    {
        var adrs="${data.houseNo},${data.floor},${data.landmark}\n${data.cityTown},${data.state},${data.country},${data.zipCode}"
        adrs=adrs.replace(",,",",")
        return adrs
    }

}