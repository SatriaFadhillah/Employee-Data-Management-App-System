package com.example.employeeapp.model

import com.google.gson.annotations.SerializedName

/**
 * Data class untuk response login setelah sukses
 * Berisi token dan informasi user
 */
data class LoginData(
    // Bearer token untuk autentikasi request berikutnya
    val token: String,

    // Nama user yang login
    val name: String,

    // Role user (admin, hr, viewer) - untuk otorisasi fitur
    val role: String,

    // Waktu expired token (format: YYYY-MM-DD HH:MM:SS)
    @SerializedName("expires_at")
    val expiresAt: String
)