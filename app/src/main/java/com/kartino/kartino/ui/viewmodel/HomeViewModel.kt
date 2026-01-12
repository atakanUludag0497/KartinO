package com.kartino.kartino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kartino.kartino.data.model.Card
import com.kartino.kartino.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen displaying list of all cards
 */
class HomeViewModel(
    private val repository: CardRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadCards()
    }
    
    /**
     * Load all cards from repository
     */
    private fun loadCards() {
        viewModelScope.launch {
            repository.getAllCards().collect { cards ->
                _uiState.value = _uiState.value.copy(
                    cards = cards,
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Delete a card
     */
    fun deleteCard(card: Card) {
        viewModelScope.launch {
            when (card) {
                is Card.Bank -> {
                    repository.deleteBankCard(card.bankCard)
                }
                is Card.Custom -> {
                    repository.deleteCustomCard(card.customCard)
                }
            }
        }
    }
    
    /**
     * Delete all cards
     */
    fun deleteAllCards() {
        viewModelScope.launch {
            repository.deleteAllCards()
        }
    }
}

/**
 * UI State for Home screen
 */
data class HomeUiState(
    val cards: List<Card> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * Factory for creating HomeViewModel
 */
class HomeViewModelFactory(
    private val repository: CardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
