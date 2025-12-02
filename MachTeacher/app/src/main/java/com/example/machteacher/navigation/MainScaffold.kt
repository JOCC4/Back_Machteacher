package com.example.machteacher.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var authId by rememberSaveable { mutableStateOf<Long?>(null) }
    var authRole by rememberSaveable { mutableStateOf<String?>(null) }

    var unreadMessages by rememberSaveable { mutableIntStateOf(0) }

    val hideBottomBar =
        currentRoute?.startsWith(Routes.Login.route) == true ||
                currentRoute == Routes.Registration.route

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                BottomBar(
                    nav = nav,
                    unreadMessages = unreadMessages
                )
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AppNavHost(
                navController = nav,
                authId = authId,
                authRole = authRole,
                setAuth = { id, role ->
                    authId = id
                    authRole = role
                },
                clearAuth = {
                    authId = null
                    authRole = null
                },
                onUnreadMessagesChanged = { count ->
                    unreadMessages = count
                }
            )
        }
    }
}

@Composable
private fun BottomBar(
    nav: NavHostController,
    unreadMessages: Int
) {
    data class Item(val route: Routes, val label: String, val icon: ImageVector)

    val items = listOf(
        Item(Routes.Home, "Inicio", Icons.Outlined.Home),
        Item(Routes.Search, "Buscar", Icons.Outlined.Search),
        Item(Routes.Sessions, "Sesiones", Icons.Outlined.Event),
        Item(Routes.Messages, "Mensajes", Icons.Outlined.Chat),
        Item(Routes.Profile, "Perfil", Icons.Outlined.Person),
    )

    val current by nav.currentBackStackEntryAsState()
    val currentRoute = current?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        nav.navigate(item.route.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.route == Routes.Messages && unreadMessages > 0) {
                        Box {
                            Icon(item.icon, contentDescription = null)

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(androidx.compose.ui.Alignment.TopEnd)
                            ) {
                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    drawCircle(Color(0xFFB70A0A))
                                }
                            }
                        }
                    } else {
                        Icon(item.icon, contentDescription = null)
                    }
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
