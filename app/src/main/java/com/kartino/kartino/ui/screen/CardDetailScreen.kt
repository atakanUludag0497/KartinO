package com.kartino.kartino.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kartino.kartino.data.model.Card
import com.kartino.kartino.data.repository.CardRepository
import com.kartino.kartino.ui.viewmodel.CardDetailViewModel
import com.kartino.kartino.ui.viewmodel.CardDetailViewModelFactory

/**
 * Screen for viewing card details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    repository: CardRepository,
    cardId: Long,
    cardType: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long, String) -> Unit,
    onDeleteCard: () -> Unit
) {
    val viewModel: CardDetailViewModel = viewModel(
        factory = CardDetailViewModelFactory(repository, cardId, cardType)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    // Navigate back if card not found
    LaunchedEffect(uiState) {
        if (uiState == null || (uiState?.card == null && !uiState?.isLoading!!)) {
            onNavigateBack()
        }
    }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState?.card?.displayName ?: "Card Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState?.card != null) {
                        IconButton(onClick = { onNavigateToEdit(cardId, cardType) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState == null || uiState?.isLoading == true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val state = uiState!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (val currentCard = state.card) {
                    is Card.Bank -> {
                        BankCardDetailContent(currentCard.bankCard)
                    }
                    is Card.Custom -> {
                        CustomCardDetailContent(
                            customCard = currentCard.customCard,
                            fields = state.customFields
                        )
                    }
                    null -> {
                        // Should not happen, but handle gracefully
                        Text("Card not found")
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Card") },
            text = { Text("Are you sure you want to delete this card? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCard(onDeleteCard)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BankCardDetailContent(card: com.kartino.kartino.data.model.BankCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailRow("Card Name", card.cardName)
            card.bankName?.let { DetailRow("Bank Name", it) }
            card.cardNumber?.let { DetailRow("Card Number", it) }
            card.expirationDate?.let { DetailRow("Expiration Date", it) }
            card.cvv?.let { DetailRow("CVV/CVC", "•••") }
            card.pin?.let { DetailRow("PIN", "•••") }
            card.notes?.let { DetailRow("Notes", it) }
        }
    }
}

@Composable
fun CustomCardDetailContent(
    customCard: com.kartino.kartino.data.model.CustomCard,
    fields: List<com.kartino.kartino.data.model.CustomField>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailRow("Card Title", customCard.cardTitle)
            customCard.cardDescription?.let { DetailRow("Description", it) }
            
            if (fields.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Custom Fields",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                fields.forEach { field ->
                    DetailRow(field.fieldLabel, field.fieldValue ?: "")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
