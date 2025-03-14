package com.example.lovecounter.base

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseDialog(private val context: Context) {
    private var dialog: BottomSheetDialog? = null

    abstract fun getLayoutId(): Int

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(getLayoutId(), null)
        dialog = BottomSheetDialog(context)
        dialog?.setContentView(view)
        setupView(view, dialog!!)
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

    abstract fun setupView(view: View, dialog: Dialog)
}
