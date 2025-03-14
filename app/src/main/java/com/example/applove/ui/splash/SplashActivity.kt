package com.example.applove.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivitySplashBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.onbording.OnbordingActivity
import com.example.applove.ui.setting.LanguageActivity
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel

class SplashActivity : BaseActivity<ActivitySplashBinding, BaseViewModel>() {
    override fun createBinding() = ActivitySplashBinding.inflate(layoutInflater)
    override fun setViewModel() = BaseViewModel()

    override fun initView() {
        super.initView()

        val text = "LOVE COUNTER"
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(Color.RED),
            5, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.nameApp.text = spannable

        val showAd = RemoteConfigManager.getBoolean("show_ad")
        val showInterstitial = Math.random() < 0.5

        Handler(Looper.getMainLooper()).postDelayed({
            if (showInterstitial && showAd) {
                AdsManager.showAdIfAvailable{
                    navigateToNextScreen()
                }
            } else {
                AdsManager.showInterstitialAd(this){
                    navigateToNextScreen()
                } // Load quảng cáo ngay khi mở màn hình intro
            }
        }, 3000)
    }

    private fun getLanguage(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("name", null)
    }

    private fun getPermission(): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("permission", false)
    }

    private fun navigateToNextScreen() {
        val savedLanguage = getLanguage()
        val savedPermission = getPermission()

        val nextActivity = when {
            savedLanguage == null -> Intent(this, LanguageActivity::class.java).apply {
                putExtra("from_splash", true)
            }
            else -> Intent(this, OnbordingActivity::class.java)
        }

        startActivity(nextActivity)
        finish()
    }

}
