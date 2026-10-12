package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.FoodCalorieDatabase
import com.example.data.model.FoodCalorieItem
import com.example.data.model.MealType
import com.example.ui.MainViewModel
import com.example.ui.theme.ColorCalories
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFats
import com.example.ui.theme.ColorFiber
import com.example.ui.theme.ColorProtein

enum class CalorieSortOption(val titleAr: String) {
    DEFAULT("الافتراضي"),
    HIGHEST_PROTEIN("الأعلى بروتيناً"),
    LOWEST_CALORIES("الأقل سعرات"),
    LOWEST_FATS("الأقل دهوناً")
}

@Composable
fun CalorieTableScreen(
    viewModel: MainViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var selectedSort by remember { mutableStateOf(CalorieSortOption.DEFAULT) }
    var foodToLog by remember { mutableStateOf<FoodCalorieItem?>(null) }

    val categories = remember {
        listOf("الكل", "لحوم ودواجن", "أسماك وبحريات", "نشويات وحبوب", "ألبان وبيض", "خضروات", "بقوليات", "فواكه", "مكسرات وبذور")
    }

    val filteredItems = remember(searchQuery, selectedCategory, selectedSort) {
        var list = FoodCalorieDatabase.items

        if (selectedCategory != "الكل") {
            list = list.filter { it.category == selectedCategory }
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) || it.category.lowercase().contains(q)
            }
        }

        when (selectedSort) {
            CalorieSortOption.DEFAULT -> list
            CalorieSortOption.HIGHEST_PROTEIN -> list.sortedByDescending { it.protein }
            CalorieSortOption.LOWEST_CALORIES -> list.sortedBy { it.calories }
            CalorieSortOption.LOWEST_FATS -> list.sortedBy { it.fats }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header Hero Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "جدول السعرات الحرارية للأطعمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "مرجع غذائي شامل لحساب السعرات والماكروز بدقة، مع إمكانية تسجيل أي صنف مباشرة في سجل وجباتك.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن أي طعام (مثل: دجاج، سلمون، شوفان، بيض...)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calorie_table_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Categories Horizontal Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Sort Options Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عدد الأصناف: ${filteredItems.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    CalorieSortOption.values().forEach { sortOpt ->
                        FilterChip(
                            selected = selectedSort == sortOpt,
                            onClick = { selectedSort = sortOpt },
                            label = { Text(sortOpt.titleAr, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        // Food Items List
        items(filteredItems, key = { it.id }) { food ->
            FoodCalorieRowCard(
                item = food,
                onLog = { foodToLog = food }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Dialog for adding from Calorie Table into today's meals with custom portion
    foodToLog?.let { food ->
        var portionMultiplier by remember { mutableStateOf(1.0f) }
        var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }

        val dynamicCalories = (food.calories * portionMultiplier).toInt()
        val dynamicProtein = food.protein * portionMultiplier
        val dynamicCarbs = food.carbs * portionMultiplier
        val dynamicFats = food.fats * portionMultiplier

        AlertDialog(
            onDismissRequest = { foodToLog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(food.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("الحصة الافتراضية: ${food.servingSize}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Portion Multiplier Options
                    Text("اختر حجم الحصة:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val portions = listOf(
                            0.5f to "نصف حصة",
                            1.0f to "1 حصة",
                            1.5f to "1.5 حصة",
                            2.0f to "حصتان"
                        )
                        portions.forEach { (mult, label) ->
                            FilterChip(
                                selected = portionMultiplier == mult,
                                onClick = { portionMultiplier = mult },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Meal Type Selector
                    Text("إضافة إلى وجبة:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealType.values().forEach { type ->
                            FilterChip(
                                selected = selectedMealType == type,
                                onClick = { selectedMealType = type },
                                label = { Text(type.titleAr, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Live Calculated Nutrients Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$dynamicCalories", fontWeight = FontWeight.Bold, color = ColorCalories, fontSize = 14.sp)
                                Text("سعرة", fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${dynamicProtein.toInt()}غ", fontWeight = FontWeight.Bold, color = ColorProtein, fontSize = 14.sp)
                                Text("بروتين", fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${dynamicCarbs.toInt()}غ", fontWeight = FontWeight.Bold, color = ColorCarbs, fontSize = 14.sp)
                                Text("كارب", fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${dynamicFats.toInt()}غ", fontWeight = FontWeight.Bold, color = ColorFats, fontSize = 14.sp)
                                Text("دهون", fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val portionDesc = if (portionMultiplier == 1.0f) food.servingSize else "حصة x$portionMultiplier (${food.servingSize})"
                        viewModel.logMeal(
                            title = "${food.name} ($portionDesc)",
                            mealType = selectedMealType,
                            calories = dynamicCalories,
                            protein = dynamicProtein,
                            carbs = dynamicCarbs,
                            fats = dynamicFats,
                            notes = "من جدول السعرات"
                        )
                        foodToLog = null
                    }
                ) {
                    Text("تسجيل في السجل")
                }
            },
            dismissButton = {
                TextButton(onClick = { foodToLog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun FoodCalorieRowCard(
    item: FoodCalorieItem,
    onLog: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${item.category} • ${item.servingSize}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Calorie Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = ColorCalories,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.calories} سعرة",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = ColorCalories
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nutrient Micro Badges & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    MacroPill("بروتين", "${item.protein}غ", ColorProtein)
                    MacroPill("كارب", "${item.carbs}غ", ColorCarbs)
                    MacroPill("دهون", "${item.fats}غ", ColorFats)
                    if (item.fiber > 0) {
                        MacroPill("ألياف", "${item.fiber}غ", ColorFiber)
                    }
                }

                // Quick Add to Diary Button
                Surface(
                    onClick = onLog,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.testTag("log_food_item_${item.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تسجيل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (item.healthTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 ${item.healthTip}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun MacroPill(
    title: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = "$title: $value",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
