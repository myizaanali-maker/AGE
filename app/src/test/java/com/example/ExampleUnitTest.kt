package com.example

import com.example.data.GazetteRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGazetteNotificationsLoaded() {
        val notifications = GazetteRepository.notifications
        assertEquals(3, notifications.size)

        val ids = notifications.map { it.id }
        assertTrue(ids.contains("PR-1035"))
        assertTrue(ids.contains("PR-1184"))
        assertTrue(ids.contains("PR-997"))
    }

    @Test
    fun testOverallStats() {
        val stats = GazetteRepository.getOverallStats()
        assertEquals(3, stats.totalGazettesCount)
        assertEquals(40, stats.totalVillagesCount)
        assertEquals(50, stats.totalAcres)
    }

    @Test
    fun testSearchParcelsBySurveyNumber() {
        val results = GazetteRepository.searchParcels(query = "76")
        assertTrue("Should find survey 76 parcels", results.isNotEmpty())
        assertTrue(results.any { it.surveyNo == "76" })
    }

    @Test
    fun testSearchParcelsByName() {
        val results = GazetteRepository.searchParcels(query = "Doddappayya")
        assertTrue(results.isNotEmpty())
        assertTrue(results.first().khatedarNameKn.contains("ದೊಡ್ಡಪ್ಪಯ್ಯ"))
    }

    @Test
    fun testSearchParcelsByTalukFilter() {
        val kolarResults = GazetteRepository.searchParcels(query = "", filterTaluk = "Kolar")
        assertTrue(kolarResults.isNotEmpty())
        assertTrue(kolarResults.all { it.talukKn.contains("ಕೋಲಾರ") || it.talukEn.contains("Kolar") })
    }
}
