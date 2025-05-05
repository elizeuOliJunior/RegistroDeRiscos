package com.example.registroderiscos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.update


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

    fun register(email: String, password: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "Preencha o e-mail") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Preencha a senha") }
            return
        }

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.update { it.copy(isLoggedIn = true, generalError = "", emailError = "", passwordError = "") }
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.update { it.copy(generalError = "E-mail já cadastrado") }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _uiState.update { it.copy(generalError = "Senha fraca (mínimo 6 caracteres)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = "Erro ao cadastrar: ${e.message}") }
            }
        }
    }

}
