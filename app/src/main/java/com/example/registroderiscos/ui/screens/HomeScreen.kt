// ui/screens/HomeScreen.kt
package com.example.registroderiscos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BEM-VINDO\nao nosso registrador\nde riscos",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "NO QUE PODEMOS AJUDAR?",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("RegisterRiskScreen") },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Registrar novo risco", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {navController.navigate("UserRisksScreen") },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Visualizar registros", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

//        Button(
//            onClick = { /* Lógica para sair do aplicativo ou deslogar */ },
//            modifier = Modifier.fillMaxWidth(0.8f),
//            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text("Sair", color = Color.White)
//        }
    }
}