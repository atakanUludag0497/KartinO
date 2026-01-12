package com.kartino.kartino.data.dao

import androidx.room.*
import com.kartino.kartino.data.model.CustomCard
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for CustomCard operations
 */
@Dao
interface CustomCardDao {
    
    @Query("SELECT * FROM custom_cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<CustomCard>>
    
    @Query("SELECT * FROM custom_cards WHERE id = :id")
    suspend fun getCardById(id: Long): CustomCard?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CustomCard): Long
    
    @Update
    suspend fun updateCard(card: CustomCard)
    
    @Delete
    suspend fun deleteCard(card: CustomCard)
    
    @Query("DELETE FROM custom_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)
    
    @Query("DELETE FROM custom_cards")
    suspend fun deleteAllCards()
}
