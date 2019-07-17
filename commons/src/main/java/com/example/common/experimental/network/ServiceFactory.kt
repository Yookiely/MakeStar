package com.example.common.experimental.network


































data class CommonBody<out T>(
    val error_code: Int,
    val message: String,
    val data: T?
)