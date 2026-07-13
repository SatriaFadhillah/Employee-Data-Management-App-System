package com.example.employeeapp.repository


import android.content.Context
import com.example.employeeapp.model.LoginRequest
import com.example.employeeapp.network.RetrofitClient
import com.example.employeeapp.util.Result
import com.example.employeeapp.util.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository untuk operasi autentikasi (login, logout)
 * Repository bertindak sebagai jembatan antara ViewModel dan sumber data (API)
 */
class AuthRepository(private val context: Context) {

    /**
     * Fungsi login ke server
     * @param email Email user
     * @param password Password user
     * @return Result<String> Success berisi nama user, Error berisi pesan error
     */
    suspend fun login(email: String, password: String): Result<String> {
        // withContext(Dispatchers.IO) memastikan operasi network berjalan di background thread
        return withContext(Dispatchers.IO) {
            try {
                // 1. Panggil API login
                val apiService = RetrofitClient.getInstance(context)
                val response = apiService.login(LoginRequest(email, password))

                // 2. Cek apakah response sukses dan data tidak null
                if (response.success && response.data != null) {
                    // 3. Simpan token dan info user ke SecureStorage
                    SecureStorage.saveToken(context, response.data.token)
                    SecureStorage.saveUserInfo(context, response.data.name, response.data.role)
                    SecureStorage.saveTokenExp(context, response.data.expiresAt)

                    // 4. Reset instance Retrofit agar interceptor menggunakan token baru
                    RetrofitClient.resetInstance()

                    // 5. Kembalikan nama user sebagai data sukses
                    Result.Success(response.data.name)
                } else {
                    // Login gagal dari sisi server (password salah, dll)
                    Result.Error(response.message.ifEmpty { "Login gagal" })
                }
            } catch (e: IOException) {
                // Error koneksi internet (WiFi mati, server tidak bisa dijangkau)
                Result.Error("Tidak ada koneksi internet. Periksa koneksi Anda." + e)
            } catch (e: HttpException) {
                // Error HTTP (401, 404, 500, dll)
                val errorMessage = when (e.code()) {
                    401 -> "Email atau password salah"
                    404 -> "Endpoint API tidak ditemukan"
                    500 -> "Terjadi kesalahan pada server"
                    else -> "Server error: ${e.code()}"
                }
                Result.Error(errorMessage, e.code())
            } catch (e: Exception) {
                // Error tak terduga
                Result.Error(e.message ?: "Terjadi kesalahan tak terduga")
            }
        }
    }

    /**
     * Fungsi logout dari aplikasi
     * Menghapus token di server dan membersihkan data lokal
     * @return Result<Unit> Success jika berhasil, Error jika gagal
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Panggil API logout (abaikan error, tetap hapus lokal)
                val apiService = RetrofitClient.getInstance(context)
                try {
                    apiService.logout()
                } catch (e: Exception) {
                    // Abaikan error network saat logout
                }

                // 2. Hapus semua data dari SecureStorage
                SecureStorage.clearAll(context)

                // 3. Reset instance Retrofit
                RetrofitClient.resetInstance()

                // 4. Kembalikan sukses
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Logout gagal")
            }
        }
    }
}