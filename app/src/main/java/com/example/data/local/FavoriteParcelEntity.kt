package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_parcels")
data class FavoriteParcelEntity(
    @PrimaryKey val id: String,
    val gazetteId: String,
    val slNo: Int,
    val villageNameKn: String,
    val villageNameEn: String,
    val hobliKn: String,
    val talukKn: String,
    val khatedarNameKn: String,
    val khatedarNameEn: String,
    val surveyNo: String,
    val hissaNo: String,
    val landType: String,
    val totalExtent: String,
    val acquiredExtent: String,
    val chakkubandiEast: String,
    val chakkubandiWest: String,
    val chakkubandiNorth: String,
    val chakkubandiSouth: String,
    val timestamp: Long = System.currentTimeMillis()
)
