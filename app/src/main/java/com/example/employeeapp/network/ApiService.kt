package com.example.employeeapp.network

import com.example.employeeapp.model.ApiResponse
import com.example.employeeapp.model.Employee
import com.example.employeeapp.model.LoginData
import com.example.employeeapp.model.LoginRequest
import retrofit2.http.*

/**
 * Interface untuk mendefinisikan semua endpoint API
 * Retrofit akan mengimplementasikan interface ini secara otomatis
 * * Setiap function harus memiliki anotasi HTTP method (GET, POST, PUT, DELETE) dan anotasi URL endpoint
 */
interface ApiService {

    /**
     * Endpoint login
     * @param request Email dan password dalam bentuk LoginRequest
     * @return ApiResponse yang berisi LoginData (token, name, role, expires_at)
     */
    @POST("api/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginData>

    /**
     * Endpoint logout
     * Menghapus token di server
     */
    @POST("api/logout.php")
    suspend fun logout(): ApiResponse<Unit>

    /**
     * Endpoint untuk mendapatkan daftar karyawan
     * @param search Keyword pencarian (opsional)
     * @param department Filter berdasarkan departemen (opsional)
     * @return ApiResponse yang berisi List<Employee>
     */
    @GET("api/employees.php")
    suspend fun getEmployees(
        @Query("search") search: String = "",
        @Query("department") department: String = ""
    ): ApiResponse<List<Employee>>

    /**
     * Endpoint untuk menambah karyawan baru
     * @param employee Data karyawan yang akan ditambahkan
     * @return ApiResponse yang berisi Map dengan key "id" (ID karyawan baru)
     */
    @POST("api/employees.php")
    suspend fun createEmployee(
        @Body employee: Employee
    ): ApiResponse<Map<String, Int>>

    /**
     * Endpoint untuk mendapatkan detail satu karyawan
     * @param id ID karyawan
     * @return ApiResponse yang berisi Employee
     */
    @GET("api/employee.php")
    suspend fun getEmployee(
        @Query("id") id: Int
    ): ApiResponse<Employee>

    /**
     * Endpoint untuk mengupdate data karyawan
     * @param id ID karyawan yang akan diupdate
     * @param employee Data karyawan yang baru
     * @return ApiResponse<Unit> (tanpa data, hanya sukses/gagal)
     */
    @PUT("api/employee.php")
    suspend fun updateEmployee(
        @Query("id") id: Int,
        @Body employee: Employee
    ): ApiResponse<Unit>

    @DELETE("api/employee.php")
    suspend fun deleteEmployee(
        @Query("id") id: Int
    ): ApiResponse<Unit>
}
