package com.example.lovecounter.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.applove.admob.AdsManager
import com.google.android.gms.ads.MobileAds


abstract class BaseActivity<VB : ViewBinding, V : ViewModel> : AppCompatActivity() {
    protected lateinit var binding : VB
    protected lateinit var viewModel : V

    abstract fun createBinding() : VB
    abstract fun setViewModel() : V

    protected open fun initView() {}
    protected open fun bindView() {}
    protected open fun viewModel() {}

    open fun initData() {}

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        binding = createBinding()
        setContentView(binding.root)
        binding.root.setBackgroundColor(Color.WHITE)
        viewModel = setViewModel()
        viewModel()
        initView()
        bindView()


        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = Color.TRANSPARENT

    }


    override fun onResume() {
        super.onResume()
        AdsManager.onActivityResumed(this)
    }
}