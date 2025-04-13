// ui/screens/TesteDropdownScreen.kt
package com.example.registroderiscos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.registroderiscos.data.model.RiskType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TesteDropdownScreen() {
    val riskTypes = RiskType.getAllTypes()
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<RiskType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecione um tipo de risco", style = MaterialTheme.typography.titleMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedType?.displayName ?: "",
                onValueChange = {},
                label = { Text("Tipo de Risco") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor() // ESSENCIAL para funcionar corretamente
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                riskTypes.forEach { riskType ->
                    DropdownMenuItem(
                        text = { Text(riskType.displayName) },
                        onClick = {
                            selectedType = riskType
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
