package com.example.employeeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeeapp.repository.AuthRepository
import com.example.employeeapp.util.Result
import com.example.employeeapp.util.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state autentikasi
 * Menggunakan AndroidViewModel karena membutuhkan Context
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Repository untuk operasi autentikasi
    private val authRepository = AuthRepository(getApplication())

    // StateFlow untuk menyimpan hasil operasi login (null = belum ada operasi)
    // _loginState bisa diubah secara internal, loginState hanya bisa dibaca (read-only) oleh UI
    private val _loginState = MutableStateFlow<Result<String>?>(null)
    val loginState: StateFlow<Result<String>?> = _loginState.asStateFlow()

    /**
     * Mengecek apakah user sudah login
     * @return true jika token ada dan belum expired
     */
    fun isLoggedIn(): Boolean {
        // Menggunakan getApplication() untuk mendapatkan context aplikasi yang valid
        return SecureStorage.isLoggedIn(getApplication())
    }

    /**
     * Melakukan proses login
     * @param email Email user
     * @param password Password user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Set state ke Loading sebelum memulai proses login
            _loginState.value = Result.Loading

            // Panggil repository dan simpan hasilnya ke state
            val result = authRepository.login(email, password)
            _loginState.value = result
        }
    }

    /**
     * Melakukan proses logout
     * @param onComplete Callback yang dijalankan setelah logout selesai
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = null // Reset state login
            onComplete()             // Panggil callback untuk navigasi
        }
    }

    /**
     * Reset state login (misal setelah menampilkan error)
     */
    fun resetLoginState() {
        _loginState.value = null
    }
}