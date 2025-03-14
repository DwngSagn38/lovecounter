package com.example.applove.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.applove.R
import com.example.applove.databinding.FragmentMemoryBinding
import com.example.lovecounter.base.BaseFragment



class MemoryFragment : BaseFragment<FragmentMemoryBinding>() {
    override fun inflateViewBinding() = FragmentMemoryBinding.inflate(layoutInflater)

    override fun initView() {
        super.initView()
        viewBinding.btnAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Chuc nang chua lam!!!", Toast.LENGTH_SHORT).show()
        }
    }
}