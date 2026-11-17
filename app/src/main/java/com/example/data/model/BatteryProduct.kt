package com.example.data.model

enum class BatteryCategory(
    val tabLabel: String,
    val ruleKey: String,
    val popularVehicles: List<String>
) {
    CAR(
        tabLabel = "Car Batteries",
        ruleKey = "CAR",
        popularVehicles = listOf(
            "Maruti Swift / Baleno / Dzire",
            "Hyundai Creta / i20 / Venue",
            "Tata Nexon / Harrier / Punch",
            "Mahindra Scorpio / XUV700 / Bolero",
            "Toyota Innova / Fortuner"
        )
    ),
    TWO_WHEELER(
        tabLabel = "Two-Wheeler",
        ruleKey = "TWO_WHEELER",
        popularVehicles = listOf(
            "Honda Activa 6G / Dio",
            "Hero Splendor+ / HF Deluxe",
            "Bajaj Pulsar 150 / 220",
            "Royal Enfield Classic 350",
            "TVS Jupiter / Apache RTR"
        )
    ),
    INVERTER(
        tabLabel = "Home Inverters & Tubular",
        ruleKey = "INVERTER",
        popularVehicles = listOf(
            "1BHK / Shop Backup (150Ah Single)",
            "2-3BHK Home Backup (150Ah-200Ah)",
            "Duplex / Office (24V Dual Battery)",
            "Pure Sine Wave UPS + Battery Combo",
            "Solar Hybrid Tubular Setup"
        )
    ),
    COMMERCIAL(
        tabLabel = "Commercial & Tractor",
        ruleKey = "COMMERCIAL",
        popularVehicles = listOf(
            "Mahindra / Swaraj / John Deere Tractor",
            "Tata Ace / Ashok Leyland Dost",
            "Eicher / BharatBenz Heavy Truck",
            "Bolero Pickup / Tempo Traveller",
            "DG Set / Agricultural Pump"
        )
    )
}

data class BatteryProduct(
    val id: String,
    val brand: String,
    val name: String,
    val sku: String,
    val category: BatteryCategory,
    val capacityAh: String,
    val crankingPower: String,
    val warrantyBadge: String,
    val priceInr: Long,
    val exchangeBonusInr: Long,
    val highlightTag: String,
    val compatibleWith: String,
    val voltage: String = "12V"
)

data class ServiceOffering(
    val id: String,
    val serviceTypeKey: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val turnaround: String
)

data class LocalReview(
    val id: String,
    val customerName: String,
    val areaInLatur: String,
    val rating: Double,
    val vehicleOrBattery: String,
    val comment: String,
    val verifiedTag: String
)

object NathBatteryCatalog {
    val products: List<BatteryProduct> = listOf(
        // Car Batteries
        BatteryProduct(
            id = "amaron_pro_din50",
            brand = "AMARON",
            name = "Amaron PRO DIN50",
            sku = "AAM-PR-550114042",
            category = BatteryCategory.CAR,
            capacityAh = "50 Ah",
            crankingPower = "420 CCA",
            warrantyBadge = "60-Month Warranty",
            priceInr = 6450L,
            exchangeBonusInr = 950L,
            highlightTag = "Best Seller • Zero Maintenance",
            compatibleWith = "Swift, Baleno, i20, Nexon Petrol"
        ),
        BatteryProduct(
            id = "amaron_flo_42b20l",
            brand = "AMARON",
            name = "Amaron FLO 42B20L",
            sku = "AAM-FL-00042B20L",
            category = BatteryCategory.CAR,
            capacityAh = "35 Ah",
            crankingPower = "330 CCA",
            warrantyBadge = "55-Month Warranty",
            priceInr = 4650L,
            exchangeBonusInr = 750L,
            highlightTag = "High Heat Tolerance",
            compatibleWith = "WagonR, Alto, Celerio, Ignis, Honda Amaze"
        ),
        BatteryProduct(
            id = "amaron_flo_din65",
            brand = "AMARON",
            name = "Amaron FLO DIN65LH",
            sku = "AAM-FL-565106590",
            category = BatteryCategory.CAR,
            capacityAh = "65 Ah",
            crankingPower = "580 CCA",
            warrantyBadge = "55-Month Warranty",
            priceInr = 7950L,
            exchangeBonusInr = 1200L,
            highlightTag = "SUV High-Cranking Spec",
            compatibleWith = "Creta Diesel, Scorpio, Innova, Harrier"
        ),
        // Two-Wheeler Batteries
        BatteryProduct(
            id = "amaron_pro_rider_apbtz4l",
            brand = "AMARON",
            name = "Amaron Pro Rider AP-BTZ4L",
            sku = "ABR-PR-APBTZ4L",
            category = BatteryCategory.TWO_WHEELER,
            capacityAh = "3 Ah",
            crankingPower = "VRLA AGM",
            warrantyBadge = "48-Month Warranty",
            priceInr = 1250L,
            exchangeBonusInr = 150L,
            highlightTag = "Factory Sealed • Spill Proof",
            compatibleWith = "Activa 6G, Splendor+, Jupiter, Dio"
        ),
        BatteryProduct(
            id = "amaron_pro_rider_apbtz5l",
            brand = "AMARON",
            name = "Amaron Pro Rider AP-BTZ5L",
            sku = "ABR-PR-APBTZ5L",
            category = BatteryCategory.TWO_WHEELER,
            capacityAh = "4 Ah",
            crankingPower = "High Cranking",
            warrantyBadge = "48-Month Warranty",
            priceInr = 1490L,
            exchangeBonusInr = 200L,
            highlightTag = "Instant Cold Start",
            compatibleWith = "Pulsar 150, Apache RTR, Shine SP, FZ"
        ),
        BatteryProduct(
            id = "amaron_pro_rider_9ah",
            brand = "AMARON",
            name = "Amaron Pro Rider AP-BTX9",
            sku = "ABR-PR-APBTX9",
            category = BatteryCategory.TWO_WHEELER,
            capacityAh = "9 Ah",
            crankingPower = "135 CCA",
            warrantyBadge = "48-Month Warranty",
            priceInr = 2450L,
            exchangeBonusInr = 350L,
            highlightTag = "Cruiser & Touring Spec",
            compatibleWith = "Royal Enfield Classic 350, Meteor, Pulsar 220"
        ),
        // Home Inverters & Tubular
        BatteryProduct(
            id = "amaron_current_150tt",
            brand = "AMARON",
            name = "Amaron Current Tall Tubular 150Ah",
            sku = "AM-CR-AR150TT48",
            category = BatteryCategory.INVERTER,
            capacityAh = "150 Ah",
            crankingPower = "Deep Cycle",
            warrantyBadge = "60-Month Warranty",
            priceInr = 13800L,
            exchangeBonusInr = 2800L,
            highlightTag = "Long Backup • Fast Recharge",
            compatibleWith = "2-3BHK Homes, Clinics & Shops in Latur"
        ),
        BatteryProduct(
            id = "amaron_tall_tubular_200ah",
            brand = "AMARON",
            name = "Amaron Shield Tall Tubular 200Ah",
            sku = "AM-SH-200TT60",
            category = BatteryCategory.INVERTER,
            capacityAh = "200 Ah",
            crankingPower = "Heavy Backup",
            warrantyBadge = "60-Month Warranty",
            priceInr = 17900L,
            exchangeBonusInr = 3500L,
            highlightTag = "Ultra Low Water Loss",
            compatibleWith = "Large Homes, Offices & Coaching Classes"
        ),
        BatteryProduct(
            id = "amaron_sinewave_combo_1100",
            brand = "AMARON / LUMINOUS",
            name = "1100VA Pure Sine Wave + 150Ah Combo",
            sku = "NB-COMBO-1100TT",
            category = BatteryCategory.INVERTER,
            capacityAh = "150 Ah + 1100VA",
            crankingPower = "Pure Sine",
            warrantyBadge = "36+60 Mo Warranty",
            priceInr = 19200L,
            exchangeBonusInr = 3000L,
            highlightTag = "Complete Home Power Kit",
            compatibleWith = "TV, Wi-Fi, 5 Fans, 6 LED Lights Backup"
        ),
        // Commercial & Tractor
        BatteryProduct(
            id = "amaron_hw_tractor_880",
            brand = "AMARON",
            name = "Amaron Hi-Way 880 Tractor Pro",
            sku = "AAM-HW-000880D26R",
            category = BatteryCategory.COMMERCIAL,
            capacityAh = "88 Ah",
            crankingPower = "680 CCA",
            warrantyBadge = "36-Month Warranty",
            priceInr = 8200L,
            exchangeBonusInr = 1800L,
            highlightTag = "Vibration Resistant Chassis",
            compatibleWith = "Mahindra, Swaraj, John Deere Tractors"
        ),
        BatteryProduct(
            id = "amaron_hw_cv_100ah",
            brand = "AMARON",
            name = "Amaron Hi-Way HCV 100Ah",
            sku = "AAM-HW-001000H29R",
            category = BatteryCategory.COMMERCIAL,
            capacityAh = "100 Ah",
            crankingPower = "740 CCA",
            warrantyBadge = "36-Month Warranty",
            priceInr = 9650L,
            exchangeBonusInr = 2100L,
            highlightTag = "Heavy Fleet Endurance",
            compatibleWith = "Bolero Pickup, Tata Ace Mega, Eicher"
        ),
        BatteryProduct(
            id = "amaron_hw_150ah_truck",
            brand = "AMARON",
            name = "Amaron Hi-Way Heavy 150Ah",
            sku = "AAM-HW-001500N150",
            category = BatteryCategory.COMMERCIAL,
            capacityAh = "150 Ah",
            crankingPower = "920 CCA",
            warrantyBadge = "36-Month Warranty",
            priceInr = 14400L,
            exchangeBonusInr = 3200L,
            highlightTag = "Highway & DG Set Spec",
            compatibleWith = "Heavy Trucks, Buses & 25kVA+ DG Sets"
        )
    )

    val services: List<ServiceOffering> = listOf(
        ServiceOffering(
            id = "free_check",
            serviceTypeKey = "FREE_HEALTH_CHECK",
            title = "Free Battery Health & Alternator Checkup",
            subtitle = "Digital load-tester diagnostics for voltage drop, CCA health & charging current at Kaneri Chowk.",
            badge = "100% FREE",
            turnaround = "10 Mins"
        ),
        ServiceOffering(
            id = "doorstep_jumpstart",
            serviceTypeKey = "DOORSTEP_JUMPSTART",
            title = "Express Doorstep Delivery & Jumpstart",
            subtitle = "Rapid technician dispatch across Kaneri Chowk, Patel Nagar, Ring Road, MIDC & Ausa Road.",
            badge = "25-MIN LATUR DISPATCH",
            turnaround = "Doorstep"
        ),
        ServiceOffering(
            id = "buyback_exchange",
            serviceTypeKey = "HOME_INSTALLATION",
            title = "Old Battery Buyback & Exchange Bonus",
            subtitle = "Transparent scrap-weight valuation deducted instantly from your new genuine Amaron battery.",
            badge = "UP TO ₹3,500 OFF",
            turnaround = "Instant Credit"
        ),
        ServiceOffering(
            id = "alternator_test",
            serviceTypeKey = "ALTERNATOR_TEST",
            title = "20+ Years of Trusted Local Expertise",
            subtitle = "Serving Latur drivers, farmers & homeowners since 2005 with paperless warranty registration.",
            badge = "4.6★ GOOGLE RATED",
            turnaround = "Since 2005"
        )
    )

    val reviews: List<LocalReview> = listOf(
        LocalReview(
            id = "rev_1",
            customerName = "Rajeshwar Deshmukh",
            areaInLatur = "Patel Nagar, Latur",
            rating = 5.0,
            vehicleOrBattery = "Amaron FLO DIN65 • Creta",
            comment = "My car wouldn't start near Ring Road at 7:15 PM. Nath Battery reached within 20 minutes, tested the alternator first, and installed a fresh Amaron battery with great exchange value.",
            verifiedTag = "Verified Local Guide"
        ),
        LocalReview(
            id = "rev_2",
            customerName = "Sneha Kulkarni",
            areaInLatur = "Ausa Road, Latur",
            rating = 4.8,
            vehicleOrBattery = "150Ah Tall Tubular + UPS",
            comment = "Bought a complete home inverter combo. Very genuine advice on wattage calculation, neat wiring installation, and official Amaron warranty SMS registered on the spot.",
            verifiedTag = "Verified Customer"
        ),
        LocalReview(
            id = "rev_3",
            customerName = "Mahesh Shinde",
            areaInLatur = "MIDC, Latur",
            rating = 4.9,
            vehicleOrBattery = "Amaron Hi-Way 880 Tractor",
            comment = "Using Nath Battery for all our commercial pickups and farm tractors since 2012. Honest pricing, fair old-battery scrap rate, and zero hassle during warranty checks.",
            verifiedTag = "Fleet Owner"
        ),
        LocalReview(
            id = "rev_4",
            customerName = "Pratik Jadhav",
            areaInLatur = "Kaneri Chowk, Latur",
            rating = 4.7,
            vehicleOrBattery = "Pro Rider AP-BTZ5L",
            comment = "Quick 5-minute replacement for my Pulsar. They also checked the bike's regulator output for free before fitting the new battery.",
            verifiedTag = "Verified Customer"
        )
    )

    val laturAreas = listOf(
        "Kaneri Chowk / Nanded Ring Road",
        "Patel Nagar / Jadhav Complex",
        "Ausa Road, Latur",
        "MIDC Industrial Area, Latur",
        "Barshi Road / Gandhi Chowk",
        "Shivaji Nagar / Ambajogai Road"
    )
}
