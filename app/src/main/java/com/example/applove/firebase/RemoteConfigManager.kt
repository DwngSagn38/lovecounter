package com.example.applove.firebase

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigManager {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60  // Cập nhật mỗi 1 phut
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        // Thiết lập giá trị mặc định
        val defaults: Map<String, Any> = mapOf(
            "show_ad" to true,   // Bật/tắt quảng cáo toàn cục
            "native_ad_id" to false,  // ID quảng cáo native
            "inter_ad_id" to false,  // ID quảng cáo interstitial
            "banner_ad_id" to false,  // ID quảng cáo banner
            "open_ad_id" to false, // ID quảng cáo Open App
            "rewarded_ad_id" to false
        )
        remoteConfig.setDefaultsAsync(defaults)

        fetchConfig()
    }

    // Fetch dữ liệu từ Firebase
    private fun fetchConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val allValues = remoteConfig.all.mapValues { it.value.asString() } // Lấy giá trị thực
                    Log.d("RemoteConfig", "Fetch thành công: $allValues")
                } else {
                    Log.e("RemoteConfig", "Fetch thất bại", task.exception)
                }
            }
    }


    // Hàm get giá trị từ Remote Config
    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
}
