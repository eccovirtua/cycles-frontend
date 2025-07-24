package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cycles.viewmodel.ResetPasswordViewModel
import androidx.compose.runtime.getValue

@Composable
fun ResetPasswordScreen(navController: NavController,
                        email: String,
                        code: String,
                        viewModel: ResetPasswordViewModel = hiltViewModel()) {

    val pwd1 by viewModel.pwd1.collectAsState()
    val pwd2 by viewModel.pwd2.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    Surface(//para dejar el fondo del mismo color..
        modifier = Modifier.fillMaxSize()
    ) {
        //aqui arranca la ui COMO TAL
        Column(Modifier.fillMaxSize()
            .padding(32.dp),
            verticalArrangement = Arrangement.Center) {
            Text("Nueva contraseña")
            OutlinedTextField(
                value = pwd1,
                onValueChange = viewModel::onPwd1,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pwd2,
                onValueChange = viewModel::onPwd2,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMessage != null){
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.reset(navController) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Actualizar contraseña")
            }
        }
    }
}


