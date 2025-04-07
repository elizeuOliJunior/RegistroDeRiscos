// ui/screens/LoginScreen.kt
package com.example.registroderiscos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.registroderiscos.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavHostController = rememberNavController()) {
    val viewModel: AuthViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginResult by viewModel.loginResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Autenticação de Usuário", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.login(email, password)
        }) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loginResult.isNotEmpty()) {
            Text(loginResult, color = MaterialTheme.colorScheme.error)
        }

        // Observe o estado de autenticação e navegue para a HomeScreen
        LaunchedEffect(viewModel.isAuthenticated) {
            if (viewModel.isAuthenticated) {
                navController.navigate("HomeScreen") {
                    popUpTo("LoginScreen") { inclusive = true } // Evita voltar para a tela de login
                }
            }
        }
    }
}