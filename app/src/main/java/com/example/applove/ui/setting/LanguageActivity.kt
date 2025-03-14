package com.example.applove.ui.setting

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applove.R
import com.example.applove.admob.AdsManager
import com.example.applove.roomdb.model.LanguageModel
import com.example.applove.databinding.ActivityLanguageBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.applove.ui.onbording.OnbordingActivity
import com.example.lovecounter.adapter.LanguageAdapter
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class LanguageActivity : BaseActivity<ActivityLanguageBinding, BaseViewModel>() {
    override fun createBinding() = ActivityLanguageBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    private lateinit var languge : String

    override fun initView() {
        super.initView()

        val languages = listOf(
            LanguageModel("English", R.drawable.ic_english),
            LanguageModel("Hindi", R.drawable.ic_hindi),
            LanguageModel("Spanish", R.drawable.ic_spanish),
            LanguageModel("French", R.drawable.ic_french),
            LanguageModel("Portuguese", R.drawable.ic_portuguese),
            LanguageModel("Indonesian", R.drawable.ic_indonesian),
            LanguageModel("German", R.drawable.ic_german),
        )

        var choosenLanguage = false

        val savedLanguage = getLanguage()

        if (savedLanguage != null) {
            for (language in languages) {
                if (language.name == savedLanguage) {
                    choosenLanguage = true
                    break
                }
            }
        }

        val adapter = LanguageAdapter(languages) { selectedLanguage ->
            choosenLanguage = true
            languge = selectedLanguage.name
        }

        binding.rcvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcvLanguage.adapter = adapter

        val fromSplash = intent.getBooleanExtra("from_splash", false)
        if (fromSplash) {
            binding.imgBack.visibility = View.GONE
            binding.txtLanguage.gravity = Gravity.START
            binding.imgDone.setOnClickListener {
                if (choosenLanguage) {
                    val intent = Intent(this, OnbordingActivity::class.java)
                    startActivity(intent)
                    saveLanguage(languge)
                }else{
                    Toast.makeText(this, "Please choose a language", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            binding.imgDone.setOnClickListener {
                if (choosenLanguage) {
                    saveLanguage(languge)
                    finish()
                }else{
                    Toast.makeText(this, "Please choose a language", Toast.LENGTH_SHORT).show()
                }
            }
            binding.imgBack.setOnClickListener {
                finish()
            }
        }

        // Load quảng cáo native
        AdsManager.loadNativeAd(this, binding.nativeAdView)
    }

    private fun saveLanguage(name : String){
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.apply()
    }

    private fun getLanguage(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("name", null)
    }


}