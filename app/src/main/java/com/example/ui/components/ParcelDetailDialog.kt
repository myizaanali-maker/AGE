package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LandParcel
import com.example.ui.theme.*

@Composable
fun ParcelDetailDialog(
    parcel: LandParcel,
    isKannada: Boolean,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("parcel_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "${if (isKannada) "ಸ.ನಂ." else "Sy No."} ${parcel.surveyNo}${if (parcel.hissaNo != "-" && parcel.hissaNo.isNotBlank()) "/${parcel.hissaNo}" else ""}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GazetteSecondary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = parcel.gazetteId,
                                    color = GazetteSecondary,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${if (isKannada) parcel.villageNameKn else parcel.villageNameEn}, ${if (isKannada) parcel.hobliKn else parcel.hobliEn} • ${if (isKannada) parcel.talukKn else parcel.talukEn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier.testTag("dialog_bookmark_btn")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) GazetteSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("dialog_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Khathedar / Owner Name Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isKannada) "ಪಹಣಿಯಂತೆ ಖಾತೆದಾರರ ಹೆಸರು (ಖಾತೆದಾರರು)" else "Landowner / Khatedar as per Record of Rights",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = parcel.khatedarNameKn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (parcel.khatedarNameEn.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = parcel.khatedarNameEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Extent & Classification Grid
                    Text(
                        text = if (isKannada) "ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಕಂದಾಯ ವಿವರಗಳು" else "Land Extent & Assessment Details",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBox(
                            title = if (isKannada) "ಭೂಮಿಯ ತರಹೆ" else "Land Type",
                            value = parcel.landType,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBox(
                            title = if (isKannada) "ಆಕಾರ (ಕಂದಾಯ)" else "Akar / Assess",
                            value = parcel.assessmentAkar.ifBlank { "-" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBox(
                            title = if (isKannada) "ಒಟ್ಟು ಕ್ಷೇತ್ರ" else "Total Extent",
                            value = "${parcel.totalExtent} A-G-A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoBox(
                            title = if (isKannada) "ಖರಾಬು ಕ್ಷೇತ್ರ" else "Kharabu Extent",
                            value = parcel.kharabExtent.ifBlank { "-" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GazetteTertiary.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isKannada) "ಭೂಸ್ವಾಧೀನಕ್ಕೆ ಒಳಪಡುವ ವಿಸ್ತೀರ್ಣ" else "Land To Be Acquired (Final)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GazetteTertiary
                                )
                                Text(
                                    text = if (isKannada) "ಎಕರೆ - ಗುಂಟೆ - ಆಣೆ" else "Acre - Gunta - Anna",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GazetteTertiary.copy(alpha = 0.8f)
                                )
                            }
                            Text(
                                text = parcel.acquiredExtent,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = GazetteTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chakkubandi / Boundaries
                    Text(
                        text = if (isKannada) "ಭೂಸ್ವಾಧೀನ ಕ್ಷೇತ್ರದ ಚಕ್ಕುಬಂದಿ (ನಾಲ್ಕು ದಿಕ್ಕುಗಳು)" else "Acquisition Area Boundaries (Chakkubandi)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            BoundaryRow(
                                directionKn = "ಪೂರ್ವ (East)",
                                directionEn = "East",
                                boundary = parcel.chakkubandiEast
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            BoundaryRow(
                                directionKn = "ಪಶ್ಚಿಮ (West)",
                                directionEn = "West",
                                boundary = parcel.chakkubandiWest
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            BoundaryRow(
                                directionKn = "ಉತ್ತರ (North)",
                                directionEn = "North",
                                boundary = parcel.chakkubandiNorth
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            BoundaryRow(
                                directionKn = "ದಕ್ಷಿಣ (South)",
                                directionEn = "South",
                                boundary = parcel.chakkubandiSouth
                            )
                        }
                    }

                    if (parcel.remarks.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${if (isKannada) "ಷರಾ (ವಿಶೇಷ ಸೂಚನೆ)" else "Remarks"}: ${parcel.remarks}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Bottom action bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val copyText = buildString {
                                appendLine("Karnataka Gazette Land Acquisition Detail")
                                appendLine("Gazette Ref: ${parcel.gazetteId}")
                                appendLine("Village: ${parcel.villageNameKn} (${parcel.villageNameEn})")
                                appendLine("Hobli: ${parcel.hobliKn}, Taluk: ${parcel.talukKn}")
                                appendLine("Survey No: ${parcel.surveyNo} Hissa: ${parcel.hissaNo}")
                                appendLine("Owner: ${parcel.khatedarNameKn}")
                                appendLine("Land Type: ${parcel.landType}")
                                appendLine("Acquired Extent: ${parcel.acquiredExtent} (A-G-A)")
                                appendLine("East: ${parcel.chakkubandiEast}")
                                appendLine("West: ${parcel.chakkubandiWest}")
                                appendLine("North: ${parcel.chakkubandiNorth}")
                                appendLine("South: ${parcel.chakkubandiSouth}")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Parcel Details", copyText))
                            Toast.makeText(context, if (isKannada) "ವಿವರಗಳನ್ನು ನಕಲಿಸಲಾಗಿದೆ" else "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_parcel_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isKannada) "ನಕಲಿಸಿ" else "Copy Details", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dismiss_parcel_dialog_btn")
                    ) {
                        Text(if (isKannada) "ಮುಚ್ಚಿ" else "Done", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BoundaryRow(
    directionKn: String,
    directionEn: String,
    boundary: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = directionKn,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = boundary.ifBlank { "-" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
