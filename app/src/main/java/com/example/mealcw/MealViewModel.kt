package com.example.mealcw


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

// The ViewModel handles background tasks and holds UI state
class MealViewModel(private val mealDao: MealDao) : ViewModel() {
    // UI State: Current screen to display
    var currentScreen by mutableStateOf("Menu")
    // Track if dark theme is enabled manually by the user
    var isDarkTheme by mutableStateOf(false)
    var searchResultsText by mutableStateOf("") // This holds the big string of meal details
    var statusMessage by mutableStateOf("")
    val mealList = mutableStateListOf<Meal>()
    var localSearchResults by mutableStateOf<List<Meal>>(emptyList())
    var localSearchQuery by mutableStateOf("")
    var ingredientName by mutableStateOf("")
    var mealNameQuery by mutableStateOf("")

    // Helper function to update the status message
    private suspend fun updateStatus(msg: String) {
        withContext(Dispatchers.Main) {
            statusMessage = msg
        }
    }

    // Function to add initial data to the Room database
    fun addHardcodedMeals() {
        viewModelScope.launch {
            mealDao.insertMeals(MealData.hardcodedMeals)
            updateStatus("Hardcoded meals added to database.")        }
    }

     // Fetches all meals saved in the local Room database
    fun loadAllSavedMeals() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Retrieve the list of Meal objects from the DAO
                val allMeals = mealDao.getAllMeals()

                // Switch back to the Main thread to update the UI State
                withContext(Dispatchers.Main) {
                    // Update the list used by LazyColumn to display MealCards
                    localSearchResults = allMeals

                    //Update the large text string with all the meals
                    val displayBuilder = StringBuilder()
                    allMeals.forEach { meal ->
                        displayBuilder.append(meal.formatToDisplayString()).append("\n\n---\n\n")
                    }
                    searchResultsText = displayBuilder.toString()

                    //update the status message for the user
                    statusMessage = if (allMeals.isEmpty()) {
                        "Your collection is empty."
                    } else {
                        "Successfully loaded ${allMeals.size} meals from local storage."
                    }
                }
            } catch (e: Exception) {
                // Error handling: update the status message if the database query fails
                withContext(Dispatchers.Main) {
                    statusMessage = "Database Error: ${e.message}"
                }
            }
        }
    }
    // Function to delete a meal from the local Room database
    fun deleteMealFromDB(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Delete the meal from the local database
                mealDao.deleteMeal(meal)

                // Refresh the local search results
                loadAllSavedMeals()

                //  Notify the user via the status message
                withContext(Dispatchers.Main) {
                    statusMessage = "Removed ${meal.strMeal} from collection"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusMessage = "Delete failed: ${e.message}"
                }
            }
        }
    }
    fun resetToDefault() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mealDao.deleteAllMeals()

                // Re-insert defaults
                mealDao.insertMeals(MealData.hardcodedMeals)

                // Refresh UI
                loadAllSavedMeals()

                withContext(Dispatchers.Main) {
                    statusMessage = "Database reset successfully"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusMessage = "Error: ${e.message}"
                }
            }
        }
    }

    // Function to search for meals by ingredient
    fun searchMealsByIngredient(ingredientName: String) {
        mealList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateStatus("Searching...")
                withContext(Dispatchers.Main) { searchResultsText = "" }
                val filterUrl =
                    "https://www.themealdb.com/api/json/v1/1/filter.php?i=$ingredientName"
                val filterJson = URL(filterUrl).readText()
                val filterRoot = JSONObject(filterJson)

                // 1. CHECK FOR NULL (Lecture 10)
                if (!filterRoot.isNull("meals")) {
                    val mealsArray = filterRoot.getJSONArray("meals")
                    val finalDisplay = StringBuilder()

                    for (i in 0 until mealsArray.length()) {
                        val id = mealsArray.getJSONObject(i).getString("idMeal")
                        val lookupUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id"
                        val fullJson = URL(lookupUrl).readText()

                        // DATABASE LOGIC: Convert the JSON to a Meal object and add to list
                        val mealObject = jsonToMealObject(fullJson)
                        withContext(Dispatchers.Main) {
                            mealList.add(mealObject)
                        }
                        val formattedText = mealObject.formatToDisplayString()
                        finalDisplay.append(formattedText).append("\n\n---\n\n")
                    }

                    withContext(Dispatchers.Main) { searchResultsText = finalDisplay.toString() }
                    updateStatus("Found ${mealsArray.length()} meals.")
                } else {
                    updateStatus("No meals found.")
                }
            } catch (e: Exception) {
                updateStatus("Error: ${e.message}")
            }
        }
    }

// Function to save all meals to the local Room database
    fun onSaveButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (mealList.isNotEmpty()) {
                    // Save the entire list to SQLite using Room
                    // We use .toList() to ensure we pass a stable list to Room
                    mealDao.insertMeals(mealList.toList())
                    updateStatus("Successfully saved ${mealList.size} meals to Database!")
                } else {
                    updateStatus("Nothing to save. Please search first.")
                }
            } catch (e: Exception) {
                updateStatus("Database Error: ${e.message}")
            }
        }
    }

    // Helper to convert Web JSON to a Room Entity
    private fun jsonToMealObject(jsonString: String): Meal {
        val root = JSONObject(jsonString)
        val meal = root.getJSONArray("meals").getJSONObject(0)

        return Meal(
            idMeal = meal.getString("idMeal"),
            strMeal = meal.getString("strMeal"),
            strCategory = meal.optString("strCategory", ""),
            strArea = meal.optString("strArea", ""),
            strInstructions = meal.optString("strInstructions", ""),
            strMealThumb = meal.optString("strMealThumb", ""),
            strTags = meal.optString("strTags", ""),
            strYoutube = meal.optString("strYoutube", ""),
            // Map all ingredients for full details storage
            strIngredient1 = meal.optString("strIngredient1", ""),
            strMeasure1 = meal.optString("strMeasure1", ""),
            strIngredient2 = meal.optString("strIngredient2", ""),
            strMeasure2 = meal.optString("strMeasure2", ""),
            strIngredient3 = meal.optString("strIngredient3", ""),
            strMeasure3 = meal.optString("strMeasure3", ""),
            strIngredient4 = meal.optString("strIngredient4", ""),
            strMeasure4 = meal.optString("strMeasure4", ""),
            strIngredient5 = meal.optString("strIngredient5", ""),
            strMeasure5 = meal.optString("strMeasure5", ""),
            strIngredient6 = meal.optString("strIngredient6", ""),
            strMeasure6 = meal.optString("strMeasure6", ""),
            strIngredient7 = meal.optString("strIngredient7", ""),
            strMeasure7 = meal.optString("strMeasure7", ""),
            strIngredient8 = meal.optString("strIngredient8", ""),
            strMeasure8 = meal.optString("strMeasure8", ""),
            strIngredient9 = meal.optString("strIngredient9", ""),
            strMeasure9 = meal.optString("strMeasure9", ""),
            strIngredient10 = meal.optString("strIngredient10", ""),
            strMeasure10 = meal.optString("strMeasure10", ""),
            strIngredient11 = meal.optString("strIngredient11", ""),
            strMeasure11 = meal.optString("strMeasure11", ""),
            strIngredient12 = meal.optString("strIngredient12", ""),
            strMeasure12 = meal.optString("strMeasure12", ""),
            strIngredient13 = meal.optString("strIngredient13", ""),
            strMeasure13 = meal.optString("strMeasure13", ""),
            strIngredient14 = meal.optString("strIngredient14", ""),
            strMeasure14 = meal.optString("strMeasure14", ""),
            strIngredient15 = meal.optString("strIngredient15", ""),
            strMeasure15 = meal.optString("strMeasure15", ""),
            strIngredient16 = meal.optString("strIngredient16", ""),
            strMeasure16 = meal.optString("strMeasure16", ""),
            strIngredient17 = meal.optString("strIngredient17", ""),
            strMeasure17 = meal.optString("strMeasure17", ""),
            strIngredient18 = meal.optString("strIngredient18", ""),
            strMeasure18 = meal.optString("strMeasure18", ""),
            strIngredient19 = meal.optString("strIngredient19", ""),
            strMeasure19 = meal.optString("strMeasure19", ""),
            strIngredient20 = meal.optString("strIngredient20", ""),
            strMeasure20 = meal.optString("strMeasure20", "")
        )
    }
    // Helper to format a Meal object for display
    fun Meal.formatToDisplayString(): String {
        val sb = StringBuilder()
        sb.append("\"Meal\":\"$strMeal\",\n")
        sb.append("\"Category\":\"$strCategory\",\n")
        sb.append("\"Area\":\"$strArea\",\n")
        sb.append("\"Instructions\":\n \"$strInstructions\",\n")
        sb.append("\"Tags\":\"$strTags\",\n")
        sb.append("\"Youtube\":\"$strYoutube\",\n")

        val ingredients = listOf(
            strIngredient1 to strMeasure1, strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3, strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5, strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7, strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9, strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13, strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15, strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17, strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19, strIngredient20 to strMeasure20
        )

        ingredients.forEachIndexed { index, (ingredient, measure) ->
            if (!ingredient.isNullOrBlank() && ingredient != "null") {
                sb.append("\"Ingredient${index + 1}\":\"$ingredient\",\n")
                sb.append("\"Measure${index + 1}\":\"$measure\",\n")
            }
        }
        return sb.toString()
    }

    // Function to search for meals in the local Room database
    fun searchInDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val nameMatches = mealDao.searchByName(localSearchQuery)
            val ingredientMatches = mealDao.searchByIngredients(localSearchQuery)
            val allResults = (nameMatches + ingredientMatches).distinctBy { it.idMeal }

            withContext(Dispatchers.Main) {
                localSearchResults = allResults
            }
            updateStatus(if (allResults.isEmpty()) "No matches found." else "Found ${allResults.size} matches.")
        }
    }

    // Function to search for meals by name
    fun searchMealsByName(mealNameQuery: String) {
        mealList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    statusMessage = "Searching..."
                    searchResultsText = ""
                }
                val searchURL = "https://www.themealdb.com/api/json/v1/1/search.php?s=$mealNameQuery"
                val searchJson = URL(searchURL).readText()
                val searchRoot = JSONObject(searchJson)

                // CHECK FOR NULL
                if (!searchRoot.isNull("meals")) {
                    val mealsArray = searchRoot.getJSONArray("meals")
                    val finalDisplay = StringBuilder()

                    for (i in 0 until mealsArray.length()) {
                        val mealEntryJson = mealsArray.getJSONObject(i).toString()
                        val compatibleJson = "{\"meals\":[$mealEntryJson]}"
                        val mealObject = jsonToMealObject(compatibleJson)

                        withContext(Dispatchers.Main) {
                            mealList.add(mealObject)
                        }
                        finalDisplay.append(mealObject.formatToDisplayString()).append("\n\n---\n\n")
                    }
                    withContext(Dispatchers.Main) { searchResultsText = finalDisplay.toString() }
                    updateStatus("Found ${mealsArray.length()} meals online.")
                } else {
                    updateStatus("No meals found with this name.")
                }
            } catch (e: Exception) {
                updateStatus("Error: ${e.message}")
            }
        }

    }
}

