package com.kartino.kartino.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kartino.kartino.data.dao.BankCardDao
import com.kartino.kartino.data.dao.CustomCardDao
import com.kartino.kartino.data.dao.CustomFieldDao
import com.kartino.kartino.data.model.BankCard
import com.kartino.kartino.data.model.CustomCard
import com.kartino.kartino.data.model.CustomField

/**
 * Room Database for the card wallet application
 * Version 1 - Initial schema
 */
@Database(
    entities = [BankCard::class, CustomCard::class, CustomField::class],
    version = 2,
    exportSchema = false
)
abstract class CardDatabase : RoomDatabase() {
    abstract fun bankCardDao(): BankCardDao
    abstract fun customCardDao(): CustomCardDao
    abstract fun customFieldDao(): CustomFieldDao
    
    companion object {
        const val DATABASE_NAME = "card_database"
    }
}
