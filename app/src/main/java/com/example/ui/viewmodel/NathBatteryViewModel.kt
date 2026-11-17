package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BatteryCategory
import com.example.data.model.BatteryProduct
import com.example.data.model.NathBatteryCatalog
import com.example.data.model.QuoteRequest
import com.example.data.model.ServiceBooking
import com.example.data.repository.NathBatteryRepository
import com.example.data.repository.OWNER_ADMIN_EMAIL
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class FinderMode {
    BATTERY_QUOTE,
    FREE_CHECKUP_BOOKING
}

data class FinderFormState(
    val mode: FinderMode = FinderMode.BATTERY_QUOTE,
    val step: Int = 1,
    val selectedCategory: BatteryCategory = BatteryCategory.CAR,
    val vehicleOrSystem: String = "Maruti Swift / Baleno / Dzire",
    val selectedBatteryModel: String = "Amaron PRO DIN50",
    val estimatedPriceInr: Long = 6450L,
    val exchangeBonusInr: Long = 950L,
    val exchangeOldBattery: Boolean = true,
    val doorstepService: Boolean = true,
    val customerName: String = "",
    val phoneNumber: String = "",
    val locationArea: String = NathBatteryCatalog.laturAreas.first(),
    val serviceType: String = "FREE_HEALTH_CHECK",
    val preferredSlot: String = "Morning (9:30 AM - 1:00 PM)",
    val isSubmitting: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
    val lastSavedSummary: String? = null
)

class NathBatteryViewModel(
    private val repository: NathBatteryRepository,
    private val currentUserId: String,
    userEmail: String = "",
    defaultCustomerName: String = ""
) : ViewModel() {

    val isOwnerAdmin: Boolean = userEmail.equals(OWNER_ADMIN_EMAIL, ignoreCase = true)

    private val _selectedCatalogCategory = MutableStateFlow(BatteryCategory.CAR)
    val selectedCatalogCategory: StateFlow<BatteryCategory> = _selectedCatalogCategory.asStateFlow()

    private val _formState = MutableStateFlow(
        FinderFormState(customerName = defaultCustomerName.take(60))
    )
    val formState: StateFlow<FinderFormState> = _formState.asStateFlow()

    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminActionMessage = MutableStateFlow<String?>(null)
    val adminActionMessage: StateFlow<String?> = _adminActionMessage.asStateFlow()

    val quoteRequestsState: StateFlow<UiState<List<QuoteRequest>>> =
        repository.observeQuoteRequests(currentUserId)
            .map<List<QuoteRequest>, UiState<List<QuoteRequest>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing quote requests", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load saved quote requests"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val serviceBookingsState: StateFlow<UiState<List<ServiceBooking>>> =
        repository.observeServiceBookings(currentUserId)
            .map<List<ServiceBooking>, UiState<List<ServiceBooking>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing service bookings", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load service bookings"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val adminQuotesState: StateFlow<UiState<List<QuoteRequest>>> =
        if (isOwnerAdmin) {
            repository.observeAllQuoteRequestsForAdmin()
                .map<List<QuoteRequest>, UiState<List<QuoteRequest>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing admin quote requests", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load all customer quotes"))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = UiState.Loading
                )
        } else {
            flowOf<UiState<List<QuoteRequest>>>(UiState.Success(emptyList()))
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = UiState.Success(emptyList())
                )
        }

    val adminBookingsState: StateFlow<UiState<List<ServiceBooking>>> =
        if (isOwnerAdmin) {
            repository.observeAllServiceBookingsForAdmin()
                .map<List<ServiceBooking>, UiState<List<ServiceBooking>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing admin service bookings", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load all service bookings"))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = UiState.Loading
                )
        } else {
            flowOf<UiState<List<ServiceBooking>>>(UiState.Success(emptyList()))
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = UiState.Success(emptyList())
                )
        }

    fun selectCatalogCategory(category: BatteryCategory) {
        _selectedCatalogCategory.value = category
    }

    fun prefillQuoteFromProduct(product: BatteryProduct) {
        _formState.update { state ->
            state.copy(
                mode = FinderMode.BATTERY_QUOTE,
                step = 2,
                selectedCategory = product.category,
                vehicleOrSystem = product.compatibleWith.take(80),
                selectedBatteryModel = product.name,
                estimatedPriceInr = product.priceInr,
                exchangeBonusInr = product.exchangeBonusInr,
                feedbackMessage = null,
                errorMessage = null
            )
        }
    }

    fun switchFinderMode(mode: FinderMode, serviceTypeKey: String? = null) {
        _formState.update { state ->
            state.copy(
                mode = mode,
                step = 1,
                serviceType = serviceTypeKey ?: state.serviceType,
                feedbackMessage = null,
                errorMessage = null
            )
        }
    }

    fun setStep(step: Int) {
        _formState.update { it.copy(step = step.coerceIn(1, 3), errorMessage = null) }
    }

    fun selectFinderCategory(category: BatteryCategory) {
        val defaultProduct = NathBatteryCatalog.products.firstOrNull { it.category == category }
        _formState.update { state ->
            state.copy(
                selectedCategory = category,
                vehicleOrSystem = category.popularVehicles.firstOrNull() ?: "",
                selectedBatteryModel = defaultProduct?.name ?: "Amaron Recommended Battery",
                estimatedPriceInr = defaultProduct?.priceInr ?: 5000L,
                exchangeBonusInr = defaultProduct?.exchangeBonusInr ?: 800L,
                errorMessage = null
            )
        }
    }

    fun updateVehicleOrSystem(value: String) {
        _formState.update { it.copy(vehicleOrSystem = value.take(80), errorMessage = null) }
    }

    fun selectProductForFinder(product: BatteryProduct) {
        _formState.update {
            it.copy(
                selectedBatteryModel = product.name,
                estimatedPriceInr = product.priceInr,
                exchangeBonusInr = product.exchangeBonusInr,
                errorMessage = null
            )
        }
    }

    fun toggleExchangeOldBattery(enabled: Boolean) {
        _formState.update { it.copy(exchangeOldBattery = enabled) }
    }

    fun toggleDoorstepService(enabled: Boolean) {
        _formState.update { it.copy(doorstepService = enabled) }
    }

    fun updateCustomerName(name: String) {
        _formState.update { it.copy(customerName = name.take(60), errorMessage = null) }
    }

    fun updatePhoneNumber(phone: String) {
        _formState.update { it.copy(phoneNumber = phone.take(16), errorMessage = null) }
    }

    fun updateLocationArea(area: String) {
        _formState.update { it.copy(locationArea = area.take(80), errorMessage = null) }
    }

    fun selectServiceType(serviceType: String) {
        _formState.update { it.copy(serviceType = serviceType, errorMessage = null) }
    }

    fun selectPreferredSlot(slot: String) {
        _formState.update { it.copy(preferredSlot = slot, errorMessage = null) }
    }

    fun submitQuoteRequest() {
        val state = _formState.value
        val name = state.customerName.trim()
        val phone = state.phoneNumber.trim()
        val vehicle = state.vehicleOrSystem.trim()

        when {
            vehicle.length < 2 -> {
                _formState.update { it.copy(errorMessage = "Please enter your vehicle or inverter setup.") }
                return
            }
            name.length < 2 -> {
                _formState.update { it.copy(errorMessage = "Please enter your name (at least 2 characters).") }
                return
            }
            phone.count { it.isDigit() } < 10 -> {
                _formState.update { it.copy(errorMessage = "Please enter a valid 10-digit mobile number.") }
                return
            }
        }

        val netEstimate = if (state.exchangeOldBattery) {
            (state.estimatedPriceInr - state.exchangeBonusInr).coerceAtLeast(0L)
        } else {
            state.estimatedPriceInr
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null, feedbackMessage = null) }
            val request = QuoteRequest(
                userId = currentUserId,
                category = state.selectedCategory.ruleKey,
                vehicleOrSystem = vehicle,
                selectedBatteryModel = state.selectedBatteryModel,
                customerName = name,
                phoneNumber = phone,
                exchangeOldBattery = state.exchangeOldBattery,
                doorstepService = state.doorstepService,
                locationArea = state.locationArea,
                estimatedPriceInr = netEstimate,
                status = "PENDING"
            )
            val result = repository.submitQuoteRequest(currentUserId, request)
            result.fold(
                onSuccess = {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            feedbackMessage = "Quote saved! Our Kaneri Chowk team will reach out shortly.",
                            lastSavedSummary = "${state.selectedBatteryModel} for $vehicle (~₹$netEstimate)"
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.localizedMessage ?: "Unable to submit quote request."
                        )
                    }
                }
            )
        }
    }

    fun submitServiceBooking() {
        val state = _formState.value
        val name = state.customerName.trim()
        val phone = state.phoneNumber.trim()
        val vehicle = "${state.vehicleOrSystem.trim()} (${state.locationArea.trim()})".take(100)

        when {
            state.vehicleOrSystem.trim().length < 2 -> {
                _formState.update { it.copy(errorMessage = "Please enter your vehicle or inverter details.") }
                return
            }
            name.length < 2 -> {
                _formState.update { it.copy(errorMessage = "Please enter your name.") }
                return
            }
            phone.count { it.isDigit() } < 10 -> {
                _formState.update { it.copy(errorMessage = "Please enter a valid 10-digit mobile number.") }
                return
            }
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null, feedbackMessage = null) }
            val booking = ServiceBooking(
                userId = currentUserId,
                serviceType = state.serviceType,
                vehicleDetails = vehicle,
                customerName = name,
                phoneNumber = phone,
                preferredSlot = state.preferredSlot,
                status = "SCHEDULED"
            )
            val result = repository.createServiceBooking(currentUserId, booking)
            result.fold(
                onSuccess = {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            feedbackMessage = "Service checkup booked for ${state.preferredSlot}!",
                            lastSavedSummary = "${state.serviceType.replace('_', ' ')} • $vehicle"
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.localizedMessage ?: "Unable to schedule service checkup."
                        )
                    }
                }
            )
        }
    }

    fun cancelQuote(quoteId: String) {
        viewModelScope.launch {
            repository.cancelQuoteRequest(currentUserId, quoteId)
        }
    }

    fun deleteQuote(quoteId: String) {
        viewModelScope.launch {
            repository.deleteQuoteRequest(currentUserId, quoteId)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelServiceBooking(currentUserId, bookingId)
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteServiceBooking(currentUserId, bookingId)
        }
    }

    fun rescheduleBooking(bookingId: String, nextSlot: String) {
        viewModelScope.launch {
            repository.rescheduleServiceBooking(currentUserId, bookingId, nextSlot)
        }
    }

    // --- Admin Actions (Owner: dsslatur10@gmail.com) ---
    fun updateAdminSearch(query: String) {
        _adminSearchQuery.value = query
    }

    fun clearAdminMessage() {
        _adminActionMessage.value = null
    }

    fun adminUpdateQuote(quote: QuoteRequest, newStatus: String, newPriceInr: Long? = null) {
        if (!isOwnerAdmin) return
        viewModelScope.launch {
            val res = repository.adminUpdateQuote(quote.userId, quote.id, newStatus, newPriceInr)
            _adminActionMessage.value = res.fold(
                onSuccess = { "Updated ${quote.customerName}'s quote to $newStatus" },
                onFailure = { it.localizedMessage ?: "Failed to update quote" }
            )
        }
    }

    fun adminDeleteQuote(quote: QuoteRequest) {
        if (!isOwnerAdmin) return
        viewModelScope.launch {
            val res = repository.adminDeleteQuote(quote.userId, quote.id)
            _adminActionMessage.value = res.fold(
                onSuccess = { "Deleted quote from ${quote.customerName}" },
                onFailure = { it.localizedMessage ?: "Failed to delete quote" }
            )
        }
    }

    fun adminUpdateBookingStatus(booking: ServiceBooking, newStatus: String) {
        if (!isOwnerAdmin) return
        viewModelScope.launch {
            val res = repository.adminUpdateBookingStatus(booking.userId, booking.id, newStatus)
            _adminActionMessage.value = res.fold(
                onSuccess = { "Updated ${booking.customerName}'s booking to $newStatus" },
                onFailure = { it.localizedMessage ?: "Failed to update booking" }
            )
        }
    }

    fun adminDeleteBooking(booking: ServiceBooking) {
        if (!isOwnerAdmin) return
        viewModelScope.launch {
            val res = repository.adminDeleteBooking(booking.userId, booking.id)
            _adminActionMessage.value = res.fold(
                onSuccess = { "Deleted booking from ${booking.customerName}" },
                onFailure = { it.localizedMessage ?: "Failed to delete booking" }
            )
        }
    }

    fun buildWhatsAppMessage(): String {
        val s = _formState.value
        val netPrice = if (s.exchangeOldBattery) {
            (s.estimatedPriceInr - s.exchangeBonusInr).coerceAtLeast(0L)
        } else {
            s.estimatedPriceInr
        }
        return if (s.mode == FinderMode.BATTERY_QUOTE) {
            buildString {
                append("Hello Nath Battery (Kaneri Chowk, Latur),\n")
                append("I would like an instant battery quote:\n")
                append("• Category: ${s.selectedCategory.tabLabel}\n")
                append("• Vehicle/Setup: ${s.vehicleOrSystem}\n")
                append("• Model: ${s.selectedBatteryModel}\n")
                append("• Old Battery Exchange: ${if (s.exchangeOldBattery) "Yes" else "No"}\n")
                append("• Doorstep Service: ${if (s.doorstepService) "Yes (${s.locationArea})" else "Visit Shop"}\n")
                append("• Est. Price Shown: ₹$netPrice\n")
                if (s.customerName.isNotBlank()) append("• Name: ${s.customerName}\n")
                if (s.phoneNumber.isNotBlank()) append("• Phone: ${s.phoneNumber}")
            }
        } else {
            buildString {
                append("Hello Nath Battery (Kaneri Chowk, Latur),\n")
                append("I want to book a service checkup:\n")
                append("• Service: ${s.serviceType.replace('_', ' ')}\n")
                append("• Vehicle/Setup: ${s.vehicleOrSystem}\n")
                append("• Area: ${s.locationArea}\n")
                append("• Preferred Slot: ${s.preferredSlot}\n")
                if (s.customerName.isNotBlank()) append("• Name: ${s.customerName}\n")
                if (s.phoneNumber.isNotBlank()) append("• Phone: ${s.phoneNumber}")
            }
        }
    }

    private companion object {
        const val TAG = "NathBatteryVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
