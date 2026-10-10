package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VillageSummary
import com.example.ui.theme.*

data class VillageMapPoint(
    val id: String,
    val nameKn: String,
    val nameEn: String,
    val hobli: String,
    val taluk: String,
    val gazetteId: String,
    val totalArea: String,
    val parcelCount: Int,
    val xNorm: Float, // Normalized X position on map canvas (0.0 to 1.0)
    val yNorm: Float  // Normalized Y position on map canvas (0.0 to 1.0)
)

@Composable
fun GazetteMapView(
    villages: List<VillageSummary>,
    isKannada: Boolean,
    onVillageScheduleClick: (VillageSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterGazette by remember { mutableStateOf("All") }
    var selectedPoint by remember { mutableStateOf<VillageMapPoint?>(null) }
    var zoomScale by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Geographic map points for villages in Malur and Kolar taluks along KSHDP highway corridor
    val mapPoints = remember {
        listOf(
            VillageMapPoint("1", "ಮಾಲೂರು", "Malur Town", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-997", "6-34.00", 77, 0.32f, 0.72f),
            VillageMapPoint("2", "ಕರಿನಾಯಕನಹಳ್ಳಿ", "Karinayakanahalli", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "0-23.00", 11, 0.28f, 0.65f),
            VillageMapPoint("3", "ಮಿಂಡಹಳ್ಳಿ", "Mindahalli", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "0-04.00", 5, 0.26f, 0.60f),
            VillageMapPoint("4", "ಶಿವರಾಪಟ್ಟಣ", "Shivarapattana", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "3-20.08", 9, 0.36f, 0.61f),
            VillageMapPoint("5", "ವರದೇನನಹಳ್ಳಿ", "Varadenanahalli", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "0-39.12", 7, 0.40f, 0.68f),
            VillageMapPoint("6", "ದೊಡ್ಡಶಿವರ", "Doddashivara", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "0-34.12", 2, 0.44f, 0.65f),
            VillageMapPoint("7", "ಚಿಕ್ಕಕಡತೂರು", "Chikkakadathuru", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "2-31.12", 15, 0.48f, 0.73f),
            VillageMapPoint("8", "ದೊಡ್ಡಕಡತೂರು", "Doddakadathuru", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-1035", "0-35.00", 5, 0.52f, 0.71f),
            VillageMapPoint("9", "ಸೀತಾನಾಯಕನಹಳ್ಳಿ", "Seethanayakanahalli", "ಲಕ್ಕೂರು", "ಮಾಲೂರು", "PR-1035", "0-10.04", 5, 0.20f, 0.80f),
            VillageMapPoint("10", "ಜಯಮಂಗಲ", "Jayamangala", "ಲಕ್ಕೂರು", "ಮಾಲೂರು", "PR-1035", "2-31.12", 8, 0.22f, 0.85f),
            VillageMapPoint("11", "ಸೊಣ್ಣೂರು", "Sonnuru", "ಲಕ್ಕೂರು", "ಮಾಲೂರು", "PR-1035", "3-15.06", 6, 0.27f, 0.88f),
            VillageMapPoint("12", "ಆಲಂಬಾಡಿ", "Alambadi", "ಲಕ್ಕೂರು", "ಮಾಲೂರು", "PR-1035", "0-37.04", 5, 0.16f, 0.82f),
            VillageMapPoint("13", "ಚೊಕ್ಕಂಡಹಳ್ಳಿ", "Chokkandahalli", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-997", "0-20.12", 46, 0.34f, 0.76f),
            VillageMapPoint("14", "ನೊಸಗೆರೆ", "Nosagere", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-997", "0-33.08", 16, 0.38f, 0.74f),
            VillageMapPoint("15", "ಕೊರಂಡಹಳ್ಳಿ", "Korandahalli", "ಕಸಬಾ", "ಮಾಲೂರು", "PR-997", "0-21.08", 10, 0.42f, 0.77f),

            // Kolar Taluk & Industrial Cluster
            VillageMapPoint("16", "ವೇಮಗಲ್ಲು", "Vemagallu", "ವೇಮಗಲ್ಲು", "ಕೋಲಾರ", "PR-1184", "0-13.12", 12, 0.62f, 0.32f),
            VillageMapPoint("17", "ಚನ್ನಪ್ಪನಹಳ್ಳಿ", "Channappanahalli", "ವೇಮಗಲ್ಲು", "ಕೋಲಾರ", "PR-1184", "2-36.00", 32, 0.65f, 0.35f),
            VillageMapPoint("18", "ಸುಜ್ಜನಹಳ್ಳಿ", "Sujjanahalli", "ವೇಮಗಲ್ಲು", "ಕೋಲಾರ", "PR-1184", "0-38.04", 4, 0.60f, 0.38f),
            VillageMapPoint("19", "ನರಸಾಪುರ", "Narasapura", "ನರಸಾಪುರ", "ಕೋಲಾರ", "PR-1184", "3-26.04", 13, 0.72f, 0.44f),
            VillageMapPoint("20", "ಚೌಡದೇನಹಳ್ಳಿ", "Chowdadenahalli", "ನರಸಾಪುರ", "ಕೋಲಾರ", "PR-1184", "1-28.04", 3, 0.75f, 0.40f),
            VillageMapPoint("21", "ಕುರ್ಕಿ", "Kurki", "ನರಸಾಪುರ", "ಕೋಲಾರ", "PR-1184", "0-27.00", 9, 0.78f, 0.46f),
            VillageMapPoint("22", "ಬೆಳ್ಯೂರು", "Belyuru", "ನರಸಾಪುರ", "ಕೋಲಾರ", "PR-1184", "0-14.04", 6, 0.82f, 0.48f)
        )
    }

    val filteredPoints = remember(mapPoints, selectedFilterGazette) {
        if (selectedFilterGazette == "All") mapPoints
        else mapPoints.filter { it.gazetteId == selectedFilterGazette }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gazette_map_component"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                            .background(GazetteTertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = GazetteTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isKannada) "ಭೂಸ್ವಾಧೀನ ಗ್ರಾಮಗಳು & ಹೆದ್ದಾರಿ ಮ್ಯಾಪ್" else "Acquisition Parcels & Highway Corridor Map",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isKannada) "ಮಾಲೂರು & ಕೋಲಾರ ತಾಲ್ಲೂಕುಗಳ ಗ್ರಾಮಗಳ ನಕ್ಷೆ" else "Geographical plot of acquired villages in Kolar District",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedFilterGazette == "All",
                    onClick = { selectedFilterGazette = "All" },
                    label = { Text(if (isKannada) "ಎಲ್ಲಾ ಗ್ರಾಮಗಳು (${mapPoints.size})" else "All Villages (${mapPoints.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilterGazette == "PR-1035",
                    onClick = { selectedFilterGazette = "PR-1035" },
                    label = { Text("PR-1035 (Malur)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilterGazette == "PR-1184",
                    onClick = { selectedFilterGazette = "PR-1184" },
                    label = { Text("PR-1184 (Kolar)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilterGazette == "PR-997",
                    onClick = { selectedFilterGazette = "PR-997" },
                    label = { Text("PR-997 (Malur Town)", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Map Canvas Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A)) // Dark tactical map style
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(filteredPoints) {
                            detectTapGestures { tapOffset ->
                                val w = size.width
                                val h = size.height
                                val clicked = filteredPoints.minByOrNull { point ->
                                    val px = point.xNorm * w
                                    val py = point.yNorm * h
                                    val dx = px - tapOffset.x
                                    val dy = py - tapOffset.y
                                    dx * dx + dy * dy
                                }
                                if (clicked != null) {
                                    val px = clicked.xNorm * w
                                    val py = clicked.yNorm * h
                                    val dist = (px - tapOffset.x) * (px - tapOffset.x) + (py - tapOffset.y) * (py - tapOffset.y)
                                    if (dist < 40 * 40) {
                                        selectedPoint = clicked
                                    }
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw District boundary & background grid lines
                    for (i in 1..5) {
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(0f, h * i / 6),
                            end = Offset(w, h * i / 6),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(w * i / 6, 0f),
                            end = Offset(w * i / 6, h),
                            strokeWidth = 1f
                        )
                    }

                    // Draw Highway Route Corridor 1 (Malur South-West to North-East)
                    val malurPath = Path().apply {
                        moveTo(0.16f * w, 0.82f * h)
                        lineTo(0.24f * w, 0.85f * h)
                        lineTo(0.32f * w, 0.72f * h) // Malur Town
                        lineTo(0.36f * w, 0.61f * h) // Shivarapattana
                        lineTo(0.44f * w, 0.65f * h) // Doddashivara
                        lineTo(0.52f * w, 0.71f * h) // Doddakadathuru
                    }
                    drawPath(
                        path = malurPath,
                        color = Color(0xFFF59E0B).copy(alpha = 0.5f),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Draw Highway Route Corridor 2 (Vemagal - Narasapura industrial link)
                    val vemPath = Path().apply {
                        moveTo(0.60f * w, 0.38f * h) // Sujjanahalli
                        lineTo(0.65f * w, 0.35f * h) // Channappanahalli
                        lineTo(0.72f * w, 0.44f * h) // Narasapura Hub
                        lineTo(0.82f * w, 0.48f * h) // Belyuru
                    }
                    drawPath(
                        path = vemPath,
                        color = Color(0xFF10B981).copy(alpha = 0.5f),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Draw connecting artery
                    drawLine(
                        color = Color(0xFF64748B).copy(alpha = 0.4f),
                        start = Offset(0.32f * w, 0.72f * h),
                        end = Offset(0.72f * w, 0.44f * h),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw Village Parcel Markers
                    filteredPoints.forEach { pt ->
                        val px = pt.xNorm * w
                        val py = pt.yNorm * h
                        val isSelected = selectedPoint?.id == pt.id

                        val pinColor = when (pt.gazetteId) {
                            "PR-1035" -> Color(0xFF38BDF8) // Sky blue
                            "PR-1184" -> Color(0xFF34D399) // Emerald
                            else -> Color(0xFFFBBF24)      // Amber
                        }

                        // Glow circle if selected
                        if (isSelected) {
                            drawCircle(
                                color = pinColor.copy(alpha = 0.35f),
                                radius = 18.dp.toPx(),
                                center = Offset(px, py)
                            )
                        }

                        // Marker circle
                        drawCircle(
                            color = pinColor,
                            radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
                            center = Offset(px, py)
                        )

                        // Center dot
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }

                // Legend & Controls Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PR-1035", color = Color.White, fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF34D399)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PR-1184", color = Color.White, fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PR-997", color = Color.White, fontSize = 9.sp)
                        }
                    }
                }

                // Tap helper text
                Text(
                    text = if (isKannada) "ಗ್ರಾಮದ ಮಾರ್ಕರ್ ಮೇಲೆ ಟ್ಯಾಪ್ ಮಾಡಿ" else "Tap on any village pin to view parcel summary",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                )
            }

            // Selected Marker Information Card
            AnimatedVisibility(visible = selectedPoint != null) {
                selectedPoint?.let { pt ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        border = BorderStroke(1.dp, GazetteSecondary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isKannada) pt.nameKn else pt.nameEn,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GazettePrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = pt.gazetteId,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GazettePrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${pt.hobli} ಹೋಬಳಿ • ${pt.taluk} ತಾಲ್ಲೂಕು",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "ಸ್ವಾಧೀನ ವಿಸ್ತೀರ್ಣ: ${pt.totalArea} A-G-A (${pt.parcelCount} ಖಾತೆಗಳು)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = GazetteSecondary
                                )
                            }

                            Button(
                                onClick = {
                                    val matched = villages.firstOrNull { it.villageNameKn == pt.nameKn }
                                    if (matched != null) {
                                        onVillageScheduleClick(matched)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GazettePrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(if (isKannada) "ಟೇಬಲ್ ನೋಡಿ" else "View Table", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
