package com.example.mealcw

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.example.mealcw.ui.theme.NewTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDBSearch(viewModel : MealViewModel) {
    // AUTOMATIC LOADING:
    // This side-effect triggers as soon as the screen is composed.
    // It calls the ViewModel to fetch all meals from the Room database.
    LaunchedEffect(Unit) {
        viewModel.loadAllSavedMeals()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Meals", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    // Navigate back to the Main Menu
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
            // Search Input for filtering the local database
            NewTextField(
                value = viewModel.localSearchQuery,
                onValueChange = {
                    viewModel.localSearchQuery = it
                    // Optional: Real-time search/filtering as the user types
                    viewModel.searchInDatabase()
                },
                label = "Enter meal name or ingredient",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Search
            )

            // Explicit search button
            Button(
                onClick = { viewModel.searchInDatabase() },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                Text("Search in DB")
            }

            // Status message showing the number of results or errors
            Text(
                text = viewModel.statusMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // If the database is empty or no search matches are found show a message
            if (viewModel.localSearchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (viewModel.localSearchQuery.isEmpty())
                            "Your collection is empty.\nGo to Web Search to save some meals!"
                        else "No local matches found.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                // Displays the saved meals.
                LazyColumn {
                    items(viewModel.localSearchResults) { meal ->
                        MealCard(
                            meal = meal,
                            onDeleteClick = { mealToDelete ->
                                viewModel.deleteMealFromDB(mealToDelete)
                            }
                        )
                    }
                }
            }
        }
    }
}
// Helper function to load and display an image
@Composable
fun ManualMealImage(url: String?) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // LaunchedEffect runs when the URL changes or when the item enters the screen
    LaunchedEffect(url) {
        if (!url.isNullOrEmpty()) {
            try {
                // Move the image loading to a background thread
                val loadedBitmap = withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
                // Update the state on the main thread
                bitmap = loadedBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Display the image once it's loaded, otherwise show a placeholder
    if (bitmap != null) {
        Image(bitmap = bitmap!!,
            contentDescription = "Meal Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Keeps all images the same height
            contentScale = androidx.compose.ui.layout.ContentScale.Crop  // Crops the image to fit
        )
    } else {
        // Placeholder while the image is loading
        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Loading image...", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
@Composable
fun MealCard(
    meal: Meal,
    onDeleteClick: (Meal) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (!meal.strMealThumb.isNullOrEmpty()) {
                ManualMealImage(meal.strMealThumb)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row with Title and Delete Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = meal.strMeal,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDeleteClick(meal) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "Category: ${meal.strCategory}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.Gray
                )

                // YouTube Link Section
                Text(
                    buildAnnotatedString {
                        append("Youtube: ")
                        withLink(
                            LinkAnnotation.Url(
                                "${meal.strYoutube}",
                                TextLinkStyles(style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.Bold
                                ))
                            )
                        ) {
                            append("CLICK HERE")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isExpanded) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = meal.strInstructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Tap to show instructions...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}


