package com.example.data

import com.example.model.GazetteNotification
import com.example.model.LandParcel
import com.example.model.OverallStats
import com.example.model.VillageSummary

object GazetteRepository {

    val notifications: List<GazetteNotification> by lazy {
        listOf(
            Gazette1Data.getNotification(),
            Gazette2Data.getNotification(),
            Gazette3Data.getNotification()
        )
    }

    val allVillageSummaries: List<VillageSummary> by lazy {
        notifications.flatMap { it.villageSummaries }
    }

    val allParcels: List<LandParcel> by lazy {
        notifications.flatMap { it.parcels }
    }

    fun getNotificationById(id: String): GazetteNotification? {
        return notifications.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    fun getOverallStats(): OverallStats {
        val totalGazettes = notifications.size
        val totalVillages = allVillageSummaries.size
        val totalParcels = allParcels.size

        // Total Land = (22-31.06) + (11-07.04) + (16-12.00) = 50 Acres 10 Guntas 10 Annas
        val totalAcres = 50
        val totalGuntas = 10
        val totalAnnas = 10

        val talukBreakdown = mapOf(
            "ಮಾಲೂರು (Malur)" to "38 ಎಕರೆ 43 ಗುಂಟೆ 06 ಆಣೆ (30 ಗ್ರಾಮಗಳು)",
            "ಕೋಲಾರ (Kolar)" to "11 ಎಕರೆ 07 ಗುಂಟೆ 04 ಆಣೆ (10 ಗ್ರಾಮಗಳು)"
        )

        val hobliBreakdown = mapOf(
            "ಕಸಬಾ (Kasaba)" to "31 ಗ್ರಾಮಗಳು / 30-22.14",
            "ಲಕ್ಕೂರು (Lakkuru)" to "9 ಗ್ರಾಮಗಳು / 08-20.12",
            "ವೇಮಗಲ್ಲು (Vemagal)" to "5 ಗ್ರಾಮಗಳು / 04-27.04",
            "ನರಸಾಪುರ (Narasapura)" to "5 ಗ್ರಾಮಗಳು / 06-19.16"
        )

        val landTypeBreakdown = mapOf(
            "ಖುಷ್ಕಿ (Dry Land)" to 24,
            "ಭೂ.ಪ (Converted Land)" to 11,
            "ಸರ್ಕಾರಿ (Govt Land)" to 7,
            "ಇತರ (Public / Kharab)" to 3
        )

        return OverallStats(
            totalGazettesCount = totalGazettes,
            totalVillagesCount = totalVillages,
            totalParcelsCount = totalParcels,
            totalAcres = totalAcres,
            totalGuntas = totalGuntas,
            totalAnnas = totalAnnas,
            totalAreaFormatted = "50 ಎಕರೆ 10 ಗುಂಟೆ 10 ಆಣೆ (~50.26 Acres)",
            talukBreakdown = talukBreakdown,
            hobliBreakdown = hobliBreakdown,
            landTypeBreakdown = landTypeBreakdown
        )
    }

    fun searchParcels(
        query: String,
        filterTaluk: String? = null,
        filterHobli: String? = null,
        filterLandType: String? = null,
        filterGazette: String? = null
    ): List<LandParcel> {
        val q = query.trim().lowercase()

        return allParcels.filter { parcel ->
            val matchesQuery = if (q.isEmpty()) true else {
                parcel.khatedarNameKn.lowercase().contains(q) ||
                parcel.khatedarNameEn.lowercase().contains(q) ||
                parcel.surveyNo.lowercase().contains(q) ||
                parcel.hissaNo.lowercase().contains(q) ||
                parcel.villageNameKn.lowercase().contains(q) ||
                parcel.villageNameEn.lowercase().contains(q) ||
                parcel.hobliKn.lowercase().contains(q) ||
                parcel.hobliEn.lowercase().contains(q) ||
                parcel.talukKn.lowercase().contains(q) ||
                parcel.talukEn.lowercase().contains(q) ||
                parcel.gazetteId.lowercase().contains(q) ||
                parcel.chakkubandiEast.lowercase().contains(q) ||
                parcel.chakkubandiWest.lowercase().contains(q) ||
                parcel.chakkubandiNorth.lowercase().contains(q) ||
                parcel.chakkubandiSouth.lowercase().contains(q)
            }

            val matchesTaluk = filterTaluk == null || filterTaluk == "All" ||
                    parcel.talukKn.contains(filterTaluk, ignoreCase = true) ||
                    parcel.talukEn.contains(filterTaluk, ignoreCase = true)

            val matchesHobli = filterHobli == null || filterHobli == "All" ||
                    parcel.hobliKn.contains(filterHobli, ignoreCase = true) ||
                    parcel.hobliEn.contains(filterHobli, ignoreCase = true)

            val matchesLandType = filterLandType == null || filterLandType == "All" ||
                    parcel.landType.contains(filterLandType, ignoreCase = true)

            val matchesGazette = filterGazette == null || filterGazette == "All" ||
                    parcel.gazetteId.equals(filterGazette, ignoreCase = true)

            matchesQuery && matchesTaluk && matchesHobli && matchesLandType && matchesGazette
        }
    }

    fun searchVillages(query: String, filterTaluk: String? = null): List<VillageSummary> {
        val q = query.trim().lowercase()
        return allVillageSummaries.filter { village ->
            val matchesQuery = if (q.isEmpty()) true else {
                village.villageNameKn.lowercase().contains(q) ||
                village.villageNameEn.lowercase().contains(q) ||
                village.hobliKn.lowercase().contains(q) ||
                village.hobliEn.lowercase().contains(q) ||
                village.talukKn.lowercase().contains(q) ||
                village.talukEn.lowercase().contains(q) ||
                village.gazetteId.lowercase().contains(q)
            }

            val matchesTaluk = filterTaluk == null || filterTaluk == "All" ||
                    village.talukKn.contains(filterTaluk, ignoreCase = true) ||
                    village.talukEn.contains(filterTaluk, ignoreCase = true)

            matchesQuery && matchesTaluk
        }
    }
}
