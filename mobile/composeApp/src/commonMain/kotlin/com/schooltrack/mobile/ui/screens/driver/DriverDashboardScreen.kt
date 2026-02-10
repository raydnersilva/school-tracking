package com.schooltrack.mobile.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class TripResponse(
    val id: Long = 0,
    val busRouteId: Long = 0,
    val busRouteName: String = "",
    val driverId: Long = 0,
    val driverName: String = "",
    val startedAt: String? = null,
    val endedAt: String? = null,
    val status: String = "",
    val studentsBoarded: Int = 0,
    val studentsDropped: Int = 0,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(onBack: () -> Unit) {
    val apiClient = koinInject<ApiClient>()
    val scope = rememberCoroutineScope()

    var currentTrip by remember { mutableStateOf<TripResponse?>(null) }
    var tripHistory by remember { mutableStateOf<List<TripResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            currentTrip = apiClient.getCurrentTrip()
        } catch (_: Exception) { currentTrip = null }
        try {
            tripHistory = apiClient.getDriverTrips()
        } catch (_: Exception) { }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel do Motorista", color = OnPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OnPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Error
            errorMessage?.let { msg ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(msg, modifier = Modifier.padding(16.dp), color = Error)
                    }
                }
            }

            // Current Trip or Start
            item {
                if (currentTrip != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.05f)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Success, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Viagem em Andamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Success)
                                    Text("Rota: ${currentTrip!!.busRouteName}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${currentTrip!!.studentsBoarded}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Success)
                                    Text("Embarcados", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            apiClient.endTrip(currentTrip!!.id)
                                            currentTrip = null
                                            tripHistory = apiClient.getDriverTrips()
                                        } catch (e: Exception) {
                                            errorMessage = e.message
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Error),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Encerrar Viagem", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Iniciar Viagem", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            currentTrip = apiClient.startTrip(1)
                                        } catch (e: Exception) {
                                            errorMessage = e.message
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Success),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Iniciar Viagem", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Trip History
            item {
                Text("Histórico Recente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (tripHistory.isEmpty()) {
                item {
                    Text("Nenhuma viagem registrada.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
            }

            items(tripHistory.take(10), key = { it.id }) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(
                                if (trip.status == "COMPLETED") Success.copy(alpha = 0.1f) else Warning.copy(alpha = 0.1f)
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (trip.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (trip.status == "COMPLETED") Success else Warning,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(trip.busRouteName, style = MaterialTheme.typography.titleSmall)
                            Text(trip.startedAt ?: "", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                if (trip.status == "COMPLETED") "Concluída" else "Em andamento",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (trip.status == "COMPLETED") Success else Warning,
                            )
                            Text("${trip.studentsBoarded} alunos", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
