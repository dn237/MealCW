package com.example.mealcw

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealcw.ui.theme.NewTextField
import com.example.mealcw.ui.theme.formatMealResultsAnnotated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByIngredientScreen(viewModel: MealViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Web Search", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen = "Menu" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Input Section
            NewTextField(
                value = viewModel.ingredientName,
                onValueChange = { viewModel.ingredientName = it },
                label = "Enter ingredient name",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Search            )

            Button(
                onClick = { viewModel.searchMealsByIngredient(viewModel.ingredientName) },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Retrieve Meals")
            }

            // Results Label (Using your TitleSmall)
            Text(
                text = "SEARCH RESULTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Progress Bar for Searching
            if (viewModel.statusMessage == "Searching...") {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (viewModel.searchResultsText.isNotEmpty()) {
                        Text(
                            text = formatMealResultsAnnotated(viewModel.searchResultsText),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            ),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    } else {
                        Text(
                            text = "No results to display",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = { viewModel.onSaveButtonClicked() },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.searchResultsText.isNotEmpty()
            ) {
                Text("Save meals to Database")
            }
        }
    }
}