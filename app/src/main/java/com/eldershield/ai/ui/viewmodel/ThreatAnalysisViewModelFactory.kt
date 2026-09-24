package com.eldershield.ai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eldershield.ai.domain.service.ThreatAnalysisService

class ThreatAnalysisViewModelFactory(
    private val threatAnalysisService: ThreatAnalysisService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThreatAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThreatAnalysisViewModel(threatAnalysisService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
