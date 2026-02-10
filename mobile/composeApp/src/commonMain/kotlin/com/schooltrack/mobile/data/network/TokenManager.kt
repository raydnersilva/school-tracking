package com.schooltrack.mobile.data.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    val isAuthenticated: Boolean get() = _token.value != null

    fun saveAuth(token: String, role: String, name: String) {
        _token.value = token
        _role.value = role
        _userName.value = name
    }

    fun clearAuth() {
        _token.value = null
        _role.value = null
        _userName.value = null
    }

    fun getToken(): String? = _token.value
}
