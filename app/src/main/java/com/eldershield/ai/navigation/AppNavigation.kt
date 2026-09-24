package com.eldershield.ai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.domain.model.ThreatType
import com.eldershield.ai.ui.screens.*
import com.eldershield.ai.ui.viewmodel.AnalysisState
import com.eldershield.ai.ui.viewmodel.ThreatAnalysisViewModel
import com.eldershield.ai.ui.viewmodel.ThreatAnalysisViewModelFactory
import com.eldershield.ai.domain.service.ThreatAnalysisService

@Composable
fun AppNavigation(threatAnalysisService: ThreatAnalysisService) {
    val navController = rememberNavController()

    // Using the injected service which could be Mock or Firebase
    val viewModel: ThreatAnalysisViewModel = viewModel(
        factory = ThreatAnalysisViewModelFactory(threatAnalysisService)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Navigate to result screen when state is Success or Error
    LaunchedEffect(uiState) {
        when (uiState) {
            is AnalysisState.Success -> {
                navController.navigate("result") {
                    popUpTo("home")
                }
            }
            is AnalysisState.Error -> {
                navController.navigate("result_error") {
                    popUpTo("home")
                }
            }
            else -> {}
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToCheckMessage = { navController.navigate("check_message") },
                onNavigateToCheckLink = { navController.navigate("check_link") },
                onNavigateToCheckCall = { navController.navigate("check_call") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("check_message") {
            if (uiState is AnalysisState.Loading) {
                AnalyzingScreen()
            } else {
                CheckMessageScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAnalyze = { message ->
                        viewModel.analyzeContent(ThreatType.MESSAGE, message)
                    }
                )
            }
        }

        composable("check_link") {
            if (uiState is AnalysisState.Loading) {
                AnalyzingScreen()
            } else {
                CheckLinkScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAnalyze = { link ->
                        viewModel.analyzeContent(ThreatType.LINK, link)
                    }
                )
            }
        }

        composable("check_call") {
            if (uiState is AnalysisState.Loading) {
                AnalyzingScreen()
            } else {
                CheckCallScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAnalyze = { phone ->
                        viewModel.analyzeContent(ThreatType.CALL, phone)
                    }
                )
            }
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("result") {
            val state = uiState as? AnalysisState.Success
            if (state != null) {
                ThreatResultScreen(
                    result = state.result,
                    onNavigateHome = {
                        viewModel.resetState()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("result_error") {
            val state = uiState as? AnalysisState.Error
            if (state != null) {
                ThreatResultScreen(
                    result = ThreatAnalysisResult(
                        status = ThreatStatus.UNKNOWN,
                        simpleExplanation = state.message,
                        recommendedAction = "We could not securely verify this. Please be extra cautious."
                    ),
                    onNavigateHome = {
                        viewModel.resetState()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
