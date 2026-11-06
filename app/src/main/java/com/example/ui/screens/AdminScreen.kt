package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: BattleRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val battles by repository.battles.collectAsState()
    val characters by repository.characters.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Arena Administration & AI Control", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArenaBgDark)
            )
        },
        containerColor = ArenaBgDark
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metrics Overview
            item {
                Text("PLATFORM OVERVIEW", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldNeon)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Total Battles", value = "${battles.size}", color = CrimsonNeon, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Active Roster", value = "${characters.size}", color = CyanNeon, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "AI Model Engine", value = "Gemini / Hybrid", color = GoldNeon, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Health Status", value = "99.98% OK", color = HealthGreen, modifier = Modifier.weight(1f))
                }
            }

            // AI Multi-Agent Architecture
            item {
                Text("13 SPECIALIZED AI AGENTS ARCHITECTURE", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val agents = listOf(
                            "Agent 1: Character Research Agent" to "Extracts canon abilities, weaknesses & combat records",
                            "Agent 2: Canon Verification Agent" to "Checks official publications vs fan theory claims",
                            "Agent 3: Feat Analysis Agent" to "Calculates Joules, speed factors & dimensional scaling",
                            "Agent 4: Power Scaling Agent" to "Assigns one of 17 Custom Power Tiers",
                            "Agent 5: Ability Analysis Agent" to "Evaluates hax counters, conceptual immunities",
                            "Agent 6: Battle Strategy Agent" to "Simulates opening stratagems and adaptation curves",
                            "Agent 7: Battle Simulation Agent" to "Generates 4 Round turnarounds and HP depletion",
                            "Agent 8: Verdict Judge Agent" to "Applies victory conditions and difficulty tier",
                            "Agent 9: Fact Checker Agent" to "Verifies citations, removes hallucinations",
                            "Agent 10: Story Writer Agent" to "Crafts 6-chapter novel battle narrative",
                            "Agent 11: Translation Agent" to "Multi-language contextual localization",
                            "Agent 12: Voice Agent" to "Text-to-Speech audio synthesizer",
                            "Agent 13: Report Editor Agent" to "Assembles final analytical debrief"
                        )
                        agents.forEach { (name, role) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                                Text(role, fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // Content Moderation / Featured Battles Manager
            item {
                Text("FEATURED BATTLES MODERATION", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(battles) { battle ->
                Surface(
                    color = ArenaSurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(battle.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text("Creator: ${battle.creatorName} • ${battle.likes} likes", fontSize = 11.sp, color = TextMuted)
                        }

                        Switch(
                            checked = battle.isFeatured,
                            onCheckedChange = { repository.toggleFeatured(battle.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldNeon)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
