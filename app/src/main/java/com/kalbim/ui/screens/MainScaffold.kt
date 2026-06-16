package com.kalbim.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.kalbim.R
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.stringResource

@Composable
fun MainScaffold(vm: KalbimViewModel) {
    val navController = rememberNavController()
    val tabs = listOf(Tab.Home, Tab.Profile, Tab.List, Tab.Chart, Tab.Report)

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                tonalElevation = 2.dp,
                containerColor = MaterialTheme.colorScheme.background,
                windowInsets   = WindowInsets.navigationBars
            ) {
                val current = navController
                    .currentBackStackEntryAsState().value?.destination?.route
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon = {
                            when (tab) {
                                Tab.Home    -> Icon(Icons.Filled.Home,                   null)
                                Tab.Profile -> Icon(Icons.Filled.Person,                 null)
                                Tab.List    -> Icon(Icons.AutoMirrored.Filled.List,      null)
                                Tab.Chart   -> Icon(Icons.Filled.BarChart,               null)
                                Tab.Report  -> Icon(Icons.Filled.PictureAsPdf,           null)
                            }
                        },
                        label = {
                            Text(
                                when (tab) {
                                    Tab.Home    -> stringResource(R.string.nav_home)
                                    Tab.Profile -> stringResource(R.string.nav_profile)
                                    Tab.List    -> stringResource(R.string.nav_list)
                                    Tab.Chart   -> stringResource(R.string.nav_chart)
                                    Tab.Report  -> stringResource(R.string.nav_report)
                                }
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Tab.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Tab.Home.route)    { HomeScreen(vm) }
            composable(Tab.Profile.route) { ProfileScreen(vm) }
            composable(Tab.List.route)    { ListScreen(vm) }
            composable(Tab.Chart.route)   { ChartScreen(vm) }
            composable(Tab.Report.route)  { ReportScreen(vm) }
        }
    }
}