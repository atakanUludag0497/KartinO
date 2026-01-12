package com.kartino.kartino.data.dao

import androidx.room.*
import com.kartino.kartino.data.model.CustomField
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for CustomField operations
 */
@Dao
interface CustomFieldDao {
    
    @Query("SELECT * FROM custom_fields WHERE customCardId = :cardId ORDER BY displayOrder ASC")
    fun getFieldsByCardId(cardId: Long): Flow<List<CustomField>>
    
    @Query("SELECT * FROM custom_fields WHERE customCardId = :cardId ORDER BY displayOrder ASC")
    suspend fun getFieldsByCardIdSuspend(cardId: Long): List<CustomField>
    
    @Query("SELECT * FROM custom_fields WHERE id = :id")
    suspend fun getFieldById(id: Long): CustomField?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: CustomField): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<CustomField>)
    
    @Update
    suspend fun updateField(field: CustomField)
    
    @Delete
    suspend fun deleteField(field: CustomField)
    
    @Query("DELETE FROM custom_fields WHERE id = :id")
    suspend fun deleteFieldById(id: Long)
    
    @Query("DELETE FROM custom_fields WHERE customCardId = :cardId")
    suspend fun deleteFieldsByCardId(cardId: Long)
    
    @Query("DELETE FROM custom_fields")
    suspend fun deleteAllFields()
}
