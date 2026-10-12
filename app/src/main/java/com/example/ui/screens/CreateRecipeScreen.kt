package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedRecipe
import com.example.data.model.GoalType
import com.example.data.model.MealType
import com.example.ui.MainViewModel
import com.example.ui.theme.ColorCalories
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFats
import com.example.ui.theme.ColorFiber
import com.example.ui.theme.ColorProtein

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRecipeScreen(
    viewModel: MainViewModel
) {
    val goalProfile by viewModel.goalProfile.collectAsState()
    val isGenerating by viewModel.isGeneratingRecipe.collectAsState()
    val recipeResult by viewModel.generatedRecipeResult.collectAsState()

    var ingredientsText by remember { mutableStateOf("") }
    var selectedGoal by remember {
        mutableStateOf(
            try {
                GoalType.valueOf(goalProfile?.goalType ?: "WEIGHT_LOSS")
            } catch (e: Exception) {
                GoalType.WEIGHT_LOSS
            }
        )
    }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var selectedCalorieTarget by remember { mutableStateOf<Int?>(null) }
    var showLogMealDialog by remember { mutableStateOf(false) }

    val quickIngredients = listOf(
        "صدر دجاج", "بيض", "شوفان", "سبانخ", "أفوكادو",
        "تونة", "زبادي يوناني", "بروكلي", "كينوا", "طماطم",
        "بطاطا حلوة", "جبن قريش", "سلمون", "مكسرات نيئة"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header Hero
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ابتكار وصفات صحية بمكوناتك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "اكتب المكونات المتوفرة في ثلاجتك، وسيقوم الذكاء الاصطناعي بابتكار وجبة صحية محسوبة السعرات تناسب هدفك بدقة.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Ingredients Form Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المكونات المتاحة لديك:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ingredientsText,
                        onValueChange = { ingredientsText = it },
                        placeholder = { Text("مثلاً: صدر دجاج، فطر، سبانخ، شوفان، ثوم...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("ingredients_input_field"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "إضافة سريعة لمكونات شائعة:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Ingredients FlowRow
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickIngredients.forEach { item ->
                            Surface(
                                onClick = {
                                    ingredientsText = if (ingredientsText.isBlank()) {
                                        item
                                    } else if (!ingredientsText.contains(item)) {
                                        "$ingredientsText، $item"
                                    } else {
                                        ingredientsText
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(item, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Goal Selection
                    Text(
                        text = "هدفك الرياضي والصحي للوجبة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GoalType.values().forEach { goal ->
                            FilterChip(
                                selected = selectedGoal == goal,
                                onClick = { selectedGoal = goal },
                                label = { Text(goal.titleAr, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Meal Type Selection
                    Text(
                        text = "نوع الوجبة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealType.values().forEach { type ->
                            FilterChip(
                                selected = selectedMealType == type,
                                onClick = { selectedMealType = type },
                                label = { Text(type.titleAr, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calorie Target Chips
                    Text(
                        text = "السعرات المستهدفة التقريبية:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val calOptions = listOf(
                            null to "تلقائي",
                            350 to "350 سعرة",
                            450 to "450 سعرة",
                            600 to "600 سعرة"
                        )
                        calOptions.forEach { (cal, label) ->
                            FilterChip(
                                selected = selectedCalorieTarget == cal,
                                onClick = { selectedCalorieTarget = cal },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Generate Button
                    Button(
                        onClick = {
                            viewModel.generateRecipe(
                                ingredients = ingredientsText,
                                goal = selectedGoal,
                                mealType = selectedMealType.titleAr,
                                targetCalories = selectedCalorieTarget
                            )
                        },
                        enabled = !isGenerating && ingredientsText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_recipe_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("جارٍ ابتكار الوصفة بالذكاء الاصطناعي...")
                        } else {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ابتكار الوصفة الصحية الآن", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Generated Recipe Result Display
        recipeResult?.let { recipe ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    GeneratedRecipeResultCard(
                        recipe = recipe,
                        onLogToDiary = { showLogMealDialog = true },
                        onSaveToFavorites = {
                            viewModel.saveRecipe(
                                title = recipe.title,
                                category = recipe.category,
                                calories = recipe.calories,
                                protein = recipe.protein,
                                carbs = recipe.carbs,
                                fats = recipe.fats,
                                ingredients = recipe.ingredients,
                                instructions = recipe.instructions,
                                chefTip = recipe.chefTip
                            )
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showLogMealDialog && recipeResult != null) {
        val recipe = recipeResult!!
        var chosenMealType by remember { mutableStateOf(selectedMealType) }

        AlertDialog(
            onDismissRequest = { showLogMealDialog = false },
            title = { Text("تسجيل في يوميات الطعام", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إضافة إلى وجبة:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealType.values().forEach { type ->
                            FilterChip(
                                selected = chosenMealType == type,
                                onClick = { chosenMealType = type },
                                label = { Text(type.titleAr, fontSize = 11.sp) }
                            )
                        }
                    }
                    Text(
                        text = "الوصفة: ${recipe.title}\nالسعرات: ${recipe.calories} سعرة | ب:${recipe.protein.toInt()}غ • ك:${recipe.carbs.toInt()}غ • د:${recipe.fats.toInt()}غ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logMeal(
                            title = recipe.title,
                            mealType = chosenMealType,
                            calories = recipe.calories,
                            protein = recipe.protein,
                            carbs = recipe.carbs,
                            fats = recipe.fats,
                            notes = "وصفة مبتكرة بالذكاء الاصطناعي"
                        )
                        showLogMealDialog = false
                    }
                ) {
                    Text("تسجيل في السجل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogMealDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun GeneratedRecipeResultCard(
    recipe: GeneratedRecipe,
    onLogToDiary: () -> Unit,
    onSaveToFavorites: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generated_recipe_result_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = recipe.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Prep time badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(recipe.prepTime, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nutritional Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientBadge("السعرات", "${recipe.calories}", "سعرة", ColorCalories, Modifier.weight(1f))
                NutrientBadge("بروتين", "${recipe.protein.toInt()}", "غ", ColorProtein, Modifier.weight(1f))
                NutrientBadge("كارب", "${recipe.carbs.toInt()}", "غ", ColorCarbs, Modifier.weight(1f))
                NutrientBadge("دهون", "${recipe.fats.toInt()}", "غ", ColorFats, Modifier.weight(1f))
                NutrientBadge("ألياف", "${recipe.fiber.toInt()}", "غ", ColorFiber, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Goal suitability note
            if (recipe.goalSuitability.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = recipe.goalSuitability,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Ingredients
            Text(
                text = "المقادير الصحية والمكونات:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            recipe.ingredients.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instructions
            Text(
                text = "طريقة الإعداد خطوة بخطوة:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            recipe.instructions.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(step, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }

            // Chef Tip
            if (recipe.chefTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(recipe.chefTip, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onLogToDiary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("log_generated_recipe_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أضف لليوميات", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onSaveToFavorites,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_generated_recipe_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ بالمفضلة", fontSize = 13.sp)
                }
            }
        }
    }
}
