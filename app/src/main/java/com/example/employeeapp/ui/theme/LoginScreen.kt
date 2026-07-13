package com.example.employeeapp.ui.theme

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employeeapp.ui.theme.EmployeeAppTheme
import com.example.employeeapp.util.Result
import com.example.employeeapp.viewmodel.AuthViewModel
import java.util.regex.Pattern

/**
 * Screen Login.
 * Tempat user memasukkan email dan password untuk autentikasi
 * @param authViewModel ViewModel untuk autentikasi (disediakan oleh sistem)
 * @param onLoginSuccess Callback yang dipanggil setelah login sukses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    // State untuk input form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // State untuk menampilkan error validasi lokal (sebelum ke server)
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Ambil state login dari ViewModel
    val loginState by authViewModel.loginState.collectAsState()

    // Snackbar untuk menampilkan pesan error dari server
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Efek samping: handle perubahan state login
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Result.Success -> {
                // Login sukses panggil callback untuk navigasi
                onLoginSuccess()
            }
            is Result.Error -> {
                // Login gagal tampilkan pesan error di Snackbar
                snackbarHostState.showSnackbar(state.message)
                // Reset state agar tidak muncul lagi
                authViewModel.resetLoginState()
            }
            else -> { /* Loading atau null, tidak perlu aksi */ }
        }
    }

    // Scaffold menyediakan struktur dasar layar (termasuk SnackbarHost)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            // Column untuk layout vertikal
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judul aplikasi
            Text(
                text = "Employee App",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Field input Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    // Validasi real-time: cek format email
                    emailError = if (it.isNotBlank() && !Pattern.matches(CustomPatterns.EMAIL_ADDRESS.pattern(), it)) {
                        "Format email tidak valid"
                    } else {
                        null
                    }
                },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field input Password (dengan toggle show/hide)
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (it.isNotBlank() && it.length < 6) {
                        "Password minimal 6 karakter"
                    } else {
                        null
                    }
                },
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None // Tampilkan teks asli
                } else {
                    PasswordVisualTransformation() // Tampilkan sebagai bullet
                },
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Sembunyikan" else "Tampilkan")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =================================================================
            // LOGIKA VALIDASI & TOMBOL LOGIN
            // =================================================================
            val isLoading = loginState is Result.Loading
            val isFormValid = emailError == null && passwordError == null &&
                    email.isNotBlank() && password.isNotBlank()

            Button(
                onClick = {
                    // Panggil ViewModel untuk login
                    authViewModel.login(email.trim(), password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading && isFormValid // Nonaktifkan jika loading atau form tidak valid
            ) {
                if (isLoading) {
                    // Tampilkan loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Masuk")
                }
            }

            // Informasi demo (untuk membantu mahasiswa saat praktikum)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Info Demo",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Email: admin@company.id\nPassword: password",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEW UNTUK MELIHAT TAMPILAN LOGINSCREEN DI ANDROID STUDIO
// =============================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    EmployeeAppTheme {
        // Preview tidak butuh ViewModel asli, panggil dengan placeholder
        LoginScreen(
            authViewModel = viewModel(),
            onLoginSuccess = {}
        )
    }
}

// =============================================================================
// REGEX PATTERN UNTUK VALIDASI EMAIL (STANDAR ANDROID)
// =============================================================================
private object CustomPatterns {
    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )
}