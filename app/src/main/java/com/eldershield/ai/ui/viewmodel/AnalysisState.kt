package com.eldershield.ai.ui.viewmodel

import com.eldershield.ai.domain.model.ThreatAnalysisResult

/**
 * Represents the UI state for threat analysis.
 */
sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val result: ThreatAnalysisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
