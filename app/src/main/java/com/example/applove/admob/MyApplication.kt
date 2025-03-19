package com.example.applove.admob

import com.amazic.ads.util.AdsApplication
import com.google.android.gms.ads.MobileAds

//class MyApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        MobileAds.initialize(this) {}
//        AdsManager.initialize(this)
//        AppLifecycleObserver()
//
//    }
//}


class MyApplication : AdsApplication() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AdsManager.initialize(this)
        AppLifecycleObserver()
    }
    fun enableAdsResume(): Boolean {
        return true
    }

    val listTestDeviceId: List<String>?
        get() = null

    val resumeAdId: String
        get() = AdConfig.getDefaultAdId("open_ad_id").toString()

    override fun getAppTokenAdjust(): String {
        return "your_app_token_adjust" // Thay bằng token thực tế của bạn hoặc một chuỗi tạm thời
    }

    override fun getFacebookID(): String {
        return "your_facebook_id" // Thay bằng ID Facebook thực tế hoặc một chuỗi tạm thời
    }

    override fun buildDebug(): Boolean {
        return true
    }
}

