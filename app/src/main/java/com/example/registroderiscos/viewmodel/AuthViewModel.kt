// viewmodel/AuthViewModel.kt
package com.example.registroderiscos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroderiscos.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {

    private val _loginResult = MutableStateFlow("")
    val loginResult: StateFlow<String> = _loginResult

    var isAuthenticated by mutableStateOf(false)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signInWithEmailAndPassword(email, password)
            if (result.isSuccess) {
                isAuthenticated = true
                _loginResult.value = "" // Limpa qualquer mensagem de erro anterior
            } else {
                isAuthenticated = false
                _loginResult.value = result.exceptionOrNull()?.message ?: "Erro desconhecido ao fazer login."
            }
        }
    }
}