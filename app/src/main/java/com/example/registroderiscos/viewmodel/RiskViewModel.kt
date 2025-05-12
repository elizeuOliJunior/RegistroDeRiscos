package com.example.registroderiscos.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroderiscos.data.model.Risk
import com.example.registroderiscos.data.model.RiskType
import com.example.registroderiscos.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun updateSelectedRiskType(riskType: RiskType) {
        selectedRiskType = riskType
    }

    fun updateAddress(address: String) {
        currentAddress = address
    }

    fun updateSelectedImage(uri: Uri?) {
        selectedImageUri = uri
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerRisk(description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(registrationMessage = "Salvando risco...", isRegistrationSuccessful = false) }

            try {
                var imageUrl: String? = null

                selectedImageUri?.let { uri ->
                    imageUrl = repository.uploadImageToStorage(uri)
                }

                val risk = Risk(
                    description = description,
                    address = currentAddress,
                    riskType = selectedRiskType?.displayName,
                    userId = FirebaseAuth.getInstance().currentUser?.uid,
                    imageUrl = imageUrl,
                    date = LocalDate.now().toString(),
                    Status = "Analise"
                )

                val result = repository.addRisk(risk)

                _uiState.update {
                    if (result.isSuccess) {
                        it.copy(
                            registrationMessage = "Registro salvo com sucesso",
                            isRegistrationSuccessful = true
                        )
                    } else {
                        it.copy(
                            registrationMessage = result.exceptionOrNull()?.message
                                ?: "Falha ao registrar risco. Tente novamente",
                            isRegistrationSuccessful = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        registrationMessage = "Erro ao registrar risco: ${e.message}",
                        isRegistrationSuccessful = false
                    )
                }
            }

            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(registrationMessage = "") }
        }
    }
}


