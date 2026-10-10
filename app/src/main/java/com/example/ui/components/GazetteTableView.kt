package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandParcel
import com.example.model.VillageSummary
import com.example.ui.theme.*

private val TableBorderColor = Color(0xFF334155) // Dark slate for sharp print lines
private val HeaderBgColor = Color(0xFFF1F5F9)    // Official paper off-white header
private val HeaderNumberBgColor = Color(0xFFE2E8F0)
private val RowAltBgColor = Color(0xFFF8FAFC)

// Column widths matching the Gazette proportions
private val ColWidthSlNo = 45.dp
private val ColWidthKhatedar = 220.dp
private val ColWidthSyNo = 55.dp
private val ColWidthHissa = 50.dp
private val ColWidthType = 75.dp
private val ColWidthExtent = 75.dp
private val ColWidthAkar = 55.dp
private val ColWidthAcqExtent = 75.dp
private val ColWidthAcqAkar = 50.dp
private val ColWidthChakku = 95.dp
private val ColWidthRemarks = 80.dp

@Composable
fun GazetteScheduleTableView(
    parcels: List<LandParcel>,
    villageNameKn: String = "",
    villageNameEn: String = "",
    hobliKn: String = "",
    talukKn: String = "",
    districtKn: String = "ಕೋಲಾರ",
    isKannada: Boolean = true,
    onParcelClick: (LandParcel) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gazette_schedule_table_view")
    ) {
        // Village Banner Header as printed in Gazette (e.g. "ಗ್ರಾಮ : ಕರಿನಾಯಕನಹಳ್ಳಿ  ಹೋಬಳಿ : ಕಸಬಾ  ತಾಲ್ಲೂಕು : ಮಾಲೂರು  ಜಿಲ್ಲೆ : ಕೋಲಾರ")
        if (villageNameKn.isNotBlank() || parcels.isNotEmpty()) {
            val vKn = villageNameKn.ifBlank { parcels.firstOrNull()?.villageNameKn ?: "" }
            val vEn = villageNameEn.ifBlank { parcels.firstOrNull()?.villageNameEn ?: "" }
            val hKn = hobliKn.ifBlank { parcels.firstOrNull()?.hobliKn ?: "" }
            val tKn = talukKn.ifBlank { parcels.firstOrNull()?.talukKn ?: "" }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                color = GazettePrimary
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = ":: ಷೆಡ್ಯೂಲ್ (SCHEDULE) ::",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ಗ್ರಾಮ : $vKn (${vEn.ifBlank { vKn }})  |  ಹೋಬಳಿ : $hKn  |  ತಾಲ್ಲೂಕು : $tKn  |  ಜಿಲ್ಲೆ : $districtKn",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Horizontal scrolling table
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .border(1.5.dp, TableBorderColor)
                    .background(Color.White)
            ) {
                // TIER 1: Main Header Row
                Row(
                    modifier = Modifier
                        .background(HeaderBgColor)
                        .height(IntrinsicSize.Min)
                ) {
                    TableHeaderCell("1\nಕ್ರ.\nಸಂ", ColWidthSlNo, rowSpan = true)
                    TableHeaderCell(if (isKannada) "2\nಪಹಣಿಯಂತೆ\nಖಾತೆದಾರರ ಹೆಸರು" else "2\nKhatedar Name as per\nPahani / ROR", ColWidthKhatedar, rowSpan = true)

                    // Land Details group (Cols 3, 4, 5)
                    Column(modifier = Modifier.width(ColWidthSyNo + ColWidthHissa + ColWidthType)) {
                        TableHeaderCell(
                            text = if (isKannada) "ಜಮೀನಿನ ವಿವರ" else "Land Particulars",
                            width = ColWidthSyNo + ColWidthHissa + ColWidthType,
                            isSubHeader = true
                        )
                        Row {
                            TableHeaderCell(if (isKannada) "3\nಸ.ನಂ" else "3\nSy No", ColWidthSyNo)
                            TableHeaderCell(if (isKannada) "4\nಹಿಸ್ಸಾ\nನಂ" else "4\nHissa", ColWidthHissa)
                            TableHeaderCell(if (isKannada) "5\nತರಹೆ\nಖುಷ್ಕಿ/ತರಿ" else "5\nType", ColWidthType)
                        }
                    }

                    // Akarbandh Extent group (Cols 6, 7, 8, 9)
                    Column(modifier = Modifier.width(ColWidthExtent * 3 + ColWidthAkar)) {
                        TableHeaderCell(
                            text = if (isKannada) "ಆಕಾರ ಬಂದಂತೆ ವಿಸ್ತೀರ್ಣ" else "Extent as per Akarbandh",
                            width = ColWidthExtent * 3 + ColWidthAkar,
                            isSubHeader = true
                        )
                        Row {
                            TableHeaderCell(if (isKannada) "6\nಒಟ್ಟು\nಎ.ಗುಂ.ಆ" else "6\nTotal\nA-G-A", ColWidthExtent)
                            TableHeaderCell(if (isKannada) "7\nಖರಾಬು\nಎ.ಗುಂ.ಆ" else "7\nKharab\nA-G-A", ColWidthExtent)
                            TableHeaderCell(if (isKannada) "8\nಐನು\nಎ.ಗುಂ.ಆ" else "8\nNet\nA-G-A", ColWidthExtent)
                            TableHeaderCell(if (isKannada) "9\nಆಕಾರ\nರೂ.ಪೈ" else "9\nAkar", ColWidthAkar)
                        }
                    }

                    // Subject to Acquisition Extent group (Cols 10, 11, 12, 13)
                    Column(modifier = Modifier.width(ColWidthAcqExtent * 3 + ColWidthAcqAkar)) {
                        TableHeaderCell(
                            text = if (isKannada) "ಭೂಸ್ವಾಧೀನಕ್ಕೆ ಒಳಪಡುವ ವಿಸ್ತೀರ್ಣ" else "Area Subject to Acquisition",
                            width = ColWidthAcqExtent * 3 + ColWidthAcqAkar,
                            isSubHeader = true,
                            highlightColor = GazetteSecondary.copy(alpha = 0.1f)
                        )
                        Row {
                            TableHeaderCell(if (isKannada) "10\nಒಟ್ಟು\nಎ.ಗುಂ.ಆ" else "10\nTotal\nA-G-A", ColWidthAcqExtent, isAcquired = true)
                            TableHeaderCell(if (isKannada) "11\nಖರಾಬು\nಎ.ಗುಂ.ಆ" else "11\nKharab\nA-G-A", ColWidthAcqExtent, isAcquired = true)
                            TableHeaderCell(if (isKannada) "12\nಐನು\nಎ.ಗುಂ.ಆ" else "12\nInam\nA-G-A", ColWidthAcqExtent, isAcquired = true)
                            TableHeaderCell(if (isKannada) "13\nಆಕಾರ" else "13\nAkar", ColWidthAcqAkar, isAcquired = true)
                        }
                    }

                    // Chakkubandi Boundaries group (Cols 14, 15, 16, 17)
                    Column(modifier = Modifier.width(ColWidthChakku * 4)) {
                        TableHeaderCell(
                            text = if (isKannada) "ಭೂಸ್ವಾಧೀನ ಕ್ಷೇತ್ರದ ಚಕ್ಕುಬಂದಿ" else "Boundaries (Chakkubandi)",
                            width = ColWidthChakku * 4,
                            isSubHeader = true
                        )
                        Row {
                            TableHeaderCell(if (isKannada) "14\nಪೂರ್ವ" else "14\nEast", ColWidthChakku)
                            TableHeaderCell(if (isKannada) "15\nಪಶ್ಚಿಮ" else "15\nWest", ColWidthChakku)
                            TableHeaderCell(if (isKannada) "16\nಉತ್ತರ" else "16\nNorth", ColWidthChakku)
                            TableHeaderCell(if (isKannada) "17\nದಕ್ಷಿಣ" else "17\nSouth", ColWidthChakku)
                        }
                    }

                    TableHeaderCell(if (isKannada) "18\nಷರಾ" else "18\nRemarks", ColWidthRemarks, rowSpan = true)
                }

                // TIER 2: Column Number Index Row (1 to 18) - exact signature of Karnataka Gazette
                Row(
                    modifier = Modifier
                        .background(HeaderNumberBgColor)
                        .border(1.dp, TableBorderColor)
                ) {
                    TableNumberCell("1", ColWidthSlNo)
                    TableNumberCell("2", ColWidthKhatedar)
                    TableNumberCell("3", ColWidthSyNo)
                    TableNumberCell("4", ColWidthHissa)
                    TableNumberCell("5", ColWidthType)
                    TableNumberCell("6", ColWidthExtent)
                    TableNumberCell("7", ColWidthExtent)
                    TableNumberCell("8", ColWidthExtent)
                    TableNumberCell("9", ColWidthAkar)
                    TableNumberCell("10", ColWidthAcqExtent, isAcquired = true)
                    TableNumberCell("11", ColWidthAcqExtent, isAcquired = true)
                    TableNumberCell("12", ColWidthAcqExtent, isAcquired = true)
                    TableNumberCell("13", ColWidthAcqAkar, isAcquired = true)
                    TableNumberCell("14", ColWidthChakku)
                    TableNumberCell("15", ColWidthChakku)
                    TableNumberCell("16", ColWidthChakku)
                    TableNumberCell("17", ColWidthChakku)
                    TableNumberCell("18", ColWidthRemarks)
                }

                // DATA ROWS - Grouped by village with official Gazette section banners
                val groupedByVillage = remember(parcels) {
                    parcels.groupBy { it.villageNameKn }
                }

                if (parcels.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isKannada) "ಯಾವುದೇ ದಾಖಲೆಗಳು ಕಂಡುಬಂದಿಲ್ಲ" else "No matching parcel records found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    groupedByVillage.forEach { (villageKn, vParcels) ->
                        val firstP = vParcels.first()

                        // Village separator banner when multiple villages are displayed
                        if (groupedByVillage.size > 1 || villageNameKn.isBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GazettePrimary.copy(alpha = 0.92f))
                                    .border(0.5.dp, TableBorderColor)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ಗ್ರಾಮ : $villageKn (${firstP.villageNameEn})  |  ಹೋಬಳಿ : ${firstP.hobliKn} (${firstP.hobliEn})  |  ತಾಲ್ಲೂಕು : ${firstP.talukKn}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GazetteSecondaryVariant
                                ) {
                                    Text(
                                        text = "${vParcels.size} ${if (isKannada) "ಖಾತೆಗಳು" else "Rows"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Village Parcels Rows
                        vParcels.forEachIndexed { index, parcel ->
                            val rowBg = if (index % 2 == 1) RowAltBgColor else Color.White

                            Row(
                                modifier = Modifier
                                    .height(IntrinsicSize.Max)
                                    .background(rowBg)
                                    .border(0.5.dp, TableBorderColor.copy(alpha = 0.5f))
                                    .clickable { onParcelClick(parcel) }
                            ) {
                                TableCell("${parcel.slNo}", ColWidthSlNo, isBold = true, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(
                                    text = parcel.khatedarNameKn,
                                    width = ColWidthKhatedar,
                                    isBold = true,
                                    subText = if (isKannada) "" else parcel.khatedarNameEn,
                                    modifier = Modifier.fillMaxHeight()
                                )
                                TableCell(parcel.surveyNo, ColWidthSyNo, isBold = true, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.hissaNo.ifBlank { "-" }, ColWidthHissa, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.landType.substringBefore(" "), ColWidthType, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.totalExtent, ColWidthExtent, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.kharabExtent.ifBlank { "-" }, ColWidthExtent, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.displayAinuExtent, ColWidthExtent, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.assessmentAkar.ifBlank { "-" }, ColWidthAkar, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())

                                // Acquired area columns (10, 11, 12, 13) with distinct highlight tint
                                TableCell(
                                    text = parcel.acquiredExtent,
                                    width = ColWidthAcqExtent,
                                    isBold = true,
                                    textColor = GazetteSecondary,
                                    textAlign = TextAlign.Center,
                                    bgColor = GazetteSecondary.copy(alpha = 0.05f),
                                    modifier = Modifier.fillMaxHeight()
                                )
                                TableCell(
                                    text = parcel.acquiredKharab,
                                    width = ColWidthAcqExtent,
                                    textAlign = TextAlign.Center,
                                    bgColor = GazetteSecondary.copy(alpha = 0.05f),
                                    modifier = Modifier.fillMaxHeight()
                                )
                                TableCell(
                                    text = parcel.displayAcquiredAinu,
                                    width = ColWidthAcqExtent,
                                    isBold = true,
                                    textColor = GazetteSecondary,
                                    textAlign = TextAlign.Center,
                                    bgColor = GazetteSecondary.copy(alpha = 0.05f),
                                    modifier = Modifier.fillMaxHeight()
                                )
                                TableCell(
                                    text = parcel.acquiredAkar,
                                    width = ColWidthAcqAkar,
                                    textAlign = TextAlign.Center,
                                    bgColor = GazetteSecondary.copy(alpha = 0.05f),
                                    modifier = Modifier.fillMaxHeight()
                                )

                                // Chakkubandi (14, 15, 16, 17)
                                TableCell(parcel.chakkubandiEast, ColWidthChakku, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.chakkubandiWest, ColWidthChakku, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.chakkubandiNorth, ColWidthChakku, modifier = Modifier.fillMaxHeight())
                                TableCell(parcel.chakkubandiSouth, ColWidthChakku, modifier = Modifier.fillMaxHeight())

                                TableCell(parcel.remarks.ifBlank { "-" }, ColWidthRemarks, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GhoswaraTableView(
    villages: List<VillageSummary>,
    isKannada: Boolean = true,
    onVillageClick: (VillageSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ghoswara_table_view")
    ) {
        // Table Header Label
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = GazettePrimary
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ಘೋಷ್ವಾರಾ (GHOSWARA - ABSTRACT)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = if (isKannada)
                        "ಕಲಂ 15 ರಂತೆ ಭೂಸ್ವಾಧೀನಕ್ಕೆ ಒಳಪಡುವ ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣ (ಗ್ರಾಮವಾರು ಸಂಕ್ಷಿಪ್ತ ವಿವರ)"
                    else
                        "Village-wise Abstract of Land Acquisition under Section 15",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .border(1.5.dp, TableBorderColor)
                    .background(Color.White)
            ) {
                // Tier 1 Header
                Row(
                    modifier = Modifier
                        .background(HeaderBgColor)
                        .height(IntrinsicSize.Min)
                ) {
                    TableHeaderCell("ಕ್ರ.\nಸಂ", 45.dp, rowSpan = true)
                    TableHeaderCell(if (isKannada) "ಗ್ರಾಮದ ಹೆಸರು" else "Village Name", 160.dp, rowSpan = true)
                    TableHeaderCell(if (isKannada) "ಹೋಬಳಿ" else "Hobli", 90.dp, rowSpan = true)
                    TableHeaderCell(if (isKannada) "ತಾಲ್ಲೂಕು" else "Taluk", 85.dp, rowSpan = true)
                    TableHeaderCell(if (isKannada) "ಜಿಲ್ಲೆ" else "District", 75.dp, rowSpan = true)

                    // Extent subject to acquisition (3 sub-columns)
                    Column(modifier = Modifier.width(300.dp)) {
                        TableHeaderCell(
                            text = if (isKannada) "ಕಲಂ 15 ರಂತೆ ಭೂಸ್ವಾಧೀನಕ್ಕೆ ಒಳಪಡುವ ವಿಸ್ತೀರ್ಣ" else "Land Extent Under Section 15",
                            width = 300.dp,
                            isSubHeader = true,
                            highlightColor = GazetteSecondary.copy(alpha = 0.15f)
                        )
                        Row {
                            TableHeaderCell(if (isKannada) "ಐನು\n(ಎ-ಗು-ಆ)" else "Inam\n(A-G-A)", 95.dp, isAcquired = true)
                            TableHeaderCell(if (isKannada) "ಖರಾಬು\n(ಎ-ಗು-ಆ)" else "Kharab\n(A-G-A)", 95.dp, isAcquired = true)
                            TableHeaderCell(if (isKannada) "ಒಟ್ಟು ವಿಸ್ತೀರ್ಣ\n(ಎ-ಗು-ಆ)" else "Total Extent\n(A-G-A)", 110.dp, isAcquired = true)
                        }
                    }
                }

                // Table Rows
                villages.forEachIndexed { index, village ->
                    val rowBg = if (index % 2 == 1) RowAltBgColor else Color.White

                    Row(
                        modifier = Modifier
                            .background(rowBg)
                            .border(0.5.dp, TableBorderColor.copy(alpha = 0.5f))
                            .clickable { onVillageClick(village) }
                    ) {
                        TableCell("${village.slNo}.", 45.dp, isBold = true, textAlign = TextAlign.Center)
                        TableCell(
                            text = if (isKannada) village.villageNameKn else village.villageNameEn,
                            width = 160.dp,
                            isBold = true,
                            subText = if (isKannada) "" else village.villageNameKn
                        )
                        TableCell(if (isKannada) village.hobliKn else village.hobliEn, 90.dp, textAlign = TextAlign.Center)
                        TableCell(if (isKannada) village.talukKn else village.talukEn, 85.dp, textAlign = TextAlign.Center)
                        TableCell("ಕೋಲಾರ", 75.dp, textAlign = TextAlign.Center)
                        TableCell(village.inamArea, 95.dp, textAlign = TextAlign.Center)
                        TableCell(village.kharabArea, 95.dp, textAlign = TextAlign.Center)
                        TableCell(
                            text = village.totalArea,
                            width = 110.dp,
                            isBold = true,
                            textColor = GazetteSecondary,
                            textAlign = TextAlign.Center,
                            bgColor = GazetteSecondary.copy(alpha = 0.08f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    text: String,
    width: Dp,
    rowSpan: Boolean = false,
    isSubHeader: Boolean = false,
    isAcquired: Boolean = false,
    highlightColor: Color? = null
) {
    val bg = highlightColor ?: if (isAcquired) GazetteSecondary.copy(alpha = 0.1f) else HeaderBgColor

    Box(
        modifier = Modifier
            .width(width)
            .then(if (rowSpan) Modifier.fillMaxHeight() else Modifier)
            .background(bg)
            .border(0.8.dp, TableBorderColor)
            .padding(horizontal = 4.dp, vertical = if (isSubHeader) 4.dp else 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (isSubHeader) 11.sp else 10.sp,
                lineHeight = 13.sp
            ),
            color = if (isAcquired) GazetteSecondary else TableBorderColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TableNumberCell(
    number: String,
    width: Dp,
    isAcquired: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(width)
            .background(if (isAcquired) GazetteSecondary.copy(alpha = 0.15f) else HeaderNumberBgColor)
            .border(0.5.dp, TableBorderColor)
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            ),
            color = TableBorderColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    isBold: Boolean = false,
    textColor: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Start,
    subText: String = "",
    bgColor: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .background(bgColor)
            .border(0.5.dp, TableBorderColor.copy(alpha = 0.4f))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Column(
            horizontalAlignment = when (textAlign) {
                TextAlign.Center -> Alignment.CenterHorizontally
                TextAlign.End -> Alignment.End
                else -> Alignment.Start
            }
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = textColor,
                textAlign = textAlign
            )
            if (subText.isNotBlank()) {
                Text(
                    text = subText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = Color.Gray
                    ),
                    textAlign = textAlign
                )
            }
        }
    }
}
