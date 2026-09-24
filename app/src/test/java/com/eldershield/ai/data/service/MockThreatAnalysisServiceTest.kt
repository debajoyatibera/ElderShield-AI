package com.eldershield.ai.data.service

import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.domain.model.ThreatType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MockThreatAnalysisServiceTest {

    private lateinit var service: MockThreatAnalysisServiceImpl

    @Before
    fun setup() {
        service = MockThreatAnalysisServiceImpl()
    }

    @Test
    fun testEmptyInputReturnsUnknown() = runBlocking {
        val result = service.analyzeContent(ThreatType.MESSAGE, "").first()
        assertEquals(ThreatStatus.UNKNOWN, result.status)
    }

    @Test
    fun testScamMessageReturnsDanger() = runBlocking {
        val result = service.analyzeContent(ThreatType.MESSAGE, "This is a scam message").first()
        assertEquals(ThreatStatus.DANGER, result.status)
        assertEquals("Phishing", result.scamCategory)
    }

    @Test
    fun testUnknownMessageReturnsUnknown() = runBlocking {
        val result = service.analyzeContent(ThreatType.MESSAGE, "Some unknown content").first()
        assertEquals(ThreatStatus.UNKNOWN, result.status)
    }

    @Test
    fun testLoginLinkReturnsDanger() = runBlocking {
        val result = service.analyzeContent(ThreatType.LINK, "http://login-update.com").first()
        assertEquals(ThreatStatus.DANGER, result.status)
        assertEquals("Malicious Website", result.scamCategory)
    }

    @Test
    fun test800NumberReturnsWarning() = runBlocking {
        val result = service.analyzeContent(ThreatType.CALL, "800-123-4567").first()
        assertEquals(ThreatStatus.WARNING, result.status)
        assertEquals("Suspicious Caller", result.scamCategory)
    }

    @Test
    fun testNormalMessageReturnsSafe() = runBlocking {
        val result = service.analyzeContent(ThreatType.MESSAGE, "Hello grandson, how are you?").first()
        assertEquals(ThreatStatus.SAFE, result.status)
    }
}
