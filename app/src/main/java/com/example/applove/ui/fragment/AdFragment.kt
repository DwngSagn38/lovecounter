package com.example.applove.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.databinding.FragmentAdBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.lovecounter.base.BaseFragment
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class AdFragment : BaseFragment<FragmentAdBinding>() {

    override fun inflateViewBinding() = FragmentAdBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleAdView(false)
        Handler(Looper.getMainLooper()).postDelayed({
            toggleAdView(true)
        }, 5000) // Ẩn sau 5 giây

        loadBannerAd()
    }

    private fun loadBannerAd() {
        val adView = AdView(requireContext())
        adView.adUnitId = AdConfig.getDefaultAdId("banner_ad_id").toString()
        adView.setAdSize(getAdSize())

        val extras = Bundle()
        extras.putString("collapsible", "bottom") // Không chắc chắn AdMob hỗ trợ

        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()

        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                viewBinding.containerAdview.removeAllViews() // Xóa trước khi thêm để tránh lỗi
                viewBinding.containerAdview.addView(adView)
                viewBinding.containerAdview.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                viewBinding.containerAdview.visibility = View.GONE
            }
        }
    }


    private fun getAdSize(): AdSize {
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val density = displayMetrics.density
        val adWidth = (widthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)
    }

    private fun toggleAdView(show: Boolean) {
        if (show) {
            viewBinding.containerAdview.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        } else {
            viewBinding.containerAdview.animate()
                .translationY(viewBinding.containerAdview.height.toFloat())
                .setDuration(300)
                .withEndAction { viewBinding.containerAdview.visibility = View.GONE }
                .start()
        }
    }

}
