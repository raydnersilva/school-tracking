package com.schooltrack.mobile.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.theme.*
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val tokenManager = koinInject<TokenManager>()
    val userName by tokenManager.userName.collectAsState()
    val userRole by tokenManager.role.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = OnPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OnPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(56.dp), tint = Primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(userName ?: "Usuário", style = MaterialTheme.typography.titleMedium)
                        Text(
                            when (userRole) {
                                "parent" -> "Responsável"
                                "admin" -> "Administrador"
                                "driver" -> "Motorista"
                                else -> userRole ?: ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Primary,
                        )
                    }
                }
            }

            // Preferências
            Text("Preferências", style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column {
                    SettingsToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificações Push",
                        subtitle = "Receber alertas do ônibus",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Border.copy(alpha = 0.3f))
                    SettingsToggleItem(
                        icon = Icons.Default.DarkMode,
                        title = "Modo Escuro",
                        subtitle = "Tema escuro do aplicativo",
                        checked = darkMode,
                        onCheckedChange = { darkMode = it },
                    )
                }
            }

            // Sobre
            Text("Sobre", style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column {
                    SettingsItem(icon = Icons.Default.Info, title = "Versão", subtitle = "1.0.0")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Border.copy(alpha = 0.3f))
                    SettingsItem(icon = Icons.Default.Policy, title = "Política de Privacidade", subtitle = "Termos e condições")
                }
            }

            // Logout
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onLogout() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.05f)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Error)
                    Spacer(Modifier.width(12.dp))
                    Text("Sair da Conta", style = MaterialTheme.typography.titleMedium, color = Error)
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Primary),
        )
    }
}
