package com.schooltrack.mobile.data.network

import com.schooltrack.mobile.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(private val tokenProvider: () -> String?) {

    companion object {
        // Alterar para o IP da máquina em rede local para testes no dispositivo
        const val BASE_URL = "http://10.0.2.2:8080/api"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    private fun HttpRequestBuilder.applyAuth() {
        tokenProvider()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    // === Auth ===
    suspend fun login(request: LoginRequest): AuthResponse =
        httpClient.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        httpClient.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun forgotPassword(request: ForgotPasswordRequest): MessageResponse =
        httpClient.post("$BASE_URL/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    // === Captcha ===
    suspend fun getCaptcha(): CaptchaResponse =
        httpClient.get("$BASE_URL/captcha").body()

    // === Buses ===
    suspend fun getActiveBuses(): List<BusResponse> =
        httpClient.get("$BASE_URL/buses") { applyAuth() }.body()

    suspend fun getBus(id: Long): BusResponse =
        httpClient.get("$BASE_URL/buses/$id") { applyAuth() }.body()

    // === Routes ===
    suspend fun getRoutes(): List<BusRouteResponse> =
        httpClient.get("$BASE_URL/routes") { applyAuth() }.body()

    suspend fun getRoute(id: Long): BusRouteResponse =
        httpClient.get("$BASE_URL/routes/$id") { applyAuth() }.body()

    // === Students ===
    suspend fun getStudents(): List<StudentResponse> =
        httpClient.get("$BASE_URL/students") { applyAuth() }.body()

    // === Notifications ===
    suspend fun getNotifications(): List<NotificationResponse> =
        httpClient.get("$BASE_URL/notifications") { applyAuth() }.body()

    suspend fun markNotificationRead(id: Long): NotificationResponse =
        httpClient.put("$BASE_URL/notifications/$id/read") { applyAuth() }.body()

    // === Schedules ===
    suspend fun getSchedules(): List<ScheduleResponse> =
        httpClient.get("$BASE_URL/schedules") { applyAuth() }.body()

    suspend fun getSchedulesByStudent(studentId: Long): List<ScheduleResponse> =
        httpClient.get("$BASE_URL/schedules/student/$studentId") { applyAuth() }.body()

    // === Driver ===
    suspend fun getCurrentTrip(): com.schooltrack.mobile.ui.screens.driver.TripResponse =
        httpClient.get("$BASE_URL/driver/trip/current") { applyAuth() }.body()

    suspend fun startTrip(busRouteId: Long): com.schooltrack.mobile.ui.screens.driver.TripResponse =
        httpClient.post("$BASE_URL/driver/trip/start?busRouteId=$busRouteId") { applyAuth() }.body()

    suspend fun endTrip(tripId: Long): com.schooltrack.mobile.ui.screens.driver.TripResponse =
        httpClient.put("$BASE_URL/driver/trip/$tripId/end") { applyAuth() }.body()

    suspend fun getDriverTrips(): List<com.schooltrack.mobile.ui.screens.driver.TripResponse> =
        httpClient.get("$BASE_URL/driver/trips") { applyAuth() }.body()

    // === Chat ===
    suspend fun getChatMessages(roomId: String): List<com.schooltrack.mobile.ui.screens.chat.ChatMessageDto> =
        httpClient.get("$BASE_URL/chat/$roomId") { applyAuth() }.body()

    suspend fun sendChatMessage(roomId: String, content: String): com.schooltrack.mobile.ui.screens.chat.ChatMessageDto =
        httpClient.post("$BASE_URL/chat/$roomId/send") {
            applyAuth()
            contentType(ContentType.Application.Json)
            setBody(mapOf("roomId" to roomId, "content" to content))
        }.body()

    // === Ratings ===
    suspend fun rateDriver(driverId: Long, rating: Int, comment: String) =
        httpClient.post("$BASE_URL/ratings") {
            applyAuth()
            contentType(ContentType.Application.Json)
            setBody(mapOf("driverId" to driverId.toString(), "rating" to rating.toString(), "comment" to comment))
        }.body<Any>()
}
