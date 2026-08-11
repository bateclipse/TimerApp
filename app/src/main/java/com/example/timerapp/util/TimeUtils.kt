package com.example.timerapp.util

/**
 * Утилиты для работы со временем
 */
object TimeUtils {
    
    /**
     * Форматирование времени в MM:SS
     */
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    /**
     * Форматирование времени в HH:MM:SS если больше часа
     */
    fun formatTimeExtended(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
    
    /**
     * Конвертация минут в секунды
     */
    fun minutesToSeconds(minutes: Int): Int = minutes * 60
    
    /**
     * Конвертация часов и минут в секунды
     */
    fun hoursMinutesToSeconds(hours: Int, minutes: Int): Int = hours * 3600 + minutes * 60
}
