package com.eldershield.ai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.ai.domain.model.ThreatType
import com.eldershield.ai.domain.service.ThreatAnalysisService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ThreatAnalysisViewModel(
    private val threatAnalysisService: ThreatAnalysisService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val uiState: StateFlow<AnalysisState> = _uiState.asStateFlow()

    fun analyzeContent(type: ThreatType, content: String) {
        if (content.isBlank()) {
            _uiState.value = AnalysisState.Error("Please enter content to check.")
            return
        }

        _uiState.value = AnalysisState.Loading
        
        viewModelScope.launch {
            threatAnalysisService.analyzeContent(type, content)
                .catch { _ ->
                    // Network unavailable, backend unavailable, timeouts, etc.
                    _uiState.value = AnalysisState.Error(
                        "We couldn't check this. Please treat it as suspicious and be careful."
                    )
                }
                .collect { result ->
                    _uiState.value = AnalysisState.Success(result)
                }
        }
    }

    fun resetState() {
        _uiState.value = AnalysisState.Idle
    }
}
