package com.example.data.repository

import com.example.data.model.QuoteRequest
import com.example.data.model.ServiceBooking
import com.example.data.remote.OperationType
import com.example.data.remote.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

const val OWNER_ADMIN_EMAIL = "dsslatur10@gmail.com"

class NathBatteryRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    private fun requireUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in with Google to save or view requests.")
        check(uid == expectedUserId) { "Signed-in account changed; refusing cross-user operation." }
        return uid
    }

    private fun requireAdminUser(): String {
        val user = auth.currentUser
            ?: throw IllegalStateException("Owner sign-in required.")
        check(user.email.equals(OWNER_ADMIN_EMAIL, ignoreCase = true) && user.isEmailVerified) {
            "Admin access restricted to verified owner $OWNER_ADMIN_EMAIL."
        }
        return user.uid
    }

    fun observeQuoteRequests(userId: String): Flow<List<QuoteRequest>> {
        val path = "users/$userId/quote_requests"
        return db.collection("users")
            .document(userId)
            .collection("quote_requests")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    QuoteRequest::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeServiceBookings(userId: String): Flow<List<ServiceBooking>> {
        val path = "users/$userId/service_bookings"
        return db.collection("users")
            .document(userId)
            .collection("service_bookings")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    ServiceBooking::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeAllQuoteRequestsForAdmin(): Flow<List<QuoteRequest>> {
        val path = "collectionGroup/quote_requests"
        return db.collectionGroup("quote_requests")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    QuoteRequest::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.email.equals(OWNER_ADMIN_EMAIL, ignoreCase = true)) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeAllServiceBookingsForAdmin(): Flow<List<ServiceBooking>> {
        val path = "collectionGroup/service_bookings"
        return db.collectionGroup("service_bookings")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    ServiceBooking::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.email.equals(OWNER_ADMIN_EMAIL, ignoreCase = true)) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun submitQuoteRequest(userId: String, request: QuoteRequest): Result<String> {
        var docPath = "users/$userId/quote_requests"
        return try {
            val uid = requireUserId(userId)
            require(request.category in VALID_CATEGORIES) { "Invalid battery category." }
            val vehicle = request.vehicleOrSystem.trim()
            require(vehicle.length in 2..80) { "Vehicle or system details must be 2–80 characters." }
            val model = request.selectedBatteryModel.trim()
            require(model.length in 2..80) { "Battery model must be 2–80 characters." }
            val name = request.customerName.trim()
            require(name.length in 2..60) { "Customer name must be 2–60 characters." }
            val phone = request.phoneNumber.trim()
            require(phone.length in 10..16) { "Phone number must be 10–16 characters." }
            val area = request.locationArea.trim().take(80)
            val price = request.estimatedPriceInr.coerceIn(0L, 500_000L)

            val docRef = db.collection("users")
                .document(uid)
                .collection("quote_requests")
                .document()
            docPath = docRef.path

            val sanitized = request.copy(
                vehicleOrSystem = vehicle,
                selectedBatteryModel = model,
                customerName = name,
                phoneNumber = phone,
                locationArea = area,
                estimatedPriceInr = price
            )
            docRef.set(sanitized.toCreateMap(uid)).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cancelQuoteRequest(userId: String, quoteId: String): Result<Unit> {
        val docPath = "users/$userId/quote_requests/$quoteId"
        return try {
            val uid = requireUserId(userId)
            require(quoteId.isNotBlank()) { "Quote ID cannot be blank." }
            db.collection("users")
                .document(uid)
                .collection("quote_requests")
                .document(quoteId)
                .update(
                    mapOf(
                        "status" to "CANCELLED",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteQuoteRequest(userId: String, quoteId: String): Result<Unit> {
        val docPath = "users/$userId/quote_requests/$quoteId"
        return try {
            val uid = requireUserId(userId)
            require(quoteId.isNotBlank()) { "Quote ID cannot be blank." }
            db.collection("users")
                .document(uid)
                .collection("quote_requests")
                .document(quoteId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateQuote(
        targetUserId: String,
        quoteId: String,
        newStatus: String,
        newPriceInr: Long? = null
    ): Result<Unit> {
        val docPath = "users/$targetUserId/quote_requests/$quoteId"
        return try {
            requireAdminUser()
            require(targetUserId.isNotBlank() && quoteId.isNotBlank()) { "Target quote ID missing." }
            require(newStatus in VALID_QUOTE_STATUSES) { "Invalid quote status." }
            val payload = mutableMapOf<String, Any>(
                "status" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (newPriceInr != null) {
                payload["estimatedPriceInr"] = newPriceInr.coerceIn(0L, 500_000L)
            }
            db.collection("users")
                .document(targetUserId)
                .collection("quote_requests")
                .document(quoteId)
                .update(payload)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminDeleteQuote(targetUserId: String, quoteId: String): Result<Unit> {
        val docPath = "users/$targetUserId/quote_requests/$quoteId"
        return try {
            requireAdminUser()
            require(targetUserId.isNotBlank() && quoteId.isNotBlank()) { "Target quote ID missing." }
            db.collection("users")
                .document(targetUserId)
                .collection("quote_requests")
                .document(quoteId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun createServiceBooking(userId: String, booking: ServiceBooking): Result<String> {
        var docPath = "users/$userId/service_bookings"
        return try {
            val uid = requireUserId(userId)
            require(booking.serviceType in VALID_SERVICE_TYPES) { "Invalid service type." }
            val vehicle = booking.vehicleDetails.trim()
            require(vehicle.length in 2..100) { "Vehicle & location details must be 2–100 characters." }
            val name = booking.customerName.trim()
            require(name.length in 2..60) { "Customer name must be 2–60 characters." }
            val phone = booking.phoneNumber.trim()
            require(phone.length in 10..16) { "Phone number must be 10–16 characters." }
            val slot = booking.preferredSlot.trim()
            require(slot.length in 3..60) { "Preferred slot must be 3–60 characters." }

            val docRef = db.collection("users")
                .document(uid)
                .collection("service_bookings")
                .document()
            docPath = docRef.path

            val sanitized = booking.copy(
                vehicleDetails = vehicle,
                customerName = name,
                phoneNumber = phone,
                preferredSlot = slot
            )
            docRef.set(sanitized.toCreateMap(uid)).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun rescheduleServiceBooking(
        userId: String,
        bookingId: String,
        newSlot: String
    ): Result<Unit> {
        val docPath = "users/$userId/service_bookings/$bookingId"
        return try {
            val uid = requireUserId(userId)
            require(bookingId.isNotBlank()) { "Booking ID cannot be blank." }
            val slot = newSlot.trim()
            require(slot.length in 3..60) { "Preferred slot must be 3–60 characters." }
            db.collection("users")
                .document(uid)
                .collection("service_bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "preferredSlot" to slot,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cancelServiceBooking(userId: String, bookingId: String): Result<Unit> {
        val docPath = "users/$userId/service_bookings/$bookingId"
        return try {
            val uid = requireUserId(userId)
            require(bookingId.isNotBlank()) { "Booking ID cannot be blank." }
            db.collection("users")
                .document(uid)
                .collection("service_bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to "CANCELLED",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteServiceBooking(userId: String, bookingId: String): Result<Unit> {
        val docPath = "users/$userId/service_bookings/$bookingId"
        return try {
            val uid = requireUserId(userId)
            require(bookingId.isNotBlank()) { "Booking ID cannot be blank." }
            db.collection("users")
                .document(uid)
                .collection("service_bookings")
                .document(bookingId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateBookingStatus(
        targetUserId: String,
        bookingId: String,
        newStatus: String
    ): Result<Unit> {
        val docPath = "users/$targetUserId/service_bookings/$bookingId"
        return try {
            requireAdminUser()
            require(targetUserId.isNotBlank() && bookingId.isNotBlank()) { "Target booking ID missing." }
            require(newStatus in VALID_BOOKING_STATUSES) { "Invalid booking status." }
            db.collection("users")
                .document(targetUserId)
                .collection("service_bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminDeleteBooking(targetUserId: String, bookingId: String): Result<Unit> {
        val docPath = "users/$targetUserId/service_bookings/$bookingId"
        return try {
            requireAdminUser()
            require(targetUserId.isNotBlank() && bookingId.isNotBlank()) { "Target booking ID missing." }
            db.collection("users")
                .document(targetUserId)
                .collection("service_bookings")
                .document(bookingId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    private companion object {
        val VALID_CATEGORIES = setOf("CAR", "TWO_WHEELER", "INVERTER", "COMMERCIAL")
        val VALID_QUOTE_STATUSES = setOf("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED")
        val VALID_BOOKING_STATUSES = setOf("SCHEDULED", "CONFIRMED", "COMPLETED", "CANCELLED")
        val VALID_SERVICE_TYPES = setOf(
            "FREE_HEALTH_CHECK",
            "DOORSTEP_JUMPSTART",
            "ALTERNATOR_TEST",
            "HOME_INSTALLATION"
        )
    }
}
