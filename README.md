# Kitchen Timer App

Приложение кухонного таймера для Android с поддержкой двух независимых таймеров.

## 🚀 Возможности

### Основной экран "Таймеры"
- **Два независимых таймера** (Timer A и Timer B) с собственными настройками
- **Круговой циферблат (CircularPicker)** - вращайте пальцем для точной установки времени
- **Цифровое отображение** MM:SS или HH:MM:SS крупным шрифтом
- **Быстрые предустановки**: 1, 3, 5, 10, 15, 30 минут
- **Предустановки продуктов**:
  - Куриная грудка — 25 мин
  - Яйца всмятку — 4 мин
  - Яйца вкрутую — 10 мин
  - Макароны — 8 мин
  - Рис — 20 мин
  - Картофель — 25 мин
  - Рыба — 15 мин
  - Говядина — 40 мин
  - Чай — 5 мин

### Foreground Service
- При запуске таймера стартует сервис с уведомлением в шторке
- Постоянное отображение оставшегося времени
- Кнопки Pause/Stop прямо из уведомления
- Защита от убийства приложения системой

### Навигация
- Выдвижное меню слева (DrawerLayout)
- Свайп от левого края или кнопка-гамбургер
- Страницы: Таймеры, Продукты, Рецепты, Калькуляторы, Заметки, Температуры, Настройки

### Дизайн
- Material Design 3
- Тёплые кухонные оттенки (терракотовый, оливковый, кремовый)
- Светлая/тёмная тема с динамическими цветами (Android 12+)
- Плавные анимации и закруглённые углы

## 🏗️ Архитектура

- **MVVM** (ViewModel + Repository)
- **Jetpack Compose** для UI
- **Navigation Compose** для навигации
- **DataStore Preferences** для хранения настроек
- Kotlin Coroutines & Flow

## 📋 Требования

- minSdk: 24 (Android 7.0)
- targetSdk: 34 (Android 14)
- Kotlin 2.0.21
- Jetpack Compose BOM 2024.09.00

## 🛠️ Сборка

1. Откройте проект в Android Studio Hedgehog или новее
2. Синхронизируйте Gradle файлы
3. Соберите и запустите на устройстве или эмуляторе

```bash
./gradlew assembleDebug
```

## 📁 Структура проекта

```
app/src/main/java/com/example/timerapp/
├── MainActivity.kt              # Главная активность с навигацией
├── ui/
│   ├── theme/                   # Цвета, типографика, темы
│   ├── screens/
│   │   ├── TimerScreen.kt       # Главный экран с таймерами
│   │   ├── PlaceholderScreen.kt # Заглушка для страниц
│   │   ├── SettingsScreen.kt    # Экран настроек
│   │   └── AboutScreen.kt       # О программе
│   └── components/
│       ├── CircularTimePicker.kt # Круглый селектор времени
│       ├── TimerCard.kt         # Карточка таймера
│       ├── PresetChips.kt       # Чипсы предустановок
│       └── AppDrawer.kt         # Выдвижное меню
├── service/
│   └── TimerForegroundService.kt # Foreground сервис
├── viewmodel/
│   └── TimerViewModel.kt        # ViewModel для таймеров
├── data/
│   ├── Presets.kt               # Список предустановок
│   └── SettingsRepository.kt    # Репозиторий настроек
└── util/
    └── TimeUtils.kt             # Утилиты времени
```

## 🔐 Разрешения

- `POST_NOTIFICATIONS` - для уведомлений (Android 13+)
- `VIBRATE` - для вибрации при завершении таймера
- `FOREGROUND_SERVICE` - для работы в фоне

## 🌐 Локализация

- Русский (`values-ru/strings.xml`)
- Английский (`values/strings.xml`)

## 📝 Лицензия

MIT License

---
Made with ❤️ for cooking enthusiasts
