package com.example.registroderiscos.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroderiscos.data.model.RiskType
import com.example.registroderiscos.viewmodel.RiskViewModel
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.material3.ExposedDropdownMenuDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRiskScreen() {
    val viewModel: RiskViewModel = viewModel()
    var description by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.all { it.value }
            if (hasLocationPermission) {
                getLocationWithAddress(context) { address ->
                    viewModel.currentAddress = address
                }
            } else {
                Toast.makeText(context, "Permissão de localização não concedida.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLocationWithAddress(context) { address ->
                viewModel.currentAddress = address
            }
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedRiskType by remember { mutableStateOf<RiskType?>(null) }
    val riskTypes = RiskType.getAllTypes()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text("Registrar Novo Risco", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = selectedRiskType?.displayName ?: "",
            onValueChange = { /* Não permite edição direta */ },
            label = { Text("Qual seria o tipo de risco?") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.fillMaxWidth(),
            onFocus = { expanded = true }
        )
        ExposedDropdownMenuDefaults(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            riskTypes.forEach { riskType ->
                DropdownMenuItem(
                    text = { Text(riskType.displayName) },
                    onClick = {
                        selectedRiskType = riskType
                        expanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Risco") },
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.currentAddress.isNotEmpty()) {
            Text("Localização: ${viewModel.currentAddress}", style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                if (hasLocationPermission) {
                    viewModel.registerRisk(description, viewModel.currentAddress, selectedRiskType?.displayName)
                } else {
                    Toast.makeText(context, "Permissão de localização não concedida.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = selectedRiskType != null && description.isNotBlank()
        ) {
            Text("Registrar Risco")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.registrationMessage.isNotEmpty()) {
            Text(
                uiState.registrationMessage,
                color = if (uiState.isRegistrationSuccessful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@SuppressLint("MissingPermission")
fun getLocationWithAddress(context: Context, onAddressResult: (String) -> Unit) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val fullAddress = with(address) {
                        (0..maxAddressLineIndex).joinToString(separator = ", ") { getAddressLine(it) }
                    }
                    Log.d("LocationDebug", "Address found: $fullAddress")
                    onAddressResult(fullAddress)
                } else {
                    Log.d("LocationDebug", "No address found for the location")
                    onAddressResult("") // Or handle the error as needed
                }
            } catch (e: Exception) {
                Log.e("LocationDebug", "Error getting address: ${e.localizedMessage}")
                onAddressResult("") // Or handle the error as needed
            }
        } else {
            Log.d("LocationDebug", "Last known location was null")
            onAddressResult("") // Or handle the error as needed
        }
    }
}