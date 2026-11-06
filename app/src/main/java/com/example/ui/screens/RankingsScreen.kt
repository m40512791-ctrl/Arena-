package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.model.BattlePost
import com.example.model.CreatorProfile
import com.example.ui.components.CharacterAvatarView
import com.example.ui.components.PowerTierBadge
import com.example.ui.theme.*

@Composable
fun RankingsScreen(
    repository: BattleRepository,
    onBattleClick: (BattlePost) -> Unit,
    onCreatorClick: (CreatorProfile) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val battles by repository.battles.collectAsState()
    val creators by repository.creators.collectAsState()
    val characters by repository.characters.collectAsState()

    var selectedRankingType by remember { mutableStateOf("Popular Battles") }
    val rankingTabs = listOf("Popular Battles", "Character Roster", "Top Creators")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaBgDark)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = GoldNeon, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "ARENA LEADERBOARDS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Rankings based on views, likes & community engagement",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rankingTabs.forEach { tab ->
                val isSelected = tab == selectedRankingType
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedRankingType = tab },
                    label = { Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldNeon,
                        selectedLabelColor = ArenaBgDark,
                        containerColor = ArenaSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("ranking_tab_$tab")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedRankingType) {
            "Popular Battles" -> {
                val sortedBattles = battles.sortedByDescending { it.likes + (it.views / 10) }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(sortedBattles) { index, battle ->
                        LeaderboardBattleItem(rank = index + 1, battle = battle, onClick = { onBattleClick(battle) })
                    }
                }
            }
            "Character Roster" -> {
                val sortedChars = characters.sortedByDescending { it.overallTier.rank * 10 + it.combatStats.specialAbility }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(sortedChars) { index, char ->
                        Surface(
                            color = ArenaSurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(if (index < 3) GoldNeon else ArenaSurfaceElevated, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (index < 3) ArenaBgDark else TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(char.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${char.sourceType.displayName} • ${char.universe}", fontSize = 11.sp, color = TextMuted)
                                }
                                PowerTierBadge(char.overallTier)
                            }
                        }
                    }
                }
            }
            "Top Creators" -> {
                val sortedCreators = creators.sortedByDescending { it.followersCount }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(sortedCreators) { index, creator ->
                        Surface(
                            color = ArenaSurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCreatorClick(creator) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(if (index < 3) CyanNeon else ArenaSurfaceElevated, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (index < 3) ArenaBgDark else TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                CharacterAvatarView(name = creator.displayName, avatarUrl = creator.avatarUrl, accentColor = CyanNeon, size = 38.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(creator.displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                                        if (creator.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text("${creator.followersCount} followers • ${creator.totalBattles} battles", fontSize = 11.sp, color = TextMuted)
                                }
                                Button(
                                    onClick = { repository.toggleFollowCreator(creator.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (creator.isFollowed) ArenaSurfaceElevated else CyanNeon
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (creator.isFollowed) "Following" else "Follow",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (creator.isFollowed) TextSecondary else ArenaBgDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardBattleItem(rank: Int, battle: BattlePost, onClick: () -> Unit) {
    Surface(
        color = ArenaSurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (rank == 1) GoldNeon.copy(alpha = 0.5f) else ArenaBorder)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        when (rank) {
                            1 -> GoldNeon
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> ArenaSurfaceElevated
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (rank <= 3) ArenaBgDark else TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(battle.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                Text("Winner: ${battle.verdict.winnerName} • ${battle.likes} likes • ${battle.views} views", fontSize = 11.sp, color = TextMuted)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
