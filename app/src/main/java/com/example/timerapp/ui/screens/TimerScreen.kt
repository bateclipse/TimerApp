package com.example.timerapp.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timerapp.R
import com.example.timerapp.service.TimerForegroundService
import com.example.timerapp.ui.components.AppDrawer
import com.example.timerapp.ui.components.ProductPresetChips
import com.example.timerapp.ui.components.TimePresetChips
import com.example.timerapp.ui.components.TimerCard
import com.example.timerapp.viewmodel.TimerViewModel

/**
 * Главный экран с двумя таймерами
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Запрос разрешения на уведомления для Android 13+
    var showNotificationDialog by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showNotificationDialog = true
        }
    }
    
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    
    // Состояние Drawer
    var drawerOpen by remember { mutableStateOf(false) }
    
    // Меню "..." в AppBar
    var showMenu by remember { mutableStateOf(false) }
    
    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(
                currentRoute = "timers",
                onNavigate = onNavigate,
                onCloseDrawer = { drawerOpen = false }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.nav_timers)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.drawer_open)
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.menu_export)) },
                                    onClick = {
                                        showMenu = false
                                        // TODO: Export functionality
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.menu_import)) },
                                    onClick = {
                                        showMenu = false
                                        // TODO: Import functionality
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.menu_share)) },
                                    onClick = {
                                        showMenu = false
                                        // TODO: Share functionality
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "Kitchen Timer App")
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, null))
                                    }
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Быстрые предустановки времени
                item {
                    TimePresetChips(
                        presets = viewModel.getTimePresets(),
                        onPresetSelected = { seconds ->
                            viewModel.setTimeForActiveTimer(seconds)
                        }
                    )
                }
                
                // Предустановки продуктов
                item {
                    ProductPresetChips(
                        presets = viewModel.getProductPresets(),
                        onProductSelected = { seconds ->
                            viewModel.setTimeForActiveTimer(seconds)
                        }
                    )
                }
                
                // Выбор активного таймера для предустановок
                item {
                    val activeTimer by viewModel.activeTimerId.collectAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activeTimer == "A",
                            onClick = { viewModel.setActiveTimer("A") },
                            label = { Text(stringResource(R.string.timer_a)) }
                        )
                        FilterChip(
                            selected = activeTimer == "B",
                            onClick = { viewModel.setActiveTimer("B") },
                            label = { Text(stringResource(R.string.timer_b)) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.select_timer_for_preset),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Таймер A
                item {
                    TimerCard(timerId = "A")
                }
                
                // Таймер B
                item {
                    TimerCard(timerId = "B")
                }
                
                // Настройки звука
                item {
                    SoundSettingsSection(viewModel = viewModel)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    
    // Диалог если разрешение не предоставлено
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Разрешение на уведомления") },
            text = { Text("Для работы таймера в фоне требуется разрешение на отправку уведомлений") },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Секция настроек звука
 */
@Composable
private fun SoundSettingsSection(
    viewModel: TimerViewModel
) {
    var showSoundDialog by remember { mutableStateOf(false) }
    val sounds by viewModel.systemSounds.collectAsState()
    val selectedUri by viewModel.selectedSoundUri.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_sound),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showSoundDialog = true },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(stringResource(R.string.settings_select_sound))
            }
        }
    }
    
    // Диалог выбора звука
    if (showSoundDialog) {
        AlertDialog(
            onDismissRequest = { showSoundDialog = false },
            title = { Text(stringResource(R.string.settings_select_sound)) },
            text = {
                LazyColumn {
                    items(sounds) { (title, uri) ->
                        val uriString = uri.toString()
                        ListItem(
                            headlineContent = { Text(title) },
                            supportingContent = {
                                if (uriString == selectedUri) {
                                    Text("Выбрано", style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            leadingContent = {
                                RadioButton(
                                    selected = uriString == selectedUri,
                                    onClick = {
                                        viewModel.saveSoundUri(uriString)
                                    }
                                )
                            },
                            trailingContent = {
                                if (uri.toString().isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            viewModel.previewSound(uri)
                                        }
                                    ) {
                                        Text(stringResource(R.string.settings_preview_sound))
                                    }
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSoundDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
