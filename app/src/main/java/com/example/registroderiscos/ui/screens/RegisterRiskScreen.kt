package com.example.registroderiscos.ui.screens

import coil.compose.rememberAsyncImagePainter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroderiscos.viewmodel.RiskViewModel
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.registroderiscos.data.model.RiskType
import java.io.File
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.core.content.FileProvider
import java.util.UUID

import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRiskScreen() {
    val viewModel: RiskViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var description by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


    var expanded by remember { mutableStateOf(false) }
    val selectedRiskType = viewModel.selectedRiskType
    val riskTypes = RiskType.getAllTypes()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val photoUri = remember { createImageUri(context) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri = photoUri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedImageUri = it }
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.all { it.value }
            if (hasLocationPermission) {
                getLocationWithAddress(context) { address ->
                    viewModel.updateAddress(address)
                }
            } else {
                Toast.makeText(context, "Permissão de localização não concedida.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLocationWithAddress(context) { address ->
                viewModel.updateAddress(address)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text("Registrar Novo Risco", style = MaterialTheme.typography.headlineMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedRiskType?.displayName ?: "",
                onValueChange = {},
                label = { Text("Tipo de Risco") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                riskTypes.forEach { riskType ->
                    DropdownMenuItem(
                        text = { Text(riskType.displayName) },
                        onClick = {
                            viewModel.updateSelectedRiskType(riskType)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Risco") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = "Selecionar da Galeria")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galeria")
            }
        }


        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Imagem Selecionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Button(
            onClick = {
                if (hasLocationPermission) {
                    viewModel.updateSelectedImage(selectedImageUri)
                    viewModel.registerRisk(description)
                } else {
                    Toast.makeText(context, "Permissão de localização não concedida.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = selectedRiskType != null && description.isNotBlank()
        ) {
            Text("Registrar Risco")
        }

        if (uiState.registrationMessage.isNotEmpty()) {
            Text(
                uiState.registrationMessage,
                color = if (uiState.isRegistrationSuccessful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "captured_image_${UUID.randomUUID()}.png")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@SuppressLint("MissingPermission")
fun getLocationWithAddress(context: Context, onAddressResult: (String) -> Unit) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val fullAddress = (0..address.maxAddressLineIndex).joinToString(", ") { index ->
                        address.getAddressLine(index)
                    }
                    onAddressResult(fullAddress)
                } else {
                    onAddressResult("")
                }
            } catch (e: Exception) {
                onAddressResult("")
            }
        } else {
            onAddressResult("")
        }
    }.addOnFailureListener {
        onAddressResult("")
    }
}
