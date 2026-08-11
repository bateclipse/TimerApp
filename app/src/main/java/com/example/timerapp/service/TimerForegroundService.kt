package com.example.timerapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.timerapp.MainActivity
import com.example.timerapp.R
import com.example.timerapp.util.TimeUtils

/**
 * Foreground Service для отображения таймера в уведомлении
 * Предотвращает убийство приложения системой и показывает оставшееся время
 */
class TimerForegroundService : Service() {
    
    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001
        
        // Action для кнопок уведомления
        const val ACTION_PAUSE = "com.example.timerapp.PAUSE"
        const val ACTION_STOP = "com.example.timerapp.STOP"
        
        // Ключи для данных
        const val EXTRA_TIMER_NAME = "timer_name"
        const val EXTRA_TIME_REMAINING = "time_remaining"
        const val EXTRA_IS_RUNNING = "is_running"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> {
                // Обработка паузы - отправляем broadcast или используем callback
                sendBroadcast(Intent(ACTION_PAUSE).setPackage(packageName))
            }
            ACTION_STOP -> {
                // Остановка таймера
                sendBroadcast(Intent(ACTION_STOP).setPackage(packageName))
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                // Обновление уведомления
                val timerName = intent?.getStringExtra(EXTRA_TIMER_NAME) ?: "Timer"
                val timeRemaining = intent?.getIntExtra(EXTRA_TIME_REMAINING, 0) ?: 0
                val isRunning = intent?.getBooleanExtra(EXTRA_IS_RUNNING, false) ?: false
                
                updateNotification(timerName, timeRemaining, isRunning)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * Создание канала уведомлений
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Запуск foreground service
     */
    fun startService(timerName: String, timeRemaining: Int, isRunning: Boolean) {
        val notification = createNotification(timerName, timeRemaining, isRunning)
        startForeground(NOTIFICATION_ID, notification)
    }
    
    /**
     * Обновление существующего уведомления
     */
    private fun updateNotification(timerName: String, timeRemaining: Int, isRunning: Boolean) {
        val notification = createNotification(timerName, timeRemaining, isRunning)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Создание уведомления
     */
    private fun createNotification(timerName: String, timeRemaining: Int, isRunning: Boolean): Notification {
        // Intent для открытия MainActivity при клике на уведомление
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Action кнопки
        val pauseIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val formattedTime = TimeUtils.formatTime(timeRemaining)
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(timerName)
            .setContentText(getString(R.string.notification_time_remaining, formattedTime))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                if (isRunning) getString(R.string.pause) else getString(R.string.start),
                pausePendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.stop),
                stopPendingIntent
            )
            .build()
    }
}
