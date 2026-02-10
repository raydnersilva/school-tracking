package com.schooltrack.mobile.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.model.NotificationResponse
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.ui.components.LoadingOverlay
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val apiClient = koinInject<ApiClient>()
    val scope = rememberCoroutineScope()

    var notifications by remember { mutableStateOf<List<NotificationResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            notifications = apiClient.getNotifications()
        } catch (_: Exception) { }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificações", color = OnPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OnPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                LoadingOverlay(true)
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextMuted)
                        Spacer(Modifier.height(8.dp))
                        Text("Nenhuma notificação", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkRead = {
                                scope.launch {
                                    try {
                                        apiClient.markNotificationRead(notification.id)
                                        notifications = notifications.map {
                                            if (it.id == notification.id) it.copy(read = true) else it
                                        }
                                    } catch (_: Exception) { }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationResponse,
    onMarkRead: () -> Unit,
) {
    val borderColor = when (notification.type) {
        "info" -> Info
        "warning" -> Warning
        "alert" -> Error
        else -> TextMuted
    }

    val icon = when (notification.type) {
        "info" -> Icons.Default.Info
        "warning" -> Icons.Default.Warning
        "alert" -> Icons.Default.Error
        else -> Icons.Default.Notifications
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.read) Surface else borderColor.copy(alpha = 0.05f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(borderColor, RoundedCornerShape(2.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Icon(icon, contentDescription = null, tint = borderColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(notification.message, style = MaterialTheme.typography.bodyMedium)
            }
            if (!notification.read) {
                IconButton(onClick = onMarkRead, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Check, contentDescription = "Marcar como lida", tint = Success, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
