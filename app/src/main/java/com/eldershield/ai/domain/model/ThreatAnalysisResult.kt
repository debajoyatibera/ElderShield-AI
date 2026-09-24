package com.eldershield.ai.domain.model

/**
 * A clean placeholder abstraction representing the future AI analysis result.
 * It is structured to handle Phase 3 Gemini results without UI rewrite.
 * 
 * @property status The overarching threat level.
 * @property confidence A value between 0.0 and 1.0 indicating confidence.
 * @property scamCategory The type of scam (e.g., "Phishing", "Impersonation", null if safe).
 * @property simpleExplanation An easy-to-understand reason for the elderly user.
 * @property recommendedAction What the user should do next.
 * @property detectedLanguage The ISO language code detected (e.g., "en", "bn", "hi").
 */
data class ThreatAnalysisResult(
    val status: ThreatStatus,
    val confidence: Float = 1.0f,
    val scamCategory: String? = null,
    val simpleExplanation: String,
    val recommendedAction: String,
    val detectedLanguage: String? = null
)
