package com.example.applove.ui.setting

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.util.Admob
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.admob.AdsManager
import com.example.applove.roomdb.model.LanguageModel
import com.example.applove.databinding.ActivityLanguageBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.applove.ui.onbording.OnbordingActivity
import com.example.applove.utils.SystemUtil
import com.example.lovecounter.adapter.LanguageAdapter
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.Locale

class LanguageActivity : BaseActivity<ActivityLanguageBinding, BaseViewModel>() {
    override fun createBinding() = ActivityLanguageBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    private lateinit var languge : String
    private lateinit var adapter: LanguageAdapter
    private var listLanguage: ArrayList<LanguageModel> = ArrayList()
    private var codeLang = ""
    private var choosenLanguage = false

    override fun initView() {
        super.initView()

        initData()

        val fromSplash = intent.getBooleanExtra("from_splash", false)
        if (fromSplash) {
            binding.imgBack.visibility = View.GONE
            binding.txtLanguage.gravity = Gravity.START
            binding.imgDone.setOnClickListener {
                if (choosenLanguage) {
                    val intent = Intent(this, OnbordingActivity::class.java)
                    startActivity(intent)
                    SystemUtil.saveLocale(this,codeLang)
                }else{
                    Toast.makeText(this, R.string.please_choose_language, Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            binding.imgDone.setOnClickListener {
                if (choosenLanguage) {
                    SystemUtil.saveLocale(this,codeLang)
                    back()
                }else{
                    Toast.makeText(this, R.string.please_choose_language, Toast.LENGTH_SHORT).show()
                }
            }
            binding.imgBack.setOnClickListener {
                finish()
            }
        }
        showNativeAds()

        // Load quảng cáo native
//        AdsManager.loadNativeAd(this, binding.nativeAdView)
    }
    override fun dataObservable() {
        setCodeLanguage()
        initData()
    }

    private fun setCodeLanguage() {
        //language
        val codeLangDefault = Locale.getDefault().language
        val langDefault = arrayOf("fr", "pt", "es", "de", "in", "en", "hi", "vi", "ja") //"hi" ấn độ
        codeLang =
            if (SystemUtil.getPreLanguage(this).equals(""))
                if (!mutableListOf(*langDefault)
                        .contains(codeLangDefault)
                ) {
                    "en"
                } else {
                    codeLangDefault
                } else ({
                SystemUtil.getPreLanguage(this)
            }).toString()
    }

    private fun initData() {
        listLanguage.clear()
        listLanguage.addAll(SystemUtil.listLanguage())
        adapter = LanguageAdapter(listLanguage) { selectedLanguage ->
            choosenLanguage = true
            codeLang = selectedLanguage.code
        }

        binding.rcvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcvLanguage.adapter = adapter
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

    private fun back() {
        finishAffinity()
        val intent = Intent(this@LanguageActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}