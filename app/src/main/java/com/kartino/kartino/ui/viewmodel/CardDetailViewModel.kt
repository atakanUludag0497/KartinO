package com.kartino.kartino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kartino.kartino.data.model.Card
import com.kartino.kartino.data.model.CustomField
import com.kartino.kartino.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for viewing card details
 */
class CardDetailViewModel(
    private val repository: CardRepository,
    cardId: Long,
    cardType: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CardDetailUiState?>(null)
    val uiState: StateFlow<CardDetailUiState?> = _uiState.asStateFlow()
    
    init {
        loadCard(cardId, cardType)
    }
    
    /**
     * Load card by ID and type
     */
    private fun loadCard(cardId: Long, cardType: String) {
        viewModelScope.launch {
            _uiState.value = CardDetailUiState(isLoading = true)
            
            val card = when (cardType) {
                "bank" -> {
                    repository.getBankCardById(cardId)?.let { bankCard ->
                        Card.Bank(bankCard)
                    }
                }
                "custom" -> {
                    repository.getCustomCardById(cardId)
                }
                else -> null
            }
            
            if (card != null) {
                if (card is Card.Custom) {
                    loadCustomFields(card.customCard.id, card)
                } else {
                    _uiState.value = CardDetailUiState(
                        card = card,
                        isLoading = false
                    )
                }
            } else {
                _uiState.value = null // Card not found
            }
        }
    }
    
    /**
     * Load custom fields for custom cards
     */
    private fun loadCustomFields(cardId: Long, card: Card.Custom) {
        viewModelScope.launch {
            repository.getCustomCardFields(cardId).collect { fields ->
                _uiState.value = CardDetailUiState(
                    card = card,
                    customFields = fields,
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Delete the card
     */
    fun deleteCard(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value?.let { state ->
                when (val currentCard = state.card) {
                    is Card.Bank -> {
                        repository.deleteBankCard(currentCard.bankCard)
                    }
                    is Card.Custom -> {
                        repository.deleteCustomCard(currentCard.customCard)
                    }
                    null -> {
                        // Card is null, do nothing
                    }
                }
                onSuccess()
            }
        }
    }
}

/**
 * UI State for Card Detail screen
 */
data class CardDetailUiState(
    val card: Card? = null,
    val customFields: List<CustomField> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * Factory for creating CardDetailViewModel
 */
class CardDetailViewModelFactory(
    private val repository: CardRepository,
    private val cardId: Long,
    private val cardType: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardDetailViewModel(repository, cardId, cardType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
