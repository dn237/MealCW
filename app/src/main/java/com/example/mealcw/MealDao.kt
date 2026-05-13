package com.example.mealcw

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



//data access object which communicates with the database

@Dao
interface MealDao {
    //insert all meals
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<Meal>)

    // delete a meal
    @Delete
    suspend fun deleteMeal(meal: Meal)

    // delete all meals
    @Query("DELETE FROM Meal")
    suspend fun deleteAllMeals()
    // get all meals
    @Query("SELECT * FROM Meal")
    suspend fun getAllMeals(): List<Meal>

    // search for meals by name
    @Query("SELECT * FROM Meal WHERE strMeal LIKE '%' || :mealNameQuery || '%'")
    suspend fun searchByName(mealNameQuery: String): List<Meal>

    // search for meals by ingredient name
    @Query("""SELECT * FROM Meal
    WHERE strIngredient1 LIKE '%' || :ingredientName || '%' 
       OR strIngredient2 LIKE '%' || :ingredientName || '%' 
       OR strIngredient3 LIKE '%' || :ingredientName || '%' 
       OR strIngredient4 LIKE '%' || :ingredientName || '%' 
       OR strIngredient5 LIKE '%' || :ingredientName || '%' 
       OR strIngredient6 LIKE '%' || :ingredientName || '%' 
       OR strIngredient7 LIKE '%' || :ingredientName || '%' 
       OR strIngredient8 LIKE '%' || :ingredientName || '%' 
       OR strIngredient9 LIKE '%' || :ingredientName || '%' 
       OR strIngredient10 LIKE '%' || :ingredientName || '%' 
       OR strIngredient11 LIKE '%' || :ingredientName || '%' 
       OR strIngredient12 LIKE '%' || :ingredientName || '%' 
       OR strIngredient13 LIKE '%' || :ingredientName || '%' 
       OR strIngredient14 LIKE '%' || :ingredientName || '%' 
       OR strIngredient15 LIKE '%' || :ingredientName || '%' 
       OR strIngredient16 LIKE '%' || :ingredientName || '%' 
       OR strIngredient17 LIKE '%' || :ingredientName || '%' 
       OR strIngredient18 LIKE '%' || :ingredientName || '%' 
       OR strIngredient19 LIKE '%' || :ingredientName || '%' 
       OR strIngredient20 LIKE '%' || :ingredientName || '%'
""")
    suspend fun searchByIngredients(ingredientName: String): List<Meal>

}

