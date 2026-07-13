package com.example.employeeapp.network

import android.content.Context
import android.content.pm.ApplicationInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Object singleton untuk membuat instance Retrofit
 * Singleton memastikan hanya ada satu instance Retrofit sepanjang aplikasi berjalan
 */
object RetrofitClient {

    // =========================================================================
    // BASE URL - SESUAIKAN DENGAN LINGKUNGAN DEVELOPMENT ANDA!
    // =========================================================================
    // Untuk emulator Android: gunakan 10.0.2.2 (localhost dari emulator)
    // Untuk perangkat fisik: gunakan IP komputer di jaringan yang sama
    // Contoh: "http://192.168.1.100/employee_api/"
    // private const val BASE_URL = "http://10.0.2.2/employee_api/"
    private const val BASE_URL = "http://10.113.162.138/employee_api/"

    // Volatile memastikan perubahan instance terlihat oleh semua thread
    @Volatile
    private var instance: ApiService? = null

    /**
     * Mendapatkan instance ApiService
     * @param context Context aplikasi (untuk SecureStorage)
     * @return ApiService yang sudah siap pakai
     */
    fun getInstance(context: Context): ApiService {
        return instance ?: synchronized(this) {
            instance ?: createApiService(context).also { instance = it }
        }
    }

    /**
     * Mereset instance (dipanggil saat logout atau token berubah)
     * Memaksa pembuatan instance baru dengan token terbaru
     */
    fun resetInstance() {
        instance = null
    }

    /**
     * Membuat instance ApiService baru
     * @param context Context aplikasi
     * @return ApiService yang sudah dikonfigurasi
     */
    private fun createApiService(context: Context): ApiService {
        // =====================================================================
        // LOGGING INTERCEPTOR (untuk debugging)
        // =====================================================================
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

            // Di debug mode, log semua request dan response
            // Di production, set ke NONE untuk keamanan
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY // Log body request/response
            } else {
                HttpLoggingInterceptor.Level.NONE // Tidak log apapun
            }
        }

        // =====================================================================
        // OKHTTP CLIENT
        // =====================================================================
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Interceptor untuk autentikasi
            .addInterceptor(loggingInterceptor)         // Interceptor untuk logging
            .connectTimeout(30, TimeUnit.SECONDS)       // Timeout koneksi: 30 detik
            .readTimeout(30, TimeUnit.SECONDS)          // Timeout baca: 30 detik
            .writeTimeout(30, TimeUnit.SECONDS)         // Timeout tulis: 30 detik
            .build()

        // =====================================================================
        // RETROFIT BUILDER
        // =====================================================================
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)                                    // Base URL semua endpoint
            .client(okHttpClient)                                 // OkHttp client
            .addConverterFactory(GsonConverterFactory.create())   // Parser JSON ke Object
            .build()

        // Buat dan kembalikan implementasi ApiService
        return retrofit.create(ApiService::class.java)
    }
}