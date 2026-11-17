package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.signOut
import com.example.data.repository.NathBatteryRepository
import com.example.ui.components.AdminPanelScreen
import com.example.ui.components.BatteryFinderAndBookingSection
import com.example.ui.components.ContactAndLocationFooter
import com.example.ui.components.HeroSection
import com.example.ui.components.ProductShowcaseSection
import com.example.ui.components.ReviewsTrustSection
import com.example.ui.components.SavedRequestsBottomSheet
import com.example.ui.components.ServicesBentoGridSection
import com.example.ui.components.StickyMobileCallWhatsAppBar
import com.example.ui.components.StickyTopNavigationBar
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.NathBatteryTheme
import com.example.ui.viewmodel.FinderMode
import com.example.ui.viewmodel.NathBatteryViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

private const val NATH_BATTERY_PHONE = "+919850638290"
private const val NATH_BATTERY_WHATSAPP = "919850638290"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NathBatteryTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            NathBatteryDealershipScreen(
                currentUserId = user.uid,
                userEmail = user.email.orEmpty(),
                userDisplayName = user.displayName.orEmpty()
            )
        }
    }
}

@Composable
fun NathBatteryDealershipScreen(
    currentUserId: String,
    userEmail: String,
    userDisplayName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: NathBatteryViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                NathBatteryViewModel(
                    repository = NathBatteryRepository(db),
                    currentUserId = currentUserId,
                    userEmail = userEmail,
                    defaultCustomerName = userDisplayName
                )
            }
        }
    )

    val selectedCatalogCategory by viewModel.selectedCatalogCategory.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val quotesState by viewModel.quoteRequestsState.collectAsStateWithLifecycle()
    val bookingsState by viewModel.serviceBookingsState.collectAsStateWithLifecycle()
    val adminQuotesState by viewModel.adminQuotesState.collectAsStateWithLifecycle()
    val adminBookingsState by viewModel.adminBookingsState.collectAsStateWithLifecycle()
    val adminSearchQuery by viewModel.adminSearchQuery.collectAsStateWithLifecycle()
    val adminActionMessage by viewModel.adminActionMessage.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var showSavedSheet by remember { mutableStateOf(false) }
    var showAdminPanel by remember { mutableStateOf(false) }

    if (showSavedSheet) {
        BackHandler { showSavedSheet = false }
    }

    if (showAdminPanel && viewModel.isOwnerAdmin) {
        AdminPanelScreen(
            quotesState = adminQuotesState,
            bookingsState = adminBookingsState,
            searchQuery = adminSearchQuery,
            actionMessage = adminActionMessage,
            onSearchChange = viewModel::updateAdminSearch,
            onDismissMessage = viewModel::clearAdminMessage,
            onBack = { showAdminPanel = false },
            onCallCustomer = { phone -> dialPhoneNumber(context, phone) },
            onWhatsAppCustomer = { phone, msg -> openWhatsAppChat(context, phone, msg) },
            onUpdateQuote = viewModel::adminUpdateQuote,
            onDeleteQuote = viewModel::adminDeleteQuote,
            onUpdateBookingStatus = viewModel::adminUpdateBookingStatus,
            onDeleteBooking = viewModel::adminDeleteBooking
        )
        return
    }

    val scrollProgress by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 900f).coerceIn(0f, 1f)
        }
    }

    val savedCount = remember(quotesState, bookingsState) {
        val qCount = (quotesState as? UiState.Success)?.data?.size ?: 0
        val bCount = (bookingsState as? UiState.Success)?.data?.size ?: 0
        qCount + bCount
    }

    fun scrollToSection(index: Int) {
        scope.launch {
            listState.animateScrollToItem(index)
        }
    }

    Scaffold(
        containerColor = DeepCharcoal,
        topBar = {
            StickyTopNavigationBar(
                savedRequestsCount = savedCount,
                isOwnerAdmin = viewModel.isOwnerAdmin,
                onNavClick = { section ->
                    when (section) {
                        "Home" -> scrollToSection(0)
                        "Products" -> scrollToSection(1)
                        "Services" -> scrollToSection(2)
                        "Finder" -> scrollToSection(3)
                        "Reviews" -> scrollToSection(4)
                        "Contact" -> scrollToSection(5)
                    }
                },
                onBookFreeCheckClick = {
                    viewModel.switchFinderMode(FinderMode.FREE_CHECKUP_BOOKING, "FREE_HEALTH_CHECK")
                    scrollToSection(3)
                },
                onOpenSavedSheet = { showSavedSheet = true },
                onOpenAdminClick = { showAdminPanel = true },
                onSignOutClick = {
                    signOut(credentialManager = credentialManager, scope = scope)
                }
            )
        },
        bottomBar = {
            StickyMobileCallWhatsAppBar(
                onCallClick = { dialPhoneNumber(context, NATH_BATTERY_PHONE) },
                onWhatsAppClick = {
                    openWhatsAppChat(context, NATH_BATTERY_WHATSAPP, viewModel.buildWhatsAppMessage())
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepCharcoal, Color(0xFF0E1525), DeepCharcoal)
                    )
                )
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item(key = "hero") {
                    HeroSection(
                        scrollProgress = scrollProgress,
                        onGetInstantQuoteClick = {
                            viewModel.switchFinderMode(FinderMode.BATTERY_QUOTE)
                            scrollToSection(3)
                        },
                        onCallNowClick = { dialPhoneNumber(context, NATH_BATTERY_PHONE) }
                    )
                }

                item(key = "products") {
                    ProductShowcaseSection(
                        selectedCategory = selectedCatalogCategory,
                        onSelectCategory = viewModel::selectCatalogCategory,
                        onEnquireProduct = { product ->
                            viewModel.prefillQuoteFromProduct(product)
                            scrollToSection(3)
                        }
                    )
                }

                item(key = "services") {
                    ServicesBentoGridSection(
                        onBookServiceClick = { service ->
                            viewModel.switchFinderMode(
                                mode = FinderMode.FREE_CHECKUP_BOOKING,
                                serviceTypeKey = service.serviceTypeKey
                            )
                            scrollToSection(3)
                        }
                    )
                }

                item(key = "finder") {
                    BatteryFinderAndBookingSection(
                        formState = formState,
                        viewModel = viewModel,
                        onSendWhatsApp = {
                            openWhatsAppChat(context, NATH_BATTERY_WHATSAPP, viewModel.buildWhatsAppMessage())
                        }
                    )
                }

                item(key = "reviews") {
                    ReviewsTrustSection()
                }

                item(key = "contact") {
                    ContactAndLocationFooter(
                        onOpenMapClick = { openKaneriChowkMap(context) },
                        onCallClick = { dialPhoneNumber(context, NATH_BATTERY_PHONE) },
                        onWhatsAppClick = {
                            openWhatsAppChat(context, NATH_BATTERY_WHATSAPP, viewModel.buildWhatsAppMessage())
                        }
                    )
                }
            }
        }
    }

    if (showSavedSheet) {
        SavedRequestsBottomSheet(
            quotesState = quotesState,
            bookingsState = bookingsState,
            onDismiss = { showSavedSheet = false },
            onCancelQuote = viewModel::cancelQuote,
            onDeleteQuote = viewModel::deleteQuote,
            onCancelBooking = viewModel::cancelBooking,
            onDeleteBooking = viewModel::deleteBooking,
            onRescheduleBooking = viewModel::rescheduleBooking
        )
    }
}

private fun dialPhoneNumber(context: Context, phone: String) {
    val sanitized = phone.filter { it.isDigit() || it == '+' }
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$sanitized"))
    runCatching { context.startActivity(intent) }
}

private fun openWhatsAppChat(context: Context, rawPhone: String, message: String) {
    val digits = rawPhone.filter { it.isDigit() }
    val waNumber = when {
        digits.length == 10 -> "91$digits"
        else -> digits
    }
    val encoded = Uri.encode(message)
    val uri = Uri.parse("https://wa.me/$waNumber?text=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    runCatching { context.startActivity(intent) }
}

private fun openKaneriChowkMap(context: Context) {
    val geoUri = Uri.parse(
        "geo:18.4088,76.5604?q=" + Uri.encode("Nath Battery, Kaneri Chowk, Nanded Ring Road, Latur, Maharashtra 413531")
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
    val launched = runCatching { context.startActivity(mapIntent) }.isSuccess
    if (!launched) {
        val webUri = Uri.parse(
            "https://maps.google.com/?q=" + Uri.encode("Nath Battery, Kaneri Chowk, Nanded Ring Road, Latur, Maharashtra 413531")
        )
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, webUri)) }
    }
}
