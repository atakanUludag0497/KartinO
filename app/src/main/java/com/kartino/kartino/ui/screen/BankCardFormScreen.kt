package com.kartino.kartino.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kartino.kartino.data.repository.CardRepository
import com.kartino.kartino.ui.viewmodel.BankCardFormViewModel
import com.kartino.kartino.ui.viewmodel.BankCardFormViewModelFactory

/**
 * Screen for creating/editing bank cards
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankCardFormScreen(
    repository: CardRepository,
    cardId: Long?,
    onNavigateBack: () -> Unit
) {
    val viewModel: BankCardFormViewModel = viewModel(
        factory = BankCardFormViewModelFactory(repository, cardId)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Error will be shown in UI
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId != null) "Edit Card" else "New Bank Card") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveCard(onNavigateBack) },
                        enabled = !uiState.isLoading
                    ) {
                        //Icon(Icons.Default.Save, contentDescription = "Save")
                        Icon(Icons.Default.Star, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card Name (Required)
                OutlinedTextField(
                    value = uiState.cardName,
                    onValueChange = viewModel::updateCardName,
                    label = { Text("Card Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.cardName.isBlank() && uiState.errorMessage != null
                )
                
                // Bank Name
                OutlinedTextField(
                    value = uiState.bankName,
                    onValueChange = viewModel::updateBankName,
                    label = { Text("Bank Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Card Number
                OutlinedTextField(
                    value = uiState.cardNumber,
                    onValueChange = viewModel::updateCardNumber,
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                // Expiration Date
                OutlinedTextField(
                    value = uiState.expirationDate,
                    onValueChange = viewModel::updateExpirationDate,
                    label = { Text("Expiration Date (MM/YY)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("MM/YY") }
                )
                
                // CVV
                OutlinedTextField(
                    value = uiState.cvv,
                    onValueChange = viewModel::updateCvv,
                    label = { Text("CVV/CVC") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                // PIN
                OutlinedTextField(
                    value = uiState.pin,
                    onValueChange = viewModel::updatePin,
                    label = { Text("PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                // Notes
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::updateNotes,
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                // Error message
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                // Info text
                Text(
                    text = "* Required field. Other fields are optional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
