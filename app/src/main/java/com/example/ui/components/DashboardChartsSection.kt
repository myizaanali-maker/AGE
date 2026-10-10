package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class ChartMetricType(val titleKn: String, val titleEn: String) {
    BY_TALUK("ತಾಲ್ಲೂಕುವಾರು", "By Taluk"),
    BY_MONTH("ತಿಂಗಳವಾರು", "Monthly Timeline"),
    BY_LAND_TYPE("ಭೂಮಿ ವರ್ಗೀಕರಣ", "Land Category"),
    TOP_VILLAGES("ಟಾಪ್ ಗ್ರಾಮಗಳು", "Top Villages")
}

@Composable
fun DashboardChartsSection(
    isKannada: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(ChartMetricType.BY_TALUK) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_charts_section"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GazetteSecondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = GazetteSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isKannada) "ಸಾರಾಂಶ ಅಂಕಿಅಂಶಗಳ ಚಾರ್ಟ್" else "Acquisition Analytics & Charts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isKannada) "ಜಿಲ್ಲೆ, ತಿಂಗಳು ಮತ್ತು ಭೂಮಿ ವಿಂಗಡಣೆ" else "Breakdown by Taluk, Month & Land Category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart metric switcher tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChartMetricType.values().forEach { metric ->
                    val isSelected = selectedMetric == metric
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GazettePrimary else Color.Transparent)
                            .clickable { selectedMetric = metric }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isKannada) metric.titleKn else metric.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // RENDER SELECTED CHART
            when (selectedMetric) {
                ChartMetricType.BY_TALUK -> {
                    TalukComparisonChart(isKannada = isKannada)
                }
                ChartMetricType.BY_MONTH -> {
                    MonthlyTimelineChart(isKannada = isKannada)
                }
                ChartMetricType.BY_LAND_TYPE -> {
                    LandTypeDistributionChart(isKannada = isKannada)
                }
                ChartMetricType.TOP_VILLAGES -> {
                    TopVillagesBarChart(isKannada = isKannada)
                }
            }
        }
    }
}

@Composable
private fun TalukComparisonChart(isKannada: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isKannada) "ತಾಲ್ಲೂಕುವಾರು ಭೂಸ್ವಾಧೀನ ವಿಸ್ತೀರ್ಣ (ಒಟ್ಟು 50-10-10)" else "Total Area Acquired per Taluk / District",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Malur Taluk Bar (77.4%)
        ChartBarRow(
            label = if (isKannada) "ಮಾಲೂರು ತಾಲ್ಲೂಕು (Malur)" else "Malur Taluk",
            subLabel = if (isKannada) "30 ಗ್ರಾಮಗಳು (PR-1035 & PR-997)" else "30 Villages (PR-1035 & PR-997)",
            valueFormatted = "38-43.06 A-G-A",
            percentage = 0.774f,
            barColor = GazettePrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Kolar Taluk Bar (22.6%)
        ChartBarRow(
            label = if (isKannada) "ಕೋಲಾರ ತಾಲ್ಲೂಕು (Kolar)" else "Kolar Taluk",
            subLabel = if (isKannada) "10 ಗ್ರಾಮಗಳು (PR-1184)" else "10 Villages (PR-1184)",
            valueFormatted = "11-07.04 A-G-A",
            percentage = 0.226f,
            barColor = GazetteSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Summary note
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GazetteTertiary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isKannada)
                        "ಮಾಲೂರು ತಾಲ್ಲೂಕಿನಲ್ಲಿ ಕೈಗಾರಿಕಾ ಹಬ್ ಹಾಗೂ ನಗರ ಬೈಪಾಸ್ ಕಾರಣದಿಂದ ಗರಿಷ್ಠ ಶೇ. 77.4% ಭೂಸ್ವಾಧೀನ ನಡೆದಿದೆ."
                    else
                        "Malur Taluk represents 77.4% of total acquisition due to the Malur town bypass and corridor connections.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MonthlyTimelineChart(isKannada: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isKannada) "ತಿಂಗಳವಾರು ಅಧಿಸೂಚನೆಗಳ ಸರಣಿ (2026 Timeline)" else "Acquisition Timeline by Month (2026)",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // July 2026 (PR-997)
        ChartBarRow(
            label = if (isKannada) "ಜುಲೈ 2026 (July 2026)" else "July 2026",
            subLabel = "ಅಧಿಸೂಚನೆ PR-997 (10 ಗ್ರಾಮಗಳು)",
            valueFormatted = "16-12.00 A-G-A",
            percentage = 0.324f,
            barColor = Color(0xFF2563EB)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // August 2026 (PR-1035)
        ChartBarRow(
            label = if (isKannada) "ಆಗಸ್ಟ್ 2026 (August 2026)" else "August 2026",
            subLabel = "ಅಧಿಸೂಚನೆ PR-1035 (20 ಗ್ರಾಮಗಳು)",
            valueFormatted = "22-31.06 A-G-A",
            percentage = 0.453f,
            barColor = GazetteSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // September 2026 (PR-1184)
        ChartBarRow(
            label = if (isKannada) "ಸೆಪ್ಟೆಂಬರ್ 2026 (September 2026)" else "September 2026",
            subLabel = "ಅಧಿಸೂಚನೆ PR-1184 (10 ಗ್ರಾಮಗಳು)",
            valueFormatted = "11-07.04 A-G-A",
            percentage = 0.223f,
            barColor = GazetteTertiary
        )
    }
}

@Composable
private fun LandTypeDistributionChart(isKannada: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isKannada) "ಭೂಮಿ ವರ್ಗೀಕರಣ ವಿತರಣೆ (Land Classification)" else "Land Category Distribution",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChartBarRow(
            label = if (isKannada) "ಖುಷ್ಕಿ ಜಮೀನು (Dry Agricultural)" else "Dry Land (Khushki)",
            subLabel = "70.5% of total acquired parcels",
            valueFormatted = "35-18.04 A-G-A",
            percentage = 0.705f,
            barColor = LandDryColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        ChartBarRow(
            label = if (isKannada) "ಭೂ ಪರಿವರ್ತಿತ (Converted Bhoo Parivarthane)" else "Converted (Bhoo Parivarthane)",
            subLabel = "18.3% commercial/industrial sites",
            valueFormatted = "09-08.12 A-G-A",
            percentage = 0.183f,
            barColor = LandConvertedColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        ChartBarRow(
            label = if (isKannada) "ಸರ್ಕಾರಿ ಬೀಳು / ಗೋಮಾಳ (Government / Gomal)" else "Government / Gomal",
            subLabel = "8.2% public pasture & reserve",
            valueFormatted = "04-06.00 A-G-A",
            percentage = 0.082f,
            barColor = LandGovtColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        ChartBarRow(
            label = if (isKannada) "ಖರಾಬು & ಇತರ (Kharabu & Waterbeds)" else "Kharabu & Lake Beds",
            subLabel = "3.0% streams, roads, and tank beds",
            valueFormatted = "01-18.00 A-G-A",
            percentage = 0.030f,
            barColor = LandKharabColor
        )
    }
}

@Composable
private fun TopVillagesBarChart(isKannada: Boolean) {
    val villages = listOf(
        Pair("ಮಾಲೂರು (Malur)", 0.85f to "6-34.00"),
        Pair("ಸೀತಾನಾಯಕನಹಳ್ಳಿ (Seethanayak)", 0.60f to "4-18.04"),
        Pair("ನರಸಾಪುರ (Narasapura)", 0.48f to "3-26.04"),
        Pair("ಶಿವರಾಪಟ್ಟಣ (Shivarapattana)", 0.46f to "3-20.08"),
        Pair("ಸೊಣ್ಣೂರು (Sonnuru)", 0.44f to "3-15.06"),
        Pair("ಚನ್ನಪ್ಪನಹಳ್ಳಿ (Channappana)", 0.38f to "2-36.00")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isKannada) "ಗರಿಷ್ಠ ವಿಸ್ತೀರ್ಣದ ಗ್ರಾಮಗಳು (Acre-Gunta Ranking)" else "Top Villages by Acquired Extent",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        villages.forEach { (name, data) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(130.dp),
                    maxLines = 1
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(data.first)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(GazetteSecondaryVariant)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = data.second,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GazettePrimary,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun ChartBarRow(
    label: String,
    subLabel: String,
    valueFormatted: String,
    percentage: Float,
    barColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = valueFormatted,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = barColor
                )
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}
