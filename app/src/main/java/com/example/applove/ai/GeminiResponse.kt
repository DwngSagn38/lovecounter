package com.example.applove.ai

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val output: String
)
