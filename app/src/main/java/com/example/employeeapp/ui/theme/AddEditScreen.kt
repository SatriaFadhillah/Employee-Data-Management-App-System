package com.example.employeeapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employeeapp.model.Employee
import com.example.employeeapp.ui.theme.EmployeeAppTheme
import com.example.employeeapp.util.Result
import com.example.employeeapp.viewmodel.EmployeeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    employeeId: Int? = null,
    onBack: () -> Unit
) {
    val isEditMode = employeeId != null

    val detailState by employeeViewModel.detailState.collectAsState()
    val operationState by employeeViewModel.operationState.collectAsState()
    val lastActionName by employeeViewModel.lastActionName.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var joinDate by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var positionError by remember { mutableStateOf<String?>(null) }
    var salaryError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    var isFormInitialized by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(employeeId) {
        if (isEditMode && employeeId != null) {
            employeeViewModel.getEmployeeById(employeeId)
        }
    }

    LaunchedEffect(detailState) {
        if (detailState is Result.Success && !isFormInitialized) {
            val emp = (detailState as Result.Success<Employee>).data

            name = emp.name
            email = emp.email
            phone = emp.phone
            position = emp.position
            department = emp.department
            salary = emp.salary.toLong().toString()
            joinDate = emp.joinDate

            isFormInitialized = true
        }
    }

    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is Result.Success -> {
                val message = if (isEditMode) {
                    "Data $lastActionName berhasil diperbarui"
                } else {
                    "$lastActionName berhasil ditambahkan"
                }

                snackbarHostState.showSnackbar(message)
                employeeViewModel.resetOperationState()
                onBack()
            }

            is Result.Error -> {
                snackbarHostState.showSnackbar(state.message)
                employeeViewModel.resetOperationState()
            }

            else -> {}
        }
    }

    fun validateAll(): Boolean {
        nameError = validateName(name)
        emailError = validateEmail(email)
        phoneError = validatePhone(phone)
        positionError = validatePosition(position)
        salaryError = validateSalary(salary)
        dateError = validateJoinDate(joinDate)

        return listOf(
            nameError,
            emailError,
            phoneError,
            positionError,
            salaryError,
            dateError
        ).all { it == null }
    }

    val isLoading = operationState is Result.Loading

    val hasErrors = listOf(
        nameError,
        emailError,
        positionError
    ).any { it != null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode)
                            "Edit Karyawan"
                        else
                            "Tambah Karyawan"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                ValidatedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = validateName(it)
                    },
                    label = "Nama Lengkap *",
                    error = nameError
                )

                ValidatedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = validateEmail(it)
                    },
                    label = "Email *",
                    error = emailError,
                    keyboardType = KeyboardType.Email
                )

                ValidatedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = validatePhone(it)
                    },
                    label = "Nomor HP",
                    error = phoneError,
                    keyboardType = KeyboardType.Phone
                )

                ValidatedTextField(
                    value = position,
                    onValueChange = {
                        position = it
                        positionError = validatePosition(it)
                    },
                    label = "Jabatan *",
                    error = positionError
                )

                ValidatedTextField(
                    value = department,
                    onValueChange = {
                        department = it
                    },
                    label = "Departemen",
                    error = null
                )

                ValidatedTextField(
                    value = salary,
                    onValueChange = {
                        salary = it
                        salaryError = validateSalary(it)
                    },
                    label = "Gaji (Rp)",
                    error = salaryError,
                    keyboardType = KeyboardType.Number
                )

                ValidatedTextField(
                    value = joinDate,
                    onValueChange = {
                        joinDate = it
                        dateError = validateJoinDate(it)
                    },
                    label = "Tanggal Bergabung (YYYY-MM-DD)",
                    error = dateError
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (validateAll()) {

                            val employee = Employee(
                                name = name.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                position = position.trim(),
                                department = department.trim(),
                                salary = salary.toDoubleOrNull() ?: 0.0,
                                joinDate = joinDate.trim().ifEmpty {
                                    LocalDate.now().toString()
                                }
                            )

                            if (isEditMode && employeeId != null) {
                                employeeViewModel.updateEmployee(
                                    employeeId,
                                    employee
                                ) {
                                    onBack()
                                }
                            } else {
                                employeeViewModel.createEmployee(
                                    employee
                                ) {
                                    onBack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading && !hasErrors
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            if (isEditMode)
                                "Simpan Perubahan"
                            else
                                "Tambah Karyawan"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "Menyimpan data...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

fun validateName(value: String): String? {
    if (value.isBlank()) return "Nama tidak boleh kosong"
    if (value.trim().length < 3) return "Nama minimal 3 karakter"

    if (!value.matches(Regex("^[a-zA-Z\\s.'-]+$"))) {
        return "Nama hanya boleh huruf, spasi, titik, apostrof"
    }

    return null
}

fun validateEmail(value: String): String? {
    if (value.isBlank()) return "Email tidak boleh kosong"

    val emailRegex =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    if (!emailRegex.matches(value)) {
        return "Format email tidak valid (contoh: nama@domain.com)"
    }

    return null
}

fun validatePhone(value: String): String? {
    if (value.isBlank()) return null

    val digits =
        value.replace(Regex("[+\\s-]"), "")

    if (!digits.matches(Regex("^(0|62)[0-9]+$"))) {
        return "Harus dimulai dengan 08 atau +62"
    }

    if (digits.length < 10 || digits.length > 14) {
        return "Nomor HP harus 10-14 digit"
    }

    return null
}

fun validatePosition(value: String): String? {
    if (value.isBlank()) return "Jabatan tidak boleh kosong"
    if (value.trim().length < 2) return "Jabatan minimal 2 karakter"

    return null
}

fun validateSalary(value: String): String? {
    if (value.isBlank()) return null

    val number =
        value.toLongOrNull()
            ?: return "Gaji harus berupa angka"

    if (number < 0) {
        return "Gaji tidak boleh negatif"
    }

    if (number > 999_999_999) {
        return "Melebihi batas maksimum (Rp 999.999.999)"
    }

    return null
}

fun validateJoinDate(value: String): String? {
    if (value.isBlank()) return null

    return try {
        val date = LocalDate.parse(
            value,
            DateTimeFormatter.ISO_LOCAL_DATE
        )

        if (date.isAfter(LocalDate.now())) {
            "Tanggal bergabung tidak boleh masa depan"
        } else {
            null
        }
    } catch (e: DateTimeParseException) {
        "Format harus YYYY-MM-DD (contoh: 2024-01-15)"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddEditScreenPreview() {
    EmployeeAppTheme {
        AddEditScreen(
            onBack = {}
        )
    }
}