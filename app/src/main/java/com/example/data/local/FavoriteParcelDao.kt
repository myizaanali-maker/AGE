package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteParcelDao {
    @Query("SELECT * FROM favorite_parcels ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteParcelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(entity: FavoriteParcelEntity)

    @Query("DELETE FROM favorite_parcels WHERE id = :parcelId")
    suspend fun deleteFavoriteById(parcelId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_parcels WHERE id = :parcelId)")
    fun isFavorite(parcelId: String): Flow<Boolean>

    @Query("SELECT id FROM favorite_parcels")
    fun getAllFavoriteIds(): Flow<List<String>>
}
