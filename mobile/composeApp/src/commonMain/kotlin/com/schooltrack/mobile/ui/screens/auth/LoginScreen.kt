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
import com.schooltrack.mobile.data.model.LoginRequest
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.components.*
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val apiClient = koinInject<ApiClient>()
    val tokenManager = koinInject<TokenManager>()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var captchaInput by remember { mutableStateOf("") }
    var captchaId by remember { mutableStateOf("") }
    var captchaSvg by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun loadCaptcha() {
        scope.launch {
            try {
                val response = apiClient.getCaptcha()
                captchaId = response.captchaId
                captchaSvg = response.svg
            } catch (_: Exception) { }
        }
    }

    LaunchedEffect(Unit) { loadCaptcha() }

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
                    withStyle(SpanStyle(color = Primary)) { append("Acesse o ") }
                    withStyle(SpanStyle(color = Secondary)) { append("School") }
                    withStyle(SpanStyle(color = Accent)) { append("Track") }
                },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Entre em sua conta e acompanhe em tempo real o transporte escolar do seu filho",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = OnPrimary.copy(alpha = 0.9f),
            )

            Spacer(Modifier.height(32.dp))

            SchoolTrackTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuário",
                enabled = !isLoading,
            )

            Spacer(Modifier.height(12.dp))

            SchoolTrackTextField(
                value = password,
                onValueChange = { password = it },
                label = "Senha",
                isPassword = true,
                enabled = !isLoading,
            )

            Spacer(Modifier.height(12.dp))

            SchoolTrackTextField(
                value = captchaInput,
                onValueChange = { captchaInput = it },
                label = "Código de verificação",
                enabled = !isLoading,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Esqueceu sua senha?",
                style = MaterialTheme.typography.bodyMedium,
                color = Secondary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onNavigateToForgotPassword() },
            )

            Spacer(Modifier.height(16.dp))

            ErrorMessage(errorMessage)
            if (errorMessage.isNotBlank()) Spacer(Modifier.height(8.dp))

            SchoolTrackButton(
                text = "Entrar",
                isLoading = isLoading,
                onClick = {
                    if (username.isBlank() || password.isBlank() || captchaInput.isBlank()) {
                        errorMessage = "Preencha todos os campos"
                        return@SchoolTrackButton
                    }
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            val response = apiClient.login(
                                LoginRequest(username, password, captchaId, captchaInput)
                            )
                            tokenManager.saveAuth(response.token, response.role, response.name)
                            onLoginSuccess()
                        } catch (e: Exception) {
                            errorMessage = "Erro ao fazer login. Verifique suas credenciais."
                            loadCaptcha()
                            captchaInput = ""
                        } finally {
                            isLoading = false
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("Não tem uma conta? ", color = OnPrimary.copy(alpha = 0.8f))
                Text(
                    text = "Cadastre-se",
                    color = Secondary,
                    modifier = Modifier.clickable { onNavigateToRegister() },
                )
            }
        }
    }
}
