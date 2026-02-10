package com.schooltrack.mobile.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schooltrack.mobile.data.model.BusResponse
import com.schooltrack.mobile.data.model.StudentResponse
import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.components.LoadingOverlay
import com.schooltrack.mobile.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: () -> Unit = {},
    onNavigateToDriver: () -> Unit = {},
    onNavigateToRating: () -> Unit = {},
    onLogout: () -> Unit,
) {
    val apiClient = koinInject<ApiClient>()
    val tokenManager = koinInject<TokenManager>()
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf<List<StudentResponse>>(emptyList()) }
    var buses by remember { mutableStateOf<List<BusResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val userName by tokenManager.userName.collectAsState()

    LaunchedEffect(Unit) {
        try {
            students = apiClient.getStudents()
            buses = apiClient.getActiveBuses()
        } catch (_: Exception) { }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SchoolTrack", style = MaterialTheme.typography.titleLarge, color = OnPrimary)
                        Text("Olá, ${userName ?: "Usuário"}", style = MaterialTheme.typography.bodyMedium, color = OnPrimary.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = OnPrimary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = OnPrimary)
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Início") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, indicatorColor = Primary.copy(alpha = 0.1f)),
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSchedule,
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Horários") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToNotifications,
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Alertas") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChat,
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("Chat") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Config") },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                LoadingOverlay(true)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Mapa placeholder
                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Info.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(48.dp), tint = Primary)
                                Spacer(Modifier.height(8.dp))
                                Text("Mapa em Tempo Real", style = MaterialTheme.typography.titleMedium, color = Primary)
                                Text("Acompanhe o ônibus aqui", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    // Alunos
                    Text("Meus Alunos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    if (students.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                "Nenhum aluno cadastrado",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    students.forEach { student ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${student.grade ?: ""} - ${student.school ?: ""}", style = MaterialTheme.typography.bodyMedium)
                                    student.busRouteName?.let {
                                        Text("Rota: $it", style = MaterialTheme.typography.bodyMedium, color = Primary)
                                    }
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                            }
                        }
                    }

                    // Ônibus ativos
                    Text("Ônibus Ativos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    buses.forEach { bus ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (bus.active) Success.copy(alpha = 0.05f) else Surface),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bus.licensePlate, style = MaterialTheme.typography.titleMedium)
                                    Text(bus.model, style = MaterialTheme.typography.bodyMedium)
                                    bus.driverName?.let { Text("Motorista: $it", style = MaterialTheme.typography.bodyMedium) }
                                }
                                Box(
                                    modifier = Modifier.size(12.dp).clip(CircleShape).background(if (bus.active) Success else TextMuted),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
