package com.eldershield.ai.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ThreatStatusTest {

    @Test
    fun testThreatStatusValues() {
        val safe = ThreatStatus.SAFE
        val warning = ThreatStatus.WARNING
        val danger = ThreatStatus.DANGER
        
        assertEquals("SAFE", safe.name)
        assertEquals("WARNING", warning.name)
        assertEquals("DANGER", danger.name)
    }

    @Test
    fun testThreatAnalysisResultCreation() {
        val result = ThreatAnalysisResult(
            status = ThreatStatus.DANGER,
            scamCategory = "Phishing",
            simpleExplanation = "Looks like a scam.",
            recommendedAction = "Delete message."
        )
        
        assertEquals(ThreatStatus.DANGER, result.status)
        assertEquals("Phishing", result.scamCategory)
        assertEquals(1.0f, result.confidence, 0.0f)
    }
}
