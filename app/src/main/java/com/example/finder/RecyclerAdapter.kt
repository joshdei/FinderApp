package com.example.finder

import Uploadroomate
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerAdapter(private val roommateList: List<Uploadroomate>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)
        val description: TextView = itemView.findViewById(R.id.description)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_2, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val roommate = roommateList[position]
        holder.date.text = roommate.date
        holder.title.text = roommate.title
        holder.price.text = roommate.pricePerNight
        holder.description.text = if (roommate.description.length > 20) {
            "${roommate.description.substring(0, 20)}..."
        } else {
            roommate.description
        }

        Glide.with(holder.itemView.context).load(roommate.imageUri).into(holder.image)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ViewActivity::class.java).apply {
                putExtra("USER_ID", roommate.userid) // Pass user ID
                putExtra("ROOMMATE", roommate) // Pass the entire Uploadroomate object
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return roommateList.size
    }
}
