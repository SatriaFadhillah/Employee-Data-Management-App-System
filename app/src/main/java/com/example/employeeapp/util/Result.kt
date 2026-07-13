package com.example.employeeapp.util

/**
 * Sealed class untuk merepresentasikan hasil dari operasi asynchronous
 * Digunakan di ViewModel untuk mengirim state ke UI
 * * Keuntungan sealed class:
 * 1. Type-safe - compiler tahu semua kemungkinan subclass
 * 2. When expression bisa memeriksa semua case
 * 3. Tidak perlu boolean flag (loading/success/error) terpisah
 */
sealed class Result<out T> {

    /**
     * State loading: operasi sedang berjalan
     * Tidak membawa data
     */
    object Loading : Result<Nothing>()

    /**
     * State sukses: operasi berhasil
     * @param data Data hasil operasi (bisa berupa Employee, List<Employee>, dll)
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * State error: operasi gagal
     * @param message Pesan error untuk ditampilkan ke user
     * @param code HTTP status code (401, 404, 409, dll) untuk handling khusus
     */
    data class Error(val message: String, val code: Int = -1) : Result<Nothing>()
}