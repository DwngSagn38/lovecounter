package com.example.applove.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.amazic.ads.callback.AdCallback
import com.amazic.ads.callback.InterCallback
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AppOpenManager
import com.example.applove.admob.AdConfig
import com.example.applove.databinding.ActivitySplashBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.applove.ui.onbording.OnbordingActivity
import com.example.applove.ui.setting.LanguageActivity
import com.example.applove.utils.SystemUtil
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.LoadAdError


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

//        val showAd = RemoteConfigManager.getBoolean("show_ad")
//        val showInterstitial = Math.random() < 0.5

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (showInterstitial && showAd) {
//                AdsManager.showAdIfAvailable{
//                    navigateToNextScreen()
//                }
//            } else {
//                AdsManager.showInterstitialAd(this){
//                    navigateToNextScreen()
//                } // Load quảng cáo ngay khi mở màn hình intro
//            }
//        }, 3000)

        showAds()

    }

    override fun dataObservable() {

    }

    private fun getPermission(): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("permission", false)
    }

    private fun navigateToNextScreen() {
        val savedLanguage = SystemUtil.getPreLanguage(this)
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

    @SuppressLint("SuspiciousIndentation")
    private fun showAds(){
        val showInterstitial = Math.random() < 0.5
           if(showInterstitial){
               showOpenAd()
           }else{
               showInterAd()
           }
//            binding.include.visibility = View.VISIBLE
    }

    private fun showOpenAd(){
        val idOpenAd = AdConfig.getDefaultAdId("open_ad_id")
        val checkOpenAd = RemoteConfigManager.getBoolean("open_ad_id")
        if(checkOpenAd && !idOpenAd.isNullOrEmpty()){
            val adCallback: AdCallback = object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    navigateToNextScreen()
                }

                override fun onAdFailedToLoad(i: LoadAdError?) {
                    super.onAdFailedToLoad(i)
                    navigateToNextScreen()
                }
            }
            AppOpenManager.getInstance().loadOpenAppAdSplash(
                this,
                idOpenAd,
                3000,
                10000,
                true,
                adCallback
            )
        }else {
            NextToScreen()
        }
    }

    private fun showInterAd(){
        val idInterAd = AdConfig.getDefaultAdId("inter_ad_id")
        val checkInterAd = RemoteConfigManager.getBoolean("inter_ad_id")
        Admob.getInstance().setOpenActivityAfterShowInterAds(false)
        if(!idInterAd.isNullOrEmpty() && checkInterAd){
            val interCallback: InterCallback = object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    Admob.getInstance().setOpenActivityAfterShowInterAds(true)
                    navigateToNextScreen()
                }

                override fun onAdFailedToLoad(i: LoadAdError?) {
                    super.onAdFailedToLoad(i)
                    navigateToNextScreen()
                }
            }
            Admob.getInstance().loadSplashInterAds2(this, idInterAd, 3000, interCallback)
        }else{
            NextToScreen()
        }
    }

    private fun NextToScreen(){
        binding.include.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000)
    }

}
