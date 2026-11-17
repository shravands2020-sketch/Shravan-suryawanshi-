package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BookingStatus
import com.example.data.model.QuoteRequest
import com.example.data.model.QuoteStatus
import com.example.data.model.ServiceBooking
import com.example.data.model.ServiceType
import com.example.data.repository.OWNER_ADMIN_EMAIL
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedSteel
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MetallicWhite
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonVoltGreen
import com.example.ui.theme.ObsidianSurface
import com.example.ui.viewmodel.UiState

@Composable
fun AdminPanelScreen(
    quotesState: UiState<List<QuoteRequest>>,
    bookingsState: UiState<List<ServiceBooking>>,
    searchQuery: String,
    actionMessage: String?,
    onSearchChange: (String) -> Unit,
    onDismissMessage: () -> Unit,
    onBack: () -> Unit,
    onCallCustomer: (String) -> Unit,
    onWhatsAppCustomer: (String, String) -> Unit,
    onUpdateQuote: (QuoteRequest, String, Long?) -> Unit,
    onDeleteQuote: (QuoteRequest) -> Unit,
    onUpdateBookingStatus: (ServiceBooking, String) -> Unit,
    onDeleteBooking: (ServiceBooking) -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = All Quotes, 1 = Pending Quotes, 2 = Service Bookings
    var quoteToEditPrice by remember { mutableStateOf<QuoteRequest?>(null) }

    val quotes = (quotesState as? UiState.Success)?.data.orEmpty()
    val bookings = (bookingsState as? UiState.Success)?.data.orEmpty()

    val pendingQuotesCount = quotes.count { it.status == "PENDING" }
    val scheduledBookingsCount = bookings.count { it.status == "SCHEDULED" }

    val qFilter = searchQuery.trim().lowercase()
    val filteredQuotes = remember(quotes, selectedTab, qFilter) {
        quotes.filter { item ->
            val tabMatches = if (selectedTab == 1) item.status == "PENDING" else true
            val searchMatches = qFilter.isEmpty() ||
                item.customerName.lowercase().contains(qFilter) ||
                item.phoneNumber.lowercase().contains(qFilter) ||
                item.vehicleOrSystem.lowercase().contains(qFilter) ||
                item.selectedBatteryModel.lowercase().contains(qFilter) ||
                item.locationArea.lowercase().contains(qFilter)
            tabMatches && searchMatches
        }
    }
    val filteredBookings = remember(bookings, qFilter) {
        bookings.filter { item ->
            qFilter.isEmpty() ||
                item.customerName.lowercase().contains(qFilter) ||
                item.phoneNumber.lowercase().contains(qFilter) ||
                item.vehicleDetails.lowercase().contains(qFilter)
        }
    }

    Scaffold(
        containerColor = DeepCharcoal,
        topBar = {
            Surface(
                color = DeepCharcoal,
                border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Storefront",
                            tint = MetallicWhite
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = NeonVoltGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OWNER ADMIN PANEL",
                            style = MaterialTheme.typography.titleMedium,
                            color = MetallicWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = OWNER_ADMIN_EMAIL,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonVoltGreen
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // KPI Strip
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminKpiCard(
                            label = "Total Quotes",
                            value = quotes.size.toString(),
                            accent = MetallicWhite,
                            modifier = Modifier.weight(1f)
                        )
                        AdminKpiCard(
                            label = "Pending",
                            value = pendingQuotesCount.toString(),
                            accent = AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                        AdminKpiCard(
                            label = "Checkups",
                            value = "$scheduledBookingsCount/${bookings.size}",
                            accent = NeonVoltGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Search bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search customer, phone, vehicle, or area...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = NeonVoltGreen)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonVoltGreen,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = MetallicWhite,
                            unfocusedTextColor = MetallicWhite,
                            cursorColor = NeonVoltGreen
                        )
                    )
                }

                // Filter Tabs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminFilterTab(
                            label = "All Quotes (${quotes.size})",
                            selected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        AdminFilterTab(
                            label = "Pending ($pendingQuotesCount)",
                            selected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                        AdminFilterTab(
                            label = "Checkups (${bookings.size})",
                            selected = selectedTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 2 }
                        )
                    }
                }

                if (actionMessage != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonGreenGlow,
                            border = BorderStroke(1.dp, NeonVoltGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonVoltGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(actionMessage, color = MetallicWhite, style = MaterialTheme.typography.bodyMedium)
                                }
                                TextButton(onClick = onDismissMessage) {
                                    Text("OK", color = NeonVoltGreen)
                                }
                            }
                        }
                    }
                }

                if (selectedTab == 0 || selectedTab == 1) {
                    when (quotesState) {
                        is UiState.Loading -> item {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = NeonVoltGreen)
                            }
                        }
                        is UiState.Error -> item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ErrorCoral.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, ErrorCoral)
                            ) {
                                Text(
                                    text = quotesState.message,
                                    color = ErrorCoral,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        is UiState.Success -> {
                            if (filteredQuotes.isEmpty()) {
                                item {
                                    AdminEmptyState("No quote requests match this filter yet.")
                                }
                            } else {
                                items(filteredQuotes, key = { "aq_${it.userId}_${it.id}" }) { quote ->
                                    AdminQuoteCard(
                                        quote = quote,
                                        onCall = { onCallCustomer(quote.phoneNumber) },
                                        onWhatsApp = {
                                            val msg = "Hello ${quote.customerName}, regarding your Nath Battery quote for ${quote.selectedBatteryModel} (${quote.vehicleOrSystem}) at ₹${quote.estimatedPriceInr}:"
                                            onWhatsAppCustomer(quote.phoneNumber, msg)
                                        },
                                        onEditPrice = { quoteToEditPrice = quote },
                                        onStatusChange = { status -> onUpdateQuote(quote, status, null) },
                                        onDelete = { onDeleteQuote(quote) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    when (bookingsState) {
                        is UiState.Loading -> item {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = NeonVoltGreen)
                            }
                        }
                        is UiState.Error -> item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ErrorCoral.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, ErrorCoral)
                            ) {
                                Text(
                                    text = bookingsState.message,
                                    color = ErrorCoral,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        is UiState.Success -> {
                            if (filteredBookings.isEmpty()) {
                                item {
                                    AdminEmptyState("No service checkup bookings match this filter yet.")
                                }
                            } else {
                                items(filteredBookings, key = { "ab_${it.userId}_${it.id}" }) { booking ->
                                    AdminBookingCard(
                                        booking = booking,
                                        onCall = { onCallCustomer(booking.phoneNumber) },
                                        onWhatsApp = {
                                            val msg = "Hello ${booking.customerName}, Nath Battery Kaneri Chowk confirming your ${ServiceType.fromRaw(booking.serviceType).label} for ${booking.vehicleDetails} (${booking.preferredSlot})."
                                            onWhatsAppCustomer(booking.phoneNumber, msg)
                                        },
                                        onStatusChange = { status -> onUpdateBookingStatus(booking, status) },
                                        onDelete = { onDeleteBooking(booking) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    quoteToEditPrice?.let { targetQuote ->
        var priceText by remember(targetQuote.id) {
            mutableStateOf(targetQuote.estimatedPriceInr.toString())
        }
        AlertDialog(
            onDismissRequest = { quoteToEditPrice = null },
            containerColor = ObsidianSurface,
            titleContentColor = MetallicWhite,
            textContentColor = BrushedSilver,
            title = { Text("Confirm Quote Price (₹)") },
            text = {
                Column {
                    Text("${targetQuote.customerName} • ${targetQuote.selectedBatteryModel}")
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { c -> c.isDigit() }.take(6) },
                        label = { Text("Final Quoted Price (INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonVoltGreen,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = MetallicWhite,
                            unfocusedTextColor = MetallicWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = priceText.toLongOrNull() ?: targetQuote.estimatedPriceInr
                        onUpdateQuote(targetQuote, "CONFIRMED", newPrice)
                        quoteToEditPrice = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonVoltGreen,
                        contentColor = DeepCharcoal
                    )
                ) {
                    Text("Confirm & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { quoteToEditPrice = null }) {
                    Text("Cancel", color = BrushedSilver)
                }
            }
        )
    }
}

@Composable
private fun AdminKpiCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = BrushedSilver)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AdminFilterTab(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) NeonVoltGreen else ObsidianSurface,
        border = BorderStroke(1.dp, if (selected) NeonVoltGreen else GlassBorder)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) DeepCharcoal else BrushedSilver,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminQuoteCard(
    quote: QuoteRequest,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onEditPrice: () -> Unit,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val status = QuoteStatus.fromRaw(quote.status)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, if (status == QuoteStatus.PENDING) AmberGold.copy(alpha = 0.5f) else GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(quote.customerName, style = MaterialTheme.typography.titleLarge, color = MetallicWhite)
                    Text("${quote.phoneNumber} • ${quote.locationArea}", style = MaterialTheme.typography.bodyMedium, color = BrushedSilver)
                }
                StatusBadge(status.label, status == QuoteStatus.PENDING, status == QuoteStatus.COMPLETED)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${quote.selectedBatteryModel} (${quote.category})",
                style = MaterialTheme.typography.titleMedium,
                color = NeonVoltGreen
            )
            Text(
                text = "Vehicle/Setup: ${quote.vehicleOrSystem}",
                style = MaterialTheme.typography.bodyMedium,
                color = MetallicWhite
            )
            Text(
                text = "Quote: ₹${quote.estimatedPriceInr} • Old Exchange: ${if (quote.exchangeOldBattery) "Yes" else "No"} • Doorstep: ${if (quote.doorstepService) "Yes" else "Shop Visit"}",
                style = MaterialTheme.typography.bodyMedium,
                color = BrushedSilver
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, tint = NeonVoltGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Call", color = MetallicWhite)
                }
                OutlinedButton(
                    onClick = onWhatsApp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = NeonVoltGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("WhatsApp", color = MetallicWhite)
                }
                IconButton(onClick = onEditPrice) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Quote Price", tint = ElectricCyan)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete Quote", tint = ErrorCoral)
                }
            }

            Spacer(Modifier.height(6.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusActionChip("Confirm", quote.status == "CONFIRMED") { onStatusChange("CONFIRMED") }
                StatusActionChip("Installed", quote.status == "COMPLETED") { onStatusChange("COMPLETED") }
                StatusActionChip("Pending", quote.status == "PENDING") { onStatusChange("PENDING") }
                StatusActionChip("Cancel", quote.status == "CANCELLED") { onStatusChange("CANCELLED") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminBookingCard(
    booking: ServiceBooking,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val status = BookingStatus.fromRaw(booking.status)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, if (status == BookingStatus.SCHEDULED) AmberGold.copy(alpha = 0.5f) else GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.customerName, style = MaterialTheme.typography.titleLarge, color = MetallicWhite)
                    Text(booking.phoneNumber, style = MaterialTheme.typography.bodyMedium, color = BrushedSilver)
                }
                StatusBadge(status.label, status == BookingStatus.SCHEDULED, status == BookingStatus.COMPLETED)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = ServiceType.fromRaw(booking.serviceType).label,
                style = MaterialTheme.typography.titleMedium,
                color = NeonVoltGreen
            )
            Text(
                text = "Details: ${booking.vehicleDetails}",
                style = MaterialTheme.typography.bodyMedium,
                color = MetallicWhite
            )
            Text(
                text = "Slot: ${booking.preferredSlot}",
                style = MaterialTheme.typography.bodyMedium,
                color = AmberGold
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, tint = NeonVoltGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Call", color = MetallicWhite)
                }
                OutlinedButton(
                    onClick = onWhatsApp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = NeonVoltGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("WhatsApp", color = MetallicWhite)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete Booking", tint = ErrorCoral)
                }
            }

            Spacer(Modifier.height(6.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusActionChip("Assign Tech", booking.status == "CONFIRMED") { onStatusChange("CONFIRMED") }
                StatusActionChip("Completed", booking.status == "COMPLETED") { onStatusChange("COMPLETED") }
                StatusActionChip("Scheduled", booking.status == "SCHEDULED") { onStatusChange("SCHEDULED") }
                StatusActionChip("Cancel", booking.status == "CANCELLED") { onStatusChange("CANCELLED") }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, isPending: Boolean, isDone: Boolean) {
    val bg = when {
        isDone -> NeonGreenGlow
        isPending -> AmberGold.copy(alpha = 0.16f)
        else -> ElevatedSteel
    }
    val fg = when {
        isDone -> NeonVoltGreen
        isPending -> AmberGold
        else -> BrushedSilver
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatusActionChip(label: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) NeonVoltGreen else ElevatedSteel,
        border = BorderStroke(1.dp, if (active) NeonVoltGreen else GlassBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (active) DeepCharcoal else MetallicWhite,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AdminEmptyState(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ObsidianSurface,
        border = BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = BrushedSilver,
            modifier = Modifier.padding(20.dp)
        )
    }
}
