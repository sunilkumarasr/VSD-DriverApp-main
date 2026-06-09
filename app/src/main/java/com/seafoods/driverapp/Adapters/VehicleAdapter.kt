package com.seafoods.driverapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seafoods.driverapp.R
import com.seafoods.driverapp.Vehicles

class VehicleAdapter: RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
    var list=ArrayList<Vehicles>()
    var selected_id=""
   fun setData(lists:ArrayList<Vehicles>)
    {
        list.clear()

        list.addAll(lists)
        notifyDataSetChanged()
   }
    class VehicleViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val txtVehicle=view.findViewById<TextView>(R.id.txt_vehicle_name)
        val img=view.findViewById<ImageView>(R.id.img_vehicle)
        val radio=view.findViewById<RadioButton>(R.id.radio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
val view=LayoutInflater.from(parent.context).inflate(R.layout.layout_vehicle_item,parent,false)
        return VehicleViewHolder(view)

    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.txtVehicle.text=list.get(position).vehicle
        Glide.with(holder.img.context).load(list.get(position).vehicleImage).into(holder.img)

        holder.txtVehicle.text=list.get(position).vehicle

        holder.radio.isChecked=list.get(position).isSelected
        holder.itemView.setOnClickListener {
            list.forEach{
                it.isSelected=false
            }
            list.get(position).isSelected=true
            selected_id=list.get(position).id.toString()
            notifyDataSetChanged()
        }
    }
}