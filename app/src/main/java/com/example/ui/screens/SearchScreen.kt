package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandParcel
import com.example.ui.components.GazetteScheduleTableView
import com.example.ui.components.ParcelCard
import com.example.ui.theme.GazettePrimary
import com.example.ui.theme.GazetteSecondary

@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedGazetteFilter: String,
    onGazetteFilterChange: (String) -> Unit,
    selectedTalukFilter: String,
    onTalukFilterChange: (String) -> Unit,
    selectedLandTypeFilter: String,
    onLandTypeFilterChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    filteredParcels: List<LandParcel>,
    isKannada: Boolean,
    bookmarkedIds: Set<String>,
    onToggleBookmark: (String) -> Unit,
    onParcelClick: (LandParcel) -> Unit,
    modifier: Modifier = Modifier
) {
    var isTableView by remember { mutableStateOf(true) } // Default to the requested Table format!

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = if (isKannada)
                                "ಹೆಸರು, ಸ.ನಂ., ಗ್ರಾಮ (ಉದಾ: ಮುನಿಯಪ್ಪ, 83, ಮಾಲೂರು)..."
                            else
                                "Search name, Survey #, village (e.g., 76, Muniappa)...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GazettePrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.testTag("clear_search_btn")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GazettePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Format Switcher & Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Table vs Card Format Toggle
                    FilterChip(
                        selected = isTableView,
                        onClick = { isTableView = true },
                        label = { Text(if (isKannada) "ರಾಜ್ಯಪತ್ರ ಟೇಬಲ್ (18-Col)" else "Gazette Table (18-Col)", fontSize = 12.sp, fontWeight = if (isTableView) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GazettePrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.testTag("toggle_table_view")
                    )

                    FilterChip(
                        selected = !isTableView,
                        onClick = { isTableView = false },
                        label = { Text(if (isKannada) "ಕಾರ್ಡ್ ಮಾದರಿ" else "Card View", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("toggle_card_view")
                    )

                    VerticalDivider(modifier = Modifier.height(24.dp))

                    // Gazette Filters
                    FilterChip(
                        selected = selectedGazetteFilter == "All",
                        onClick = { onGazetteFilterChange("All") },
                        label = { Text(if (isKannada) "ಎಲ್ಲಾ ಪತ್ರಗಳು" else "All Gazettes", fontSize = 12.sp) },
                        modifier = Modifier.testTag("chip_gazette_all")
                    )

                    FilterChip(
                        selected = selectedGazetteFilter == "PR-1035",
                        onClick = { onGazetteFilterChange("PR-1035") },
                        label = { Text("PR-1035 (Malur)", fontSize = 12.sp) },
                        modifier = Modifier.testTag("chip_gazette_1035")
                    )

                    FilterChip(
                        selected = selectedGazetteFilter == "PR-1184",
                        onClick = { onGazetteFilterChange("PR-1184") },
                        label = { Text("PR-1184 (Kolar)", fontSize = 12.sp) },
                        modifier = Modifier.testTag("chip_gazette_1184")
                    )

                    FilterChip(
                        selected = selectedGazetteFilter == "PR-997",
                        onClick = { onGazetteFilterChange("PR-997") },
                        label = { Text("PR-997 (Malur)", fontSize = 12.sp) },
                        modifier = Modifier.testTag("chip_gazette_997")
                    )

                    VerticalDivider(modifier = Modifier.height(24.dp))

                    // Taluk Filters
                    FilterChip(
                        selected = selectedTalukFilter == "ಮಾಲೂರು" || selectedTalukFilter == "Malur",
                        onClick = {
                            if (selectedTalukFilter == "Malur" || selectedTalukFilter == "ಮಾಲೂರು") {
                                onTalukFilterChange("All")
                            } else {
                                onTalukFilterChange("Malur")
                            }
                        },
                        label = { Text(if (isKannada) "ಮಾಲೂರು" else "Malur", fontSize = 12.sp) }
                    )

                    FilterChip(
                        selected = selectedTalukFilter == "ಕೋಲಾರ" || selectedTalukFilter == "Kolar",
                        onClick = {
                            if (selectedTalukFilter == "Kolar" || selectedTalukFilter == "ಕೋಲಾರ") {
                                onTalukFilterChange("All")
                            } else {
                                onTalukFilterChange("Kolar")
                            }
                        },
                        label = { Text(if (isKannada) "ಕೋಲಾರ" else "Kolar", fontSize = 12.sp) }
                    )

                    if (searchQuery.isNotEmpty() || selectedGazetteFilter != "All" || selectedTalukFilter != "All" || selectedLandTypeFilter != "All") {
                        TextButton(
                            onClick = onClearFilters,
                            modifier = Modifier.testTag("reset_filters_btn")
                        ) {
                            Text(if (isKannada) "ರೀಸೆಟ್" else "Reset", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // Results bar & count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKannada)
                    "ಫಲಿತಾಂಶ: ${filteredParcels.size} ಪಹಣಿ ಸರ್ವೇ ದಾಖಲೆಗಳು"
                else
                    "Results: ${filteredParcels.size} Survey Parcels found",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isTableView) {
                Text(
                    text = if (isKannada) "← ಎಡ-ಬಲ ಸ್ಕ್ರಾಲ್ ಮಾಡಿ →" else "← Scroll horizontally →",
                    style = MaterialTheme.typography.labelSmall,
                    color = GazetteSecondary
                )
            }
        }

        // Empty state
        if (filteredParcels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isKannada) "ಯಾವುದೇ ಫಲಿತಾಂಶ ದೊರೆತಿಲ್ಲ" else "No matching records found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isKannada)
                            "ಬೇರೆ ಸರ್ವೇ ನಂಬರ್ ಅಥವಾ ಖಾತೆದಾರರ ಹೆಸರು ಹುಡುಕಿ (ಉದಾ: 76, 83, ಮುನಿಯಪ್ಪ, ನರಸಾಪುರ, ವೇಮಗಲ್ಲು)"
                        else
                            "Try searching with Survey # or Khatedar name (e.g., 76, 83, Muniappa, Narasapura, Vemagal)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onClearFilters) {
                        Text(if (isKannada) "ಫಿಲ್ಟರ್ ತೆರವುಗೊಳಿಸಿ" else "Clear All Filters")
                    }
                }
            }
        } else {
            if (isTableView) {
                // EXACT GAZETTE TABLE FORMAT
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("parcels_table_results"),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp)
                ) {
                    item {
                        GazetteScheduleTableView(
                            parcels = filteredParcels,
                            isKannada = isKannada,
                            onParcelClick = onParcelClick
                        )
                    }
                }
            } else {
                // CARD VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("parcels_results_list"),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredParcels, key = { it.id }) { parcel ->
                        ParcelCard(
                            parcel = parcel,
                            isKannada = isKannada,
                            isBookmarked = bookmarkedIds.contains(parcel.id),
                            onBookmarkToggle = { onToggleBookmark(parcel.id) },
                            onClick = { onParcelClick(parcel) }
                        )
                    }
                }
            }
        }
    }
}
