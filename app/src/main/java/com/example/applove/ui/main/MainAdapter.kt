package com.example.applove.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.applove.ui.fragment.HomeFragment
import com.example.applove.ui.fragment.LoveTestFragment
import com.example.applove.ui.fragment.MemoryFragment

class MainAdapter(fragmentManager : FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int  = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> LoveTestFragment()
            2 -> MemoryFragment()
            else -> HomeFragment()
        }
    }
}