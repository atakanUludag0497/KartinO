package com.kartino.kartino.data.model

/**
 * Unified model for displaying cards in the UI
 * Combines BankCard and CustomCard with their associated data
 */
sealed class Card {
    abstract val id: Long
    abstract val createdAt: Long
    abstract val updatedAt: Long

    abstract val displayName: String
    
    data class Bank(
        val bankCard: BankCard
    ) : Card() {
        override val id: Long = bankCard.id
        override val createdAt: Long = bankCard.createdAt
        override val updatedAt: Long = bankCard.updatedAt
        override val displayName: String = bankCard.cardName
    }
    
    data class Custom(
        val customCard: CustomCard,
        val fields: List<CustomField> = emptyList()
    ) : Card() {
        override val id: Long = customCard.id
        override val createdAt: Long = customCard.createdAt
        override val updatedAt: Long = customCard.updatedAt
        override val displayName: String = customCard.cardTitle
    }
}
