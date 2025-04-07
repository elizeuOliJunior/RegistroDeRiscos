// ui/screens/RegisterRiskScreen.kt
package com.example.registroderiscos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroderiscos.viewmodel.RiskViewModel

@Composable
fun RegisterRiskScreen() {
    val viewModel: RiskViewModel = viewModel()
    var description by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registrar Novo Risco", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Risco") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.registerRisk(description)
        }) {
            Text("Registrar Risco")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.registrationMessage.isNotEmpty()) {
            Text(uiState.registrationMessage, color = if (uiState.isRegistrationSuccessful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
    }
}