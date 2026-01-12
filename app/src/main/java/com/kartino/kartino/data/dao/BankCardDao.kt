package com.kartino.kartino.data.dao

import androidx.room.*
import com.kartino.kartino.data.model.BankCard
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for BankCard operations
 */
@Dao
interface BankCardDao {
    
    @Query("SELECT * FROM bank_cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<BankCard>>
    
    @Query("SELECT * FROM bank_cards WHERE id = :id")
    suspend fun getCardById(id: Long): BankCard?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BankCard): Long
    
    @Update
    suspend fun updateCard(card: BankCard)
    
    @Delete
    suspend fun deleteCard(card: BankCard)
    
    @Query("DELETE FROM bank_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)
    
    @Query("DELETE FROM bank_cards")
    suspend fun deleteAllCards()
}
