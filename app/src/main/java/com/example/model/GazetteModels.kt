package com.example.model

data class GazetteNotification(
    val id: String,                         // e.g. "PR-1035", "PR-1184", "PR-997"
    val gazetteNumber: String,              // e.g. "ಕರ್ನಾಟಕ ರಾಜ್ಯಪತ್ರ, ಭಾಗ ೩"
    val issueDateKn: String,                // "03 ಆಗಸ್ಟ್ 2026"
    val issueDateEn: String,                // "03 August 2026"
    val weekdayKn: String,                  // "ಸೋಮವಾರ"
    val weekdayEn: String,                  // "Monday"
    val pageRange: String,                  // "8236 - 8327"
    val titleKn: String,
    val titleEn: String,
    val departmentKn: String,
    val departmentEn: String,
    val legalActKn: String,
    val legalActEn: String,
    val districtKn: String,
    val districtEn: String,
    val taluks: List<String>,
    val hoblis: List<String>,
    val totalAcres: Int,
    val totalGuntas: Int,
    val totalAnnas: Int,
    val totalLandFormatted: String,
    val inamExtFormatted: String,
    val kharabExtFormatted: String,
    val signatoryKn: String,
    val signatoryEn: String,
    val projectDirectorKn: String,
    val projectDirectorEn: String,
    val summaryKn: String,
    val summaryEn: String,
    val keyHighlights: List<String>,
    val villageSummaries: List<VillageSummary>,
    val parcels: List<LandParcel>
) {
    val totalVillages: Int get() = villageSummaries.size
    val totalParcelsCount: Int get() = parcels.size
}

data class VillageSummary(
    val slNo: Int,
    val villageNameKn: String,
    val villageNameEn: String,
    val hobliKn: String,
    val hobliEn: String,
    val talukKn: String,
    val talukEn: String,
    val inamArea: String,
    val kharabArea: String,
    val totalArea: String,
    val parcelCount: Int,
    val gazetteId: String
)

data class LandParcel(
    val id: String,
    val gazetteId: String,
    val slNo: Int,
    val villageNameKn: String,
    val villageNameEn: String,
    val hobliKn: String,
    val hobliEn: String,
    val talukKn: String,
    val talukEn: String,
    val khatedarNameKn: String,
    val khatedarNameEn: String,
    val surveyNo: String,
    val hissaNo: String,
    val landType: String,
    val totalExtent: String,
    val kharabExtent: String,
    val acquiredExtent: String,
    val assessmentAkar: String,
    val chakkubandiEast: String,
    val chakkubandiWest: String,
    val chakkubandiNorth: String,
    val chakkubandiSouth: String,
    val remarks: String = "",
    val ainuExtent: String = "",
    val acquiredKharab: String = "-",
    val acquiredAinu: String = "",
    val acquiredAkar: String = "-"
) {
    val displayAinuExtent: String
        get() = if (ainuExtent.isNotBlank()) ainuExtent else totalExtent

    val displayAcquiredAinu: String
        get() = if (acquiredAinu.isNotBlank()) acquiredAinu else acquiredExtent
}

data class OverallStats(
    val totalGazettesCount: Int,
    val totalVillagesCount: Int,
    val totalParcelsCount: Int,
    val totalAcres: Int,
    val totalGuntas: Int,
    val totalAnnas: Int,
    val totalAreaFormatted: String,
    val talukBreakdown: Map<String, String>,
    val hobliBreakdown: Map<String, String>,
    val landTypeBreakdown: Map<String, Int>
)
