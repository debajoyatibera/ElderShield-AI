package com.eldershield.ai.domain.service

import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatType
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the AI service contract.
 */
interface ThreatAnalysisService {
    
    /**
     * Analyzes communication content for potential threats.
     * 
     * @param type The type of content (MESSAGE, LINK, CALL).
     * @param content The raw string to analyze.
     * @return A Flow emitting the result of the analysis.
     */
    fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult>
}
