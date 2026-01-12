package com.kartino.kartino.data.repository

import com.kartino.kartino.data.dao.BankCardDao
import com.kartino.kartino.data.dao.CustomCardDao
import com.kartino.kartino.data.dao.CustomFieldDao
import com.kartino.kartino.data.model.BankCard
import com.kartino.kartino.data.model.Card
import com.kartino.kartino.data.model.CustomCard
import com.kartino.kartino.data.model.CustomField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Repository for managing all card operations
 * Combines BankCard and CustomCard data into unified Card model
 */
class CardRepository(
    private val bankCardDao: BankCardDao,
    private val customCardDao: CustomCardDao,
    private val customFieldDao: CustomFieldDao
) {
    
    /**
     * Get all cards (both bank and custom) as a unified list
     * Sorted by most recently updated first
     */
    fun getAllCards(): Flow<List<Card>> {
        val bankCardsFlow = bankCardDao.getAllCards()
        val customCardsFlow = customCardDao.getAllCards()
        
        return combine(bankCardsFlow, customCardsFlow) { bankCards, customCards ->
            val bankCardList = bankCards.map { Card.Bank(it) }
            val customCardList = customCards.map { card ->
                // For each custom card, we need to get its fields
                // Since we're in a Flow, we'll need to handle this differently
                Card.Custom(card, emptyList()) // Fields will be loaded separately when needed
            }
            
            // Combine and sort by updatedAt
            (bankCardList + customCardList).sortedByDescending { it.updatedAt }
        }
    }
    
    /**
     * Get a bank card by ID
     */
    suspend fun getBankCardById(id: Long): BankCard? {
        return bankCardDao.getCardById(id)
    }
    
    /**
     * Get a custom card with its fields by ID
     */
    suspend fun getCustomCardById(id: Long): Card.Custom? {
        val card = customCardDao.getCardById(id) ?: return null
        val fields = customFieldDao.getFieldsByCardIdSuspend(id)
        return Card.Custom(card, fields)
    }
    
    /**
     * Get custom card fields as Flow
     */
    fun getCustomCardFields(cardId: Long): Flow<List<CustomField>> {
        return customFieldDao.getFieldsByCardId(cardId)
    }
    
    /**
     * Insert a new bank card
     */
    suspend fun insertBankCard(card: BankCard): Long {
        return bankCardDao.insertCard(card.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Insert a new custom card with its fields
     */
    suspend fun insertCustomCard(card: CustomCard, fields: List<CustomField>): Long {
        val cardId = customCardDao.insertCard(card.copy(updatedAt = System.currentTimeMillis()))
        if (fields.isNotEmpty()) {
            val fieldsWithCardId = fields.mapIndexed { index, field ->
                field.copy(customCardId = cardId, displayOrder = index)
            }
            customFieldDao.insertFields(fieldsWithCardId)
        }
        return cardId
    }
    
    /**
     * Update a bank card
     */
    suspend fun updateBankCard(card: BankCard) {
        bankCardDao.updateCard(card.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Update a custom card and its fields
     */
    suspend fun updateCustomCard(card: CustomCard, fields: List<CustomField>) {
        customCardDao.updateCard(card.copy(updatedAt = System.currentTimeMillis()))
        
        // Delete existing fields and insert new ones
        customFieldDao.deleteFieldsByCardId(card.id)
        if (fields.isNotEmpty()) {
            val fieldsWithCardId = fields.mapIndexed { index, field ->
                field.copy(customCardId = card.id, displayOrder = index)
            }
            customFieldDao.insertFields(fieldsWithCardId)
        }
    }
    
    /**
     * Delete a bank card
     */
    suspend fun deleteBankCard(card: BankCard) {
        bankCardDao.deleteCard(card)
    }
    
    /**
     * Delete a custom card (fields will be deleted automatically due to CASCADE)
     */
    suspend fun deleteCustomCard(card: CustomCard) {
        customCardDao.deleteCard(card)
    }
    
    /**
     * Delete all cards and fields (clear all data)
     */
    suspend fun deleteAllCards() {
        bankCardDao.deleteAllCards()
        customCardDao.deleteAllCards()
        customFieldDao.deleteAllFields()
    }
    
    /**
     * Add a field to a custom card
     */
    suspend fun addCustomField(field: CustomField): Long {
        return customFieldDao.insertField(field)
    }
    
    /**
     * Update a custom field
     */
    suspend fun updateCustomField(field: CustomField) {
        customFieldDao.updateField(field)
    }
    
    /**
     * Delete a custom field
     */
    suspend fun deleteCustomField(field: CustomField) {
        customFieldDao.deleteField(field)
    }
}
