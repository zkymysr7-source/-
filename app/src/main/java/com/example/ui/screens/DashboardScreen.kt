package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.local.GoalProfileEntity
import com.example.data.local.MealEntity
import com.example.data.model.GoalType
import com.example.data.model.MealType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFats
import com.example.ui.theme.ColorProtein
import com.example.ui.theme.ColorWater

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val meals by viewModel.currentMeals.collectAsState()
    val goalProfile by viewModel.goalProfile.collectAsState()
    val formattedDate = viewModel.getFormattedSelectedDate()

    var showAddMealDialog by remember { mutableStateOf(false) }
    var selectedMealTypeForAdd by remember { mutableStateOf(MealType.LUNCH) }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.map { it.protein }.sum()
    val totalCarbs = meals.map { it.carbs }.sum()
    val totalFats = meals.map { it.fats }.sum()

    val targetCalories = goalProfile?.targetCalories ?: 2000
    val targetProtein = goalProfile?.targetProtein ?: 140f
    val targetCarbs = goalProfile?.targetCarbs ?: 200f
    val targetFats = goalProfile?.targetFats ?: 60f

    val remainingCalories = targetCalories - totalCalories
    val calProgress = (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
    val animatedCalProgress by animateFloatAsState(targetValue = calProgress, label = "cal_progress")

    val goalType = try {
        GoalType.valueOf(goalProfile?.goalType ?: "WEIGHT_LOSS")
    } catch (e: Exception) {
        GoalType.WEIGHT_LOSS
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Date Navigator Bar
            item {
                DateNavigatorCard(
                    formattedDate = formattedDate,
                    onPrevDay = { viewModel.changeDateOffset(-1) },
                    onNextDay = { viewModel.changeDateOffset(1) },
                    onToday = { viewModel.setToday() }
                )
            }

            // Hero Calorie & Goal Tracker Card
            item {
                CalorieGoalHeroCard(
                    totalCalories = totalCalories,
                    targetCalories = targetCalories,
                    remainingCalories = remainingCalories,
                    progress = animatedCalProgress,
                    goalType = goalType,
                    totalProtein = totalProtein,
                    targetProtein = targetProtein,
                    totalCarbs = totalCarbs,
                    targetCarbs = targetCarbs,
                    totalFats = totalFats,
                    targetFats = targetFats,
                    onConfigureGoal = { onNavigate(AppScreen.GOAL_CALCULATOR) }
                )
            }

            // Weight & Water Progress Card
            item {
                HydrationAndWeightCard(
                    goalProfile = goalProfile,
                    goalType = goalType,
                    onIncrementWater = { viewModel.incrementWater() },
                    onDecrementWater = { viewModel.decrementWater() },
                    onUpdateWeightClick = { onNavigate(AppScreen.GOAL_CALCULATOR) }
                )
            }

            // Quick AI Action Shortcuts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedCard(
                        onClick = { onNavigate(AppScreen.SCAN_DISH) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_scan_dish_shortcut"),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "تصوير الطبق",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "حساب سعرات الوجبة بالذكاء الاصطناعي",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    ElevatedCard(
                        onClick = { onNavigate(AppScreen.CREATE_RECIPE) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_create_recipe_shortcut"),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "تكوين وصفة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "وصفة صحية مخصصة بمكوناتك",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Calorie Table Shortcut Card
            item {
                ElevatedCard(
                    onClick = { onNavigate(AppScreen.CALORIE_TABLE) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_calorie_table_shortcut"),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "جدول السعرات الحرارية للأطعمة 🥗",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "تصفح أكثر من 40 صنفاً غذائياً وسجل منها مباشرة",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Meal Categories Sections (Breakfast, Lunch, Dinner, Snack)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "وجبات اليوم المسجلة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(
                        onClick = {
                            selectedMealTypeForAdd = MealType.LUNCH
                            showAddMealDialog = true
                        },
                        modifier = Modifier.testTag("add_meal_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة وجبة يدويًا")
                    }
                }
            }

            val mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK)
            items(mealTypes.size) { index ->
                val type = mealTypes[index]
                val typeMeals = meals.filter { it.mealType == type.name }
                MealCategoryCard(
                    mealType = type,
                    meals = typeMeals,
                    onAddMeal = {
                        selectedMealTypeForAdd = type
                        showAddMealDialog = true
                    },
                    onDeleteMeal = { meal -> viewModel.deleteMeal(meal) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Quick Floating Add Button
        FloatingActionButton(
            onClick = {
                selectedMealTypeForAdd = MealType.LUNCH
                showAddMealDialog = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_meal")
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة وجبة")
        }
    }

    if (showAddMealDialog) {
        AddMealDialog(
            initialMealType = selectedMealTypeForAdd,
            onDismiss = { showAddMealDialog = false },
            onConfirm = { title, type, cal, prot, carb, fat, notes ->
                viewModel.logMeal(title, type, cal, prot, carb, fat, notes)
                showAddMealDialog = false
            }
        )
    }
}

@Composable
fun DateNavigatorCard(
    formattedDate: String,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevDay,
                modifier = Modifier.testTag("prev_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "اليوم السابق"
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToday() }
            ) {
                Text(
                    text = formattedDate,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "اضغط للعودة لليوم",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onNextDay,
                modifier = Modifier.testTag("next_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "اليوم التالي"
                )
            }
        }
    }
}

@Composable
fun CalorieGoalHeroCard(
    totalCalories: Int,
    targetCalories: Int,
    remainingCalories: Int,
    progress: Float,
    goalType: GoalType,
    totalProtein: Float,
    targetProtein: Float,
    totalCarbs: Float,
    targetCarbs: Float,
    totalFats: Float,
    targetFats: Float,
    onConfigureGoal: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calorie_goal_hero_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Goal Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ملخص السعرات والماكروز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الهدف: ${goalType.titleAr}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    onClick = onConfigureGoal,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "تعديل الهدف",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Circular Calorie Visual & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calorie Circular Progress Ring
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(110.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 9.dp,
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(110.dp),
                        color = if (totalCalories > targetCalories) Color(0xFFE53935) else MaterialTheme.colorScheme.primary,
                        strokeWidth = 9.dp,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalCalories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "من $targetCalories",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "سعرة",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Calorie Status Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المتبقي لهدفك:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (remainingCalories >= 0) "$remainingCalories سعرة" else "${-remainingCalories} زيادة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingCalories >= 0) MaterialTheme.colorScheme.primary else Color(0xFFE53935)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val statusMsg = when {
                        totalCalories == 0 -> "لم تسجل أي وجبة بعد، ابدأ الآن!"
                        totalCalories <= targetCalories -> "أنت في المسار الصحيح لهدفك اليومي 👏"
                        else -> "تجاوزت الحد اليومي، ركز على خيارات خفيفة 🥗"
                    }
                    Text(
                        text = statusMsg,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress percentage badge
                    val pct = (progress * 100).toInt()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "تم استهلاك $pct% من الاحتياج اليومي",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Macronutrient Progress Bars (Protein, Carbs, Fats)
            Text(
                text = "العناصر الكبرى (الماكروز):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            MacroProgressBar(
                title = "البروتين",
                current = totalProtein,
                target = targetProtein,
                color = ColorProtein,
                unit = "غ"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MacroProgressBar(
                title = "الكربوهيدرات",
                current = totalCarbs,
                target = targetCarbs,
                color = ColorCarbs,
                unit = "غ"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MacroProgressBar(
                title = "الدهون الصحية",
                current = totalFats,
                target = targetFats,
                color = ColorFats,
                unit = "غ"
            )
        }
    }
}

@Composable
fun MacroProgressBar(
    title: String,
    current: Float,
    target: Float,
    color: Color,
    unit: String
) {
    val progress = (current / target.coerceAtLeast(1f)).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${current.toInt()} / ${target.toInt()} $unit (${(progress * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun HydrationAndWeightCard(
    goalProfile: GoalProfileEntity?,
    goalType: GoalType,
    onIncrementWater: () -> Unit,
    onDecrementWater: () -> Unit,
    onUpdateWeightClick: () -> Unit
) {
    val glasses = goalProfile?.waterGlassesToday ?: 0
    val targetGlasses = 8
    val currentWeight = goalProfile?.currentWeightKg ?: 75f
    val targetWeight = goalProfile?.targetWeightKg ?: 70f
    val weightDiff = currentWeight - targetWeight

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Water Tracker Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = ColorWater,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "$glasses / $targetGlasses أكواب",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorWater
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "شرب الماء",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDecrementWater,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "تقليل", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${glasses * 250} مل",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = onIncrementWater,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ColorWater.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "زيادة", tint = ColorWater, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Weight Progress Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .weight(1f)
                .clickable { onUpdateWeightClick() }
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "هدف الوزن",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$currentWeight كغ",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                val weightMsg = when (goalType) {
                    GoalType.WEIGHT_LOSS -> "الهدف: $targetWeight كغ (${if (weightDiff > 0) "باقي $weightDiff كغ" else "وصلت للهدف! 🎯"})"
                    GoalType.MUSCLE_GAIN -> "الهدف: $targetWeight كغ (${if (targetWeight > currentWeight) "باقي ${targetWeight - currentWeight} كغ" else "ممتاز! 💪"})"
                    GoalType.MAINTENANCE -> "المحافظة على وزن $targetWeight كغ"
                }

                Text(
                    text = weightMsg,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun MealCategoryCard(
    mealType: MealType,
    meals: List<MealEntity>,
    onAddMeal: () -> Unit,
    onDeleteMeal: (MealEntity) -> Unit
) {
    val totalMealCal = meals.sumOf { it.calories }

    val icon = when (mealType) {
        MealType.BREAKFAST -> Icons.Default.WbSunny
        MealType.LUNCH -> Icons.Default.Restaurant
        MealType.DINNER -> Icons.Default.NightlightRound
        MealType.SNACK -> Icons.Default.LocalDrink
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = mealType.titleAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$totalMealCal سعرة حرارية",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onAddMeal,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة إلى ${mealType.titleAr}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (meals.isEmpty()) {
                Text(
                    text = "لم يتم تسجيل أي صنف بعد.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 10.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                meals.forEach { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = meal.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${meal.calories} سعرة | ب:${meal.protein.toInt()}غ • ك:${meal.carbs.toInt()}غ • د:${meal.fats.toInt()}غ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onDeleteMeal(meal) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف الوجبة",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMealDialog(
    initialMealType: MealType,
    onDismiss: () -> Unit,
    onConfirm: (String, MealType, Int, Float, Float, Float, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialMealType) }
    var caloriesText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var carbsText by remember { mutableStateOf("") }
    var fatsText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل وجبة جديدة", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Meal type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.titleAr, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الوجبة أو الطبق *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { char -> char.isDigit() } },
                    label = { Text("السعرات الحرارية (Kcal) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("بروتين (غ)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = { Text("كارب (غ)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatsText,
                        onValueChange = { fatsText = it },
                        label = { Text("دهون (غ)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("ملاحظات (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val cal = caloriesText.toIntOrNull() ?: 250
                        val p = proteinText.toFloatOrNull() ?: 15f
                        val c = carbsText.toFloatOrNull() ?: 25f
                        val f = fatsText.toFloatOrNull() ?: 8f
                        onConfirm(title, selectedType, cal, p, c, f, notesText)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("حفظ في اليوميات")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
