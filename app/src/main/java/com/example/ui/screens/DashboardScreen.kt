package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GazetteNotification
import com.example.model.OverallStats
import com.example.ui.components.DashboardChartsSection
import com.example.ui.components.GazetteMapView
import com.example.ui.components.GazetteSummaryCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    stats: OverallStats?,
    notifications: List<GazetteNotification>,
    isKannada: Boolean,
    onSearchClick: () -> Unit,
    onGazetteClick: (GazetteNotification) -> Unit,
    onSearchWithinGazette: (GazetteNotification) -> Unit,
    onViewAllVillagesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gazette_banner_1791621696004),
                    contentDescription = "Government Gazette Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GazettePrimary.copy(alpha = 0.5f),
                                    GazettePrimary.copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GazetteSecondaryVariant
                            ) {
                                Text(
                                    text = if (isKannada) "ಕರ್ನಾಟಕ ಸರ್ಕಾರ" else "GOVERNMENT OF KARNATAKA",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isKannada) "ಅಧಿಕೃತ ರಾಜ್ಯಪತ್ರ" else "Official State Gazette",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isKannada) "ಕೋಲಾರ ಜಿಲ್ಲೆ ಭೂಸ್ವಾಧೀನ ಪೋರ್ಟಲ್" else "Kolar District Land Acquisition Gazette Explorer",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )

                        Text(
                            text = if (isKannada)
                                "ಮಾಲೂರು & ಕೋಲಾರ ತಾಲ್ಲೂಕು ಹೆದ್ದಾರಿ ಯೋಜನೆ (KSHDP) ಅಧಿಸೂಚನೆಗಳ ಸಾರಾಂಶ"
                            else
                                "Comprehensive Dashboard & Survey Landowner Schedules (KSHDP Road Widening)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Search Prompt Shortcut Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSearchClick() }
                            .testTag("dashboard_search_bar_trigger"),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GazettePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isKannada)
                                    "ಖಾತೆದಾರರ ಹೆಸರು, ಸ.ನಂ., ಗ್ರಾಮ, ಹೋಬಳಿ ಹುಡುಕಿ..."
                                else
                                    "Search by Khatedar name, Survey No, Village, Hobli...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    // Direct link button to 18-column Gazette Table format
                    Button(
                        onClick = {
                            notifications.firstOrNull()?.let { onGazetteClick(it) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("open_gazette_table_direct"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GazetteSecondaryVariant,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            Icons.Default.TableChart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isKannada)
                                "ರಾಜ್ಯಪತ್ರದ ಅದೇ 18-ಕಾಲಂ ಟೇಬಲ್ ವೀಕ್ಷಿಸಿ"
                            else
                                "View Exact 18-Column Gazette Table",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Stats Overview Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = if (isKannada) "ಪ್ರಮುಖ ಮುಖ್ಯಾಂಶಗಳು (Dashboard Metrics)" else "Key Metrics & Overall Statistics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isKannada) "ರಾಜ್ಯಪತ್ರಗಳು" else "Gazettes",
                        value = "${stats?.totalGazettesCount ?: 3}",
                        subtitle = if (isKannada) "ಅಧಿಸೂಚನೆಗಳು" else "Notifications",
                        icon = Icons.Filled.Description,
                        iconBgColor = GazettePrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = if (isKannada) "ಒಟ್ಟು ಭೂಸ್ವಾಧೀನ" else "Total Land",
                        value = "50-10-10",
                        subtitle = if (isKannada) "ಎಕರೆ-ಗುಂಟೆ-ಆಣೆ" else "Acre-Gunta-Anna",
                        icon = Icons.Default.Landscape,
                        iconBgColor = GazetteSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isKannada) "ಒಳಪಟ್ಟ ಗ್ರಾಮಗಳು" else "Villages",
                        value = "${stats?.totalVillagesCount ?: 40}",
                        subtitle = if (isKannada) "4 ಹೋಬಳಿಗಳಲ್ಲಿ" else "Across 4 Hoblis",
                        icon = Icons.Default.HolidayVillage,
                        iconBgColor = GazetteTertiary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = if (isKannada) "ಸರ್ವೇ ನಂಬರ್‌ಗಳು" else "Parcels",
                        value = "550+",
                        subtitle = if (isKannada) "ಪಹಣಿ ಖಾತೆದಾರರು" else "Survey Parcels",
                        icon = Icons.Default.FormatListNumbered,
                        iconBgColor = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Taluk Breakdown Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isKannada) "ತಾಲ್ಲೂಕುವಾರು ಭೂಸ್ವಾಧೀನ ವಿಂಗಡಣೆ" else "Taluk-wise Acquisition Distribution",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKannada) "ಮಾಲೂರು ತಾಲ್ಲೂಕು (Malur)" else "Malur Taluk",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isKannada) "30 ಗ್ರಾಮಗಳು • ಕಸಬಾ & ಲಕ್ಕೂರು" else "30 Villages • Kasaba & Lakkuru",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GazettePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "38-43.06 A-G-A",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = GazettePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKannada) "ಕೋಲಾರ ತಾಲ್ಲೂಕು (Kolar)" else "Kolar Taluk",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isKannada) "10 ಗ್ರಾಮಗಳು • ವೇಮಗಲ್ಲು & ನರಸಾಪುರ" else "10 Villages • Vemagal & Narasapura",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GazetteSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "11-07.04 A-G-A",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = GazetteSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Charts Section (Total Area per District, Monthly Timeline, Land Category, Top Villages)
        item {
            DashboardChartsSection(
                isKannada = isKannada,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Map Component (Visualizing land parcels across Kolar & Malur taluks)
        item {
            GazetteMapView(
                villages = notifications.flatMap { it.villageSummaries },
                isKannada = isKannada,
                onVillageScheduleClick = { village ->
                    val matchedGazette = notifications.firstOrNull { it.id == village.gazetteId }
                    if (matchedGazette != null) {
                        onGazetteClick(matchedGazette)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Section header for Gazettes
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isKannada) "ರಾಜ್ಯಪತ್ರಗಳ ಅಧಿಸೂಚನೆಗಳು (All 3 Gazettes)" else "Gazette Notifications (All 3 Issues)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 3 Gazette Cards
        items(notifications) { gazette ->
            GazetteSummaryCard(
                gazette = gazette,
                isKannada = isKannada,
                onViewSummaryClick = { onGazetteClick(gazette) },
                onSearchWithinClick = { onSearchWithinGazette(gazette) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Top Impacted Villages preview
        item {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKannada) "ಗರಿಷ್ಠ ಸ್ವಾಧೀನ ಗ್ರಾಮಗಳು (Top Impacted)" else "Top Impacted Villages",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(
                        onClick = onViewAllVillagesClick,
                        modifier = Modifier.testTag("view_all_villages_btn")
                    ) {
                        Text(if (isKannada) "ಎಲ್ಲಾ ಗ್ರಾಮಗಳು (${stats?.totalVillagesCount})" else "View All (${stats?.totalVillagesCount})")
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                val topVillages = listOf(
                    Triple("ಮಾಲೂರು (Malur)", "ಕಸಬಾ, ಮಾಲೂರು", "6 ಎಕರೆ 34 ಗುಂಟೆ (6-34.00)"),
                    Triple("ಸೀತಾನಾಯಕನಹಳ್ಳಿ (Seethanayakanahalli)", "ಲಕ್ಕೂರು, ಮಾಲೂರು", "4 ಎಕರೆ 18 ಗುಂಟೆ (4-18.04)"),
                    Triple("ಶಿವರಾಪಟ್ಟಣ (Shivarapattana)", "ಕಸಬಾ, ಮಾಲೂರು", "3 ಎಕರೆ 20 ಗುಂಟೆ (3-20.08)"),
                    Triple("ನರಸಾಪುರ (Narasapura)", "ನರಸಾಪುರ, ಕೋಲಾರ", "3 ಎಕರೆ 26 ಗುಂಟೆ (3-26.04)"),
                    Triple("ಸೊಣ್ಣೂರು (Sonnuru)", "ಲಕ್ಕೂರು, ಮಾಲೂರು", "3 ಎಕರೆ 15 ಗುಂಟೆ (3-15.06)"),
                    Triple("ಚನ್ನಪ್ಪನಹಳ್ಳಿ (Channappanahalli)", "ವೇಮಗಲ್ಲು, ಕೋಲಾರ", "2 ಎಕರೆ 36 ಗುಂಟೆ (2-36.00)")
                )

                topVillages.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GazettePrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GazettePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.first,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.second,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = item.third.substringAfter("(").substringBefore(")"),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GazetteSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
