package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String, // BREAKFAST, LUNCH, DINNER, SNACK
    val title: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val ingredients: String, // Pipe or newline separated
    val instructions: String,
    val chefTip: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_goals")
data class GoalProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val goalType: String = "WEIGHT_LOSS", // WEIGHT_LOSS, MUSCLE_GAIN, MAINTENANCE
    val targetCalories: Int = 2000,
    val targetProtein: Float = 140f,
    val targetCarbs: Float = 200f,
    val targetFats: Float = 60f,
    val currentWeightKg: Float = 75f,
    val targetWeightKg: Float = 70f,
    val heightCm: Float = 175f,
    val age: Int = 28,
    val gender: String = "MALE", // MALE, FEMALE
    val waterGlassesToday: Int = 4,
    val waterDate: String = ""
)
