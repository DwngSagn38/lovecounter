package com.example.applove.adpater

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.applove.R
import com.example.applove.roomdb.model.BackgroundModel

class ChooseBgAdapter(
    private val onClick: (BackgroundModel) -> Unit, // Hàm callback khi chọn ảnh
    private val onChooseFromGallery: () -> Unit // Callback mở thư viện ảnh
) : ListAdapter<BackgroundModel, ChooseBgAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBackground: ImageView = view.findViewById(R.id.imgBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_background, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageRes = getItem(position)
        val imageUri = Uri.parse(imageRes.imageUri)

        if (imageRes.imageUri.startsWith("android.resource://")) {
            // Load ảnh từ drawable bằng resource ID
            val resId = imageUri.pathSegments.last().toIntOrNull() ?: R.drawable.img_home
            holder.imgBackground.setImageResource(resId)
        } else {
            // Load ảnh từ URI
            holder.imgBackground.setImageURI(imageUri)
        }

        holder.itemView.setOnClickListener {
            if (position == 0) {
                onChooseFromGallery() // Mở thư viện ảnh
            } else {
                Log.d("ChooseBgAdapter", "Chọn ảnh: ${imageRes.imageUri}")
                onClick(imageRes) // Chọn ảnh từ danh sách
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<BackgroundModel>() {
    override fun areItemsTheSame(oldItem: BackgroundModel, newItem: BackgroundModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: BackgroundModel, newItem: BackgroundModel) = oldItem == newItem
}
