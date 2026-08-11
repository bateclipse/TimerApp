package com.example.timerapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.timerapp.R

/**
 * Компонент выдвижного меню (Navigation Drawer)
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        // Заголовок приложения
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Kitchen Timer",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Пункты меню навигации
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_timers)) },
            selected = currentRoute == "timers",
            onClick = {
                onNavigate("timers")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Timer, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_products)) },
            selected = currentRoute == "products",
            onClick = {
                onNavigate("products")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_recipes)) },
            selected = currentRoute == "recipes",
            onClick = {
                onNavigate("recipes")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_calculators)) },
            selected = currentRoute == "calculators",
            onClick = {
                onNavigate("calculators")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_notes)) },
            selected = currentRoute == "notes",
            onClick = {
                onNavigate("notes")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Note, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_temperatures)) },
            selected = currentRoute == "temperatures",
            onClick = {
                onNavigate("temperatures")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Thermostat, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_settings)) },
            selected = currentRoute == "settings",
            onClick = {
                onNavigate("settings")
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // О программе внизу
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    onNavigate("about")
                    onCloseDrawer()
                }
        ) {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = stringResource(R.string.about_title),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.about_title),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
