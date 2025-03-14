package com.example.applove.ui.lovetest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applove.R
import com.example.applove.admob.AdsManager
import com.example.applove.ai.GeminiRepository
import com.example.applove.databinding.ActivityHoroscopeBinding
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel

class HoroscopeActivity : BaseActivity<ActivityHoroscopeBinding, BaseViewModel>() {
    override fun createBinding() = ActivityHoroscopeBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    override fun initView() {
        super.initView()
        binding.icBack.setOnClickListener {
            finish()
        }
        binding.btnLoveTest.setOnClickListener{
            GeminiRepository.analyzeCompatibility("Gemini", "Aries") { advice ->
                runOnUiThread {
                    binding.txtResult.text = advice
                }
            }
        }

    }

}