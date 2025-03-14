package com.example.applove.admob

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class AppLifecycleObserver() : LifecycleObserver {
    private var isAppInBackground = true

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        if (isAppInBackground) {
            Log.d("AppLifecycleObserver", "App moved to foreground")
            AdsManager.showAdIfAvailable{}
        }
        isAppInBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        Log.d("AppLifecycleObserver", "App moved to background")
        isAppInBackground = true
    }
}

