package com.example.applove.ui.onbording


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.amazic.ads.callback.InterCallback
import com.amazic.ads.util.Admob
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.databinding.ActivityOnbordingBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.applove.ui.permission.PermissionActivity
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class OnbordingActivity : BaseActivity<ActivityOnbordingBinding, BaseViewModel>() {

    private lateinit var viewPager: ViewPager2

    override fun createBinding() = ActivityOnbordingBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    override fun initView() {
        super.initView()

        viewPager = findViewById(R.id.viewPage)

        val onboardingItem = listOf(
            OnboardingItem(
                R.drawable.ic_onboarding1,
                getString(R.string.title_intro1),
                getString(R.string.description_intro1)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding2,
                getString(R.string.title_intro2),
                getString(R.string.description_intro2)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding3,
                getString(R.string.title_intro3),
                getString(R.string.description_intro3)
            )
        )

        val adapter = OnboardingAdapter(onboardingItem)
        viewPager.adapter = adapter

        binding.circleIndicator.setViewPager(viewPager)

        binding.txtNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else if (viewPager.currentItem == onboardingItem.size - 1) {
                showInterAds()
            }
        }

        showNativeAds()

        // Load quảng cáo native
//        AdsManager.loadNativeAd(this, binding.nativeAdView)

    }

    override fun dataObservable() {

    }

    private fun hasPermissions(): Boolean {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13+
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> { // Android 11+
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            else -> { // Android 10 trở xuống
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun showNativeAds(){
        val idNativeAd = AdConfig.getDefaultAdId("native_ad_id")
        val checkNativeAd = RemoteConfigManager.getBoolean("native_ad_id")

        if (checkNativeAd && !idNativeAd.isNullOrEmpty()) {
            binding.nativeAds.visibility = View.VISIBLE
            Admob.getInstance()
                .loadNativeAd(this, idNativeAd, binding.nativeAds, R.layout.layout_native_ad)
        } else {
            binding.nativeAds.visibility = View.GONE
        }
    }

    private fun showInterAds(){
        val idInterAd = AdConfig.getDefaultAdId("inter_ad_id")
        val checkInterAd = RemoteConfigManager.getBoolean("inter_ad_id")
        Admob.getInstance().setOpenActivityAfterShowInterAds(false)
        if (!idInterAd.isNullOrEmpty() && checkInterAd) {
            val interCallback: InterCallback = object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    Admob.getInstance().setOpenActivityAfterShowInterAds(true)
                    navigateToNextScreen()
                }
            }
            Admob.getInstance().loadSplashInterAds2(this, idInterAd, 10, interCallback)
        }else{
            navigateToNextScreen()
        }
    }

    // Hàm điều hướng sau khi đóng quảng cáo
    private fun navigateToNextScreen() {
        if (hasPermissions()) {
            startActivity(Intent(this@OnbordingActivity, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@OnbordingActivity, PermissionActivity::class.java))
        }
    }
}