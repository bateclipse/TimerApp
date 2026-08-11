package com.example.timerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timerapp.data.ProductPreset
import com.example.timerapp.util.TimeUtils

/**
 * Компонент чипсов для быстрого выбора времени
 */
@Composable
fun TimePresetChips(
    presets: List<Int>,
    onPresetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { seconds ->
            AssistChip(
                onClick = { onPresetSelected(seconds) },
                label = { Text(TimeUtils.formatTime(seconds)) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

/**
 * Компонент чипсов для выбора продуктов с иконками
 */
@Composable
fun ProductPresetChips(
    presets: List<ProductPreset>,
    onProductSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Продукты",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(presets.size) { index ->
                val preset = presets[index]
                ProductChip(
                    preset = preset,
                    onClick = { onProductSelected(preset.durationSeconds) }
                )
            }
        }
    }
}

/**
 * Отдельный чипс продукта
 */
@Composable
private fun ProductChip(
    preset: ProductPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(preset.nameResId),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Text(
                    text = TimeUtils.formatTime(preset.durationSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .widthIn(min = 80.dp, max = 100.dp)
            .height(70.dp)
    )
}

/**
 * Секция с заголовком для группировки элементов
 */
@Composable
fun SectionWithHeader(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        content()
    }
}
