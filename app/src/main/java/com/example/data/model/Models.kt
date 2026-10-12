package com.example.data.model

enum class GoalType(val titleAr: String, val descAr: String, val calorieMultiplier: Float) {
    WEIGHT_LOSS("فقدان الوزن", "حرق الدهون مع الحفاظ على الكتلة العضلية", 0.82f),
    MUSCLE_GAIN("زيادة العضلات", "فائض سعرات خفيف مع تركيز عالي على البروتين", 1.15f),
    MAINTENANCE("المحافظة على الوزن", "توازن مثالي للسعرات والنشاط اليومي", 1.0f)
}

enum class MealType(val titleAr: String, val iconName: String) {
    BREAKFAST("فطور", "wb_sunny"),
    LUNCH("غداء", "restaurant"),
    DINNER("عشاء", "nightlight_round"),
    SNACK("وجبة خفيفة", "local_cafe")
}

data class DishAnalysisResult(
    val dishName: String,
    val healthyAlternativeName: String = "",
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatsGrams: Float,
    val fiberGrams: Float = 3f,
    val healthScore: Int = 8, // 1 to 10
    val summary: String,
    val ingredients: List<String> = emptyList(),
    val recipeSteps: List<String> = emptyList(),
    val tipsForHealth: List<String> = emptyList()
)

data class GeneratedRecipe(
    val title: String,
    val category: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float = 4f,
    val prepTime: String,
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val chefTip: String = "",
    val goalSuitability: String = ""
)
