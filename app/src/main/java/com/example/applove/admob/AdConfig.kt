package com.example.applove.admob

object AdConfig {
    // Danh sách ID quảng cáo mặc định
    private val defaultAdIds = mapOf(
        "native_ad_id" to "ca-app-pub-3940256099942544/2247696110",
        "inter_ad_id" to "ca-app-pub-3940256099942544/1033173712",
        "banner_ad_id" to "ca-app-pub-3940256099942544/2014213617",
        "open_ad_id" to "ca-app-pub-3940256099942544/9257395921",
        "rewarded_ad_id" to "ca-app-pub-3940256099942544/5224354917"
    )

    // Hàm lấy ID quảng cáo mặc định
    fun getDefaultAdId(key: String): String? {
        return defaultAdIds[key]
    }
}
