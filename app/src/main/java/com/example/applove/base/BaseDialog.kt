package com.example.lovecounter.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.viewbinding.ViewBinding
import com.example.applove.R
import com.example.applove.utils.SystemUtil
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseDialog<VB : ViewBinding>(var activity: Activity, var canAble: Boolean) :
    Dialog(activity, R.style.BaseDialog) {

    lateinit var binding: VB

    abstract fun getContentView(): VB
    abstract fun initView()
    abstract fun bindView()

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(activity)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = getContentView()
        setContentView(binding.root)
        setCancelable(canAble)
        initView()
        bindView()
    }
}
