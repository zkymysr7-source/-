package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.BottomNav
import com.example.ui.components.TopHealthyBar
import com.example.ui.screens.CalorieTableScreen
import com.example.ui.screens.CreateRecipeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoalCalculatorScreen
import com.example.ui.screens.SavedRecipesScreen
import com.example.ui.screens.ScanDishScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                // Arabic RTL Layout direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    HealthyAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HealthyAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val goalProfile by viewModel.goalProfile.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Predictive Back Handling: Return to Dashboard if on a sub-screen
    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopHealthyBar(
                isDarkMode = isDarkMode,
                onToggleTheme = { viewModel.toggleDarkMode() },
                goalProfile = goalProfile,
                onGoalClick = { viewModel.navigateTo(AppScreen.GOAL_CALCULATOR) }
            )
        },
        bottomBar = {
            BottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                AppScreen.SCAN_DISH -> ScanDishScreen(
                    viewModel = viewModel
                )
                AppScreen.CALORIE_TABLE -> CalorieTableScreen(
                    viewModel = viewModel
                )
                AppScreen.CREATE_RECIPE -> CreateRecipeScreen(
                    viewModel = viewModel
                )
                AppScreen.SAVED_RECIPES -> SavedRecipesScreen(
                    viewModel = viewModel
                )
                AppScreen.GOAL_CALCULATOR -> GoalCalculatorScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
