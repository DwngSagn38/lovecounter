package com.example.applove.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import com.amazic.ads.callback.RewardCallback
import com.amazic.ads.util.Admob
import com.example.applove.R
import com.example.applove.admob.AdConfig
import com.example.applove.databinding.FragmentLoveTestBinding
import com.example.applove.firebase.RemoteConfigManager
import com.example.applove.ui.lovetest.HoroscopeActivity
import com.example.lovecounter.base.BaseFragment
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd


class LoveTestFragment : BaseFragment<FragmentLoveTestBinding>() {
    override fun inflateViewBinding() = FragmentLoveTestBinding.inflate(layoutInflater)
    private var rewardedAd: RewardedAd? = null
    private val TAG = "RewardedAd"
    private var unlock = false
    override fun initView() {
        super.initView()

        val text = getString(R.string.love_test)
        val spannable = SpannableString(text)

        spannable.setSpan(
            ForegroundColorSpan(Color.RED), // Màu đỏ
            5, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        setBackground()

        viewBinding.txtLoveTest.text = spannable

        viewBinding.rlNametest.setOnClickListener {
            Toast.makeText(requireContext(),R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }

//        MobileAds.initialize(requireContext()) // Khởi tạo AdMob
//        loadRewardedAd() // Load quảng cáo lúc mở app
        val idRewarAd = AdConfig.getDefaultAdId("rewarded_ad_id")
        Admob.getInstance().initRewardAds(requireContext(),idRewarAd);

        viewBinding.rlHoroscope.setOnClickListener {
            if (unlock){
                startActivity(Intent(requireContext(), HoroscopeActivity::class.java))
            }else{
                showDialogUnlock()
            }
        }

    }

    private fun showDialogUnlock(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.unlock)
        builder.setMessage(R.string.unlock_desc)
        builder.setPositiveButton(R.string.watch_video) { dialog, which ->
//            showRewardedAd()
            showRewardedAd()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

//    private fun loadRewardedAd() {
//        val adUnitId = "ca-app-pub-3940256099942544/5224354917" // ID test của Google
//
////        val adUnitId = RemoteConfigManager.getString("rewarded_ad_id")
//        Log.d(TAG, "Rewarded Ad ID: $adUnitId")
//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(requireContext(), adUnitId, adRequest, object : RewardedAdLoadCallback() {
//            override fun onAdLoaded(ad: RewardedAd) {
//                rewardedAd = ad
//                Log.d(TAG, "Rewarded Ad Loaded")
//            }
//
//            override fun onAdFailedToLoad(error: LoadAdError) {
//                Log.e(TAG, "Failed to Load Rewarded Ad: ${error.message}")
//                rewardedAd = null
//            }
//        })
//    }
//
//    private fun showRewardedAd() {
//        rewardedAd?.let { ad ->
//            ad.show(requireActivity()) { rewardItem: RewardItem ->
//                val rewardAmount = rewardItem.amount
//                val rewardType = rewardItem.type
//                Log.d(TAG, "User earned reward: $rewardAmount $rewardType")
//                // TODO: Thêm logic nhận thưởng tại đây (ví dụ: tăng điểm, mở khóa nội dung)
//                unlock = true
//                setBackground()
//            }
//            rewardedAd = null // Xóa quảng cáo sau khi xem, cần load lại
//            loadRewardedAd() // Load lại quảng cáo mới
//        } ?: run {
//            Log.e(TAG, "Rewarded Ad is not ready yet")
//        }
//    }

    private fun showRewardedAd() {
        val checkRewardedAd = RemoteConfigManager.getBoolean("rewarded_ad_id")
        val idRewarAd = AdConfig.getDefaultAdId("rewarded_ad_id")

        if(!checkRewardedAd){
            Toast.makeText(requireContext(),R.string.ads_not_ready, Toast.LENGTH_SHORT).show()
            return
        }
        Admob.getInstance().showRewardAds(requireActivity(), object : RewardCallback {
            override fun onEarnedReward(rewardItem: RewardItem) {
                // code here
                unlock = true
                setBackground()
                Admob.getInstance().initRewardAds(requireContext(),idRewarAd);
            }

            override fun onAdClosed() {
                // code here
//                unlock = true
//                setBackground()
            }

            override fun onAdFailedToShow(codeError: Int) {
                // code here
                Toast.makeText(requireContext(),R.string.ads_not_ready, Toast.LENGTH_SHORT).show()
            }

            override fun onAdImpression() {

            }
        })

    }

    private fun setBackground(){
        if (!unlock){
            viewBinding.rlHoroscope.setBackgroundResource(R.drawable.bg_lock)
        }else{
            viewBinding.rlHoroscope.setBackgroundResource(R.drawable.bg_gray)
        }
    }
}