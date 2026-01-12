package com.kartino.kartino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kartino.kartino.data.model.BankCard
import com.kartino.kartino.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for creating/editing bank cards
 */
class BankCardFormViewModel(
    private val repository: CardRepository,
    private val cardId: Long? = null
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BankCardFormUiState())
    val uiState: StateFlow<BankCardFormUiState> = _uiState.asStateFlow()
    
    init {
        if (cardId != null) {
            loadCard(cardId)
        }
    }
    
    /**
     * Load existing card for editing
     */
    private fun loadCard(id: Long) {
        viewModelScope.launch {
            repository.getBankCardById(id)?.let { card ->
                _uiState.value = _uiState.value.copy(
                    cardName = card.cardName,
                    bankName = card.bankName ?: "",
                    cardNumber = card.cardNumber ?: "",
                    expirationDate = card.expirationDate ?: "",
                    cvv = card.cvv ?: "",
                    pin = card.pin ?: "",
                    notes = card.notes ?: "",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Update card name
     */
    fun updateCardName(name: String) {
        _uiState.value = _uiState.value.copy(cardName = name)
    }
    
    /**
     * Update bank name
     */
    fun updateBankName(name: String) {
        _uiState.value = _uiState.value.copy(bankName = name)
    }
    
    /**
     * Update card number
     */
    fun updateCardNumber(number: String) {
        _uiState.value = _uiState.value.copy(cardNumber = number)
    }
    
    /**
     * Update expiration date
     */
    fun updateExpirationDate(date: String) {
        _uiState.value = _uiState.value.copy(expirationDate = date)
    }
    
    /**
     * Update CVV
     */
    fun updateCvv(cvv: String) {
        _uiState.value = _uiState.value.copy(cvv = cvv)
    }
    
    /**
     * Update PIN
     */
    fun updatePin(pin: String) {
        _uiState.value = _uiState.value.copy(pin = pin)
    }
    
    /**
     * Update notes
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    /**
     * Save the card (create or update)
     */
    fun saveCard(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.cardName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Card name is required")
            return
        }
        
        viewModelScope.launch {
            try {
                val card = BankCard(
                    id = cardId ?: 0L,
                    cardName = state.cardName,
                    bankName = state.bankName.takeIf { it.isNotBlank() },
                    cardNumber = state.cardNumber.takeIf { it.isNotBlank() },
                    expirationDate = state.expirationDate.takeIf { it.isNotBlank() },
                    cvv = state.cvv.takeIf { it.isNotBlank() },
                    pin = state.pin.takeIf { it.isNotBlank() },
                    notes = state.notes.takeIf { it.isNotBlank() }
                )
                
                if (cardId != null) {
                    repository.updateBankCard(card)
                } else {
                    repository.insertBankCard(card)
                }
                
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(errorMessage = "Failed to save card: ${e.message}")
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * UI State for Bank Card Form
 */
data class BankCardFormUiState(
    val cardName: String = "",
    val bankName: String = "",
    val cardNumber: String = "",
    val expirationDate: String = "",
    val cvv: String = "",
    val pin: String = "",
    val notes: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Factory for creating BankCardFormViewModel
 */
class BankCardFormViewModelFactory(
    private val repository: CardRepository,
    private val cardId: Long? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankCardFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BankCardFormViewModel(repository, cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
