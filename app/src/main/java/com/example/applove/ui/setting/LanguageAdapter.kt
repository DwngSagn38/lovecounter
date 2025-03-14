package com.example.lovecounter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.applove.R
import com.example.applove.roomdb.model.LanguageModel
import com.example.applove.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val languages: List<LanguageModel>,
    private val onItemClick: (LanguageModel) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = -1 // Vị trí của item được chọn

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: LanguageModel, position: Int) {
            binding.txtLanguage.text = language.name
            binding.imgLanguage.setImageResource(language.icon)

            if (position == selectedPosition) {
                binding.root.setBackgroundResource(R.drawable.bg_red)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_gray)
            }
            binding.root.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onItemClick(language)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languages[position],position)
    }

    override fun getItemCount() = languages.size
}
