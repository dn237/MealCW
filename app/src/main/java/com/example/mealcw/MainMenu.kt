package com.example.mealcw

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mealcw.ui.theme.MealCWTheme
import androidx.compose.material.icons.Icons.Default

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 1. Initialize Room Database and DAO
        val db = Room.databaseBuilder(this, AppData::class.java, "meals.db").build()
        val dao = db.mealDao()

        // 2. Create ViewModel FIRST (so it can be used below)
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MealViewModel(dao) as T
            }
        })[MealViewModel::class.java]

        setContent {
            // Apply the custom theme to the entire application
            MealCWTheme(darkTheme = viewModel.isDarkTheme) {
                val context = LocalContext.current

                // --- ADDED BACK BUTTON HANDLER HERE ---
                // If we are NOT on the Menu, the back button will return us to the Menu
                // instead of closing the app.
                BackHandler(enabled = viewModel.currentScreen != "Menu") {
                    viewModel.currentScreen = "Menu"
                }

                // Display status messages as transient Toast notifications
                LaunchedEffect(viewModel.statusMessage) {
                    if (viewModel.statusMessage.isNotEmpty()) {
                        Toast.makeText(context, viewModel.statusMessage, Toast.LENGTH_SHORT).show()
                        viewModel.statusMessage = ""
                    }
                }

                // Main Navigation Logic
                when (viewModel.currentScreen) {
                    "Menu" -> DisplayMainScreenMenu(viewModel)
                    "SearchByIngredient" -> SearchByIngredientScreen(viewModel)
                    "SearchForMeals" -> LocalDBSearch(viewModel)
                    "WebSearchByName" -> WebSearchByName(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayMainScreenMenu(viewModel: MealViewModel) {
    Scaffold(
        // Ensures the background color updates when switching themes
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Theme Toggle Section: Aligned to the top-right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (viewModel.isDarkTheme) "Dark Mode" else "Light Mode",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = viewModel.isDarkTheme,
                    onCheckedChange = { viewModel.isDarkTheme = it },
                    thumbContent = {
                        val icon = if (viewModel.isDarkTheme)
                            Default.DarkMode
                        else
                            Default.LightMode
                        Icon(icon, contentDescription = "Toggle Theme", modifier = Modifier.size(16.dp))
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Title Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Meal",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "HELPER",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 4.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Menu Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "Search by Ingredient",
                    subtitle = "Find recipes by what's in your fridge",
                    onClick = { viewModel.currentScreen = "SearchByIngredient" },
                    isPrimary = true
                )

                MenuButton(
                    text = "My Saved Meals",
                    subtitle = "Access your local meal collection",
                    onClick = { viewModel.currentScreen = "SearchForMeals" },
                    isPrimary = false
                )

                MenuButton(
                    text = "Web Search",
                    subtitle = "Search the full online meal library",
                    onClick = { viewModel.currentScreen = "WebSearchByName" },
                    isPrimary = false
                )
            }

            Spacer(modifier = Modifier.weight(1.2f))
            // Sync Data Button Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = { viewModel.addHardcodedMeals() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Meals to DB")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Reset Button
                OutlinedButton(
                    onClick = { viewModel.resetToDefault() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset to Hardcoded Only")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Copyright Section
                Text(
                    text = "v1.0 | Student ID: 20165015",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// Custom Button Component
@Composable
fun MenuButton(
    text: String,
    subtitle: String,
    onClick: () -> Unit,
    isPrimary: Boolean,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            // Primary buttons use the theme's primary color, others use surface white
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Title with bold style
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Subtitle with appropriate opacity
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPrimary) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}