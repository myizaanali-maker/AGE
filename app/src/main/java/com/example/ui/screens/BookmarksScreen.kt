package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GazetteRepository
import com.example.data.local.FavoriteParcelEntity
import com.example.model.LandParcel
import com.example.ui.components.ParcelCard
import com.example.ui.theme.GazettePrimary
import com.example.ui.theme.GazetteSecondary

@Composable
fun BookmarksScreen(
    favoritesList: List<FavoriteParcelEntity>,
    isKannada: Boolean,
    onRemoveFavorite: (String) -> Unit,
    onParcelClick: (LandParcel) -> Unit,
    onGoToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isKannada) "ಮೆಚ್ಚಿನವುಗಳು (Saved Favorites)" else "Saved Favorites (Room DB)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isKannada) "ರೂಮ್ ಡಾಟಾಬೇಸ್‌ನಲ್ಲಿ ಸುರಕ್ಷಿತವಾಗಿ ಉಳಿಸಲಾಗಿದೆ" else "Persisted offline via Room Database",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GazetteSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${favoritesList.size} ${if (isKannada) "ದಾಖಲೆಗಳು" else "Saved"}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GazetteSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (favoritesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isKannada) "ಯಾವುದೇ ಮೆಚ್ಚಿನ ಪಹಣಿ ಉಳಿಸಲಾಗಿಲ್ಲ" else "No favorite parcels saved yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isKannada)
                            "ಹುಡುಕಾಟ ಅಥವಾ ಅಧಿಸೂಚನೆಗಳ ಪಟ್ಟಿಯಿಂದ ಪಹಣಿಗಳನ್ನು ಮೆಚ್ಚಿನವುಗಳಿಗೆ ಸೇರಿಸಿ. ಅವುಗಳು ಅಪ್ಲಿಕೇಶನ್‌ನಲ್ಲಿ ಶಾಶ್ವತವಾಗಿ ಉಳಿಯುತ್ತವೆ."
                        else
                            "Star any survey parcel from search or gazette tables. They will be saved to your device with Room database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onGoToSearch,
                        colors = ButtonDefaults.buttonColors(containerColor = GazettePrimary)
                    ) {
                        Text(if (isKannada) "ಪಹಣಿಗಳನ್ನು ಹುಡುಕಿ" else "Search Gazette Parcels")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favoritesList, key = { it.id }) { favEntity ->
                    val fullParcel = GazetteRepository.allParcels.firstOrNull { it.id == favEntity.id }

                    if (fullParcel != null) {
                        ParcelCard(
                            parcel = fullParcel,
                            isKannada = isKannada,
                            isBookmarked = true,
                            onBookmarkToggle = { onRemoveFavorite(favEntity.id) },
                            onClick = { onParcelClick(fullParcel) }
                        )
                    } else {
                        // Fallback card from Room entity directly
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val fallbackParcel = LandParcel(
                                        id = favEntity.id,
                                        gazetteId = favEntity.gazetteId,
                                        slNo = favEntity.slNo,
                                        villageNameKn = favEntity.villageNameKn,
                                        villageNameEn = favEntity.villageNameEn,
                                        hobliKn = favEntity.hobliKn,
                                        hobliEn = favEntity.hobliKn,
                                        talukKn = favEntity.talukKn,
                                        talukEn = favEntity.talukKn,
                                        khatedarNameKn = favEntity.khatedarNameKn,
                                        khatedarNameEn = favEntity.khatedarNameEn,
                                        surveyNo = favEntity.surveyNo,
                                        hissaNo = favEntity.hissaNo,
                                        landType = favEntity.landType,
                                        totalExtent = favEntity.totalExtent,
                                        kharabExtent = "-",
                                        acquiredExtent = favEntity.acquiredExtent,
                                        assessmentAkar = "-",
                                        chakkubandiEast = favEntity.chakkubandiEast,
                                        chakkubandiWest = favEntity.chakkubandiWest,
                                        chakkubandiNorth = favEntity.chakkubandiNorth,
                                        chakkubandiSouth = favEntity.chakkubandiSouth
                                    )
                                    onParcelClick(fallbackParcel)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GazettePrimary
                                        ) {
                                            Text(
                                                text = "ಸ.ನಂ. ${favEntity.surveyNo}/${favEntity.hissaNo}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = favEntity.villageNameKn,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = favEntity.khatedarNameKn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ಸ್ವಾಧೀನ: ${favEntity.acquiredExtent} A-G-A",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GazetteSecondary
                                    )
                                }

                                IconButton(onClick = { onRemoveFavorite(favEntity.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
