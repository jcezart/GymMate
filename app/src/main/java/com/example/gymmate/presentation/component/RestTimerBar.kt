package com.example.gymmate.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymmate.ui.theme.GymMateTheme

@Composable
fun RestTimerBar(
    time: String,
    isRunning: Boolean,
    onToggleTimer: () -> Unit,
    onAddThirtySeconds: () -> Unit,
    onSubtractThirtySeconds: () -> Unit,
    onResetTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Rest",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            TextButton(
                onClick = onToggleTimer
            ) {
                Icon(
                    imageVector = if (isRunning) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = if (isRunning) "Pausar" else "Iniciar"
                )
            }

            TextButton(
                onClick = onSubtractThirtySeconds
            ) {
                Text("-30s")
            }

            TextButton(
                onClick = onAddThirtySeconds
            ) {
                Text("+30s")
            }

            IconButton(
                onClick = onResetTimer
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reiniciar cronômetro"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RestTimerBarPreview() {
    GymMateTheme {
        RestTimerBar(
            time = "01:30",
            isRunning = false,
            onToggleTimer = {},
            onAddThirtySeconds = {},
            onSubtractThirtySeconds = {},
            onResetTimer = {}
        )
    }
}