package com.example.applove.admob

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.example.applove.R
import com.example.applove.firebase.RemoteConfigManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

@SuppressLint("StaticFieldLeak")
object AdsManager : Application.ActivityLifecycleCallbacks {
    private var appOpenAd: AppOpenAd? = null
    private var nativeAd: NativeAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var currentActivity: Activity? = null


    fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
        loadAppOpenAd(application)
        loadInterstitialAd(application)
    }

    // 🔹 Load Native Ad
    fun loadNativeAd(activity: Activity, adContainer: FrameLayout) {
        val adUnitId = AdConfig.getDefaultAdId("native_ad_id")
        val adLoader = AdLoader.Builder(activity, adUnitId!!)
            .forNativeAd { ad ->
                nativeAd = ad
                val inflater = LayoutInflater.from(activity)
                val nativeAdView = inflater.inflate(R.layout.layout_native_ad, null) as NativeAdView

                populateNativeAdView(ad, nativeAdView)

                // Thêm quảng cáo vào `FrameLayout`
                adContainer.removeAllViews()
                adContainer.addView(nativeAdView)
                adContainer.visibility = View.VISIBLE
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdsManager", "Native Ad lỗi: ${error.message}")
                    adContainer.visibility = View.GONE
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
        nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
        nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
        nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
        nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)

        (nativeAdView.headlineView as? TextView)?.text = nativeAd.headline
        (nativeAdView.bodyView as? TextView)?.text = nativeAd.body
        (nativeAdView.callToActionView as? Button)?.text = nativeAd.callToAction

        nativeAdView.setNativeAd(nativeAd)
    }
    // 🔹 Load App Open Ad
    private fun loadAppOpenAd(application: Application) {
        val adUnitId = AdConfig.getDefaultAdId("open_ad_id")
//        val adUnitId = "ca-app-pub-3940256099942544/9257395921" // Open Ad test ID của Google
        val checkOpenAd = RemoteConfigManager.getBoolean("open_ad_id")
        if(!checkOpenAd){
            appOpenAd = null
            return
        }

        Log.d("AdsManager", "Id Open Ad: $adUnitId")

        val adRequest = AdRequest.Builder().build()
        Log.d("AdsManager", "🔄 Bắt đầu tải Open Ad...") // Log để kiểm tra khi bắt đầu tải

        AppOpenAd.load(application, adUnitId!!, adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d("AdsManager", "✅ Open Ad đã tải thành công!")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdsManager", "❌ Open Ad lỗi: ${error.code} - ${error.message} - Domain: ${error.domain}")
                    appOpenAd = null
                }

            })
    }



    fun showAdIfAvailable( onAdDismissed: () -> Unit) {
        val activity = currentActivity ?: return
        if (appOpenAd != null) {
            Log.d("AdsManager", "🎬 Hiển thị Open Ad...")
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    Log.d("AdsManager", "✅ Open Ad đã hiển thị, tải lại quảng cáo mới...")
                    loadAppOpenAd(activity.application) // Tải lại quảng cáo ngay sau khi hiển thị
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e("AdsManager", "❌ Open Ad lỗi: ${error.message}")
                    loadAppOpenAd(activity.application)
                    onAdDismissed()
                }
            }
            appOpenAd!!.show(activity)
        } else {
            Log.d("AdsManager", "⏳ Quảng cáo chưa sẵn sàng, không hiển thị!")
            onAdDismissed()
        }
    }




    private val TAG = "InterstitialAd"

    fun loadInterstitialAd(application: Application) {
        val adUnitId = AdConfig.getDefaultAdId("inter_ad_id")

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(application, adUnitId!!, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d(TAG, "Interstitial Ad Loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Failed to Load Interstitial Ad: ${error.message}")
                interstitialAd = null
            }
        })
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed, moving to next screen")
                    loadInterstitialAd(activity.application)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e(TAG, "Ad failed to show: ${error.message}")
                    loadInterstitialAd(activity.application)
                    onAdDismissed()
                }
            }
            ad.show(activity) // Hiển thị quảng cáo
        } ?: run {
            Log.e(TAG, "Interstitial Ad not ready, moving to next screen")
            onAdDismissed() // Nếu quảng cáo chưa sẵn sàng, vẫn chuyển màn hình
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
