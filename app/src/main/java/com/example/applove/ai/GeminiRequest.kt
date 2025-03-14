package com.example.applove.ai

data class GeminiRequest(
    val model: String = "gemini-pro",
    val prompt: GeminiPrompt
)

data class GeminiPrompt(
    val text: String
)
