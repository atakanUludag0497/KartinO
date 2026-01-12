package com.kartino.kartino.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a bank or credit card
 * All fields except cardName are optional
 */
@Entity(tableName = "bank_cards")
data class BankCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardName: String, // Required field
    val bankName: String? = null,
    val cardNumber: String? = null,
    val expirationDate: String? = null, // Format: MM/YY
    val cvv: String? = null,
    val pin: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
