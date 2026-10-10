package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.GazetteMapView
import com.example.ui.components.ParcelDetailDialog
import com.example.ui.theme.GazettePrimary
import com.example.ui.theme.GazetteSecondaryVariant
import com.example.viewmodel.AppTab
import com.example.viewmodel.GazetteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: GazetteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button: if in detail or secondary tab, return to Dashboard
    BackHandler(enabled = uiState.currentTab != AppTab.DASHBOARD || uiState.selectedParcelDetail != null) {
        if (uiState.selectedParcelDetail != null) {
            viewModel.selectParcelDetail(null)
        } else {
            viewModel.selectTab(AppTab.DASHBOARD)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.gazette_app_icon_1791621581360),
                            contentDescription = "AGE Emblem",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AGE - Alam's Gazette Extractor",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = if (uiState.isKannada) "ಕರ್ನಾಟಕ ರಾಜ್ಯಪತ್ರ ಭೂಸ್ವಾಧೀನ" else "Karnataka Gazette Land Acquisition",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Bilingual Language Switcher Pill
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.toggleLanguage() },
                        label = {
                            Text(
                                text = if (uiState.isKannada) "ಕನ್ನಡ" else "ENG",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Translate,
                                contentDescription = "Language",
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GazetteSecondaryVariant,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("lang_toggle_btn")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    val label = if (uiState.isKannada) tab.titleKn else tab.titleEn

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            when (tab) {
                                AppTab.DASHBOARD -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                    contentDescription = label
                                )
                                AppTab.SEARCH -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = label
                                )
                                AppTab.SUMMARIES -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Description else Icons.Outlined.Description,
                                    contentDescription = label
                                )
                                AppTab.MAP -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.Map else Icons.Outlined.Map,
                                    contentDescription = label
                                )
                                AppTab.VILLAGES -> Icon(
                                    imageVector = if (isSelected) Icons.Filled.HolidayVillage else Icons.Outlined.HolidayVillage,
                                    contentDescription = label
                                )
                                AppTab.FAVORITES -> {
                                    BadgedBox(
                                        badge = {
                                            if (uiState.favoriteParcelsList.isNotEmpty()) {
                                                Badge {
                                                    Text("${uiState.favoriteParcelsList.size}")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = label,
                                            tint = if (isSelected) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GazettePrimary,
                            selectedTextColor = GazettePrimary,
                            indicatorColor = GazetteSecondaryVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        stats = uiState.overallStats,
                        notifications = uiState.notifications,
                        isKannada = uiState.isKannada,
                        onSearchClick = { viewModel.selectTab(AppTab.SEARCH) },
                        onGazetteClick = { gazette ->
                            viewModel.selectGazetteDetail(gazette)
                            viewModel.selectTab(AppTab.SUMMARIES)
                        },
                        onSearchWithinGazette = { gazette ->
                            viewModel.onGazetteFilterChange(gazette.id)
                            viewModel.selectTab(AppTab.SEARCH)
                        },
                        onViewAllVillagesClick = { viewModel.selectTab(AppTab.VILLAGES) }
                    )
                }
                AppTab.SEARCH -> {
                    SearchScreen(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                        selectedGazetteFilter = uiState.selectedGazetteFilter,
                        onGazetteFilterChange = { viewModel.onGazetteFilterChange(it) },
                        selectedTalukFilter = uiState.selectedTalukFilter,
                        onTalukFilterChange = { viewModel.onTalukFilterChanged(it) },
                        selectedLandTypeFilter = uiState.selectedLandTypeFilter,
                        onLandTypeFilterChange = { viewModel.onLandTypeFilterChanged(it) },
                        onClearFilters = { viewModel.clearFilters() },
                        filteredParcels = uiState.filteredParcels,
                        isKannada = uiState.isKannada,
                        bookmarkedIds = uiState.bookmarkedParcelIds,
                        onToggleBookmark = { viewModel.toggleFavorite(it) },
                        onParcelClick = { viewModel.selectParcelDetail(it) }
                    )
                }
                AppTab.SUMMARIES -> {
                    GazetteSummaryScreen(
                        notifications = uiState.notifications,
                        selectedGazette = uiState.selectedGazetteDetail,
                        onSelectGazette = { viewModel.selectGazetteDetail(it) },
                        isKannada = uiState.isKannada,
                        onSearchWithin = { gazette ->
                            viewModel.onGazetteFilterChange(gazette.id)
                            viewModel.selectTab(AppTab.SEARCH)
                        },
                        onVillageClick = { village ->
                            viewModel.onSearchQueryChanged(village.villageNameKn)
                            viewModel.selectTab(AppTab.SEARCH)
                        },
                        onParcelClick = { viewModel.selectParcelDetail(it) },
                        bookmarkedIds = uiState.bookmarkedParcelIds,
                        onToggleBookmark = { viewModel.toggleFavorite(it) }
                    )
                }
                AppTab.MAP -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        GazetteMapView(
                            villages = uiState.notifications.flatMap { it.villageSummaries },
                            isKannada = uiState.isKannada,
                            onVillageScheduleClick = { village ->
                                val matched = uiState.notifications.firstOrNull { it.id == village.gazetteId }
                                if (matched != null) {
                                    viewModel.selectGazetteDetail(matched)
                                    viewModel.selectTab(AppTab.SUMMARIES)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                AppTab.VILLAGES -> {
                    VillagesScreen(
                        villages = uiState.filteredVillages,
                        isKannada = uiState.isKannada,
                        onVillageClick = { village ->
                            viewModel.onSearchQueryChanged(village.villageNameKn)
                            viewModel.selectTab(AppTab.SEARCH)
                        }
                    )
                }
                AppTab.FAVORITES -> {
                    BookmarksScreen(
                        favoritesList = uiState.favoriteParcelsList,
                        isKannada = uiState.isKannada,
                        onRemoveFavorite = { viewModel.removeFavorite(it) },
                        onParcelClick = { viewModel.selectParcelDetail(it) },
                        onGoToSearch = { viewModel.selectTab(AppTab.SEARCH) }
                    )
                }
            }

            // Parcel detail dialog when a parcel is active
            uiState.selectedParcelDetail?.let { parcel ->
                ParcelDetailDialog(
                    parcel = parcel,
                    isKannada = uiState.isKannada,
                    isBookmarked = viewModel.isFavorite(parcel.id),
                    onBookmarkToggle = { viewModel.toggleFavorite(parcel.id) },
                    onDismiss = { viewModel.selectParcelDetail(null) }
                )
            }
        }
    }
}
