package com.example.employeeapp.network

import android.content.Context
import com.example.employeeapp.util.SecureStorage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor untuk menambahkan header autentikasi ke setiap request
 * Interceptor ini akan dipanggil sebelum request dikirim ke server
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Ambil request asli
        val originalRequest = chain.request()

        // Buat builder untuk memodifikasi request
        val requestBuilder = originalRequest.newBuilder()

        // =================================================================
        // TAMBAHKAN HEADER X-API-KEY (wajib untuk semua request)
        // =================================================================
        val apiKey = SecureStorage.getApiKey(context)
        requestBuilder.addHeader("X-API-KEY", apiKey)

        // =================================================================
        // TAMBAHKAN HEADER AUTHORIZATION BEARER TOKEN (jika ada)
        // =================================================================
        val token = SecureStorage.getToken(context)
        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Tambahkan header Content-Type untuk request dengan body
        requestBuilder.addHeader("Content-Type", "application/json")

        // Bangun request baru
        val modifiedRequest = requestBuilder.build()

        // Eksekusi request
        val response = chain.proceed(modifiedRequest)

        // =================================================================
        // HANDLE RESPONSE 401 (UNAUTHORIZED / TOKEN EXPIRED)
        // =================================================================
        // Jika server meresponse 401, hapus token lokal karena sudah tidak valid
        if (response.code == 401) {
            SecureStorage.clearAll(context)
        }

        return response
    }
}