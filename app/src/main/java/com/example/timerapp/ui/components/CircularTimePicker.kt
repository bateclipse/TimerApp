package com.example.timerapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Кастомный круглый селектор времени с возможностью вращения пальцем
 * @param totalSeconds общее время в секундах
 * @param onSecondsChanged колбэк при изменении времени
 * @param modifier модификатор Compose
 * @param size размер компонента
 * @param trackColor цвет трека (фона)
 * @param progressColor цвет прогресса
 * @param knobColor цвет "бегунка"
 */
@Composable
fun CircularTimePicker(
    totalSeconds: Int,
    onSecondsChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    knobColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    var angle by remember { mutableStateOf(0f) }
    
    // Вычисляем начальный угол на основе текущего totalSeconds
    // Максимум - 2 часа (7200 секунд) для полного круга
    val maxSeconds = 7200
    val initialAngle = (totalSeconds.toFloat() / maxSeconds) * 360f
    
    LaunchedEffect(totalSeconds) {
        angle = initialAngle
    }
    
    Canvas(
        modifier = modifier
            .size(size)
            .pointerInput(angle) {
                detectDragGestures { change, _ ->
                    // Получаем центр канваса
                    val center = Offset(size.toPx() / 2, size.toPx() / 2)
                    
                    // Вычисляем угол от центра до точки касания
                    val dx = change.position.x - center.x
                    val dy = change.position.y - center.y
                    val newAngle = (atan2(dy, dx) * 180 / PI).toFloat() + 90f
                    
                    // Нормализуем угол от 0 до 360
                    val normalizedAngle = if (newAngle < 0) newAngle + 360f else newAngle
                    
                    angle = normalizedAngle
                    
                    // Конвертируем угол в секунды
                    val newSeconds = ((angle / 360f) * maxSeconds).toInt().coerceIn(0, maxSeconds)
                    onSecondsChanged(newSeconds)
                }
            }
    ) {
        val radius = size.toPx() / 2 - 20f
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        
        // Рисуем трек (фон)
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = 12f, cap = StrokeCap.Round)
        )
        
        // Рисуем прогресс
        if (angle > 0) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
        }
        
        // Рисуем "бегунок" (knob)
        val knobAngleRad = ((angle - 90) * PI / 180).toFloat()
        val knobX = center.x + radius * cos(knobAngleRad)
        val knobY = center.y + radius * sin(knobAngleRad)
        
        drawCircle(
            color = knobColor,
            radius = 16f,
            center = Offset(knobX, knobY)
        )
    }
}
