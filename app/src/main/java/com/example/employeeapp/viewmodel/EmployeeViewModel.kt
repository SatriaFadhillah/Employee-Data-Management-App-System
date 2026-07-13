package com.example.employeeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeeapp.model.Employee
import com.example.employeeapp.repository.EmployeeRepository
import com.example.employeeapp.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state operasi CRUD karyawan.
 */
class EmployeeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = EmployeeRepository(getApplication())

    // State untuk daftar karyawan (default Loading)
    private val _employeesState =
        MutableStateFlow<Result<List<Employee>>>(Result.Loading)

    val employeesState: StateFlow<Result<List<Employee>>> =
        _employeesState.asStateFlow()

    // State untuk detail karyawan (null = belum ada operasi detail)
    private val _detailState =
        MutableStateFlow<Result<Employee>?>(null)

    val detailState: StateFlow<Result<Employee>?> =
        _detailState.asStateFlow()

    // State untuk hasil operasi (Create, Update, Delete)
    private val _operationState =
        MutableStateFlow<Result<Unit>?>(null)

    val operationState: StateFlow<Result<Unit>?> =
        _operationState.asStateFlow()

    // Job untuk debounce search
    // (mencegah request terlalu sering)
    private var searchJob: Job? = null

    // State untuk menyimpan nama karyawan terakhir
    // yang dioperasikan (untuk Snackbar)
    private val _lastActionName =
        MutableStateFlow("")

    val lastActionName: StateFlow<String> =
        _lastActionName.asStateFlow()

    /**
     * Inisialisasi:
     * Ambil semua karyawan saat ViewModel dibuat.
     */
    init {
        getAllEmployees()
    }

    /**
     * Mendapatkan semua karyawan.
     *
     * @param search Keyword pencarian
     * @param department Filter departemen
     */
    fun getAllEmployees(
        search: String = "",
        department: String = ""
    ) {

        viewModelScope.launch {

            _employeesState.value = Result.Loading

            val result = repository.getAllEmployees(
                search,
                department
            )

            _employeesState.value = result
        }
    }

    /**
     * Pencarian dengan debounce.
     *
     * Menunggu user berhenti mengetik selama 500 ms
     * agar tidak melakukan request API setiap karakter.
     *
     * @param query Keyword pencarian
     */
    fun searchWithDebounce(query: String) {

        // Batalkan pencarian sebelumnya
        searchJob?.cancel()

        // Buat pencarian baru
        searchJob = viewModelScope.launch {

            delay(500L)

            getAllEmployees(query)
        }
    }

    /**
     * Mendapatkan detail karyawan berdasarkan ID.
     *
     * @param id ID karyawan
     */
    fun getEmployeeById(id: Int) {

        viewModelScope.launch {

            _detailState.value = Result.Loading

            val result = repository.getEmployeeById(id)

            _detailState.value = result
        }
    }

    /**
     * Menambah karyawan baru.
     *
     * @param employee Data karyawan baru
     * @param onSuccess Callback jika berhasil
     */
    fun createEmployee(
        employee: Employee,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _lastActionName.value = employee.name

            _operationState.value = Result.Loading

            val result =
                repository.createEmployee(employee)

            // Transform Result<Map<String, Int>>
            // menjadi Result<Unit>
            val transformedResult: Result<Unit> =
                when (result) {

                    is Result.Success ->
                        Result.Success(Unit)

                    is Result.Error ->
                        Result.Error(
                            result.message,
                            result.code
                        )

                    is Result.Loading ->
                        Result.Loading
                }

            if (transformedResult is Result.Success) {

                getAllEmployees()

                onSuccess()
            }

            _operationState.value = transformedResult
        }
    }

    /**
     * Mengupdate data karyawan.
     *
     * @param id ID karyawan
     * @param employee Data baru
     * @param onSuccess Callback jika berhasil
     */
    fun updateEmployee(
        id: Int,
        employee: Employee,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _lastActionName.value = employee.name

            _operationState.value = Result.Loading

            val result =
                repository.updateEmployee(
                    id,
                    employee
                )

            if (result is Result.Success) {

                getAllEmployees()

                onSuccess()
            }

            _operationState.value = result
        }
    }

    /**
     * Menghapus karyawan.
     *
     * @param id ID karyawan
     */
    fun deleteEmployee(id: Int) {

        viewModelScope.launch {

            _operationState.value = Result.Loading

            val result =
                repository.deleteEmployee(id)

            if (result is Result.Success) {

                getAllEmployees()
            }

            _operationState.value = result
        }
    }

    /**
     * Reset state operasi.
     *
     * Dipanggil setelah Snackbar ditampilkan.
     */
    fun resetOperationState() {
        _operationState.value = null
    }

    /**
     * Reset state detail.
     *
     * Dipanggil saat keluar dari screen detail.
     */
    fun resetDetailState() {
        _detailState.value = null
    }
}