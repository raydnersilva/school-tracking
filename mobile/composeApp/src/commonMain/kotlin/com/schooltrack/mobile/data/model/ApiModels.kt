package com.schooltrack.mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val captchaId: String,
    val captchaAnswer: String,
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val role: String,
    val name: String,
)

@Serializable
data class MessageResponse(
    val message: String,
)

@Serializable
data class CaptchaResponse(
    val captchaId: String,
    val svg: String,
)

@Serializable
data class BusResponse(
    val id: Long,
    val licensePlate: String,
    val capacity: Int,
    val model: String,
    val driverName: String? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val active: Boolean,
)

@Serializable
data class BusRouteResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val busId: Long? = null,
    val busLicensePlate: String? = null,
    val active: Boolean,
    val stops: List<RouteStopResponse> = emptyList(),
)

@Serializable
data class RouteStopResponse(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stopOrder: Int,
    val estimatedTime: String? = null,
)

@Serializable
data class StudentResponse(
    val id: Long,
    val name: String,
    val grade: String? = null,
    val school: String? = null,
    val parentName: String? = null,
    val busRouteId: Long? = null,
    val busRouteName: String? = null,
)

@Serializable
data class NotificationResponse(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val read: Boolean,
    val createdAt: String? = null,
)

@Serializable
data class ScheduleResponse(
    val id: Long,
    val period: String,
    val startTime: String,
    val endTime: String,
    val busPickupTime: String,
    val busDropoffTime: String,
    val busRouteId: Long? = null,
    val busRouteName: String? = null,
    val studentId: Long? = null,
    val studentName: String? = null,
)

@Serializable
data class BusLocationUpdate(
    val busId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
)
