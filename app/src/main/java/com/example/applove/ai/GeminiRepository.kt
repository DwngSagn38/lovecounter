package com.example.applove.ai

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiRepository {
    private const val API_KEY = "AIzaSyC5ZBF29fgHSsRvxHWXTSFT2etga5YloxM"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val service = retrofit.create(GeminiApiService::class.java)

    fun analyzeCompatibility(userSign: String, partnerSign: String, callback: (String) -> Unit) {
        val prompt = "Analyze the love compatibility between $userSign and $partnerSign and provide relationship advice."
        val request = GeminiRequest(
            model = "gemini-pro",
            prompt = GeminiPrompt(prompt)
        )


        val call = service.getHoroscopeAnalysis(API_KEY, request)

        call.enqueue(object : retrofit2.Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                if (response.isSuccessful) {
                    val advice = response.body()?.candidates?.get(0)?.output ?: "No advice available."
                    callback(advice)
                } else {
                    callback("Error: ${response.errorBody()?.string()}")
                    Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<GeminiResponse>, t: Throwable) {
                callback("Error: ${t.message}")
            }
        })
    }
}
