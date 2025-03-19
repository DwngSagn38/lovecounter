package com.example.applove.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.amazic.ads.callback.BannerCallBack
import com.amazic.ads.util.Admob
import com.amazic.ads.util.BannerGravity
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivityMainBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.fragment.AdFragment
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>() {
    override fun createBinding() =  ActivityMainBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()
    override fun dataObservable() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = MainAdapter(this)
        binding.viewPage2.adapter = adapter
        binding.viewPage2.isUserInputEnabled = false
        binding.viewPage2.offscreenPageLimit = 3

        binding.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setDefaultIcons().also { binding.ivHome.setColorFilter(getColor(R.color.red))}
                        setDefaultText().also { binding.txtHome.setTextColor(getColor(R.color.red))}
                    }
                    1 -> {
                        setDefaultIcons().also { binding.ivLoveTest.setColorFilter(getColor(R.color.red))}
                        setDefaultText().also { binding.txtLoveTest.setTextColor(getColor(R.color.red))}
                    }
                    2 -> {
                        setDefaultIcons().also { binding.ivMemory.setColorFilter(getColor(R.color.red))}
                        setDefaultText().also { binding.txtMemory.setTextColor(getColor(R.color.red))}
                    }
                }
            }
        })

        binding.llHome.setOnClickListener { binding.viewPage2.setCurrentItem(0) }
        binding.llLoveTest.setOnClickListener { binding.viewPage2.setCurrentItem(1) }
        binding.llMemory.setOnClickListener { binding.viewPage2.setCurrentItem(2) }

        // Load AdFragment vào FragmentContainerView
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.adFragmentContainer, AdFragment())
//            .commit()

        val idBannerAd = AdConfig.getDefaultAdId("banner_ad_id")
        if(RemoteConfigManager.getBoolean("banner_ad_id") && !idBannerAd.isNullOrBlank()){
            Admob.getInstance().loadCollapsibleBanner(this, idBannerAd, BannerGravity.bottom)
            binding.include.visibility = View.VISIBLE
        }else{
            binding.include.visibility = View.GONE
        }


    }

    // Hàm set lại màu sắc các biểu tượng về mặc định
    private fun setDefaultIcons() {
        binding.ivHome.setColorFilter(getColor(R.color.black))
        binding.ivMemory.setColorFilter(getColor(R.color.black))
        binding.ivLoveTest.setColorFilter(getColor(R.color.black))
    }

    // Hàm set lại màu sắc các biểu tượng về mặc định
    private fun setDefaultText() {
        binding.txtHome.setTextColor(getColor(R.color.black))
        binding.txtLoveTest.setTextColor(getColor(R.color.black))
        binding.txtMemory.setTextColor(getColor(R.color.black))
    }

    private fun toggleAdView(show: Boolean) {
        if (show) {
            binding.include.visibility = View.VISIBLE // Hiển thị trước khi chạy animation
            binding.include.translationY = binding.include.height.toFloat() // Đặt vị trí ban đầu
            binding.include.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        } else {
            binding.include.animate()
                .translationY(binding.include.height.toFloat())
                .setDuration(300)
                .withEndAction { binding.include.visibility = View.GONE }
                .start()
        }
    }

    override fun onResume() {
        super.onResume()

    }
}