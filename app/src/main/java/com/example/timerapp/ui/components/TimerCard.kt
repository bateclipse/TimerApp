package com.example.timerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timerapp.R
import com.example.timerapp.viewmodel.TimerViewModel
import com.example.timerapp.util.TimeUtils

/**
 * Карточка таймера с круговым селектором и управлением
 */
@Composable
fun TimerCard(
    timerId: String,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel()
) {
    val state by (if (timerId == "A") viewModel.timerAState else viewModel.timerBState)
        .collectAsState()
    val isActive by viewModel.activeTimerId.collectAsState()
    
    val focusManager = LocalFocusManager.current
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(state.name) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive == timerId) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Название таймера (редактируемое)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingName) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.let { vm ->
                                    if (timerId == "A") vm.updateTimerAName(editedName)
                                    else vm.updateTimerBName(editedName)
                                }
                                isEditingName = false
                                focusManager.clearFocus()
                            }
                        )
                    )
                    IconButton(onClick = {
                        viewModel.let { vm ->
                            if (timerId == "A") vm.updateTimerAName(editedName)
                            else vm.updateTimerBName(editedName)
                        }
                        isEditingName = false
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Save")
                    }
                } else {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isEditingName = true }
                    )
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Edit name",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Круговой циферблат
            CircularTimePicker(
                totalSeconds = state.totalTimeSeconds,
                onSecondsChanged = { seconds ->
                    viewModel.let { vm ->
                        if (timerId == "A") vm.setTimeA(seconds)
                        else vm.setTimeB(seconds)
                    }
                },
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                size = 180.dp
            )
            
            Spacer(modifier = Modifier.height(-20.dp))
            
            // Цифровое отображение времени
            Text(
                text = TimeUtils.formatTimeExtended(state.remainingSeconds),
                style = MaterialTheme.typography.displayLarge,
                color = if (state.isRunning) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Кнопки управления
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start/Pause
                FilledTonalIconButton(
                    onClick = {
                        viewModel.let { vm ->
                            if (timerId == "A") vm.toggleTimerA() else vm.toggleTimerB()
                        }
                    },
                    enabled = state.totalTimeSeconds > 0,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isRunning) "Pause" else "Start",
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Reset
                FilledTonalIconButton(
                    onClick = {
                        viewModel.let { vm ->
                            if (timerId == "A") vm.resetTimerA() else vm.resetTimerB()
                        }
                    },
                    enabled = state.totalTimeSeconds > 0 && !state.isRunning,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Stop
                FilledTonalIconButton(
                    onClick = {
                        viewModel.let { vm ->
                            if (timerId == "A") vm.stopTimerA() else vm.stopTimerB()
                        }
                    },
                    enabled = state.totalTimeSeconds > 0,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
