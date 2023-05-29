package com.example.luxurycatadmin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.luxurycatadmin.databinding.ImageItemBinding

class AddCatImageAdapter(val list : ArrayList<Uri>)
    : RecyclerView.Adapter<AddCatImageAdapter.AddCatImageViewHolder>() {

    inner class AddCatImageViewHolder(val binding : ImageItemBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCatImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddCatImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddCatImageViewHolder, position: Int) {
        holder.binding.itemImg.setImageURI(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}