package com.example.employeeapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employeeapp.model.Employee
import com.example.employeeapp.ui.theme.EmployeeAppTheme
import com.example.employeeapp.util.Result
import com.example.employeeapp.viewmodel.EmployeeViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Screen untuk menampilkan detail satu karyawan
 * Juga menyediakan tombol Edit dan Delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    employeeId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val detailState by employeeViewModel.detailState.collectAsState()
    val operationState by employeeViewModel.operationState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Ambil data detail saat pertama kali masuk
    LaunchedEffect(employeeId) {
        employeeViewModel.getEmployeeById(employeeId)
    }

    // Handle hasil operasi delete
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is Result.Success -> {
                snackbarHostState.showSnackbar("Karyawan berhasil dihapus")
                employeeViewModel.resetOperationState()
                onDeleted()
            }

            is Result.Error -> {
                snackbarHostState.showSnackbar(state.message)
                employeeViewModel.resetOperationState()
            }

            else -> {
                // Loading atau null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detail Karyawan")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            employeeViewModel.resetDetailState()
                            onBack()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }

                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            when (val state = detailState) {
                is Result.Loading -> {
                    CircularProgressIndicator()
                }

                is Result.Error -> {
                    ErrorView(state.message) {
                        employeeViewModel.getEmployeeById(employeeId)
                    }
                }

                is Result.Success -> {
                    EmployeeDetailContent(
                        employee = state.data
                    )
                }

                null -> {
                    // Belum ada state
                }
            }
        }
    }

    // Dialog konfirmasi hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Hapus Karyawan?")
            },
            text = {
                Text("Data karyawan ini akan dihapus secara permanen.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        employeeViewModel.deleteEmployee(employeeId)
                    }
                ) {
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * Konten detail karyawan
 */
@Composable
fun EmployeeDetailContent(employee: Employee) {
    val rupiahFormat =
        NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        DetailRow(
            label = "Nama",
            value = employee.name
        )

        DetailRow(
            label = "Email",
            value = employee.email
        )

        DetailRow(
            label = "Jabatan",
            value = employee.position
        )

        DetailRow(
            label = "Departemen",
            value = employee.department.ifEmpty { "-" }
        )

        DetailRow(
            label = "Gaji",
            value = rupiahFormat.format(employee.salary)
        )

        DetailRow(
            label = "Tanggal Bergabung",
            value = employee.joinDate.ifEmpty { "-" }
        )

        DetailRow(
            label = "Status",
            value = if (employee.isActive == 1)
                "Aktif"
            else
                "Tidak Aktif"
        )

        DetailRow(
            label = "No. HP",
            value = employee.phone.ifEmpty { "-" }
        )
    }
}

/**
 * Satu baris detail (label dan value) dalam bentuk Card
 */
@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * PREVIEW
 */
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DetailScreenPreview() {

    val sampleEmployee = Employee(
        id = 1,
        name = "Budi Santoso",
        email = "budi@co.id",
        phone = "081234567890",
        position = "Backend Dev",
        department = "Engineering",
        salary = 8500000.0,
        joinDate = "2022-01-15",
        isActive = 1
    )

    EmployeeAppTheme {
        EmployeeDetailContent(
            employee = sampleEmployee
        )
    }
}