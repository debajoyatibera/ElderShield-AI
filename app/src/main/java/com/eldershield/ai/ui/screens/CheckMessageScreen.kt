package com.eldershield.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eldershield.ai.R
import com.eldershield.ai.ui.components.ElderShieldButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckMessageScreen(
    onNavigateBack: () -> Unit,
    onAnalyze: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.check_message_title),
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = messageText,
            onValueChange = { 
                messageText = it
                showError = false 
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .semantics { contentDescription = "Enter message to check" },
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.check_message_hint),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            isError = showError,
            shape = RoundedCornerShape(12.dp)
        )
        
        if (showError) {
            Text(
                text = stringResource(id = R.string.check_message_error_empty),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.check_message_button),
            onClick = {
                if (messageText.isBlank()) {
                    showError = true
                } else {
                    // Pass to the next screen. No real analysis happens in Phase 1.
                    onAnalyze(messageText)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.back),
            onClick = onNavigateBack,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
