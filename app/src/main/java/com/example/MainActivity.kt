package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.engine.AudioTtsManager
import com.example.model.BattlePost
import com.example.model.CreatorProfile
import com.example.ui.components.ArenaTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppScreen {
    FEED,
    NEW_BATTLE,
    RANKINGS,
    PROFILE,
    BATTLE_DETAIL,
    CREATE_OC,
    ADMIN
}

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: AudioTtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ttsManager = AudioTtsManager(this)

        setContent {
            MyApplicationTheme {
                val repository = remember { BattleRepository() }
                var currentScreen by remember { mutableStateOf(AppScreen.FEED) }
                var selectedBattlePost by remember { mutableStateOf<BattlePost?>(null) }
                var selectedCreatorProfile by remember { mutableStateOf<CreatorProfile?>(null) }
                val previousScreenStack = remember { mutableStateListOf<AppScreen>() }

                fun navigateTo(screen: AppScreen) {
                    previousScreenStack.add(currentScreen)
                    currentScreen = screen
                }

                fun navigateBack() {
                    if (selectedCreatorProfile != null) {
                        selectedCreatorProfile = null
                    }
                    if (previousScreenStack.isNotEmpty()) {
                        currentScreen = previousScreenStack.removeLast()
                    } else {
                        currentScreen = AppScreen.FEED
                    }
                }

                BackHandler(enabled = currentScreen != AppScreen.FEED || selectedCreatorProfile != null) {
                    navigateBack()
                }

                val navItems = listOf(
                    NavItem(AppScreen.FEED, "Feed", Icons.Default.DynamicFeed, "nav_feed"),
                    NavItem(AppScreen.NEW_BATTLE, "New Battle", Icons.Default.FlashOn, "nav_new_battle"),
                    NavItem(AppScreen.RANKINGS, "Rankings", Icons.Default.Leaderboard, "nav_rankings"),
                    NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
                )

                val showBottomBar = currentScreen in listOf(
                    AppScreen.FEED,
                    AppScreen.NEW_BATTLE,
                    AppScreen.RANKINGS,
                    AppScreen.PROFILE
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showBottomBar) {
                            ArenaTopBar(
                                onSearchClick = {
                                    if (currentScreen != AppScreen.FEED) {
                                        navigateTo(AppScreen.FEED)
                                    }
                                },
                                onCreateOcClick = {
                                    navigateTo(AppScreen.CREATE_OC)
                                },
                                onAdminClick = {
                                    navigateTo(AppScreen.ADMIN)
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = ArenaBgDark,
                                contentColor = TextPrimary,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .border(1.dp, ArenaBorder.copy(alpha = 0.5f))
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                navItems.forEach { item ->
                                    val isSelected = currentScreen == item.screen && (item.screen != AppScreen.PROFILE || selectedCreatorProfile == null)
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (item.screen == AppScreen.PROFILE) {
                                                selectedCreatorProfile = null
                                            }
                                            if (currentScreen != item.screen) {
                                                currentScreen = item.screen
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = if (isSelected) CrimsonNeon else TextSecondary
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.title,
                                                fontSize = 11.sp,
                                                color = if (isSelected) CrimsonNeon else TextSecondary
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = CrimsonNeon.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    },
                    containerColor = ArenaBgDark
                ) { innerPadding ->
                    Crossfade(
                        targetState = currentScreen,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            AppScreen.FEED -> {
                                HomeScreen(
                                    repository = repository,
                                    onBattleClick = { battle ->
                                        selectedBattlePost = battle
                                        repository.recordView(battle.id)
                                        navigateTo(AppScreen.BATTLE_DETAIL)
                                    },
                                    onNewBattleClick = {
                                        currentScreen = AppScreen.NEW_BATTLE
                                    }
                                )
                            }
                            AppScreen.NEW_BATTLE -> {
                                NewBattleScreen(
                                    repository = repository,
                                    onBattleCreated = { newBattle ->
                                        selectedBattlePost = newBattle
                                        navigateTo(AppScreen.BATTLE_DETAIL)
                                    }
                                )
                            }
                            AppScreen.RANKINGS -> {
                                RankingsScreen(
                                    repository = repository,
                                    onBattleClick = { battle ->
                                        selectedBattlePost = battle
                                        repository.recordView(battle.id)
                                        navigateTo(AppScreen.BATTLE_DETAIL)
                                    },
                                    onCreatorClick = { creator ->
                                        selectedCreatorProfile = creator
                                        navigateTo(AppScreen.PROFILE)
                                    }
                                )
                            }
                            AppScreen.PROFILE -> {
                                ProfileScreen(
                                    repository = repository,
                                    onBattleClick = { battle ->
                                        selectedBattlePost = battle
                                        repository.recordView(battle.id)
                                        navigateTo(AppScreen.BATTLE_DETAIL)
                                    },
                                    targetCreator = selectedCreatorProfile,
                                    onBack = {
                                        selectedCreatorProfile = null
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.BATTLE_DETAIL -> {
                                selectedBattlePost?.let { battle ->
                                    BattleDetailScreen(
                                        post = battle,
                                        repository = repository,
                                        ttsManager = ttsManager,
                                        onBack = { navigateBack() }
                                    )
                                } ?: run {
                                    currentScreen = AppScreen.FEED
                                }
                            }
                            AppScreen.CREATE_OC -> {
                                CharacterCreatorScreen(
                                    repository = repository,
                                    onBack = { navigateBack() }
                                )
                            }
                            AppScreen.ADMIN -> {
                                AdminScreen(
                                    repository = repository,
                                    onBack = { navigateBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.release()
    }
}
