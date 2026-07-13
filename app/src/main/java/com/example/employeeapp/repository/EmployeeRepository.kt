package com.example.employeeapp.repository


import android.content.Context
import com.example.employeeapp.model.ApiResponse
import com.example.employeeapp.model.Employee
import com.example.employeeapp.network.RetrofitClient
import com.example.employeeapp.util.Result
import com.example.employeeapp.util.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository untuk operasi CRUD karyawan
 * Semua fungsi memiliki error handling yang konsisten
 */
class EmployeeRepository(private val context: Context) {

    /**
     * Generic function untuk menangani error pada semua operasi API
     * Menggunakan higher-order function untuk menghindari duplikasi kode try-catch
     * @param block Fungsi API yang akan dijalankan (suspend)
     * @return Result<T> Hasil operasi (Success atau Error)
     */
    private suspend fun <T> safeApiCall(block: suspend () -> ApiResponse<T>): Result<T> {
        return try {
            // Cek apakah token masih valid sebelum melakukan request
            if (SecureStorage.isTokenExpired(context)) {
                return Result.Error("Sesi telah berakhir, silakan login ulang", 401)
            }

            // Eksekusi block API
            val response = block()

            // Cek response dari server
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message.ifEmpty { "Terjadi kesalahan" })
            }
        } catch (e: IOException) {
            // Error koneksi
            Result.Error("Tidak ada koneksi internet. Periksa koneksi Anda.")
        } catch (e: HttpException) {
            // Error HTTP
            val errorMessage = when (e.code()) {
                401 -> "Sesi habis, silakan login ulang"
                404 -> "Data tidak ditemukan"
                409 -> "Email sudah terdaftar"
                else -> "HTTP Error: ${e.code()}"
            }
            Result.Error(errorMessage, e.code())
        } catch (e: Exception) {
            // Error tak terduga
            Result.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    /**
     * Mendapatkan semua karyawan (dengan fitur search dan filter department)
     * @param search Keyword pencarian (opsional)
     * @param department Filter departemen (opsional)
     * @return Result<List<Employee>>
     */
    suspend fun getAllEmployees(search: String = "", department: String = ""): Result<List<Employee>> {
        return safeApiCall {
            val apiService = RetrofitClient.getInstance(context)
            apiService.getEmployees(search, department)
        }
    }

    /**
     * Mendapatkan detail satu karyawan berdasarkan ID
     * @param id ID karyawan
     * @return Result<Employee>
     */
    suspend fun getEmployeeById(id: Int): Result<Employee> {
        return safeApiCall {
            val apiService = RetrofitClient.getInstance(context)
            apiService.getEmployee(id)
        }
    }

    /**
     * Menambah karyawan baru
     * @param employee Data karyawan yang akan ditambahkan
     * @return Result<Map<String, Int>> (mengembalikan ID karyawan baru dalam Map)
     */
    suspend fun createEmployee(employee: Employee): Result<Map<String, Int>> {
        return safeApiCall {
            val apiService = RetrofitClient.getInstance(context)
            apiService.createEmployee(employee)
        }
    }

    /**
     * Mengupdate data karyawan
     * @param id ID karyawan yang akan diupdate
     * @param employee Data karyawan yang baru
     * @return Result<Unit>
     */
    suspend fun updateEmployee(id: Int, employee: Employee): Result<Unit> {
        return safeApiCall {
            val apiService = RetrofitClient.getInstance(context)
            apiService.updateEmployee(id, employee)
        }
    }

    /**
     * Menghapus karyawan (soft delete)
     * @param id ID karyawan yang akan dihapus
     * @return Result<Unit>
     */
    suspend fun deleteEmployee(id: Int): Result<Unit> {
        return safeApiCall {
            val apiService = RetrofitClient.getInstance(context)
            apiService.deleteEmployee(id)
        }
    }
}