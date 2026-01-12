package com.kartino.kartino.data.database

import android.content.Context
import androidx.room.Room

/**
 * Provides database instance using singleton pattern
 */
object DatabaseModule {
    
    @Volatile
    private var INSTANCE: CardDatabase? = null
    
    fun getDatabase(context: Context): CardDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                CardDatabase::class.java,
                CardDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // For development - remove in production
                .build()
            INSTANCE = instance
            instance
        }
    }
}


