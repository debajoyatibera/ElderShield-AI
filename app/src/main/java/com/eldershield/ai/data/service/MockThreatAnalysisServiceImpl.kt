package com.eldershield.ai.data.service

import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.domain.model.ThreatType
import com.eldershield.ai.domain.service.ThreatAnalysisService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A mock implementation for Phase 3 UI development and local testing.
 * This replaces the business logic previously in AppNavigation.
 */
class MockThreatAnalysisServiceImpl : ThreatAnalysisService {
    
    override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flow {
        // Simulate network delay
        delay(1500)
        
        if (content.isBlank()) {
            emit(
                ThreatAnalysisResult(
                    status = ThreatStatus.UNKNOWN,
                    simpleExplanation = "We could not understand the input.",
                    recommendedAction = "Please try again with a valid message, link, or number."
                )
            )
            return@flow
        }

        val result = when (type) {
            ThreatType.MESSAGE -> {
                if (content.contains("scam", ignoreCase = true) || content.contains("http", ignoreCase = true)) {
                    ThreatAnalysisResult(
                        status = ThreatStatus.DANGER,
                        scamCategory = "Phishing",
                        simpleExplanation = "This message looks like a common scam designed to trick you.",
                        recommendedAction = "Do not reply or click any links. Delete this message.",
                        detectedLanguage = "en"
                    )
                } else if (content.contains("unknown", ignoreCase = true)) {
                    ThreatAnalysisResult(
                        status = ThreatStatus.UNKNOWN,
                        simpleExplanation = "We cannot definitively determine if this is safe or a scam.",
                        recommendedAction = "Please be cautious. If you don't know the sender, ignore it."
                    )
                } else {
                    ThreatAnalysisResult(
                        status = ThreatStatus.SAFE,
                        simpleExplanation = "We did not find any obvious signs of a scam in this message.",
                        recommendedAction = "You can safely ignore this warning, but always stay alert."
                    )
                }
            }
            ThreatType.LINK -> {
                if (content.contains("login", ignoreCase = true) || content.contains("verify", ignoreCase = true)) {
                    ThreatAnalysisResult(
                        status = ThreatStatus.DANGER,
                        scamCategory = "Malicious Website",
                        simpleExplanation = "This website is known to steal passwords and personal information.",
                        recommendedAction = "Close the website immediately and do not enter any information."
                    )
                } else {
                    ThreatAnalysisResult(
                        status = ThreatStatus.SAFE,
                        simpleExplanation = "We did not find any obvious signs of a scam.",
                        recommendedAction = "You can safely ignore this warning, but always stay alert."
                    )
                }
            }
            ThreatType.CALL -> {
                if (content.startsWith("800") || content.startsWith("900")) {
                    ThreatAnalysisResult(
                        status = ThreatStatus.WARNING,
                        scamCategory = "Suspicious Caller",
                        simpleExplanation = "This number is suspicious and has been reported by other users.",
                        recommendedAction = "Be very careful if you answer. Do not give them money or passwords."
                    )
                } else {
                    ThreatAnalysisResult(
                        status = ThreatStatus.SAFE,
                        simpleExplanation = "We did not find any obvious signs of a scam.",
                        recommendedAction = "You can safely ignore this warning, but always stay alert."
                    )
                }
            }
        }
        
        emit(result)
    }
}
