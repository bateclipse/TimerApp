package com.example.timerapp.data

/**
 * Модель предустановки времени для продукта
 */
data class ProductPreset(
    val id: String,
    val nameResId: Int,
    val durationSeconds: Int,
    val iconResId: Int? = null
)

/**
 * Список всех предустановок продуктов
 */
object Presets {
    
    // Быстрые предустановки времени (в секундах)
    val timePresets = listOf(
        60,      // 1 мин
        180,     // 3 мин
        300,     // 5 мин
        600,     // 10 мин
        900,     // 15 мин
        1800     // 30 мин
    )
    
    // Предустановки продуктов
    val productPresets = listOf(
        ProductPreset(
            id = "chicken_breast",
            nameResId = com.example.timerapp.R.string.product_chicken_breast,
            durationSeconds = 25 * 60,  // 25 минут
            iconResId = null
        ),
        ProductPreset(
            id = "soft_eggs",
            nameResId = com.example.timerapp.R.string.product_soft_eggs,
            durationSeconds = 4 * 60,   // 4 минуты
            iconResId = null
        ),
        ProductPreset(
            id = "hard_eggs",
            nameResId = com.example.timerapp.R.string.product_hard_eggs,
            durationSeconds = 10 * 60,  // 10 минут
            iconResId = null
        ),
        ProductPreset(
            id = "pasta",
            nameResId = com.example.timerapp.R.string.product_pasta,
            durationSeconds = 8 * 60,   // 8 минут
            iconResId = null
        ),
        ProductPreset(
            id = "rice",
            nameResId = com.example.timerapp.R.string.product_rice,
            durationSeconds = 20 * 60,  // 20 минут
            iconResId = null
        ),
        ProductPreset(
            id = "potato",
            nameResId = com.example.timerapp.R.string.product_potato,
            durationSeconds = 25 * 60,  // 25 минут
            iconResId = null
        ),
        ProductPreset(
            id = "fish",
            nameResId = com.example.timerapp.R.string.product_fish,
            durationSeconds = 15 * 60,  // 15 минут
            iconResId = null
        ),
        ProductPreset(
            id = "beef",
            nameResId = com.example.timerapp.R.string.product_beef,
            durationSeconds = 40 * 60,  // 40 минут
            iconResId = null
        ),
        ProductPreset(
            id = "tea",
            nameResId = com.example.timerapp.R.string.product_tea,
            durationSeconds = 5 * 60,   // 5 минут
            iconResId = null
        )
    )
}
