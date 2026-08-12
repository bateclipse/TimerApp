package com.example.timerapp.viewmodel

import android.app.Application
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timerapp.data.Presets
import com.example.timerapp.data.SettingsRepository
import com.example.timerapp.util.TimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Модель состояния таймера
 */
data class TimerState(
    val name: String = "",
    val totalTimeSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val soundUri: String? = null
)

/**
 * ViewModel для управления таймерами
 */
class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val settingsRepository = SettingsRepository(application)
    private val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    // Состояние таймера A
    private val _timerAState = MutableStateFlow(TimerState(name = "Timer A"))
    val timerAState: StateFlow<TimerState> = _timerAState.asStateFlow()
    
    // Состояние таймера B
    private val _timerBState = MutableStateFlow(TimerState(name = "Timer B"))
    val timerBState: StateFlow<TimerState> = _timerBState.asStateFlow()
    
    // Выбранный звук уведомления
    private val _selectedSoundUri = MutableStateFlow<String?>(null)
    val selectedSoundUri: StateFlow<String?> = _selectedSoundUri.asStateFlow()
    
    // Список системных звуков
    private val _systemSounds = MutableStateFlow<List<Pair<String, Uri>>>(emptyList())
    val systemSounds: StateFlow<List<Pair<String, Uri>>> = _systemSounds.asStateFlow()
    
    private var timerAJob: Job? = null
    private var timerBJob: Job? = null
    
    // Активный таймер для предустановок (A или B)
    private val _activeTimerId = MutableStateFlow("A")
    val activeTimerId: StateFlow<String> = _activeTimerId.asStateFlow()
    
    init {
        loadSettings()
        loadSystemSounds()
    }
    
    /**
     * Загрузка настроек из DataStore
     */
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.soundUriFlow.collect { uri ->
                _selectedSoundUri.value = uri
            }
        }
        viewModelScope.launch {
            settingsRepository.timerANameFlow.collect { name ->
                _timerAState.value = _timerAState.value.copy(name = name)
            }
        }
        viewModelScope.launch {
            settingsRepository.timerBNameFlow.collect { name ->
                _timerBState.value = _timerBState.value.copy(name = name)
            }
        }
    }
    
    /**
     * Загрузка системных звуков
     */
    private fun loadSystemSounds() {
        viewModelScope.launch {
            val ringtoneManager = RingtoneManager(getApplication()).apply {
                setType(RingtoneManager.TYPE_NOTIFICATION)
            }
            
            val sounds = mutableListOf<Pair<String, Uri>>()
            val count = ringtoneManager.count
            for (i in 0 until count) {
                val uri = ringtoneManager.getRingtoneUri(i)
                val ringtone = ringtoneManager.getRingtone(i)
                val title = ringtone.getTitle(getApplication())
                if (uri != null && title != null) {
                    sounds.add(Pair(title, uri))
                }
            }
            
            // Добавляем опцию "Без звука"
            sounds.add(0, Pair("Silent", Uri.EMPTY))
            
            _systemSounds.value = sounds
        }
    }
    
    /**
     * Установка времени для активного таймера
     */
    fun setTimeForActiveTimer(seconds: Int) {
        when (_activeTimerId.value) {
            "A" -> setTimeA(seconds)
            "B" -> setTimeB(seconds)
        }
    }
    
    /**
     * Установка времени для таймера A
     */
    fun setTimeA(seconds: Int) {
        timerAJob?.cancel()
        _timerAState.value = _timerAState.value.copy(
            totalTimeSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Установка времени для таймера B
     */
    fun setTimeB(seconds: Int) {
        timerBJob?.cancel()
        _timerBState.value = _timerBState.value.copy(
            totalTimeSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Старт/Пауза таймера A
     */
    fun toggleTimerA() {
        if (_timerAState.value.isRunning) {
            pauseTimerA()
        } else if (_timerAState.value.remainingSeconds > 0) {
            startTimerA()
        }
    }
    
    /**
     * Старт/Пауза таймера B
     */
    fun toggleTimerB() {
        if (_timerBState.value.isRunning) {
            pauseTimerB()
        } else if (_timerBState.value.remainingSeconds > 0) {
            startTimerB()
        }
    }
    
    /**
     * Запуск таймера A
     */
    private fun startTimerA() {
        if (_timerAState.value.remainingSeconds <= 0) return
        
        _timerAState.value = _timerAState.value.copy(isRunning = true, isPaused = false)
        
        timerAJob = viewModelScope.launch {
            while (_timerAState.value.remainingSeconds > 0 && _timerAState.value.isRunning) {
                delay(1000)
                _timerAState.value = _timerAState.value.copy(
                    remainingSeconds = _timerAState.value.remainingSeconds - 1
                )
            }
            
            if (_timerAState.value.remainingSeconds == 0 && _timerAState.value.isRunning) {
                onTimerFinished(_timerAState.value.name)
            }
        }
    }
    
    /**
     * Запуск таймера B
     */
    private fun startTimerB() {
        if (_timerBState.value.remainingSeconds <= 0) return
        
        _timerBState.value = _timerBState.value.copy(isRunning = true, isPaused = false)
        
        timerBJob = viewModelScope.launch {
            while (_timerBState.value.remainingSeconds > 0 && _timerBState.value.isRunning) {
                delay(1000)
                _timerBState.value = _timerBState.value.copy(
                    remainingSeconds = _timerBState.value.remainingSeconds - 1
                )
            }
            
            if (_timerBState.value.remainingSeconds == 0 && _timerBState.value.isRunning) {
                onTimerFinished(_timerBState.value.name)
            }
        }
    }
    
    /**
     * Пауза таймера A
     */
    private fun pauseTimerA() {
        timerAJob?.cancel()
        _timerAState.value = _timerAState.value.copy(isRunning = false, isPaused = true)
    }
    
    /**
     * Пауза таймера B
     */
    private fun pauseTimerB() {
        timerBJob?.cancel()
        _timerBState.value = _timerBState.value.copy(isRunning = false, isPaused = true)
    }
    
    /**
     * Сброс таймера A
     */
    fun resetTimerA() {
        timerAJob?.cancel()
        val total = _timerAState.value.totalTimeSeconds
        _timerAState.value = _timerAState.value.copy(
            remainingSeconds = total,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Сброс таймера B
     */
    fun resetTimerB() {
        timerBJob?.cancel()
        val total = _timerBState.value.totalTimeSeconds
        _timerBState.value = _timerBState.value.copy(
            remainingSeconds = total,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Остановка таймера A
     */
    fun stopTimerA() {
        timerAJob?.cancel()
        _timerAState.value = _timerAState.value.copy(
            totalTimeSeconds = 0,
            remainingSeconds = 0,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Остановка таймера B
     */
    fun stopTimerB() {
        timerBJob?.cancel()
        _timerBState.value = _timerBState.value.copy(
            totalTimeSeconds = 0,
            remainingSeconds = 0,
            isRunning = false,
            isPaused = false
        )
    }
    
    /**
     * Обработка завершения таймера
     */
    private fun onTimerFinished(timerName: String) {
        when (timerName) {
            _timerAState.value.name -> {
                _timerAState.value = _timerAState.value.copy(isRunning = false)
            }
            _timerBState.value.name -> {
                _timerBState.value = _timerBState.value.copy(isRunning = false)
            }
        }
        
        // Вибрация и звук будут вызваны из Service или MainActivity
        vibrate()
    }
    
    /**
     * Вибрация при завершении таймера
     */
    private fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }
    }
    
    /**
     * Предпрослушивание звука
     */
    fun previewSound(uri: Uri): Ringtone? {
        try {
            val ringtone = RingtoneManager.getRingtone(getApplication(), uri)
            ringtone?.play()
            viewModelScope.launch {
                delay(2000)
                ringtone?.stop()
            }
            return ringtone
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Сохранение выбранного звука
     */
    fun saveSoundUri(uriString: String) {
        viewModelScope.launch {
            settingsRepository.saveSoundUri(uriString)
            _selectedSoundUri.value = uriString
        }
    }
    
    /**
     * Изменение имени таймера A
     */
    fun updateTimerAName(newName: String) {
        viewModelScope.launch {
            settingsRepository.saveTimerAName(newName)
        }
    }
    
    /**
     * Изменение имени таймера B
     */
    fun updateTimerBName(newName: String) {
        viewModelScope.launch {
            settingsRepository.saveTimerBName(newName)
        }
    }
    
    /**
     * Установка активного таймера для предустановок
     */
    fun setActiveTimer(id: String) {
        _activeTimerId.value = id
    }
    
    /**
     * Получение списка быстрых предустановок
     */
    fun getTimePresets(): List<Int> = Presets.timePresets
    
    /**
     * Получение списка предустановок продуктов
     */
    fun getProductPresets() = Presets.productPresets
    
    override fun onCleared() {
        super.onCleared()
        timerAJob?.cancel()
        timerBJob?.cancel()
    }
}
