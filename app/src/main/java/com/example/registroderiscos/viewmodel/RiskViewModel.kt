package com.example.registroderiscos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroderiscos.data.model.Risk
import com.example.registroderiscos.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RiskUiState(
    val registrationMessage: String = "",
    val isRegistrationSuccessful: Boolean = false
)

class RiskViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(RiskUiState())
    val uiState: StateFlow<RiskUiState> = _uiState

    fun registerRisk(description: String) {
        viewModelScope.launch {
            val risk = Risk(description = description) // Crie a classe Risk no seu model
            val result = repository.addRisk(risk)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(registrationMessage = "Registro salvo com sucesso", isRegistrationSuccessful = true)
                } else {
                    it.copy(registrationMessage = result.exceptionOrNull()?.message ?: "Falha ao registrar risco. Tente novamente", isRegistrationSuccessful = false)
                }
            }

            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(registrationMessage = "") }
        }
    }
}