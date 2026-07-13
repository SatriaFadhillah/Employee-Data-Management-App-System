package com.example.employeeapp.model


import com.google.gson.annotations.SerializedName

/**
 * Data class untuk merepresentasikan data karyawan
 * Setiap properti sesuai dengan kolom di tabel employees
 */
data class Employee(
    // ID unik karyawan (auto increment dari database)
    val id: Int = 0,

    // Nama lengkap karyawan
    val name: String = "",

    // Email karyawan (harus unik)
    val email: String = "",

    // Nomor telepon karyawan (opsional)
    val phone: String = "",

    // Jabatan/posisi karyawan
    val position: String = "",

    // Nama departemen (opsional)
    val department: String = "",

    // Gaji karyawan (dalam Rupiah)
    val salary: Double = 0.0,

    // Tanggal bergabung (format: YYYY-MM-DD)
    @SerializedName("join_date")
    val joinDate: String = "",

    // Status aktif (1 = aktif, 0 = nonaktif/soft deleted)
    @SerializedName("is_active")
    val isActive: Int = 1,

    // Timestamp saat record dibuat
    @SerializedName("created_at")
    val createdAt: String = ""
)