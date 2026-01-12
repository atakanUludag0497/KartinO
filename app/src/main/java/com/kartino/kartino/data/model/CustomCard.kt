package com.kartino.kartino.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a custom/special card (e.g., public transport, membership, etc.)
 * Only cardTitle is required
 */
@Entity(tableName = "custom_cards")
data class CustomCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardTitle: String, // Required field
    val cardDescription: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
