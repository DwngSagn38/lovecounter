package com.example.applove.ui.startdate

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.applove.R
import com.example.applove.admob.AdsManager
import com.example.applove.databinding.ActivityStartDateBinding
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.repository.DateRepository
import com.example.applove.ui.main.MainActivity
import com.example.applove.viewmodel.DateViewModel
import com.example.lovecounter.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Calendar

class StartDateActivity : BaseActivity<ActivityStartDateBinding, DateViewModel>() {

    override fun createBinding() = ActivityStartDateBinding.inflate(layoutInflater)

    override fun setViewModel() = DateViewModel(DateRepository(DBHelper.getIntance(this)))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        super.initView()

        setColorTitle()

        binding.icBack.setOnClickListener {
            finish()
        }

        binding.llSetDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnDone.setOnClickListener {
            val selectedDate = binding.txtDate.text.toString()
            viewModel.insertOrReplaceDate(selectedDate)
            Toast.makeText(this, R.string.save_date, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(500)
                startActivity(Intent(this@StartDateActivity, MainActivity::class.java))
            }
        }

//
//        binding.btnDone.setOnClickListener {
//            val selectedDate = binding.txtDate.text.toString()
//            viewModel.insertOrReplaceDate(selectedDate)
//
//            // Gửi thông báo qua FCM
//            sendNotificationToUser("Nhắc nhở", "Bạn đã đặt ngày: $selectedDate")
//
//            Toast.makeText(this, "Đã lưu ngày: $selectedDate", Toast.LENGTH_SHORT).show()
//            lifecycleScope.launch {
//                delay(500)
//                startActivity(Intent(this@StartDateActivity, MainActivity::class.java))
//                finish()
//            }
//        }



        // Lắng nghe LiveData để cập nhật txtDate
        viewModel.selectedDate.observe(this) { date ->
            binding.txtDate.setText(date)
        }

        // Gọi hàm để lấy ngày từ database
        viewModel.getSavedDate()
    }

    override fun dataObservable() {

    }

    private fun setColorTitle(){
        val text = getString(R.string.start_date)
        val spannable = SpannableString(text)
        // Set màu đỏ cho "Counter"
        if (text.length >= 7) {
            spannable.setSpan(
                ForegroundColorSpan(Color.RED), // Màu đỏ
                6, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        // Set text cho TextView
        binding.txtTitle.text = spannable
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.txtDate.setText(selectedDate)
            },
            year, month, day
        )
        // Giới hạn chỉ chọn ngày hôm nay trở về trước
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

//    private fun sendNotificationToUser(title: String, message: String) {
//        val jsonBody = JSONObject()
//        val notification = JSONObject()
//        val data = JSONObject()
//
//        try {
//            notification.put("title", title)
//            notification.put("body", message)
//
//            data.put("title", title)
//            data.put("body", message)
//
//            jsonBody.put("to", "/topics/all") // Gửi đến tất cả người dùng đăng ký
//            jsonBody.put("notification", notification)
//            jsonBody.put("data", data)
//
//            val url = "https://fcm.googleapis.com/fcm/send"
//            val request = object : StringRequest(
//                Method.POST, url,
//                Response.Listener<String> { response ->
//                    Log.d("FCM", "Thông báo đã gửi: $response")
//                },
//                Response.ErrorListener { error ->
//                    Log.e("FCM", "Lỗi gửi thông báo: ${error.message}")
//                }
//            ) {
//                override fun getHeaders(): Map<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers["Authorization"] = "key=BH2-gLs4OJ0G6vf_HTCF6qSpYdcxbP2b13BccOzgUmLWU3qTj3XdFFcERli-jd8mTFxyqU-FdoW8AFcBn7tH8GM" // server key trong Firebase
//                    headers["Content-Type"] = "application/json"
//                    return headers
//                }
//
//                override fun getBody(): ByteArray {
//                    return jsonBody.toString().toByteArray(Charsets.UTF_8)
//                }
//            }
//
//            Volley.newRequestQueue(this).add(request)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}