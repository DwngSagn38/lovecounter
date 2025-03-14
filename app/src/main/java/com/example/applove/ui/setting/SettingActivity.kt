package com.example.applove.ui.setting

import android.content.Intent
import com.example.applove.R
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivitySettingBinding
import com.example.applove.ui.fragment.AdFragment
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel

class SettingActivity : BaseActivity<ActivitySettingBinding, BaseViewModel>() {
    override fun createBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    private var message: String = ""
    override fun initView() {
        super.initView()

//        val remoteConfig = FirebaseRemoteConfig.getInstance()
//        remoteConfig.fetchAndActivate()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    message = remoteConfig.getString("ads")
//                    binding.txtSetting.text = message
//                    Log.d("RemoteConfig", "Giá trị từ Firebase: $message")
//                } else {
//                    Log.e("RemoteConfig", "Lỗi khi lấy dữ liệu từ Firebase")
//                }
//            }
//


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.llLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        // Load AdFragment vào FragmentContainerView
        supportFragmentManager.beginTransaction()
            .replace(R.id.adFragmentContainer, AdFragment())
            .commit()

    }

//    override fun onResume() {
//        super.onResume()
//        AdsManager.showAdIfAvailable(this)
//    }
}