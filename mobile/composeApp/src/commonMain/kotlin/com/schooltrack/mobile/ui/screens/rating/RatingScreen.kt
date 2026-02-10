package com.schooltrack.mobile.ui.screens.rating

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(onBack: () -> Unit) {
    val apiClient = koinInject<ApiClient>()
    val scope = rememberCoroutineScope()

    var selectedRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliar Motorista", color = OnPrimary) },
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (submitted) {
                Spacer(Modifier.height(48.dp))
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(64.dp), tint = Secondary)
                Text("Obrigado pela avaliação!", style = MaterialTheme.typography.headlineSmall)
                Text("Sua opinião é importante para nós.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onBack, shape = RoundedCornerShape(12.dp)) {
                    Text("Voltar")
                }
            } else {
                Text("Como foi a viagem?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { selectedRating = i }) {
                            Icon(
                                if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$i estrelas",
                                tint = if (i <= selectedRating) Secondary else TextMuted,
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    }
                }

                Text(
                    when (selectedRating) {
                        1 -> "Ruim"
                        2 -> "Regular"
                        3 -> "Bom"
                        4 -> "Muito Bom"
                        5 -> "Excelente"
                        else -> "Selecione uma nota"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedRating > 0) Primary else TextMuted,
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Comentário (opcional)") },
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                )

                errorMessage?.let {
                    Text(it, color = Error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                apiClient.rateDriver(driverId = 1, rating = selectedRating, comment = comment)
                                submitted = true
                            } catch (e: Exception) {
                                errorMessage = e.message
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = selectedRating > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text("Enviar Avaliação", color = OnPrimary)
                }
            }
        }
    }
}
