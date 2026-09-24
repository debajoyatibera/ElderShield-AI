package com.eldershield.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eldershield.ai.R
import com.eldershield.ai.ui.components.ElderShieldButton
import com.eldershield.ai.ui.theme.SafeGreen

@Composable
fun HomeScreen(
    onNavigateToCheckMessage: () -> Unit,
    onNavigateToCheckLink: () -> Unit,
    onNavigateToCheckCall: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = SafeGreen
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.home_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(id = R.string.home_status),
            style = MaterialTheme.typography.titleLarge,
            color = SafeGreen,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.home_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.check_message_button),
            onClick = onNavigateToCheckMessage
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.check_link_button),
            onClick = onNavigateToCheckLink
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.check_call_button),
            onClick = onNavigateToCheckCall
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ElderShieldButton(
            text = stringResource(id = R.string.settings_button),
            onClick = onNavigateToSettings,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
