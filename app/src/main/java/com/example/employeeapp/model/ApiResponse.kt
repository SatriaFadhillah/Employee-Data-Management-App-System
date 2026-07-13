package com.example.employeeapp.model


import com.google.gson.annotations.SerializedName

/**
 * Data class generic untuk response dari API
 * Semua endpoint mengembalikan format response yang sama
 * * @param T Tipe data yang dibungkus (Employee, List<Employee>, LoginData, dll)
 */
data class ApiResponse<T>(
    // Status sukses/gagal dari operasi API
    @SerializedName("success")
    val success: Boolean = false,

    // Pesan dari server (misal: "Login berhasil", "Email sudah terdaftar")
    @SerializedName("message")
    val message: String = "",

    // Data utama response (bisa null jika gagal)
    @SerializedName("data")
    val data: T? = null
)