package com.example.applove.ui.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazic.ads.util.Admob
import com.amazic.ads.util.BannerGravity
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.databinding.ActivityPermissionBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.main.MainActivity
import com.example.lovecounter.base.BaseActivity
import com.example.lovecounter.base.BaseViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class PermissionActivity : BaseActivity<ActivityPermissionBinding, BaseViewModel>() {
    override fun createBinding() = ActivityPermissionBinding.inflate(layoutInflater)

    override fun setViewModel() = BaseViewModel()

    override fun initView() {
        super.initView()

        binding.switchPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestPermission()
            }
        }

        binding.txtContinue.setOnClickListener {
            if (binding.switchPermission.isChecked) {
                savePermission(true)
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                Toast.makeText(this, R.string.permission_continue, Toast.LENGTH_SHORT).show()
            }
        }

        val idBannerAd = AdConfig.getDefaultAdId("banner_ad_id")
        if(RemoteConfigManager.getBoolean("banner_ad_id") && !idBannerAd.isNullOrBlank()){
            Admob.getInstance().loadBanner(this, idBannerAd,true)
            binding.include.visibility = View.VISIBLE
        }else{
            binding.include.visibility = View.GONE
        }
    }

    override fun dataObservable() {

    }

    private fun checkAndRequestPermission() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13+
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> { // Android 11+
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            else -> { // Android 10 trở xuống
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), 1001)
        } else {
//            Toast.makeText(this, "Quyền đã được cấp!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                Toast.makeText(this, "Đã cấp quyền thành công!", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Quyền bị từ chối!", Toast.LENGTH_SHORT).show()
                binding.switchPermission.isChecked = false
            }
        }
    }

    private fun savePermission(ischecked : Boolean){
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("permission", ischecked)
        editor.apply()
    }
}
