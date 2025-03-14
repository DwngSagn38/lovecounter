package com.example.applove.ui.onbording


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.applove.R
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivityOnbordingBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.applove.ui.permission.PermissionActivity
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class OnbordingActivity : BaseActivity<ActivityOnbordingBinding, BaseViewModel>() {

    private lateinit var viewPager: ViewPager2

    override fun createBinding() = ActivityOnbordingBinding.inflate(layoutInflater)

    override fun setViewModel()=  BaseViewModel()

    override fun initView() {
        super.initView()

        viewPager = findViewById(R.id.viewPage)

        val onboardingItem = listOf(
            OnboardingItem(R.drawable.ic_onboarding1, "Test Your Relationship", "Find out your love compatibility\n" +
                    " with your partner"),
            OnboardingItem(R.drawable.ic_onboarding2, "Love Day Counter", "Keep track of how long you \n" +
                    "have been together"),
            OnboardingItem(R.drawable.ic_onboarding3, "Memorable Moments", "Preserve cherished moments together")
        )

        val adapter = OnboardingAdapter(onboardingItem)
        viewPager.adapter = adapter

        binding.circleIndicator.setViewPager(viewPager)

        binding.txtNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            }
            else if (viewPager.currentItem == onboardingItem.size - 1) {
                AdsManager.showInterstitialAd(this){
                    if (hasPermissions()) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this, PermissionActivity::class.java))
                    }
                }
            }
        }

        // Load quảng cáo native
        AdsManager.loadNativeAd(this, binding.nativeAdView)

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


}