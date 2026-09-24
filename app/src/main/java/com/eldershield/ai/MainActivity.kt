package com.eldershield.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eldershield.ai.navigation.AppNavigation
import com.eldershield.ai.ui.theme.ElderShieldAITheme
import com.eldershield.ai.data.service.FirebaseThreatAnalysisServiceImpl
import com.eldershield.ai.data.service.MockThreatAnalysisServiceImpl
import com.eldershield.ai.domain.service.ThreatAnalysisService
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.functions.FirebaseFunctions

class MainActivity : ComponentActivity() {

    // LOCAL DEVELOPMENT ONLY
    private val USE_LOCAL_FIREBASE_EMULATOR = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Phase 1 ensures no data persistence, no heavy analytics here.
        // Purely Compose UI rendering.

        val service: ThreatAnalysisService = if (USE_LOCAL_FIREBASE_EMULATOR) {
            // /* LOCAL DEVELOPMENT ONLY */
            // Initialize Firebase manually to bypass google-services.json
            // These are NOT production credentials.
            val options = FirebaseOptions.Builder()
                .setProjectId("demo-eldershield")
                .setApplicationId("1:100000000000:android:0000000000000000") // Required placeholder
                .setApiKey("LOCAL_EMULATOR_ONLY_API_KEY") // Required placeholder
                .build()

            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this, options)
            }

            val functions = FirebaseFunctions.getInstance()
            // Connect to local emulator running on host machine
            functions.useEmulator("10.0.2.2", 5001)

            FirebaseThreatAnalysisServiceImpl(functions)
        } else {
            MockThreatAnalysisServiceImpl()
        }
        
        setContent {
            ElderShieldAITheme {
                AppNavigation(threatAnalysisService = service)
            }
        }
    }
}
