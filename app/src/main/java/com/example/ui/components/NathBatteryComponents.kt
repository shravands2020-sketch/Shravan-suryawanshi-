package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.BatteryCategory
import com.example.data.model.BatteryProduct
import com.example.data.model.LocalReview
import com.example.data.model.NathBatteryCatalog
import com.example.data.model.ServiceOffering
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElevatedSteel
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MetallicWhite
import com.example.ui.theme.MutedTitanium
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonVoltGreen
import com.example.ui.theme.ObsidianSurface

@Composable
fun StickyTopNavigationBar(
    savedRequestsCount: Int,
    isOwnerAdmin: Boolean = false,
    onNavClick: (String) -> Unit,
    onBookFreeCheckClick: () -> Unit,
    onOpenSavedSheet: () -> Unit,
    onOpenAdminClick: () -> Unit = {},
    onSignOutClick: () -> Unit
) {
    Surface(
        color = DeepCharcoal.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonGreenGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = "Nath Battery Logo",
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NATH BATTERY",
                            style = MaterialTheme.typography.titleMedium,
                            color = MetallicWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "EST. 2005 • LATUR",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonVoltGreen,
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isOwnerAdmin) {
                        IconButton(
                            onClick = onOpenAdminClick,
                            modifier = Modifier.testTag("open_admin_panel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = "Open Owner Admin Panel",
                                tint = NeonVoltGreen
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSavedSheet,
                        modifier = Modifier.testTag("open_saved_requests_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (savedRequestsCount > 0) {
                                    Badge(containerColor = NeonVoltGreen, contentColor = DeepCharcoal) {
                                        Text(savedRequestsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkAdded,
                                contentDescription = "My Quotes & Bookings",
                                tint = MetallicWhite
                            )
                        }
                    }

                    Button(
                        onClick = onBookFreeCheckClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonVoltGreen,
                            contentColor = DeepCharcoal
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("nav_book_check_button")
                    ) {
                        Text(
                            text = "Book Free Check",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onSignOutClick,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Sign out",
                            tint = BrushedSilver
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Home", "Products", "Services", "Finder", "Reviews", "Contact").forEach { section ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElevatedSteel.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavClick(section) }
                            .testTag("nav_chip_${section.lowercase()}")
                    ) {
                        Text(
                            text = section,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MetallicWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSection(
    scrollProgress: Float,
    onGetInstantQuoteClick: () -> Unit,
    onCallNowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = NeonGreenGlow,
            border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = NeonVoltGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "AUTHORIZED AMARON DEALER • KANERI CHOWK",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonVoltGreen
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Powering Latur’s Vehicles & Homes Since 2005.",
            style = MaterialTheme.typography.displayLarge,
            color = MetallicWhite
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Authorized Amaron Battery Dealer & Multi-Brand Power Solutions at Kaneri Chowk, Ring Road, Latur. Fast doorstep testing and installation.",
            style = MaterialTheme.typography.bodyLarge,
            color = BrushedSilver
        )

        Spacer(Modifier.height(18.dp))

        Interactive3DBatteryShowcase(scrollProgress = scrollProgress)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onGetInstantQuoteClick,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("hero_instant_quote_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonVoltGreen,
                    contentColor = DeepCharcoal
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Get Instant Quote",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }

            OutlinedButton(
                onClick = onCallNowClick,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("hero_call_now_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, NeonVoltGreen.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicWhite)
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    tint = NeonVoltGreen,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Call Now",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ProductShowcaseSection(
    selectedCategory: BatteryCategory,
    onSelectCategory: (BatteryCategory) -> Unit,
    onEnquireProduct: (BatteryProduct) -> Unit
) {
    val products = NathBatteryCatalog.products.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "INTERACTIVE PRODUCT SHOWCASE",
                style = MaterialTheme.typography.labelSmall,
                color = NeonVoltGreen
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Genuine Batteries for Every Drive & Home",
                style = MaterialTheme.typography.headlineLarge,
                color = MetallicWhite
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BatteryCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NeonVoltGreen else ObsidianSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) NeonVoltGreen else GlassBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectCategory(category) }
                        .testTag("category_tab_${category.ruleKey.lowercase()}")
                ) {
                    Text(
                        text = category.tabLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) DeepCharcoal else BrushedSilver,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        if (selectedCategory == BatteryCategory.INVERTER) {
            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_inverter_power_1794915405598),
                        contentDescription = "Tall tubular inverter battery and pure sine wave UPS",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LATUR POWER BACKUP SPECIALISTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonVoltGreen
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Free Home Load Audit & Clean Copper Wiring",
                            style = MaterialTheme.typography.titleMedium,
                            color = MetallicWhite
                        )
                        Text(
                            text = "Up to ₹3,500 scrap buyback on old tubular batteries.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrushedSilver
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                Product3DCard(
                    product = product,
                    onEnquireClick = { onEnquireProduct(product) }
                )
            }
        }
    }
}

@Composable
private fun Product3DCard(
    product: BatteryProduct,
    onEnquireClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(284.dp)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonGreenGlow,
                    border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = product.warrantyBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonVoltGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrushedSilver
                )
            }

            Spacer(Modifier.height(12.dp))

            // 3D-styled mini battery preview tile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ElevatedSteel, DeepCharcoal, ElevatedSteel)
                        )
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = product.capacityAh,
                            style = MaterialTheme.typography.displayMedium,
                            color = NeonVoltGreen
                        )
                        Text(
                            text = "${product.voltage} • ${product.crankingPower}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrushedSilver
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = NeonVoltGreen.copy(alpha = 0.85f),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                color = MetallicWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Fits: ${product.compatibleWith}",
                style = MaterialTheme.typography.bodyMedium,
                color = BrushedSilver,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.highlightTag,
                style = MaterialTheme.typography.labelSmall,
                color = AmberGold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "₹${product.priceInr - product.exchangeBonusInr}*",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MetallicWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "with ₹${product.exchangeBonusInr} old exchange",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonVoltGreen
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onEnquireClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("enquire_${product.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonVoltGreen,
                    contentColor = DeepCharcoal
                )
            ) {
                Text(
                    text = "Enquire Price",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun ServicesBentoGridSection(
    onBookServiceClick: (ServiceOffering) -> Unit
) {
    val services = NathBatteryCatalog.services
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "SERVICES & WHY CHOOSE US",
            style = MaterialTheme.typography.labelSmall,
            color = NeonVoltGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "20+ Years Powering Latur",
            style = MaterialTheme.typography.headlineLarge,
            color = MetallicWhite
        )

        Spacer(Modifier.height(14.dp))

        // Dealership Showroom Hero Bento Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_dealership_banner_1794915405422),
                    contentDescription = "Nath Battery showroom and diagnostic bay in Latur",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DeepCharcoal.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonVoltGreen
                    ) {
                        Text(
                            text = "KANERI CHOWK FLAGSHIP BAY",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepCharcoal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Instant Load Testing • Genuine Amaron Stock • Doorstep Fleet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MetallicWhite
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 2x2 Bento Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoServiceCard(
                    service = services[0],
                    icon = Icons.Filled.Build,
                    modifier = Modifier.weight(1f),
                    onClick = { onBookServiceClick(services[0]) }
                )
                BentoServiceCard(
                    service = services[1],
                    icon = Icons.Filled.LocalShipping,
                    modifier = Modifier.weight(1f),
                    onClick = { onBookServiceClick(services[1]) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoServiceCard(
                    service = services[2],
                    icon = Icons.Filled.ElectricCar,
                    modifier = Modifier.weight(1f),
                    onClick = { onBookServiceClick(services[2]) }
                )
                BentoServiceCard(
                    service = services[3],
                    icon = Icons.Filled.Shield,
                    modifier = Modifier.weight(1f),
                    onClick = { onBookServiceClick(services[3]) }
                )
            }
        }
    }
}

@Composable
private fun BentoServiceCard(
    service: ServiceOffering,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("bento_service_${service.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonGreenGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NeonVoltGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = service.turnaround,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonVoltGreen
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = service.badge,
                style = MaterialTheme.typography.labelSmall,
                color = AmberGold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = service.title,
                style = MaterialTheme.typography.titleMedium,
                color = MetallicWhite
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = service.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = BrushedSilver
            )
        }
    }
}

@Composable
fun ReviewsTrustSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CUSTOMER REVIEWS & TRUST",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonVoltGreen
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Trusted Across Latur",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MetallicWhite
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ObsidianSurface,
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "4.6 ★",
                        style = MaterialTheme.typography.titleMedium,
                        color = MetallicWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(NathBatteryCatalog.reviews, key = { it.id }) { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
private fun ReviewCard(review: LocalReview) {
    Card(
        modifier = Modifier.width(292.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = review.rating.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MetallicWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonGreenGlow
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = review.verifiedTag,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonVoltGreen
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "“${review.comment}”",
                style = MaterialTheme.typography.bodyMedium,
                color = MetallicWhite
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = review.customerName,
                style = MaterialTheme.typography.titleMedium,
                color = MetallicWhite
            )
            Text(
                text = "${review.areaInLatur} • ${review.vehicleOrBattery}",
                style = MaterialTheme.typography.bodyMedium,
                color = BrushedSilver
            )
        }
    }
}

@Composable
fun ContactAndLocationFooter(
    onOpenMapClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "VISIT OR CONTACT US",
            style = MaterialTheme.typography.labelSmall,
            color = NeonVoltGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Kaneri Chowk, Ring Road, Latur",
            style = MaterialTheme.typography.headlineLarge,
            color = MetallicWhite
        )

        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Interactive custom radar map pin graphic for Kaneri Chowk, Latur
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0E1525))
                        .clickable(onClick = onOpenMapClick)
                        .testTag("map_pin_card"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridStep = 28.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = Color(0xFF1D293E),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                            x += gridStep
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color(0xFF1D293E),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += gridStep
                        }

                        // Nanded Ring Road diagonal highway representation
                        drawLine(
                            color = NeonVoltGreen.copy(alpha = 0.45f),
                            start = Offset(0f, size.height * 0.72f),
                            end = Offset(size.width, size.height * 0.25f),
                            strokeWidth = 6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f))
                        )

                        val pinCenter = Offset(size.width * 0.5f, size.height * 0.48f)
                        drawCircle(
                            color = NeonVoltGreen.copy(alpha = 0.16f),
                            radius = 52.dp.toPx(),
                            center = pinCenter
                        )
                        drawCircle(
                            color = NeonVoltGreen.copy(alpha = 0.28f),
                            radius = 30.dp.toPx(),
                            center = pinCenter
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = NeonVoltGreen,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Nath Battery Pin",
                                    tint = DeepCharcoal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DeepCharcoal.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "Nath Battery • Kaneri Chowk, Ring Road",
                                style = MaterialTheme.typography.labelLarge,
                                color = MetallicWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Nath Battery",
                    style = MaterialTheme.typography.titleLarge,
                    color = MetallicWhite
                )
                Spacer(Modifier.height(6.dp))

                InfoRow(
                    icon = Icons.Filled.LocationOn,
                    text = "Jadhav Complex / Patel Nagar, Near Kaneri Chowk, Nanded Ring Road, Latur, Maharashtra 413531"
                )
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Filled.Schedule,
                    text = "Open Daily: 9:30 AM – 8:00 PM"
                )
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Filled.Call,
                    text = "Phone / WhatsApp: +91 98506 38290"
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenMapClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_google_maps_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElevatedSteel,
                            contentColor = MetallicWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Directions,
                            contentDescription = null,
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Open Map Pin", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = onCallClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = null,
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Call Store", style = MaterialTheme.typography.labelLarge, color = MetallicWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonVoltGreen,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(17.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = BrushedSilver
        )
    }
}

@Composable
fun StickyMobileCallWhatsAppBar(
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Surface(
        color = DeepCharcoal.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.35f)),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCallClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("sticky_call_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.2.dp, GlassBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ObsidianSurface,
                    contentColor = MetallicWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    tint = NeonVoltGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Call Now",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = onWhatsAppClick,
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("sticky_whatsapp_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonVoltGreen,
                    contentColor = DeepCharcoal
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "WhatsApp Quote",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
