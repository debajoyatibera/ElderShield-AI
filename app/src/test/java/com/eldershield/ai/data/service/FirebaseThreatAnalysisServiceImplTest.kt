package com.eldershield.ai.data.service

import com.eldershield.ai.domain.model.ThreatStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class FirebaseThreatAnalysisServiceImplTest {

    @Test
    fun `parseResponse MESSAGE maps correctly and returns response`() {
        val mockData = mapOf(
            "status" to "UNKNOWN",
            "confidence" to 0.0f,
            "scamCategory" to null,
            "simpleExplanation" to "Test mock",
            "recommendedAction" to "Do nothing",
            "detectedLanguage" to "en"
        )

        val result = FirebaseThreatAnalysisServiceImpl.parseResponse(mockData)

        assertEquals(ThreatStatus.UNKNOWN, result.status)
        assertEquals("Test mock", result.simpleExplanation)
    }

    @Test
    fun `malformed response does not become SAFE`() {
        val mockData = mapOf("invalid" to "data") // Missing mandatory fields

        val result = FirebaseThreatAnalysisServiceImpl.parseResponse(mockData)

        // Should default to UNKNOWN error state
        assertEquals(ThreatStatus.UNKNOWN, result.status)
        assertEquals(0.0f, result.confidence)
    }

    @Test
    fun `invalid status bounds do not become SAFE`() {
        val mockData = mapOf(
            "status" to "UNKNOWN",
            "confidence" to 1.5f, // Invalid confidence
            "simpleExplanation" to "Test mock",
            "recommendedAction" to "Do nothing"
        )
        val result = FirebaseThreatAnalysisServiceImpl.parseResponse(mockData)

        assertEquals(ThreatStatus.UNKNOWN, result.status)
        assertEquals(0.0f, result.confidence)
    }
}
