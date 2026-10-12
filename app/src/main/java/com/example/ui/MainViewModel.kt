package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiFoodService
import com.example.data.local.AppDatabase
import com.example.data.local.GoalProfileEntity
import com.example.data.local.MealEntity
import com.example.data.local.SavedRecipeEntity
import com.example.data.model.DishAnalysisResult
import com.example.data.model.GeneratedRecipe
import com.example.data.model.GoalType
import com.example.data.model.MealType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    SCAN_DISH,
    CALORIE_TABLE,
    CREATE_RECIPE,
    SAVED_RECIPES,
    GOAL_CALCULATOR
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val mealDao = db.mealDao()
    private val recipeDao = db.recipeDao()
    private val goalDao = db.goalDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("EEEE، d MMMM", Locale.forLanguageTag("ar"))

    // Current Date for meal logging
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Screen navigation
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Dark Theme Toggle
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Meals for the selected date
    val currentMeals: StateFlow<List<MealEntity>> = _selectedDate
        .flatMapLatest { date -> mealDao.getMealsByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All saved recipes
    val savedRecipes: StateFlow<List<SavedRecipeEntity>> = recipeDao.getAllSavedRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Goals & Profile
    val goalProfile: StateFlow<GoalProfileEntity?> = goalDao.getGoalProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dish Scanner State
    private val _selectedDishImage = MutableStateFlow<Bitmap?>(null)
    val selectedDishImage: StateFlow<Bitmap?> = _selectedDishImage.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _dishAnalysisResult = MutableStateFlow<DishAnalysisResult?>(null)
    val dishAnalysisResult: StateFlow<DishAnalysisResult?> = _dishAnalysisResult.asStateFlow()

    // Recipe Generator State
    private val _isGeneratingRecipe = MutableStateFlow(false)
    val isGeneratingRecipe: StateFlow<Boolean> = _isGeneratingRecipe.asStateFlow()

    private val _generatedRecipeResult = MutableStateFlow<GeneratedRecipe?>(null)
    val generatedRecipeResult: StateFlow<GeneratedRecipe?> = _generatedRecipeResult.asStateFlow()

    // Feedback message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Initialize default goal profile if none exists
        viewModelScope.launch {
            val existing = goalDao.getGoalProfileSync()
            if (existing == null) {
                goalDao.insertOrUpdateGoal(
                    GoalProfileEntity(
                        id = 1,
                        goalType = GoalType.WEIGHT_LOSS.name,
                        targetCalories = 1850,
                        targetProtein = 130f,
                        targetCarbs = 180f,
                        targetFats = 55f,
                        currentWeightKg = 78f,
                        targetWeightKg = 72f,
                        waterGlassesToday = 5,
                        waterDate = getTodayDateString()
                    )
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun getFormattedSelectedDate(): String {
        return try {
            val parsed = dateFormat.parse(_selectedDate.value)
            if (parsed != null) displayDateFormat.format(parsed) else _selectedDate.value
        } catch (e: Exception) {
            _selectedDate.value
        }
    }

    fun changeDateOffset(offsetDays: Int) {
        val calendar = Calendar.getInstance()
        try {
            val current = dateFormat.parse(_selectedDate.value)
            if (current != null) calendar.time = current
            calendar.add(Calendar.DAY_OF_YEAR, offsetDays)
            _selectedDate.value = dateFormat.format(calendar.time)
        } catch (e: Exception) {
            _selectedDate.value = getTodayDateString()
        }
    }

    fun setToday() {
        _selectedDate.value = getTodayDateString()
    }

    // Dish Scanner Actions
    fun onDishImageSelected(bitmap: Bitmap?) {
        _selectedDishImage.value = bitmap
        _dishAnalysisResult.value = null
    }

    fun analyzeDish(dishHint: String = "") {
        val image = _selectedDishImage.value
        _isScanning.value = true
        viewModelScope.launch {
            val result = GeminiFoodService.analyzeDishPhoto(image, dishHint)
            _isScanning.value = false
            result.onSuccess {
                _dishAnalysisResult.value = it
            }.onFailure {
                showMessage("حدث خطأ أثناء تحليل الطبق، تم تفعيل التحليل الذاتي.")
            }
        }
    }

    // Recipe Generator Actions
    fun generateRecipe(
        ingredients: String,
        goal: GoalType,
        mealType: String,
        targetCalories: Int?
    ) {
        if (ingredients.isBlank()) {
            showMessage("يرجى كتابة بعض المكونات أولاً.")
            return
        }
        _isGeneratingRecipe.value = true
        viewModelScope.launch {
            val result = GeminiFoodService.generateRecipeFromIngredients(
                ingredientsInput = ingredients,
                goalType = goal,
                mealType = mealType,
                targetCalories = targetCalories
            )
            _isGeneratingRecipe.value = false
            result.onSuccess {
                _generatedRecipeResult.value = it
            }.onFailure {
                showMessage("تعذر توليد الوصفة، تم تحضير وصفة بديلة.")
            }
        }
    }

    // Meal Logging
    fun logMeal(
        title: String,
        mealType: MealType,
        calories: Int,
        protein: Float,
        carbs: Float,
        fats: Float,
        notes: String = ""
    ) {
        viewModelScope.launch {
            mealDao.insertMeal(
                MealEntity(
                    date = _selectedDate.value,
                    mealType = mealType.name,
                    title = title,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fats = fats,
                    notes = notes
                )
            )
            showMessage("تمت إضافة \"$title\" إلى سجل وجبات اليوم بنجاح!")
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealDao.deleteMeal(meal)
            showMessage("تم حذف الوجبة.")
        }
    }

    // Saved Recipes
    fun saveRecipe(
        title: String,
        category: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fats: Float,
        ingredients: List<String>,
        instructions: List<String>,
        chefTip: String
    ) {
        viewModelScope.launch {
            recipeDao.insertRecipe(
                SavedRecipeEntity(
                    title = title,
                    category = category,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fats = fats,
                    ingredients = ingredients.joinToString("\n"),
                    instructions = instructions.joinToString("\n"),
                    chefTip = chefTip
                )
            )
            showMessage("تم حفظ الوصفة في المفضلة!")
        }
    }

    fun deleteSavedRecipe(recipe: SavedRecipeEntity) {
        viewModelScope.launch {
            recipeDao.deleteRecipe(recipe)
            showMessage("تم حذف الوصفة من المحفوظات.")
        }
    }

    // Water Tracking
    fun incrementWater() {
        viewModelScope.launch {
            val current = goalProfile.value ?: return@launch
            val newGlasses = (current.waterGlassesToday + 1).coerceAtMost(20)
            goalDao.insertOrUpdateGoal(current.copy(waterGlassesToday = newGlasses))
        }
    }

    fun decrementWater() {
        viewModelScope.launch {
            val current = goalProfile.value ?: return@launch
            val newGlasses = (current.waterGlassesToday - 1).coerceAtLeast(0)
            goalDao.insertOrUpdateGoal(current.copy(waterGlassesToday = newGlasses))
        }
    }

    // Update Goals & Calculate TDEE
    fun updateGoalProfile(
        goalType: GoalType,
        currentWeightKg: Float,
        targetWeightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String,
        activityLevelMultiplier: Float = 1.375f // Lightly active default
    ) {
        viewModelScope.launch {
            // Mifflin-St Jeor Formula
            val bmr = if (gender == "MALE") {
                (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) + 5
            } else {
                (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) - 161
            }
            val tdee = bmr * activityLevelMultiplier
            val targetCalories = (tdee * goalType.calorieMultiplier).toInt().coerceIn(1200, 4500)

            // Macro calculation based on goal
            val targetProtein = when (goalType) {
                GoalType.WEIGHT_LOSS -> (currentWeightKg * 2.0f) // 2g per kg
                GoalType.MUSCLE_GAIN -> (currentWeightKg * 2.2f) // 2.2g per kg
                GoalType.MAINTENANCE -> (currentWeightKg * 1.6f)
            }
            val proteinCalories = targetProtein * 4
            val fatCalories = targetCalories * 0.25f
            val targetFats = fatCalories / 9f
            val remainingCalories = (targetCalories - proteinCalories - fatCalories).coerceAtLeast(200f)
            val targetCarbs = remainingCalories / 4f

            val current = goalProfile.value
            val updated = (current ?: GoalProfileEntity()).copy(
                goalType = goalType.name,
                targetCalories = targetCalories,
                targetProtein = targetProtein,
                targetCarbs = targetCarbs,
                targetFats = targetFats,
                currentWeightKg = currentWeightKg,
                targetWeightKg = targetWeightKg,
                heightCm = heightCm,
                age = age,
                gender = gender
            )
            goalDao.insertOrUpdateGoal(updated)
            showMessage("تم تحديث أهدافك وسعراتك اليومية بنجاح!")
        }
    }
}
