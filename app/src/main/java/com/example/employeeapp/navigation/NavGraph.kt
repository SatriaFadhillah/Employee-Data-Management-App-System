package com.example.employeeapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.employeeapp.ui.theme.AddEditScreen
import com.example.employeeapp.ui.theme.DetailScreen
import com.example.employeeapp.ui.theme.EmployeeListScreen
import com.example.employeeapp.ui.theme.LoginScreen
import com.example.employeeapp.util.SecureStorage
import com.example.employeeapp.viewmodel.AuthViewModel
import com.example.employeeapp.viewmodel.EmployeeViewModel

/**
 * Object yang berisi route name untuk navigasi
 */
object Routes {
    const val LOGIN = "login"
    const val EMPLOYEE_LIST = "employee_list"
    const val ADD_EMPLOYEE = "add_employee"
    const val DETAIL_EMPLOYEE = "employee_detail/{empId}"
    const val EDIT_EMPLOYEE = "employee_edit/{empId}"

    // Helper function untuk membuat route dengan parameter
    fun detailRoute(id: Int): String = "employee_detail/$id"
    fun editRoute(id: Int): String = "employee_edit/$id"
}

/**
 * Graph navigasi utama aplikasi
 * Menentukan screen mana yang ditampilkan berdasarkan state login
 */
@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // ViewModel untuk auth dan employee
    val authViewModel: AuthViewModel = viewModel()
    val employeeViewModel: EmployeeViewModel = viewModel()

    // Tentukan start destination berdasarkan status login
    val isLoggedIn = SecureStorage.isLoggedIn(context) && !SecureStorage.isTokenExpired(context)
    val startDestination = if (isLoggedIn) Routes.EMPLOYEE_LIST else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // =====================================================================
        // SCREEN LOGIN
        // =====================================================================
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    // Setelah login sukses, navigasi ke list dan hapus history login
                    navController.navigate(Routes.EMPLOYEE_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // =====================================================================
        // SCREEN DAFTAR KARYAWAN
        // =====================================================================
        composable(Routes.EMPLOYEE_LIST) {
            EmployeeListScreen(
                employeeViewModel = employeeViewModel,
                onAddClick = {
                    navController.navigate(Routes.ADD_EMPLOYEE)
                },
                onDetailClick = { empId ->
                    navController.navigate(Routes.detailRoute(empId))
                },
                onLogout = {
                    authViewModel.logout {
                        // Setelah logout, navigasi ke login dan hapus semua history
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true } // 0 membersihkan seluruh backstack graph
                        }
                    }
                }
            )
        }

        // =====================================================================
        // SCREEN TAMBAH KARYAWAN
        // =====================================================================
        composable(Routes.ADD_EMPLOYEE) {
            AddEditScreen(
                employeeViewModel = employeeViewModel,
                employeeId = null,
                onBack = {
                    navController.popBackStack() // Kembali ke list
                }
            )
        }

        // =====================================================================
        // SCREEN DETAIL KARYAWAN (dengan parameter ID)
        // =====================================================================
        composable(
            route = Routes.DETAIL_EMPLOYEE,
            arguments = listOf(navArgument("empId") { type = NavType.IntType })
        ) { backStackEntry ->
            val empId = backStackEntry.arguments?.getInt("empId") ?: 0
            DetailScreen(
                employeeViewModel = employeeViewModel,
                employeeId = empId,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = {
                    navController.navigate(Routes.editRoute(empId))
                },
                onDeleted = {
                    navController.popBackStack() // Kembali ke list setelah hapus
                }
            )
        }

        // =====================================================================
        // SCREEN EDIT KARYAWAN (dengan parameter ID)
        // =====================================================================
        composable(
            route = Routes.EDIT_EMPLOYEE,
            arguments = listOf(navArgument("empId") { type = NavType.IntType })
        ) { backStackEntry ->
            val empId = backStackEntry.arguments?.getInt("empId") ?: 0
            AddEditScreen(
                employeeViewModel = employeeViewModel,
                employeeId = empId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}