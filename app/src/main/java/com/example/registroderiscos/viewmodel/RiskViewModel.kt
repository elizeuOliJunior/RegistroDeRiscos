package com.example.registroderiscos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroderiscos.data.model.Risk
import com.example.registroderiscos.data.model.RiskType
import com.example.registroderiscos.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
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

    var currentAddress by mutableStateOf("")
        private set

    var selectedRiskType by mutableStateOf<RiskType?>(null)
        private set

    fun updateSelectedRiskType(riskType: RiskType) {
        selectedRiskType = riskType
    }

    fun updateAddress(address: String) {
        currentAddress = address
    }

    fun registerRisk(description: String) {
        viewModelScope.launch {
            val risk = Risk(
                description = description,
                address = currentAddress,
                riskType = selectedRiskType?.displayName,
                userId = FirebaseAuth.getInstance().currentUser?.uid
            )
            val result = repository.addRisk(risk)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(registrationMessage = "Registro salvo com sucesso", isRegistrationSuccessful = true)
                } else {
                    it.copy(
                        registrationMessage = result.exceptionOrNull()?.message
                            ?: "Falha ao registrar risco. Tente novamente",
                        isRegistrationSuccessful = false
                    )
                }
            }
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(registrationMessage = "") }
        }
    }
}
