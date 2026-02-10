package com.schooltrack.mobile.service

import com.schooltrack.mobile.data.model.BusLocationUpdate
import com.schooltrack.mobile.data.model.BusResponse
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class BusWebSocketService {

    companion object {
        const val WS_URL = "ws://10.0.2.2:8080/ws"
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient { install(WebSockets) }
    private val _busLocations = MutableSharedFlow<BusResponse>(replay = 1)
    val busLocations: SharedFlow<BusResponse> = _busLocations.asSharedFlow()

    private var session: WebSocketSession? = null
    private var connectionJob: Job? = null

    fun connect(scope: CoroutineScope) {
        connectionJob?.cancel()
        connectionJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    client.webSocket(WS_URL) {
                        session = this
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                try {
                                    val busResponse = json.decodeFromString<BusResponse>(frame.readText())
                                    _busLocations.emit(busResponse)
                                } catch (_: Exception) { }
                            }
                        }
                    }
                } catch (_: Exception) {
                    delay(3000)
                }
            }
        }
    }

    suspend fun sendLocation(busId: Long, latitude: Double, longitude: Double) {
        try {
            val update = BusLocationUpdate(busId, latitude, longitude, System.currentTimeMillis())
            session?.send(Frame.Text(json.encodeToString(update)))
        } catch (_: Exception) { }
    }

    fun disconnect() {
        connectionJob?.cancel()
        session = null
    }
}
