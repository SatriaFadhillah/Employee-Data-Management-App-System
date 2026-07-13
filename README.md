# Employee-Data-Management

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=material-design&logoColor=white" alt="Material 3">
</p>

A modern Android employee management application built with Kotlin and Jetpack Compose, following MVVM architecture pattern.

## Features

- **Authentication** — Login/Logout with JWT token-based auth and secure storage using EncryptedSharedPreferences
- **Employee CRUD** — Create, Read, Update, and Delete employee data
- **Search & Filter** — Search employees by name and filter by department
- **Detail View** — View complete employee information
- **Material Design 3** — Modern UI with Material 3 components
- **Navigation Compose** — Type-safe navigation between screens

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (ViewModel + Repository)
- **Networking:** Retrofit + OkHttp + Gson
- **Navigation:** Navigation Compose
- **Security:** EncryptedSharedPreferences (AndroidX Security Crypto)
- **Async:** Kotlin Coroutines
- **Backend:** PHP REST API

## Screens

1. **Login Screen** — User authentication
2. **Employee List** — Browse all employees with search & department filter
3. **Detail Screen** — View employee details with edit/delete options
4. **Add/Edit Screen** — Form to create or update employee data

## Requirements

- Android SDK 36 (minSdk 24)
- PHP Backend API with endpoints for authentication and employee management

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Configure the API base URL in `RetrofitClient.kt`
4. Build and run on device/emulator

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `api/login.php` | User login |
| POST | `api/logout.php` | User logout |
| GET | `api/employees.php` | Get employee list |
| POST | `api/employees.php` | Create new employee |
| GET | `api/employee.php` | Get employee detail |
| PUT | `api/employee.php` | Update employee |
| DELETE | `api/employee.php` | Delete employee |
