package com.eldershield.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eldershield.ai.R
import com.eldershield.ai.ui.components.ElderShieldButton

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.settings_about),
            onClick = { /* Placeholder */ },
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.settings_help),
            onClick = { /* Placeholder */ },
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.settings_language),
            onClick = { /* Placeholder */ },
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        ElderShieldButton(
            text = stringResource(id = R.string.back),
            onClick = onNavigateBack
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
