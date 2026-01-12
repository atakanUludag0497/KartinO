package com.kartino.kartino.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kartino.kartino.data.repository.CardRepository
import com.kartino.kartino.ui.viewmodel.CustomCardFormViewModel
import com.kartino.kartino.ui.viewmodel.CustomCardFormViewModelFactory

/**
 * Screen for creating/editing custom cards with dynamic fields
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCardFormScreen(
    repository: CardRepository,
    cardId: Long?,
    onNavigateBack: () -> Unit
) {
    val viewModel: CustomCardFormViewModel = viewModel(
        factory = CustomCardFormViewModelFactory(repository, cardId)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId != null) "Edit Card" else "New Custom Card") },
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
                // Card Title (Required)
                OutlinedTextField(
                    value = uiState.cardTitle,
                    onValueChange = viewModel::updateCardTitle,
                    label = { Text("Card Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.cardTitle.isBlank() && uiState.errorMessage != null
                )
                
                // Card Description
                OutlinedTextField(
                    value = uiState.cardDescription,
                    onValueChange = viewModel::updateCardDescription,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                Divider()
                
                // Dynamic Fields Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom Fields",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.addField() }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Field")
                    }
                }
                
                // Fields List
                uiState.fields.forEachIndexed { index, field ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Field ${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.deleteField(index) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Field",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            OutlinedTextField(
                                value = field.label,
                                onValueChange = { viewModel.updateFieldLabel(index, it) },
                                label = { Text("Field Label") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("e.g., Serial Number, Balance, Zone") }
                            )
                            
                            OutlinedTextField(
                                value = field.value,
                                onValueChange = { viewModel.updateFieldValue(index, it) },
                                label = { Text("Field Value") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }
                
                if (uiState.fields.isEmpty()) {
                    Text(
                        text = "No custom fields added. Tap 'Add Field' to create one.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
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
