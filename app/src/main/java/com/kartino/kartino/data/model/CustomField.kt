package com.kartino.kartino.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entity representing a dynamic field for a custom card
 * Allows users to add unlimited custom fields with label-value pairs
 */
@Entity(
    tableName = "custom_fields",
    foreignKeys = [
        ForeignKey(
            entity = CustomCard::class,
            parentColumns = ["id"],
            childColumns = ["customCardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customCardId"])]
)
data class CustomField(
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customCardId: Long, // Foreign key to CustomCard
    val fieldLabel: String, // e.g., "Serial Number", "Balance", "Zone"
    val fieldValue: String? = null, // The actual value
    val displayOrder: Int = 0 // For maintaining field order
)
