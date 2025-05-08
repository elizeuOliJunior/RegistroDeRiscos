package com.example.registroderiscos.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun UserRisksScreen(navController: NavHostController) {
    var risks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("AuthError", "Erro! Usuário não autorizado!")
            isLoading = false
        } else {
            fetchUserRisks(userId) { fetchedRisks ->
                risks = fetchedRisks
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ViewRequest(risks)
    }
}

fun fetchUserRisks(userId: String, onSuccess: (List<Map<String, Any>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("risks")
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { documents ->
            val risks = documents.map { it.data }
            onSuccess(risks)
        }
        .addOnFailureListener { exception ->
            Log.e("FetchRisks", "Erro ao buscar riscos: ${exception.message}")
            onSuccess(emptyList())
        }
}

@Composable
fun ViewRequest(risks: List<Map<String, Any>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (risks.isEmpty()) {
            item {
                Text("Nenhum risco encontrado.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            items(risks.size) { index ->
                RiskCard(risks[index])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RiskCard(risk: Map<String, Any>) {
    val status = risk["Status"] as? String ?: "Sem status"
    val address = risk["address"] as? String ?: "Sem endereço"
    val date = risk["date"] as? String ?: "Sem data"
    val description = risk["description"] as? String ?: "Sem descrição"
    val imageUrl = risk["imageUrl"] as? String ?: ""
    val riskType = risk["riskType"] as? String ?: "Tipo desconhecido"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagem do risco",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text("Status: $status", style = MaterialTheme.typography.titleMedium)
            Text("Tipo: $riskType", style = MaterialTheme.typography.bodyMedium)
            Text("Data: $date", style = MaterialTheme.typography.bodyMedium)
            Text("Endereço: $address", style = MaterialTheme.typography.bodyMedium)
            Text("Descrição: $description", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
