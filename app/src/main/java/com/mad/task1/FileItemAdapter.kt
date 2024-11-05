package com.mad.task1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class FileItemAdapter(private val fileItems: List<FileItem>): RecyclerView.Adapter<FileItemAdapter.ItemViewHolder>() {

    // ViewHolder class to hold the views for each item in the list
    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val pathTextView: TextView = itemView.findViewById(R.id.pathTextView)
        val sizeTextView: TextView = itemView.findViewById(R.id.footerViewTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Bind data to each view in the ViewHolder
        val item = fileItems[position]
        holder.typeTextView.text = "Type: ${item.type}"
        holder.nameTextView.text = "Name: ${item.name}"
        holder.pathTextView.text = "Path: ${item.getPath()}"
        holder.sizeTextView.text = "Size: ${item.getSizeOrDefault()}"
    }

    override fun getItemCount(): Int = fileItems.size
}