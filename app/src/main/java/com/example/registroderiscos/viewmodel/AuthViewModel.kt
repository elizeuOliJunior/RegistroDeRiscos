package com.example.registroderiscos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val emailError: String = "",
    val passwordError: String = "",
    val generalError: String = "",
    val isLoggedIn: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState()

        when {
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(emailError = "E-mail obrigatório")
                return
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(passwordError = "Senha obrigatória")
                return
            }
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                }
                .addOnFailureListener { exception ->
                    val message = when {
                        "no user record" in exception.message.orEmpty().lowercase() -> "E-mail não cadastrado."
                        "password is invalid" in exception.message.orEmpty().lowercase() -> "Senha inválida."
                        else -> "Erro ao fazer login: ${exception.localizedMessage}"
                    }
                    _uiState.value = _uiState.value.copy(generalError = message)
                }
        }
    }
}
