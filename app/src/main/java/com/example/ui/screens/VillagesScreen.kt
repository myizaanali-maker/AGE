package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VillageSummary
import com.example.ui.components.GhoswaraTableView
import com.example.ui.components.VillageCard
import com.example.ui.theme.GazettePrimary

@Composable
fun VillagesScreen(
    villages: List<VillageSummary>,
    isKannada: Boolean,
    onVillageClick: (VillageSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTaluk by remember { mutableStateOf("All") }
    var isTableView by remember { mutableStateOf(true) } // Default to Table format!

    val filteredList = remember(villages, searchQuery, selectedTaluk) {
        villages.filter { village ->
            val matchesQuery = if (searchQuery.isBlank()) true else {
                village.villageNameKn.contains(searchQuery, ignoreCase = true) ||
                village.villageNameEn.contains(searchQuery, ignoreCase = true) ||
                village.hobliKn.contains(searchQuery, ignoreCase = true) ||
                village.hobliEn.contains(searchQuery, ignoreCase = true)
            }
            val matchesTaluk = if (selectedTaluk == "All") true else {
                village.talukKn.contains(selectedTaluk, ignoreCase = true) ||
                village.talukEn.contains(selectedTaluk, ignoreCase = true)
            }
            matchesQuery && matchesTaluk
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("villages_screen")
    ) {
        // Search & Filter header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("village_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = if (isKannada) "ಗ್ರಾಮದ ಹೆಸರು ಹುಡುಕಿ (ಉದಾ: ಮಾಲೂರು, ವೇಮಗಲ್ಲು)..." else "Search village by name...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GazettePrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Table vs Cards
                    FilterChip(
                        selected = isTableView,
                        onClick = { isTableView = true },
                        label = { Text(if (isKannada) "ಘೋಷ್ವಾರಾ ಟೇಬಲ್" else "Ghoswara Table", fontSize = 12.sp, fontWeight = if (isTableView) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GazettePrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = !isTableView,
                        onClick = { isTableView = false },
                        label = { Text(if (isKannada) "ಕಾರ್ಡ್ ಮಾದರಿ" else "Card View", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )

                    VerticalDivider(modifier = Modifier.height(24.dp))

                    FilterChip(
                        selected = selectedTaluk == "All",
                        onClick = { selectedTaluk = "All" },
                        label = { Text(if (isKannada) "ಎಲ್ಲಾ ತಾಲ್ಲೂಕುಗಳು" else "All Taluks", fontSize = 12.sp) }
                    )

                    FilterChip(
                        selected = selectedTaluk == "Malur" || selectedTaluk == "ಮಾಲೂರು",
                        onClick = { selectedTaluk = if (selectedTaluk == "Malur") "All" else "Malur" },
                        label = { Text(if (isKannada) "ಮಾಲೂರು (30 ಗ್ರಾಮ)" else "Malur (30)", fontSize = 12.sp) }
                    )

                    FilterChip(
                        selected = selectedTaluk == "Kolar" || selectedTaluk == "ಕೋಲಾರ",
                        onClick = { selectedTaluk = if (selectedTaluk == "Kolar") "All" else "Kolar" },
                        label = { Text(if (isKannada) "ಕೋಲಾರ (10 ಗ್ರಾಮ)" else "Kolar (10)", fontSize = 12.sp) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKannada) "ಗ್ರಾಮಗಳ ತಃಖ್ತೆ (${filteredList.size})" else "Villages Abstract (${filteredList.size})",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isTableView) {
                Text(
                    text = if (isKannada) "← ಎಡ-ಬಲ ಸ್ಕ್ರಾಲ್ ಮಾಡಿ →" else "← Scroll horizontally →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isTableView) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("villages_table_view"),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp)
            ) {
                item {
                    GhoswaraTableView(
                        villages = filteredList,
                        isKannada = isKannada,
                        onVillageClick = onVillageClick
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("villages_list"),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { village ->
                    VillageCard(
                        village = village,
                        isKannada = isKannada,
                        onClick = { onVillageClick(village) }
                    )
                }
            }
        }
    }
}
