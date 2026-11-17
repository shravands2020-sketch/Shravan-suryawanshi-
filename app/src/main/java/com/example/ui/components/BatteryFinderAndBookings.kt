package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BatteryCategory
import com.example.data.model.BookingStatus
import com.example.data.model.NathBatteryCatalog
import com.example.data.model.QuoteRequest
import com.example.data.model.QuoteStatus
import com.example.data.model.ServiceBooking
import com.example.data.model.ServiceType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElevatedSteel
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MetallicWhite
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonVoltGreen
import com.example.ui.theme.ObsidianSurface
import com.example.ui.viewmodel.FinderFormState
import com.example.ui.viewmodel.FinderMode
import com.example.ui.viewmodel.NathBatteryViewModel
import com.example.ui.viewmodel.UiState

private val SERVICE_SLOTS = listOf(
    "Morning (9:30 AM - 1:00 PM)",
    "Afternoon (1:00 PM - 4:30 PM)",
    "Evening (4:30 PM - 8:00 PM)"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatteryFinderAndBookingSection(
    formState: FinderFormState,
    viewModel: NathBatteryViewModel,
    onSendWhatsApp: () -> Unit
) {
    val categoryProducts = NathBatteryCatalog.products.filter {
        it.category == formState.selectedCategory
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "3-STEP BATTERY FINDER & BOOKING",
            style = MaterialTheme.typography.labelSmall,
            color = NeonVoltGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Instant Quote or Free Checkup",
            style = MaterialTheme.typography.headlineLarge,
            color = MetallicWhite
        )

        Spacer(Modifier.height(12.dp))

        // Mode switcher: Instant Battery Quote vs Book Free Checkup
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeTabButton(
                label = "Instant Battery Quote",
                selected = formState.mode == FinderMode.BATTERY_QUOTE,
                modifier = Modifier
                    .weight(1f)
                    .testTag("mode_quote_button"),
                onClick = { viewModel.switchFinderMode(FinderMode.BATTERY_QUOTE) }
            )
            ModeTabButton(
                label = "Book Free Checkup",
                selected = formState.mode == FinderMode.FREE_CHECKUP_BOOKING,
                modifier = Modifier
                    .weight(1f)
                    .testTag("mode_checkup_button"),
                onClick = { viewModel.switchFinderMode(FinderMode.FREE_CHECKUP_BOOKING) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (formState.mode == FinderMode.BATTERY_QUOTE) {
                    // 3-step indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StepPill(
                            stepNum = 1,
                            title = "Type",
                            active = formState.step == 1,
                            completed = formState.step > 1,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setStep(1) }
                        )
                        StepPill(
                            stepNum = 2,
                            title = "Model",
                            active = formState.step == 2,
                            completed = formState.step > 2,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setStep(2) }
                        )
                        StepPill(
                            stepNum = 3,
                            title = "Contact",
                            active = formState.step == 3,
                            completed = false,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setStep(3) }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    when (formState.step) {
                        1 -> {
                            Text(
                                text = "Step 1: Select Vehicle or Home Power Type",
                                style = MaterialTheme.typography.titleMedium,
                                color = MetallicWhite
                            )
                            Spacer(Modifier.height(10.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BatteryCategory.entries.forEach { cat ->
                                    val selected = cat == formState.selectedCategory
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (selected) NeonGreenGlow else ElevatedSteel,
                                        border = BorderStroke(
                                            1.dp,
                                            if (selected) NeonVoltGreen else GlassBorder
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.selectFinderCategory(cat) }
                                            .testTag("finder_category_${cat.ruleKey.lowercase()}")
                                    ) {
                                        Text(
                                            text = cat.tabLabel,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) NeonVoltGreen else MetallicWhite,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.setStep(2) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("finder_next_step2"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonVoltGreen,
                                    contentColor = DeepCharcoal
                                )
                            ) {
                                Text("Continue to Brand / Model", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        2 -> {
                            Text(
                                text = "Step 2: Choose Vehicle / System & Battery",
                                style = MaterialTheme.typography.titleMedium,
                                color = MetallicWhite
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                formState.selectedCategory.popularVehicles.forEach { preset ->
                                    val active = formState.vehicleOrSystem == preset
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (active) NeonGreenGlow else ElevatedSteel,
                                        border = BorderStroke(
                                            1.dp,
                                            if (active) NeonVoltGreen else GlassBorder
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { viewModel.updateVehicleOrSystem(preset) }
                                    ) {
                                        Text(
                                            text = preset,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (active) NeonVoltGreen else BrushedSilver,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            NathTextField(
                                value = formState.vehicleOrSystem,
                                onValueChange = viewModel::updateVehicleOrSystem,
                                label = "Vehicle Brand / Model or Inverter Setup",
                                testTag = "finder_vehicle_input"
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "Recommended Amaron Models:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrushedSilver
                            )
                            Spacer(Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                categoryProducts.forEach { prod ->
                                    val chosen = formState.selectedBatteryModel == prod.name
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (chosen) NeonGreenGlow else ElevatedSteel.copy(alpha = 0.7f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (chosen) NeonVoltGreen else GlassBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.selectProductForFinder(prod) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = prod.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MetallicWhite
                                                )
                                                Text(
                                                    text = "${prod.capacityAh} • ${prod.warrantyBadge}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = BrushedSilver
                                                )
                                            }
                                            Text(
                                                text = "₹${prod.priceInr}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = NeonVoltGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Old Battery Exchange (-₹${formState.exchangeBonusInr})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MetallicWhite
                                    )
                                    Text(
                                        text = "Instant scrap buyback discount",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = BrushedSilver
                                    )
                                }
                                Switch(
                                    checked = formState.exchangeOldBattery,
                                    onCheckedChange = viewModel::toggleExchangeOldBattery,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DeepCharcoal,
                                        checkedTrackColor = NeonVoltGreen
                                    )
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.setStep(1) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Text("Back", color = MetallicWhite)
                                }
                                Button(
                                    onClick = { viewModel.setStep(3) },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(48.dp)
                                        .testTag("finder_next_step3"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonVoltGreen,
                                        contentColor = DeepCharcoal
                                    )
                                ) {
                                    Text("Continue to Phone", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }

                        3 -> {
                            val netPrice = if (formState.exchangeOldBattery) {
                                (formState.estimatedPriceInr - formState.exchangeBonusInr).coerceAtLeast(0L)
                            } else {
                                formState.estimatedPriceInr
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonGreenGlow,
                                border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = formState.selectedBatteryModel,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MetallicWhite
                                        )
                                        Text(
                                            text = formState.vehicleOrSystem,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = BrushedSilver
                                        )
                                    }
                                    Text(
                                        text = "Est. ₹$netPrice",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = NeonVoltGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            NathTextField(
                                value = formState.customerName,
                                onValueChange = viewModel::updateCustomerName,
                                label = "Your Name",
                                testTag = "finder_name_input"
                            )

                            Spacer(Modifier.height(10.dp))

                            NathTextField(
                                value = formState.phoneNumber,
                                onValueChange = viewModel::updatePhoneNumber,
                                label = "Mobile / WhatsApp Number",
                                keyboardType = KeyboardType.Phone,
                                testTag = "finder_phone_input"
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = "Area in Latur:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrushedSilver
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NathBatteryCatalog.laturAreas.forEach { area ->
                                    val selected = formState.locationArea == area
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) NeonGreenGlow else ElevatedSteel,
                                        border = BorderStroke(
                                            1.dp,
                                            if (selected) NeonVoltGreen else GlassBorder
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { viewModel.updateLocationArea(area) }
                                    ) {
                                        Text(
                                            text = area,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (selected) NeonVoltGreen else BrushedSilver,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Doorstep Testing & Installation in Latur",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MetallicWhite,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = formState.doorstepService,
                                    onCheckedChange = viewModel::toggleDoorstepService,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DeepCharcoal,
                                        checkedTrackColor = NeonVoltGreen
                                    )
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            FormFeedbackBanners(formState)

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.setStep(2) },
                                    modifier = Modifier.height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Text("Back", color = MetallicWhite)
                                }
                                Button(
                                    onClick = { viewModel.submitQuoteRequest() },
                                    enabled = !formState.isSubmitting,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("submit_quote_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonVoltGreen,
                                        contentColor = DeepCharcoal
                                    )
                                ) {
                                    if (formState.isSubmitting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = DeepCharcoal,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Save Quote Request", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onSendWhatsApp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_whatsapp_quote_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, NeonVoltGreen.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = null,
                                    tint = NeonVoltGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Send via WhatsApp (+91 98506 38290)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MetallicWhite
                                )
                            }
                        }
                    }
                } else {
                    // Book Free Battery Check / Doorstep Jumpstart Mode
                    Text(
                        text = "Select Service Type",
                        style = MaterialTheme.typography.titleMedium,
                        color = MetallicWhite
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ServiceType.entries.forEach { st ->
                            val selected = formState.serviceType == st.name
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) NeonGreenGlow else ElevatedSteel,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) NeonVoltGreen else GlassBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.selectServiceType(st.name) }
                            ) {
                                Text(
                                    text = st.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selected) NeonVoltGreen else MetallicWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    NathTextField(
                        value = formState.vehicleOrSystem,
                        onValueChange = viewModel::updateVehicleOrSystem,
                        label = "Vehicle / Inverter Model & Landmark",
                        testTag = "booking_vehicle_input"
                    )

                    Spacer(Modifier.height(10.dp))

                    NathTextField(
                        value = formState.customerName,
                        onValueChange = viewModel::updateCustomerName,
                        label = "Your Name",
                        testTag = "booking_name_input"
                    )

                    Spacer(Modifier.height(10.dp))

                    NathTextField(
                        value = formState.phoneNumber,
                        onValueChange = viewModel::updatePhoneNumber,
                        label = "Mobile Number",
                        keyboardType = KeyboardType.Phone,
                        testTag = "booking_phone_input"
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Preferred Time Slot (Open Daily 9:30 AM – 8:00 PM):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrushedSilver
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SERVICE_SLOTS.forEach { slot ->
                            val selected = formState.preferredSlot == slot
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) NeonGreenGlow else ElevatedSteel,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) NeonVoltGreen else GlassBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.selectPreferredSlot(slot) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Schedule,
                                        contentDescription = null,
                                        tint = if (selected) NeonVoltGreen else BrushedSilver,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selected) NeonVoltGreen else BrushedSilver
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    FormFeedbackBanners(formState)

                    Button(
                        onClick = { viewModel.submitServiceBooking() },
                        enabled = !formState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_booking_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonVoltGreen,
                            contentColor = DeepCharcoal
                        )
                    ) {
                        if (formState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DeepCharcoal,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Confirm Free Battery Checkup", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onSendWhatsApp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, NeonVoltGreen.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Book via WhatsApp (+91 98506 38290)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MetallicWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormFeedbackBanners(formState: FinderFormState) {
    if (formState.errorMessage != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(10.dp),
            color = ErrorCoral.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, ErrorCoral.copy(alpha = 0.5f))
        ) {
            Text(
                text = formState.errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorCoral,
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    if (formState.feedbackMessage != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(10.dp),
            color = NeonGreenGlow,
            border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = NeonVoltGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = formState.feedbackMessage,
                        style = MaterialTheme.typography.titleMedium,
                        color = MetallicWhite
                    )
                    if (formState.lastSavedSummary != null) {
                        Text(
                            text = formState.lastSavedSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonVoltGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTabButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) NeonVoltGreen else ObsidianSurface,
        border = BorderStroke(1.dp, if (selected) NeonVoltGreen else GlassBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) DeepCharcoal else BrushedSilver
            )
        }
    }
}

@Composable
private fun StepPill(
    stepNum: Int,
    title: String,
    active: Boolean,
    completed: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = when {
        active -> NeonVoltGreen
        completed -> NeonGreenGlow
        else -> ElevatedSteel
    }
    val border = when {
        active || completed -> NeonVoltGreen
        else -> GlassBorder
    }
    val textColor = when {
        active -> DeepCharcoal
        completed -> NeonVoltGreen
        else -> BrushedSilver
    }
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$stepNum. $title",
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}

@Composable
private fun NathTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonVoltGreen,
            unfocusedBorderColor = GlassBorder,
            focusedLabelColor = NeonVoltGreen,
            unfocusedLabelColor = BrushedSilver,
            focusedTextColor = MetallicWhite,
            unfocusedTextColor = MetallicWhite,
            cursorColor = NeonVoltGreen
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRequestsBottomSheet(
    quotesState: UiState<List<QuoteRequest>>,
    bookingsState: UiState<List<ServiceBooking>>,
    onDismiss: () -> Unit,
    onCancelQuote: (String) -> Unit,
    onDeleteQuote: (String) -> Unit,
    onCancelBooking: (String) -> Unit,
    onDeleteBooking: (String) -> Unit,
    onRescheduleBooking: (String, String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        contentColor = MetallicWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MY SAVED QUOTES & BOOKINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonVoltGreen
                    )
                    Text(
                        text = "Synced Live with Nath Battery",
                        style = MaterialTheme.typography.titleLarge,
                        color = MetallicWhite
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = BrushedSilver)
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Battery Quote Requests",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonVoltGreen
                    )
                }

                when (quotesState) {
                    is UiState.Loading -> item {
                        CircularProgressIndicator(color = NeonVoltGreen, modifier = Modifier.size(24.dp))
                    }
                    is UiState.Error -> item {
                        Text(quotesState.message, color = ErrorCoral, style = MaterialTheme.typography.bodyMedium)
                    }
                    is UiState.Success -> {
                        if (quotesState.data.isEmpty()) {
                            item {
                                Text(
                                    text = "No quote requests saved yet. Use the 3-step Battery Finder to save your first quote.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrushedSilver
                                )
                            }
                        } else {
                            items(quotesState.data, key = { "q_${it.id}" }) { quote ->
                                val status = QuoteStatus.fromRaw(quote.status)
                                SavedRecordCard(
                                    title = quote.selectedBatteryModel,
                                    subtitle = "${quote.vehicleOrSystem} • ₹${quote.estimatedPriceInr} • ${quote.locationArea}",
                                    statusLabel = status.label,
                                    isActive = status == QuoteStatus.PENDING || status == QuoteStatus.CONFIRMED,
                                    onCancel = { onCancelQuote(quote.id) },
                                    onDelete = { onDeleteQuote(quote.id) }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider(color = GlassBorder)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Free Checkup & Service Bookings",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonVoltGreen
                    )
                }

                when (bookingsState) {
                    is UiState.Loading -> item {
                        CircularProgressIndicator(color = NeonVoltGreen, modifier = Modifier.size(24.dp))
                    }
                    is UiState.Error -> item {
                        Text(bookingsState.message, color = ErrorCoral, style = MaterialTheme.typography.bodyMedium)
                    }
                    is UiState.Success -> {
                        if (bookingsState.data.isEmpty()) {
                            item {
                                Text(
                                    text = "No service checkups booked yet. Tap 'Book Free Check' anytime.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrushedSilver
                                )
                            }
                        } else {
                            items(bookingsState.data, key = { "b_${it.id}" }) { booking ->
                                val status = BookingStatus.fromRaw(booking.status)
                                val nextSlot = SERVICE_SLOTS.firstOrNull { it != booking.preferredSlot }
                                    ?: SERVICE_SLOTS.first()
                                SavedRecordCard(
                                    title = ServiceType.fromRaw(booking.serviceType).label,
                                    subtitle = "${booking.vehicleDetails} • ${booking.preferredSlot}",
                                    statusLabel = status.label,
                                    isActive = status == BookingStatus.SCHEDULED || status == BookingStatus.CONFIRMED,
                                    extraActionLabel = if (status == BookingStatus.SCHEDULED) "Switch Slot" else null,
                                    onExtraAction = { onRescheduleBooking(booking.id, nextSlot) },
                                    onCancel = { onCancelBooking(booking.id) },
                                    onDelete = { onDeleteBooking(booking.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedRecordCard(
    title: String,
    subtitle: String,
    statusLabel: String,
    isActive: Boolean,
    extraActionLabel: String? = null,
    onExtraAction: (() -> Unit)? = null,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ElevatedSteel,
        border = BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MetallicWhite,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isActive) NeonGreenGlow else DeepCharcoal
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive) NeonVoltGreen else AmberGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = BrushedSilver
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (extraActionLabel != null && onExtraAction != null) {
                    TextButton(onClick = onExtraAction) {
                        Text(extraActionLabel, color = NeonVoltGreen)
                    }
                }
                if (isActive) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = AmberGold)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete record",
                        tint = ErrorCoral
                    )
                }
            }
        }
    }
}
