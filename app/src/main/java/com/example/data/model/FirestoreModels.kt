package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class QuoteStatus(val label: String) {
    PENDING("Pending Review"),
    CONFIRMED("Quote Confirmed"),
    COMPLETED("Installed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromRaw(raw: String): QuoteStatus =
            entries.firstOrNull { it.name == raw } ?: PENDING
    }
}

enum class BookingStatus(val label: String) {
    SCHEDULED("Scheduled"),
    CONFIRMED("Technician Assigned"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromRaw(raw: String): BookingStatus =
            entries.firstOrNull { it.name == raw } ?: SCHEDULED
    }
}

enum class ServiceType(val label: String) {
    FREE_HEALTH_CHECK("Free Battery Health & Alternator Check"),
    DOORSTEP_JUMPSTART("Express Doorstep Jumpstart"),
    ALTERNATOR_TEST("Alternator & Load Test"),
    HOME_INSTALLATION("Inverter / Battery Home Installation");

    companion object {
        fun fromRaw(raw: String): ServiceType =
            entries.firstOrNull { it.name == raw } ?: FREE_HEALTH_CHECK
    }
}

@Keep
@IgnoreExtraProperties
data class QuoteRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val category: String = "CAR",
    val vehicleOrSystem: String = "",
    val selectedBatteryModel: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val exchangeOldBattery: Boolean = true,
    val doorstepService: Boolean = true,
    val locationArea: String = "",
    val estimatedPriceInr: Long = 0L,
    val status: String = "PENDING",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "category" to category,
        "vehicleOrSystem" to vehicleOrSystem,
        "selectedBatteryModel" to selectedBatteryModel,
        "customerName" to customerName,
        "phoneNumber" to phoneNumber,
        "exchangeOldBattery" to exchangeOldBattery,
        "doorstepService" to doorstepService,
        "locationArea" to locationArea,
        "estimatedPriceInr" to estimatedPriceInr,
        "status" to "PENDING",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class ServiceBooking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val serviceType: String = "FREE_HEALTH_CHECK",
    val vehicleDetails: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val preferredSlot: String = "Morning (9:30 AM - 1:00 PM)",
    val status: String = "SCHEDULED",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "serviceType" to serviceType,
        "vehicleDetails" to vehicleDetails,
        "customerName" to customerName,
        "phoneNumber" to phoneNumber,
        "preferredSlot" to preferredSlot,
        "status" to "SCHEDULED",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
