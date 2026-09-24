package com.eldershield.ai.ui.viewmodel

import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.domain.model.ThreatType
import com.eldershield.ai.domain.service.ThreatAnalysisService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThreatAnalysisViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateIsIdle() {
        val service = object : ThreatAnalysisService {
            override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flowOf()
        }
        val viewModel = ThreatAnalysisViewModel(service)
        assertEquals(AnalysisState.Idle, viewModel.uiState.value)
    }

    @Test
    fun testEmptyInputSetsErrorState() {
        val service = object : ThreatAnalysisService {
            override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flowOf()
        }
        val viewModel = ThreatAnalysisViewModel(service)
        viewModel.analyzeContent(ThreatType.MESSAGE, "   ")
        assertTrue(viewModel.uiState.value is AnalysisState.Error)
    }

    @Test
    fun testSuccessfulAnalysisEmitsSuccessState() = runTest {
        val mockResult = ThreatAnalysisResult(
            status = ThreatStatus.SAFE,
            simpleExplanation = "Safe",
            recommendedAction = "None"
        )
        val service = object : ThreatAnalysisService {
            override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flowOf(mockResult)
        }
        val viewModel = ThreatAnalysisViewModel(service)
        
        viewModel.analyzeContent(ThreatType.MESSAGE, "Hello")
        
        // Skip through the loading state immediately because of flowOf and test dispatcher
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is AnalysisState.Success)
        assertEquals(ThreatStatus.SAFE, (state as AnalysisState.Success).result.status)
    }

    @Test
    fun testServiceExceptionEmitsErrorState() = runTest {
        val service = object : ThreatAnalysisService {
            override fun analyzeContent(type: ThreatType, content: String): Flow<ThreatAnalysisResult> = flow {
                throw Exception("Network Error")
            }
        }
        val viewModel = ThreatAnalysisViewModel(service)
        
        viewModel.analyzeContent(ThreatType.MESSAGE, "Hello")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is AnalysisState.Error)
    }
}
