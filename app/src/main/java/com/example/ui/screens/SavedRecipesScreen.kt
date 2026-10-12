package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.SavedRecipeEntity
import com.example.data.model.MealType
import com.example.ui.MainViewModel

data class CuratedRecipe(
    val title: String,
    val category: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val ingredients: List<String>,
    val instructions: List<String>,
    val chefTip: String
)

@Composable
fun SavedRecipesScreen(
    viewModel: MainViewModel
) {
    val savedList by viewModel.savedRecipes.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: مفضلاتي، 1: مكتبة الوصفات الصحية
    var mealToLog by remember { mutableStateOf<SavedRecipeEntity?>(null) }
    var curatedMealToLog by remember { mutableStateOf<CuratedRecipe?>(null) }

    val curatedRecipes = remember {
        listOf(
            CuratedRecipe(
                title = "سلطة كينوا سوبر فود بالأفوكادو",
                category = "غداء قليل السعرات",
                calories = 360,
                protein = 16f,
                carbs = 38f,
                fats = 14f,
                ingredients = listOf(
                    "كوب كينوا مطبوخة",
                    "نصف حبة أفوكادو مقطعة شرائح",
                    "كوب سبانخ طازجة وخيار",
                    "نصف كوب طماطم كرزية",
                    "ملعقة عصير ليمون ونقط زيت زيتون"
                ),
                instructions = listOf(
                    "اخلط الكينوا المطبوخة والباردة مع الخضروات المقطعة في وعاء واسع.",
                    "أضف عصير الليمون، رشة كمون وملح الهيمالايا.",
                    "زين بشرائح الأفوكادو ورشة من بذور اليقطين المقرمشة."
                ),
                chefTip = "وجبة ممتازة غنية بمضادات الأكسدة والدهون الأحادية غير المشبعة الصحية للقلب."
            ),
            CuratedRecipe(
                title = "صدر دجاج مشوي بالأعشاب والبروكلي",
                category = "غداء عالي البروتين",
                calories = 420,
                protein = 46f,
                carbs = 20f,
                fats = 11f,
                ingredients = listOf(
                    "200 غرام صدر دجاج منزوع الجلد",
                    "كوب ونصف بروكلي مطهو على البخار",
                    "ملعقة صغيرة زيت زيتون بكر",
                    "إكليل الجبل، ثوم مهروس، بابريكا وعصير ليمون"
                ),
                instructions = listOf(
                    "تبل صدر الدجاج بالثوم والبهارات والليمون لمدة 15 دقيقة.",
                    "اشوِ الدجاج على مقلاة جريل ساخنة لمدة 6 دقائق لكل جانب حتى ينضج تماماً.",
                    "اطبخ البروكلي على البخار لمدة 5 دقائق للحفاظ على لونه الزاهي وفيتاميناته.",
                    "قدمه مع رشة فلفل أسود وقطرات ليمون طازجة."
                ),
                chefTip = "وجبة أساسية ممتازة لبناء العضلات وخسارة الدهون بفضل كثافة البروتين العالية."
            ),
            CuratedRecipe(
                title = "بودينغ الشوفان وبذور الشيا بالتوت",
                category = "فطور غني بالألياف",
                calories = 310,
                protein = 18f,
                carbs = 42f,
                fats = 7f,
                ingredients = listOf(
                    "نصف كوب رقائق شوفان كاملة",
                    "ملعقة كبيرة بذور شيا",
                    "كوب حليب لوز أو زبادي يوناني خالي الدسم",
                    "نصف كوب توت بري أزرق أو فراولة",
                    "ملعقة صغيرة عسل طبيعي أو قرفة"
                ),
                instructions = listOf(
                    "اخلط الشوفان وبذور الشيا والحليب في وعاء زجاجي.",
                    "اتركه في الثلاجة طوال الليل أو لمدة ساعتين على الأقل.",
                    "زين بالتوت الطازج ورشة قرفة وملعقة صغيرة من العسل قبل التناول."
                ),
                chefTip = "بذور الشيا والشوفان توفر ألياف بيتا جلوكان التي تحافظ على الشبع لساعات طويلة وتنظم سكر الدم."
            ),
            CuratedRecipe(
                title = "سمك سلمون مشوي بالفرن مع الهليون",
                category = "عشاء صحي أوميغا 3",
                calories = 450,
                protein = 38f,
                carbs = 8f,
                fats = 24f,
                ingredients = listOf(
                    "180 غرام فيليه سلمون طازج",
                    "حزمة هليون طازج",
                    "فص ثوم مفروم وشرائح ليمون",
                    "ملعقة صغيرة زيت زيتون وشبت طازج"
                ),
                instructions = listOf(
                    "سخن الفرن على حرارة 200 درجة مئوية.",
                    "ضع السلمون والهليون على صينية بورق الزبدة وتبلها بالثوم والشبت ورذاذ زيت الزيتون.",
                    "ضع شرائح الليمون فوق السلمون واخبزه لمدة 14-16 دقيقة حتى يكتسب طراوة ولوناً وردياً."
                ),
                chefTip = "أوميغا 3 في السلمون يقلل الالتهابات ويدعم صحة الدماغ والاستشفاء العضلي."
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Tabs Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = { selectedTab = 0 },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "المحفوظات (${savedList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    onClick = { selectedTab = 1 },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مكتبة الوصفات الصحية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            if (savedList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد وصفات محفوظة بعد",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "عند تحليل طبق أو ابتكار وصفة جديدة، اضغط على زر \"حفظ بالمفضلة\" لتظهر هنا دائماً.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(savedList, key = { it.id }) { recipe ->
                    SavedRecipeItemCard(
                        recipe = recipe,
                        onDelete = { viewModel.deleteSavedRecipe(recipe) },
                        onLogToDiary = { mealToLog = recipe }
                    )
                }
            }
        } else {
            // Curated Healthy Library
            items(curatedRecipes) { recipe ->
                CuratedRecipeItemCard(
                    recipe = recipe,
                    onLogToDiary = { curatedMealToLog = recipe }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Dialog for logging saved recipe to diary
    mealToLog?.let { recipe ->
        var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
        AlertDialog(
            onDismissRequest = { mealToLog = null },
            title = { Text("تسجيل في يوميات الطعام", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إضافة \"${recipe.title}\" إلى:")
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logMeal(
                            title = recipe.title,
                            mealType = selectedMealType,
                            calories = recipe.calories,
                            protein = recipe.protein,
                            carbs = recipe.carbs,
                            fats = recipe.fats,
                            notes = "من الوصفات المحفوظة"
                        )
                        mealToLog = null
                    }
                ) {
                    Text("تسجيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { mealToLog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog for logging curated recipe to diary
    curatedMealToLog?.let { recipe ->
        var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
        AlertDialog(
            onDismissRequest = { curatedMealToLog = null },
            title = { Text("تسجيل في يوميات الطعام", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إضافة \"${recipe.title}\" إلى:")
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logMeal(
                            title = recipe.title,
                            mealType = selectedMealType,
                            calories = recipe.calories,
                            protein = recipe.protein,
                            carbs = recipe.carbs,
                            fats = recipe.fats,
                            notes = "من مكتبة الوصفات الصحية"
                        )
                        curatedMealToLog = null
                    }
                ) {
                    Text("تسجيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { curatedMealToLog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun SavedRecipeItemCard(
    recipe: SavedRecipeEntity,
    onDelete: () -> Unit,
    onLogToDiary: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${recipe.calories} سعرة حرارية • ب:${recipe.protein.toInt()}غ • ك:${recipe.carbs.toInt()}غ • د:${recipe.fats.toInt()}غ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "تفاصيل"
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                if (recipe.ingredients.isNotBlank()) {
                    Text("المقادير:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(recipe.ingredients, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (recipe.instructions.isNotBlank()) {
                    Text("طريقة التحضير:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(recipe.instructions, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (recipe.chefTip.isNotBlank()) {
                    Text("نصيحة الشيف: ${recipe.chefTip}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = onLogToDiary,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تسجيل هذه الوجبة في اليوميات اليوم")
                }
            }
        }
    }
}

@Composable
fun CuratedRecipeItemCard(
    recipe: CuratedRecipe,
    onLogToDiary: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${recipe.category} • ${recipe.calories} سعرة • ب:${recipe.protein.toInt()}غ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "تفاصيل"
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                Text("المكونات:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                recipe.ingredients.forEach {
                    Text("• $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("طريقة التحضير:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                recipe.instructions.forEachIndexed { i, s ->
                    Text("${i + 1}. $s", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("نصيحة الشيف: ${recipe.chefTip}", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onLogToDiary,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تسجيل هذه الوجبة في اليوميات")
                }
            }
        }
    }
}
