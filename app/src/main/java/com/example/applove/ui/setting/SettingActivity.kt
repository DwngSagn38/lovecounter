package com.example.applove.ui.setting

import android.content.Intent
import android.view.View
import com.amazic.ads.util.Admob
import com.amazic.ads.util.BannerGravity
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivitySettingBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.fragment.AdFragment
import com.example.applove.utils.HelperMenu
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel

class SettingActivity : BaseActivity<ActivitySettingBinding, BaseViewModel>() {
    override fun createBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()
    private var helperMenu: HelperMenu? = null


    override fun initView() {
        super.initView()

        binding.apply {
            imgBack.setOnClickListener { finish() }
            llLanguage.setOnClickListener { startActivity(Intent(this@SettingActivity, LanguageActivity::class.java)) }
            llPolicy.setOnClickListener { helperMenu?.showPolicy() }
            llRate.setOnClickListener { helperMenu?.showDialogRate(true) }
            llShare.setOnClickListener { helperMenu?.showShareApp() }
            llFeedback.setOnClickListener { helperMenu?.showDialogFeedback() }
        }

        val idBannerAd = AdConfig.getDefaultAdId("banner_ad_id")
        if(RemoteConfigManager.getBoolean("banner_ad_id") && !idBannerAd.isNullOrBlank()){
            Admob.getInstance().loadBanner(this, idBannerAd, true)
            binding.include.visibility = View.VISIBLE
        }else{
            binding.include.visibility = View.GONE
        }
    }

    override fun dataObservable() {
        helperMenu = HelperMenu(this)
    }

}