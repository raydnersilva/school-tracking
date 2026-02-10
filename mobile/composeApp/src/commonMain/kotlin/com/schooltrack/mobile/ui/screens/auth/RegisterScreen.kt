package com.schooltrack.mobile.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.model.RegisterRequest
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.components.*
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val apiClient = koinInject<ApiClient>()
    val tokenManager = koinInject<TokenManager>()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
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
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Primary)) { append("Junte-se ao ") }
                    withStyle(SpanStyle(color = Secondary)) { append("School") }
                    withStyle(SpanStyle(color = Accent)) { append("Track") }
                },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Cadastre-se e tenha controle total sobre o transporte escolar do seu filho",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = OnPrimary.copy(alpha = 0.9f),
            )

            Spacer(Modifier.height(32.dp))

            SchoolTrackTextField(value = name, onValueChange = { name = it }, label = "Nome Completo", enabled = !isLoading)
            Spacer(Modifier.height(12.dp))
            SchoolTrackTextField(value = email, onValueChange = { email = it }, label = "Email", enabled = !isLoading)
            Spacer(Modifier.height(12.dp))
            SchoolTrackTextField(value = password, onValueChange = { password = it }, label = "Senha", isPassword = true, enabled = !isLoading)
            Spacer(Modifier.height(12.dp))
            SchoolTrackTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Repita a Senha", isPassword = true, enabled = !isLoading)

            Spacer(Modifier.height(16.dp))

            ErrorMessage(errorMessage)
            if (errorMessage.isNotBlank()) Spacer(Modifier.height(8.dp))

            SchoolTrackButton(
                text = "Cadastre-se",
                isLoading = isLoading,
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() -> {
                            errorMessage = "Preencha todos os campos"
                            return@SchoolTrackButton
                        }
                        password != confirmPassword -> {
                            errorMessage = "As senhas não coincidem"
                            return@SchoolTrackButton
                        }
                        password.length < 6 -> {
                            errorMessage = "A senha deve ter no mínimo 6 caracteres"
                            return@SchoolTrackButton
                        }
                    }
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            val response = apiClient.register(RegisterRequest(name, email, password))
                            tokenManager.saveAuth(response.token, response.role, response.name)
                            onRegisterSuccess()
                        } catch (e: Exception) {
                            errorMessage = "Erro ao cadastrar. Tente novamente."
                        } finally {
                            isLoading = false
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("Já tem uma conta? ", color = OnPrimary.copy(alpha = 0.8f))
                Text(
                    text = "Faça login",
                    color = Secondary,
                    modifier = Modifier.clickable { onNavigateToLogin() },
                )
            }
        }
    }
}
