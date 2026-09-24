package com.eldershield.ai.data.service

import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.domain.model.ThreatType
import com.eldershield.ai.domain.service.ThreatAnalysisService
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Production-ready but LOCAL-ONLY service implementation for Phase 3B.
 * Connects to the local Firebase Functions emulator.
 */
class FirebaseThreatAnalysisServiceImpl(
    private val functions: FirebaseFunctions
) : ThreatAnalysisService {

    override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flow {
        try {
            // Map the ThreatType to the backend string format
            val contentType = when (type) {
                ThreatType.MESSAGE -> "MESSAGE"
                ThreatType.LINK -> "LINK"
                ThreatType.CALL -> "CALL"
            }

            // Construct payload safely. Never log this.
            val payload = mapOf(
                "contentType" to contentType,
                "content" to content,
                "appLanguage" to "en" // Hardcoded for now per requirements or could be passed.
            )

            // Call the local function
            val result = functions
                .getHttpsCallable("analyzeThreat")
                .call(payload)
                .await()

            // Parse response carefully
            val data = result.data as? Map<*, *>
            val analysisResult = parseResponse(data)
            emit(analysisResult)
        } catch (e: Exception) {
            // Network failure, callable error, emulator unavailable, etc.
            // Never log user content here.
            emit(createSafeErrorResult("Unable to reach the threat analysis service. Please check your connection."))
        }
    }

    companion object {
        fun parseResponse(data: Map<*, *>?): ThreatAnalysisResult {
            if (data == null) {
                return createSafeErrorResult("Backend returned an empty or invalid response format.")
            }

            // Extract fields
            val statusStr = data["status"] as? String
            val confidence = (data["confidence"] as? Number)?.toFloat()
            val scamCategory = data["scamCategory"] as? String
            val simpleExplanation = data["simpleExplanation"] as? String
            val recommendedAction = data["recommendedAction"] as? String

            // Validate mandatory fields
            if (statusStr == null || confidence == null || simpleExplanation == null || recommendedAction == null) {
                return createSafeErrorResult("Backend response missing mandatory fields.")
            }

            // Map status
            val status = try {
                ThreatStatus.valueOf(statusStr)
            } catch (e: IllegalArgumentException) {
                return createSafeErrorResult("Backend returned an unrecognizable status: $statusStr")
            }

            // Validate confidence bounds
            if (confidence < 0.0f || confidence > 1.0f) {
                return createSafeErrorResult("Backend returned invalid confidence bounds.")
            }

            // Success mapping
            return ThreatAnalysisResult(
                status = status,
                confidence = confidence,
                scamCategory = scamCategory,
                simpleExplanation = simpleExplanation,
                recommendedAction = recommendedAction,
                detectedLanguage = data["detectedLanguage"] as? String
            )
        }

        fun createSafeErrorResult(explanation: String): ThreatAnalysisResult {
            return ThreatAnalysisResult(
                status = ThreatStatus.UNKNOWN,
                confidence = 0.0f,
                scamCategory = null,
                simpleExplanation = explanation,
                recommendedAction = "Do not rely on this result. Be extra cautious."
            )
        }
    }
}
