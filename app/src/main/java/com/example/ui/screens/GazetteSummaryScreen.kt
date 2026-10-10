package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GazetteNotification
import com.example.model.LandParcel
import com.example.model.VillageSummary
import com.example.ui.components.GazetteScheduleTableView
import com.example.ui.components.GhoswaraTableView
import com.example.ui.components.ParcelCard
import com.example.ui.theme.*

enum class GazetteViewMode(val titleEn: String, val titleKn: String) {
    TABLE_SCHEDULE("Gazette Table (18-Col)", "ರಾಜ್ಯಪತ್ರ ಟೇಬಲ್"),
    GHOSWARA("Ghoswara (Abstract)", "ಘೋಷ್ವಾರಾ"),
    SUMMARY_CARDS("Summary & Cards", "ಸಾರಾಂಶ ಕಾರ್ಡ್‌ಗಳು")
}

@Composable
fun GazetteSummaryScreen(
    notifications: List<GazetteNotification>,
    selectedGazette: GazetteNotification?,
    onSelectGazette: (GazetteNotification) -> Unit,
    isKannada: Boolean,
    onSearchWithin: (GazetteNotification) -> Unit,
    onVillageClick: (VillageSummary) -> Unit,
    onParcelClick: (LandParcel) -> Unit,
    bookmarkedIds: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentGazette = selectedGazette ?: notifications.firstOrNull() ?: return
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(GazetteViewMode.TABLE_SCHEDULE) }
    var selectedVillageFilter by remember { mutableStateOf<String>("All") }
    var tableSearchQuery by remember { mutableStateOf("") }

    val displayedParcels = remember(currentGazette, selectedVillageFilter, tableSearchQuery) {
        val base = if (selectedVillageFilter == "All") {
            currentGazette.parcels
        } else {
            currentGazette.parcels.filter {
                it.villageNameKn == selectedVillageFilter || it.villageNameEn == selectedVillageFilter
            }
        }
        if (tableSearchQuery.isBlank()) {
            base
        } else {
            val q = tableSearchQuery.trim()
            base.filter {
                it.surveyNo.contains(q, ignoreCase = true) ||
                it.khatedarNameKn.contains(q, ignoreCase = true) ||
                it.khatedarNameEn.contains(q, ignoreCase = true) ||
                it.villageNameKn.contains(q, ignoreCase = true) ||
                it.villageNameEn.contains(q, ignoreCase = true) ||
                it.hissaNo.contains(q, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("gazette_summary_screen"),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp)
    ) {
        // Gazette Selector Tabs (PR-1035, PR-1184, PR-997)
        item {
            ScrollableTabRow(
                selectedTabIndex = notifications.indexOf(currentGazette).coerceAtLeast(0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                notifications.forEach { gazette ->
                    val isSelected = gazette.id == currentGazette.id
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedVillageFilter = "All"
                            onSelectGazette(gazette)
                        },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = gazette.id,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = gazette.taluks.firstOrNull()?.substringBefore(" ") ?: "",
                                    fontSize = 10.sp,
                                    color = if (isSelected) GazetteSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_${gazette.id}")
                    )
                }
            }
        }

        // View Mode Toggle (Table format vs Ghoswara vs Cards)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GazetteViewMode.values().forEach { mode ->
                        val isSelected = viewMode == mode
                        Button(
                            onClick = { viewMode = mode },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("view_mode_${mode.name.lowercase()}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) GazettePrimary else Color.Transparent,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isKannada) mode.titleKn else mode.titleEn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Official Issue Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GazettePrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GazetteSecondaryVariant
                        ) {
                            Text(
                                text = currentGazette.id,
                                color = Color.Black,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = if (isKannada) "${currentGazette.weekdayKn}, ${currentGazette.issueDateKn}" else "${currentGazette.weekdayEn}, ${currentGazette.issueDateEn}",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isKannada) currentGazette.titleKn else currentGazette.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${currentGazette.gazetteNumber} • ${if (isKannada) "ಪುಟಗಳು" else "Pages"}: ${currentGazette.pageRange} • ${currentGazette.totalLandFormatted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onSearchWithin(currentGazette) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isKannada) "ಹುಡುಕಿ" else "Search", fontSize = 11.sp)
                        }

                        FilledTonalButton(
                            onClick = {
                                val text = buildString {
                                    appendLine(currentGazette.id)
                                    appendLine(if (isKannada) currentGazette.titleKn else currentGazette.titleEn)
                                    appendLine("Date: ${currentGazette.issueDateEn}")
                                    appendLine("Total Extent: ${currentGazette.totalLandFormatted}")
                                    appendLine("Villages: ${currentGazette.totalVillages}")
                                    appendLine("\n${currentGazette.summaryEn}")
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Gazette Summary", text))
                                Toast.makeText(context, if (isKannada) "ಸಾರಾಂಶ ನಕಲಿಸಲಾಗಿದೆ" else "Summary copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = GazetteSecondaryVariant,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isKannada) "ಹಂಚಿಕೊಳ್ಳಿ" else "Copy", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // VIEW MODE: 1. FULL 18-COLUMN GAZETTE TABLE FORMAT
        if (viewMode == GazetteViewMode.TABLE_SCHEDULE) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = if (isKannada)
                            "ರಾಜ್ಯಪತ್ರದ ಮೂಲ 18-ಕಾಲಂ ಪಹಣಿ ತಃಖ್ತೆ (Official 18-Column Schedule)"
                        else
                            "Official 18-Column Schedule Table (Exact Gazette Format)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (isKannada)
                            "ಎಡ-ಬಲ ಸ್ಕ್ರಾಲ್ ಮಾಡಿ (Scroll horizontally to see all 18 columns with Chakkubandi borders)"
                        else
                            "Scroll horizontally to inspect all 18 columns including Akarbandh and Chakkubandi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Village Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedVillageFilter == "All",
                            onClick = { selectedVillageFilter = "All" },
                            label = { Text(if (isKannada) "ಎಲ್ಲಾ ಗ್ರಾಮಗಳು (${currentGazette.parcels.size})" else "All Villages (${currentGazette.parcels.size})", fontSize = 11.sp) }
                        )

                        currentGazette.villageSummaries.forEach { village ->
                            val vCount = currentGazette.parcels.count { it.villageNameKn == village.villageNameKn }
                            FilterChip(
                                selected = selectedVillageFilter == village.villageNameKn,
                                onClick = {
                                    selectedVillageFilter = if (selectedVillageFilter == village.villageNameKn) "All" else village.villageNameKn
                                },
                                label = { Text("${if (isKannada) village.villageNameKn else village.villageNameEn} ($vCount)", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instant search within table
                    OutlinedTextField(
                        value = tableSearchQuery,
                        onValueChange = { tableSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        placeholder = {
                            Text(
                                if (isKannada) "ತಃಖ್ತೆಯಲ್ಲಿ ಸರ್ವೇ ನಂ, ಖಾತೆದಾರ, ಗ್ರಾಮ ಹುಡುಕಿ..." else "Search within table by Sy No, Khatedar...",
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (tableSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { tableSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Row count and transparency badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GazetteSecondary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (isKannada)
                                    "ಪೂರ್ಣ ತಃಖ್ತೆ: ${displayedParcels.size} ಸಾಲುಗಳು ಲಭ್ಯವಿದೆ (ಯಾವುದೂ ಮರೆಯಾಗಿಲ್ಲ)"
                                else
                                    "Showing all ${displayedParcels.size} rows (0 hidden, all columns visible)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GazetteSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (selectedVillageFilter != "All" || tableSearchQuery.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    selectedVillageFilter = "All"
                                    tableSearchQuery = ""
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(if (isKannada) "ಎಲ್ಲವನ್ನೂ ತೋರಿಸಿ" else "Reset", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // THE EXACT 18-COLUMN TABLE
                    GazetteScheduleTableView(
                        parcels = displayedParcels,
                        villageNameKn = if (selectedVillageFilter == "All") "" else selectedVillageFilter,
                        villageNameEn = "",
                        hobliKn = currentGazette.hoblis.firstOrNull()?.substringBefore(" ") ?: "",
                        talukKn = currentGazette.taluks.firstOrNull()?.substringBefore(" ") ?: "",
                        districtKn = currentGazette.districtKn,
                        isKannada = isKannada,
                        onParcelClick = onParcelClick
                    )
                }
            }
        }

        // VIEW MODE: 2. GHOSWARA (ABSTRACT TABLE)
        else if (viewMode == GazetteViewMode.GHOSWARA) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = if (isKannada)
                            "ರಾಜ್ಯಪತ್ರದ ಅಧಿಕೃತ ಘೋಷ್ವಾರಾ (Ghoswara Abstract Table)"
                        else
                            "Official Ghoswara Abstract (Village Totals under Section 15)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GhoswaraTableView(
                        villages = currentGazette.villageSummaries,
                        isKannada = isKannada,
                        onVillageClick = onVillageClick
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Totals summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GazetteSecondary.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isKannada) "ಒಟ್ಟು ವಿಸ್ತೀರ್ಣ (Total Extent)" else "Total Extent Subject to Acquisition",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GazetteSecondary
                                )
                                Text(
                                    text = if (isKannada) "ಐನು: ${currentGazette.inamExtFormatted} | ಖರಾಬು: ${currentGazette.kharabExtFormatted}" else "Inam: ${currentGazette.inamExtFormatted} | Kharab: ${currentGazette.kharabExtFormatted}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = currentGazette.totalLandFormatted.substringBefore(" ("),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GazetteSecondary
                            )
                        }
                    }
                }
            }
        }

        // VIEW MODE: 3. SUMMARY CARDS
        else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Summarize,
                                contentDescription = null,
                                tint = GazettePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isKannada) "ಅಧಿಸೂಚನೆಯ ಅಧಿಕೃತ ಸಾರಾಂಶ" else "Executive Summary",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isKannada) currentGazette.summaryKn else currentGazette.summaryEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isKannada) "ಕಾನೂನು ಕಾಯ್ದೆ" else "Legal Provision",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isKannada) currentGazette.legalActKn else currentGazette.legalActEn,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isKannada) "ಅಧಿಕಾರಿ / ಸಹಿ" else "Competent Signatory",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isKannada) currentGazette.signatoryKn else currentGazette.signatoryEn,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Key Highlights
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isKannada) "ಪ್ರಮುಖ ಮುಖ್ಯಾಂಶಗಳು" else "Key Highlights",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        currentGazette.keyHighlights.forEach { highlight ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GazetteTertiary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = highlight,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Parcels list in Cards mode
            items(displayedParcels) { parcel ->
                ParcelCard(
                    parcel = parcel,
                    isKannada = isKannada,
                    isBookmarked = bookmarkedIds.contains(parcel.id),
                    onBookmarkToggle = { onToggleBookmark(parcel.id) },
                    onClick = { onParcelClick(parcel) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
