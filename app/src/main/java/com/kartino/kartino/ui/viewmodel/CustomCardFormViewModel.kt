package com.kartino.kartino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kartino.kartino.data.model.CustomCard
import com.kartino.kartino.data.model.CustomField
import com.kartino.kartino.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for creating/editing custom cards with dynamic fields
 */
class CustomCardFormViewModel(
    private val repository: CardRepository,
    private val cardId: Long? = null
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CustomCardFormUiState())
    val uiState: StateFlow<CustomCardFormUiState> = _uiState.asStateFlow()
    
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
            repository.getCustomCardById(id)?.let { card ->
                _uiState.value = _uiState.value.copy(
                    cardTitle = card.customCard.cardTitle,
                    cardDescription = card.customCard.cardDescription ?: "",
                    fields = card.fields.map { field ->
                        FieldItem(
                            id = field.id,
                            label = field.fieldLabel,
                            value = field.fieldValue ?: ""
                        )
                    },
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Update card title
     */
    fun updateCardTitle(title: String) {
        _uiState.value = _uiState.value.copy(cardTitle = title)
    }
    
    /**
     * Update card description
     */
    fun updateCardDescription(description: String) {
        _uiState.value = _uiState.value.copy(cardDescription = description)
    }
    
    /**
     * Add a new field
     */
    fun addField() {
        val currentFields = _uiState.value.fields
        val newField = FieldItem(
            id = 0L, // Temporary ID, will be assigned on save
            label = "",
            value = ""
        )
        _uiState.value = _uiState.value.copy(fields = currentFields + newField)
    }
    
    /**
     * Update a field's label
     */
    fun updateFieldLabel(index: Int, label: String) {
        val fields = _uiState.value.fields.toMutableList()
        if (index in fields.indices) {
            fields[index] = fields[index].copy(label = label)
            _uiState.value = _uiState.value.copy(fields = fields)
        }
    }
    
    /**
     * Update a field's value
     */
    fun updateFieldValue(index: Int, value: String) {
        val fields = _uiState.value.fields.toMutableList()
        if (index in fields.indices) {
            fields[index] = fields[index].copy(value = value)
            _uiState.value = _uiState.value.copy(fields = fields)
        }
    }
    
    /**
     * Delete a field
     */
    fun deleteField(index: Int) {
        val fields = _uiState.value.fields.toMutableList()
        if (index in fields.indices) {
            fields.removeAt(index)
            _uiState.value = _uiState.value.copy(fields = fields)
        }
    }
    
    /**
     * Save the card (create or update)
     */
    fun saveCard(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.cardTitle.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Card title is required")
            return
        }
        
        viewModelScope.launch {
            try {
                val card = CustomCard(
                    id = cardId ?: 0L,
                    cardTitle = state.cardTitle,
                    cardDescription = state.cardDescription.takeIf { it.isNotBlank() }
                )
                
                // Convert FieldItems to CustomFields, filtering out empty labels
                val fields = state.fields
                    .filter { it.label.isNotBlank() }
                    .map { fieldItem ->
                        CustomField(
                            id = fieldItem.id,
                            customCardId = cardId ?: 0L, // Will be updated after card creation
                            fieldLabel = fieldItem.label,
                            fieldValue = fieldItem.value.takeIf { it.isNotBlank() }
                        )
                    }
                
                if (cardId != null) {
                    repository.updateCustomCard(card, fields)
                } else {
                    val newCardId = repository.insertCustomCard(card, fields)
                    // Update field IDs if needed (though they'll be recreated)
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
 * UI State for Custom Card Form
 */
data class CustomCardFormUiState(
    val cardTitle: String = "",
    val cardDescription: String = "",
    val fields: List<FieldItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Represents a field in the form (before saving)
 */
data class FieldItem(
    val id: Long,
    val label: String,
    val value: String
)

/**
 * Factory for creating CustomCardFormViewModel
 */
class CustomCardFormViewModelFactory(
    private val repository: CardRepository,
    private val cardId: Long? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomCardFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomCardFormViewModel(repository, cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
