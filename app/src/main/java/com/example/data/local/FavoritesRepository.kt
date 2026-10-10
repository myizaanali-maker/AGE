package com.example.data.local

import com.example.model.LandParcel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(private val dao: FavoriteParcelDao) {

    val allFavorites: Flow<List<FavoriteParcelEntity>> = dao.getAllFavorites()

    val favoriteIds: Flow<Set<String>> = dao.getAllFavoriteIds().map { it.toSet() }

    suspend fun toggleFavorite(parcel: LandParcel, currentlyFavorite: Boolean) {
        if (currentlyFavorite) {
            dao.deleteFavoriteById(parcel.id)
        } else {
            val entity = FavoriteParcelEntity(
                id = parcel.id,
                gazetteId = parcel.gazetteId,
                slNo = parcel.slNo,
                villageNameKn = parcel.villageNameKn,
                villageNameEn = parcel.villageNameEn,
                hobliKn = parcel.hobliKn,
                talukKn = parcel.talukKn,
                khatedarNameKn = parcel.khatedarNameKn,
                khatedarNameEn = parcel.khatedarNameEn,
                surveyNo = parcel.surveyNo,
                hissaNo = parcel.hissaNo,
                landType = parcel.landType,
                totalExtent = parcel.totalExtent,
                acquiredExtent = parcel.acquiredExtent,
                chakkubandiEast = parcel.chakkubandiEast,
                chakkubandiWest = parcel.chakkubandiWest,
                chakkubandiNorth = parcel.chakkubandiNorth,
                chakkubandiSouth = parcel.chakkubandiSouth
            )
            dao.insertFavorite(entity)
        }
    }

    suspend fun removeFavoriteById(parcelId: String) {
        dao.deleteFavoriteById(parcelId)
    }

    fun isFavorite(parcelId: String): Flow<Boolean> = dao.isFavorite(parcelId)
}
