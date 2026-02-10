package com.schooltrack.mobile.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.model.ForgotPasswordRequest
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.ui.components.*
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ForgotPasswordScreen(onNavigateToLogin: () -> Unit) {
    val apiClient = koinInject<ApiClient>()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Problemas para acessar?",
                style = MaterialTheme.typography.headlineMedium,
                color = Primary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Digite o e-mail cadastrado e enviaremos um link para redefinir sua senha",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = OnPrimary.copy(alpha = 0.9f),
            )

            Spacer(Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Primary,
            )

            Spacer(Modifier.height(24.dp))

            SchoolTrackTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail cadastrado",
                enabled = !isLoading,
            )

            Spacer(Modifier.height(16.dp))

            ErrorMessage(errorMessage)
            SuccessMessage(successMessage)
            if (errorMessage.isNotBlank() || successMessage.isNotBlank()) Spacer(Modifier.height(8.dp))

            SchoolTrackButton(
                text = "Enviar link de recuperação",
                isLoading = isLoading,
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Informe seu e-mail"
                        return@SchoolTrackButton
                    }
                    isLoading = true
                    errorMessage = ""
                    successMessage = ""
                    scope.launch {
                        try {
                            val response = apiClient.forgotPassword(ForgotPasswordRequest(email))
                            successMessage = response.message
                        } catch (e: Exception) {
                            errorMessage = "Erro ao enviar email. Tente novamente."
                        } finally {
                            isLoading = false
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("Lembrou sua senha? ", color = OnPrimary.copy(alpha = 0.8f))
                Text(
                    text = "Voltar para o login",
                    color = Secondary,
                    modifier = Modifier.clickable { onNavigateToLogin() },
                )
            }
        }
    }
}
