package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.data.model.GoalType
import com.example.ui.MainViewModel
import com.example.ui.theme.ColorCalories
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFats
import com.example.ui.theme.ColorProtein

@Composable
fun GoalCalculatorScreen(
    viewModel: MainViewModel
) {
    val goalProfile by viewModel.goalProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var selectedGoal by remember(goalProfile) {
        mutableStateOf(
            try {
                GoalType.valueOf(goalProfile?.goalType ?: "WEIGHT_LOSS")
            } catch (e: Exception) {
                GoalType.WEIGHT_LOSS
            }
        )
    }

    var gender by remember(goalProfile) { mutableStateOf(goalProfile?.gender ?: "MALE") }
    var ageText by remember(goalProfile) { mutableStateOf((goalProfile?.age ?: 28).toString()) }
    var currentWeightText by remember(goalProfile) { mutableStateOf((goalProfile?.currentWeightKg ?: 78f).toInt().toString()) }
    var targetWeightText by remember(goalProfile) { mutableStateOf((goalProfile?.targetWeightKg ?: 72f).toInt().toString()) }
    var heightText by remember(goalProfile) { mutableStateOf((goalProfile?.heightCm ?: 175f).toInt().toString()) }
    var activityLevelMultiplier by remember { mutableStateOf(1.375f) } // 1.2: قليل، 1.375: متوسط، 1.55: نشيط

    // Calculate preview values
    val currentWeight = currentWeightText.toFloatOrNull() ?: 75f
    val targetWeight = targetWeightText.toFloatOrNull() ?: 70f
    val height = heightText.toFloatOrNull() ?: 175f
    val age = ageText.toIntOrNull() ?: 28

    val bmr = if (gender == "MALE") {
        (10 * currentWeight) + (6.25f * height) - (5 * age) + 5
    } else {
        (10 * currentWeight) + (6.25f * height) - (5 * age) - 161
    }
    val tdee = bmr * activityLevelMultiplier
    val calculatedCalories = (tdee * selectedGoal.calorieMultiplier).toInt().coerceIn(1200, 4500)

    val targetProtein = when (selectedGoal) {
        GoalType.WEIGHT_LOSS -> currentWeight * 2.0f
        GoalType.MUSCLE_GAIN -> currentWeight * 2.2f
        GoalType.MAINTENANCE -> currentWeight * 1.6f
    }
    val proteinCalories = targetProtein * 4
    val fatCalories = calculatedCalories * 0.25f
    val targetFats = fatCalories / 9f
    val remainingCalories = (calculatedCalories - proteinCalories - fatCalories).coerceAtLeast(200f)
    val targetCarbs = remainingCalories / 4f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Free App Banner (Requested by user)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9)),
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
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تطبيق مجاني بالكامل 100%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "جميع مزايا تحليل الوجبات وحساب السعرات وتكوين الوصفات مفتوحة ومجانية بدون أي اشتراكات أو قيود.",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Dark/Light Theme Setting Switch Card (Requested by user)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "مظهر التطبيق (الوضع الداكن / الفاتح)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isDarkMode) "الوضع الداكن مفعل حالياً" else "الوضع الفاتح مفعل حالياً",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("settings_theme_switch")
                    )
                }
            }
        }

        // Goal Selection Cards
        item {
            Text(
                text = "اختر هدفك الصحي والبدني:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GoalSelectionItem(
                    title = "فقدان الوزن وحرق الدهون",
                    desc = "عجز سعرات ذكي لحرق الدهون والحفاظ على عضلاتك.",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    isSelected = selectedGoal == GoalType.WEIGHT_LOSS,
                    onClick = { selectedGoal = GoalType.WEIGHT_LOSS }
                )

                GoalSelectionItem(
                    title = "زيادة العضلات وبناء الجسم",
                    desc = "فائض طاقة مع تركيز عالٍ على البروتين لبناء كتلة عضلية نقية.",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    isSelected = selectedGoal == GoalType.MUSCLE_GAIN,
                    onClick = { selectedGoal = GoalType.MUSCLE_GAIN }
                )

                GoalSelectionItem(
                    title = "المحافظة على الوزن والصحة العامة",
                    desc = "توازن مثالي بين السعرات والنشاط مع نمط حياة نشط.",
                    icon = Icons.AutoMirrored.Filled.TrendingFlat,
                    isSelected = selectedGoal == GoalType.MAINTENANCE,
                    onClick = { selectedGoal = GoalType.MAINTENANCE }
                )
            }
        }

        // Body Measurements & Activity Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "بياناتك البدنية لحساب السعرات الدقيقة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = gender == "MALE",
                            onClick = { gender = "MALE" },
                            label = { Text("ذكر 👨", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = gender == "FEMALE",
                            onClick = { gender = "FEMALE" },
                            label = { Text("أنثى 👩", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Weight and Target Weight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = currentWeightText,
                            onValueChange = { currentWeightText = it },
                            label = { Text("الوزن الحالي (كغ)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetWeightText,
                            onValueChange = { targetWeightText = it },
                            label = { Text("الوزن المستهدف (كغ)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Height and Age
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { heightText = it },
                            label = { Text("الطول (سم)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("العمر (سنة)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Activity Level
                    Text(
                        text = "مستوى نشاطك البدني الأسبوعي:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val activities = listOf(
                        1.2f to "خامل (مكتبي بدون تمارين)",
                        1.375f to "متوسط (1-3 أيام تمارين)",
                        1.55f to "نشيط (3-5 أيام تمارين)"
                    )
                    activities.forEach { (mult, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activityLevelMultiplier = mult }
                                .padding(vertical = 4.dp)
                        ) {
                            FilterChip(
                                selected = activityLevelMultiplier == mult,
                                onClick = { activityLevelMultiplier = mult },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Live Calculated Targets Result Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "الخطة المحسوبة علمياً لهدفك:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "$calculatedCalories سعرة حرارية يومياً",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedGoal.titleAr,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Macros Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroSummaryBadge("بروتين", "${targetProtein.toInt()}غ", ColorProtein, Modifier.weight(1f))
                        MacroSummaryBadge("كاربوهيدرات", "${targetCarbs.toInt()}غ", ColorCarbs, Modifier.weight(1f))
                        MacroSummaryBadge("دهون صحية", "${targetFats.toInt()}غ", ColorFats, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save as current goal button
                    Button(
                        onClick = {
                            viewModel.updateGoalProfile(
                                goalType = selectedGoal,
                                currentWeightKg = currentWeight,
                                targetWeightKg = targetWeight,
                                heightCm = height,
                                age = age,
                                gender = gender,
                                activityLevelMultiplier = activityLevelMultiplier
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_goals_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اعتماد كهدفي وتحديث اليوميات", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun GoalSelectionItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MacroSummaryBadge(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
            Text(title, fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}
