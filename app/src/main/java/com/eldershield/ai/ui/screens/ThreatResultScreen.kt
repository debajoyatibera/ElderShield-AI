package com.eldershield.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.eldershield.ai.domain.model.ThreatAnalysisResult
import com.eldershield.ai.domain.model.ThreatStatus
import com.eldershield.ai.ui.components.ElderShieldButton
import com.eldershield.ai.ui.components.ElderShieldCard
import com.eldershield.ai.ui.theme.DangerRed
import com.eldershield.ai.ui.theme.DangerRedLight
import com.eldershield.ai.ui.theme.SafeGreen
import com.eldershield.ai.ui.theme.SafeGreenLight
import com.eldershield.ai.ui.theme.WarningText
import com.eldershield.ai.ui.theme.WarningYellowLight

@Composable
fun ThreatResultScreen(
    result: ThreatAnalysisResult,
    onNavigateHome: () -> Unit
) {
    val (backgroundColor, textColor, titleRes) = when (result.status) {
        ThreatStatus.SAFE -> Triple(SafeGreenLight, SafeGreen, R.string.status_safe)
        ThreatStatus.WARNING -> Triple(WarningYellowLight, WarningText, R.string.status_warning)
        ThreatStatus.DANGER -> Triple(DangerRed, MaterialTheme.colorScheme.onError, R.string.status_danger)
        ThreatStatus.UNKNOWN -> Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, R.string.status_unknown)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        if (result.status == ThreatStatus.DANGER) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = textColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.displayLarge,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        ElderShieldCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.what_happened),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.simpleExplanation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(id = R.string.what_to_do),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.recommendedAction,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ElderShieldButton(
            text = stringResource(id = R.string.back),
            onClick = onNavigateHome,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}
