package com.example.applove.admob

import android.app.Application
import android.util.Log
import com.example.applove.admob.AdsManager
import com.example.applove.firebase.RemoteConfigManager
import com.google.android.gms.ads.MobileAds

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        AdsManager.initialize(this)
        AppLifecycleObserver()

    }
}
