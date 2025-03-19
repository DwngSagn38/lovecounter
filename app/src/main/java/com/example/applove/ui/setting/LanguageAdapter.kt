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

            if (position == selectedPosition) {
                binding.root.setBackgroundResource(R.drawable.bg_red)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_gray)
            }

            when (language.code) {
                "en" -> binding.imgLanguage.setImageResource(R.drawable.ic_english)
                "de" -> binding.imgLanguage.setImageResource(R.drawable.ic_german)
                "es" -> binding.imgLanguage.setImageResource(R.drawable.ic_spanish)
                "fr" -> binding.imgLanguage.setImageResource(R.drawable.ic_french)
                "hi" -> binding.imgLanguage.setImageResource(R.drawable.ic_hindi)
                "in" -> binding.imgLanguage.setImageResource(R.drawable.flag_indonesia)
                "pt" -> binding.imgLanguage.setImageResource(R.drawable.ic_portuguese)
                "vi" -> binding.imgLanguage.setImageResource(R.drawable.flag_vie)
                "ja" -> binding.imgLanguage.setImageResource(R.drawable.flag_japan)
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
