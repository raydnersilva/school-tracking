package com.schooltrack.mobile.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class ChatMessageDto(
    val id: Long = 0,
    val roomId: String = "",
    val senderId: Long = 0,
    val senderName: String = "",
    val content: String = "",
    val sentAt: String? = null,
    val readByRecipient: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onBack: () -> Unit) {
    val apiClient = koinInject<ApiClient>()
    val tokenManager = koinInject<TokenManager>()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember { mutableStateOf<List<ChatMessageDto>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    val userName by tokenManager.userName.collectAsState()

    LaunchedEffect(Unit) {
        try {
            messages = apiClient.getChatMessages("general")
        } catch (_: Exception) { }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat", color = OnPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OnPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Digite sua mensagem...") },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                val msg = newMessage
                                newMessage = ""
                                scope.launch {
                                    try {
                                        apiClient.sendChatMessage("general", msg)
                                        messages = apiClient.getChatMessages("general")
                                    } catch (_: Exception) { }
                                }
                            }
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Primary)
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 8.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                val isOwn = msg.senderName == userName
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 280.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (isOwn) 16.dp else 4.dp,
                            bottomEnd = if (isOwn) 4.dp else 16.dp,
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isOwn) Primary else Surface,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isOwn) {
                                Text(msg.senderName, style = MaterialTheme.typography.labelSmall, color = Primary)
                                Spacer(Modifier.height(2.dp))
                            }
                            Text(
                                msg.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isOwn) OnPrimary else TextPrimary,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                msg.sentAt?.takeLast(8)?.take(5) ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOwn) OnPrimary.copy(alpha = 0.7f) else TextMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}
