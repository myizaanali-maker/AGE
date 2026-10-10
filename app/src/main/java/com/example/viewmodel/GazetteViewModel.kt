package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GazetteRepository
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteParcelEntity
import com.example.data.local.FavoritesRepository
import com.example.model.GazetteNotification
import com.example.model.LandParcel
import com.example.model.OverallStats
import com.example.model.VillageSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val titleEn: String, val titleKn: String) {
    DASHBOARD("Dashboard", "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್"),
    SEARCH("Search", "ಹುಡುಕಿ"),
    SUMMARIES("Summaries", "ಅಧಿಸೂಚನೆಗಳು"),
    MAP("Map", "ನಕ್ಷೆ"),
    VILLAGES("Villages", "ಗ್ರಾಮಗಳು"),
    FAVORITES("Favorites", "ಮೆಚ್ಚಿನವು")
}

data class UiState(
    val currentTab: AppTab = AppTab.DASHBOARD,
    val isKannada: Boolean = true,
    val notifications: List<GazetteNotification> = emptyList(),
    val overallStats: OverallStats? = null,
    val searchQuery: String = "",
    val selectedTalukFilter: String = "All",
    val selectedHobliFilter: String = "All",
    val selectedLandTypeFilter: String = "All",
    val selectedGazetteFilter: String = "All",
    val filteredParcels: List<LandParcel> = emptyList(),
    val filteredVillages: List<VillageSummary> = emptyList(),
    val selectedGazetteDetail: GazetteNotification? = null,
    val selectedVillageDetail: VillageSummary? = null,
    val selectedParcelDetail: LandParcel? = null,
    val bookmarkedParcelIds: Set<String> = emptySet(),
    val favoriteParcelsList: List<FavoriteParcelEntity> = emptyList()
)

class GazetteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val favoritesRepository = FavoritesRepository(db.favoriteParcelDao())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeFavorites()
    }

    private fun loadInitialData() {
        val notifications = GazetteRepository.notifications
        val stats = GazetteRepository.getOverallStats()
        val parcels = GazetteRepository.allParcels
        val villages = GazetteRepository.allVillageSummaries

        _uiState.update {
            it.copy(
                notifications = notifications,
                overallStats = stats,
                filteredParcels = parcels,
                filteredVillages = villages,
                selectedGazetteDetail = notifications.firstOrNull()
            )
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collect { ids ->
                _uiState.update { it.copy(bookmarkedParcelIds = ids) }
            }
        }

        viewModelScope.launch {
            favoritesRepository.allFavorites.collect { favs ->
                _uiState.update { it.copy(favoriteParcelsList = favs) }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isKannada = !it.isKannada) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onTalukFilterChanged(taluk: String) {
        _uiState.update { it.copy(selectedTalukFilter = taluk) }
        applyFilters()
    }
    fun onTalukFilterChange(taluk: String) = onTalukFilterChanged(taluk)

    fun onHobliFilterChanged(hobli: String) {
        _uiState.update { it.copy(selectedHobliFilter = hobli) }
        applyFilters()
    }
    fun onHobliFilterChange(hobli: String) = onHobliFilterChanged(hobli)

    fun onLandTypeFilterChanged(type: String) {
        _uiState.update { it.copy(selectedLandTypeFilter = type) }
        applyFilters()
    }
    fun onLandTypeFilterChange(type: String) = onLandTypeFilterChanged(type)

    fun onGazetteFilterChanged(gazetteId: String) {
        _uiState.update { it.copy(selectedGazetteFilter = gazetteId) }
        applyFilters()
    }
    fun onGazetteFilterChange(gazetteId: String) = onGazetteFilterChanged(gazetteId)

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedTalukFilter = "All",
                selectedHobliFilter = "All",
                selectedLandTypeFilter = "All",
                selectedGazetteFilter = "All"
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val parcels = GazetteRepository.searchParcels(
            query = currentState.searchQuery,
            filterTaluk = if (currentState.selectedTalukFilter == "All") null else currentState.selectedTalukFilter,
            filterHobli = if (currentState.selectedHobliFilter == "All") null else currentState.selectedHobliFilter,
            filterLandType = if (currentState.selectedLandTypeFilter == "All") null else currentState.selectedLandTypeFilter,
            filterGazette = if (currentState.selectedGazetteFilter == "All") null else currentState.selectedGazetteFilter
        )

        val villages = GazetteRepository.searchVillages(
            query = currentState.searchQuery,
            filterTaluk = if (currentState.selectedTalukFilter == "All") null else currentState.selectedTalukFilter
        )

        _uiState.update {
            it.copy(
                filteredParcels = parcels,
                filteredVillages = villages
            )
        }
    }

    fun selectGazetteDetail(gazette: GazetteNotification?) {
        _uiState.update { it.copy(selectedGazetteDetail = gazette) }
    }

    fun selectVillageDetail(village: VillageSummary?) {
        _uiState.update { it.copy(selectedVillageDetail = village) }
    }

    fun selectParcelDetail(parcel: LandParcel?) {
        _uiState.update { it.copy(selectedParcelDetail = parcel) }
    }

    fun toggleFavorite(parcelId: String) {
        val parcel = GazetteRepository.allParcels.firstOrNull { it.id == parcelId } ?: return
        val currentlyFav = _uiState.value.bookmarkedParcelIds.contains(parcelId)
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(parcel, currentlyFav)
        }
    }

    fun removeFavorite(parcelId: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavoriteById(parcelId)
        }
    }

    fun isFavorite(parcelId: String): Boolean {
        return _uiState.value.bookmarkedParcelIds.contains(parcelId)
    }
}
