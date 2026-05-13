package com.example.mealcw

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Meal::class], version = 1, exportSchema = false)
abstract class AppData : RoomDatabase() {
    abstract fun mealDao(): MealDao
}