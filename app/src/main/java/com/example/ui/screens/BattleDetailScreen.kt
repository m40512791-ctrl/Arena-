package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.engine.AudioTtsManager
import com.example.engine.TranslationEngine
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleDetailScreen(
    post: BattlePost,
    repository: BattleRepository,
    ttsManager: AudioTtsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        ttsManager.stop()
        onBack()
    }

    val context = LocalContext.current
    val commentsMap by repository.comments.collectAsState()
    val comments = commentsMap[post.id] ?: emptyList()

    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "4 Rounds", "Novel Story", "Verdict", "Debate & Chat")

    // Translation State
    var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

    // Active Chapter in Novel Story
    var activeChapterIndex by remember { mutableIntStateOf(1) }
    val currentChapter = post.chapters.firstOrNull { it.chapterIndex == activeChapterIndex } ?: post.chapters.first()

    // Comment Input State
    var newCommentText by remember { mutableStateOf("") }

    // Community Poll State
    var userVotedAgree by remember { mutableStateOf<Boolean?>(null) }
    var agreeVotes by remember { mutableIntStateOf((post.likes * 0.72).toInt().coerceAtLeast(10)) }
    var disagreeVotes by remember { mutableIntStateOf((post.likes * 0.28).toInt().coerceAtLeast(4)) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Battle Analysis",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            ttsManager.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("battle_detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Language Switcher Chip
                    var showLangMenu by remember { mutableStateOf(false) }
                    Box {
                        TextButton(
                            onClick = { showLangMenu = true },
                            modifier = Modifier.testTag("lang_switch_button")
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(currentLanguage.nativeName, fontSize = 12.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false },
                            modifier = Modifier.background(ArenaSurfaceDark)
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.nativeName} (${lang.displayName})", color = TextPrimary) },
                                    onClick = {
                                        currentLanguage = lang
                                        showLangMenu = false
                                        Toast.makeText(context, "Language set to ${lang.displayName}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArenaBgDark)
            )
        },
        bottomBar = {
            // Audio Player Bar if listening to chapters or in Story tab
            if (selectedTab == "Novel Story" || ttsManager.isPlaying) {
                AudioPlayerBar(
                    ttsManager = ttsManager,
                    chapterTitle = currentChapter.title,
                    onPrevious = {
                        val prevIdx = if (activeChapterIndex > 1) activeChapterIndex - 1 else post.chapters.size
                        activeChapterIndex = prevIdx
                        val prevCh = post.chapters.first { it.chapterIndex == prevIdx }
                        ttsManager.playChapter(prevIdx, prevCh.title, prevCh.narrativeText)
                    },
                    onNext = {
                        val nextIdx = if (activeChapterIndex < post.chapters.size) activeChapterIndex + 1 else 1
                        activeChapterIndex = nextIdx
                        val nextCh = post.chapters.first { it.chapterIndex == nextIdx }
                        ttsManager.playChapter(nextIdx, nextCh.title, nextCh.narrativeText)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        },
        containerColor = ArenaBgDark
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sub-Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArenaSurfaceDark)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonNeon,
                            selectedLabelColor = ArenaBgDark,
                            containerColor = ArenaSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CrimsonNeon else ArenaBorder
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("detail_tab_$tab")
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                "Overview" -> OverviewTab(post = post, language = currentLanguage, repository = repository)
                "4 Rounds" -> FourRoundsTab(post = post, language = currentLanguage)
                "Novel Story" -> NovelStoryTab(
                    post = post,
                    activeChapterIndex = activeChapterIndex,
                    onChapterSelect = { activeChapterIndex = it },
                    ttsManager = ttsManager,
                    language = currentLanguage
                )
                "Verdict" -> VerdictTab(post = post, language = currentLanguage)
                "Debate & Chat" -> DebateCommunityTab(
                    post = post,
                    comments = comments,
                    userVotedAgree = userVotedAgree,
                    agreeVotes = agreeVotes,
                    disagreeVotes = disagreeVotes,
                    onVote = { agree ->
                        if (userVotedAgree == null) {
                            userVotedAgree = agree
                            if (agree) agreeVotes += 1 else disagreeVotes += 1
                        }
                    },
                    newCommentText = newCommentText,
                    onCommentTextChange = { newCommentText = it },
                    onPostComment = {
                        if (newCommentText.isNotBlank()) {
                            repository.addComment(post.id, newCommentText)
                            newCommentText = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OverviewTab(post: BattlePost, language: AppLanguage, repository: BattleRepository) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // VS Banner Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Char A
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        CharacterAvatarView(
                            name = post.charA.name,
                            avatarUrl = post.charA.avatarUrl,
                            accentColor = CrimsonNeon,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(post.charA.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CrimsonNeon, textAlign = TextAlign.Center)
                        Text(post.charA.universe, fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        SourceTypeBadge(post.charA.sourceType)
                        Spacer(modifier = Modifier.height(4.dp))
                        PowerTierBadge(post.charA.overallTier)
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ArenaBgDark, CircleShape)
                            .border(1.dp, GoldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldNeon)
                    }

                    // Char B
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        CharacterAvatarView(
                            name = post.charB.name,
                            avatarUrl = post.charB.avatarUrl,
                            accentColor = CyanNeon,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(post.charB.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CyanNeon, textAlign = TextAlign.Center)
                        Text(post.charB.universe, fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        SourceTypeBadge(post.charB.sourceType)
                        Spacer(modifier = Modifier.height(4.dp))
                        PowerTierBadge(post.charB.overallTier)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Confidence Metrics
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                color = ArenaSurfaceDark,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RESEARCH CONFIDENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${post.verdict.researchConfidence}%", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CyanNeon)
                    Text("Verified Canon Feats", fontSize = 10.sp, color = TextSecondary)
                }
            }
            Surface(
                color = ArenaSurfaceDark,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VERDICT CONFIDENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${post.verdict.verdictConfidence}%", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CrimsonNeon)
                    Text(post.verdict.difficulty.label, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Power Radar Chart
        Text(
            text = "COMBAT POWER RADAR COMPARISON",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Normalized 0-100 evaluation for UI visual scanning (STR, SPD, DUR, INT, SKL, RNG, HAX)",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = ArenaSurfaceDark,
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            PowerRadarChart(
                nameA = post.charA.name,
                statsA = post.charA.combatStats,
                colorA = CrimsonNeon,
                nameB = post.charB.name,
                statsB = post.charB.combatStats,
                colorB = CyanNeon
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Advanced Stats Comparison
        Text("ADVANCED CHARACTER STATISTICS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                StatRowComparison("Attack Potency", post.charA.advancedStats.attackPotency, post.charB.advancedStats.attackPotency, post.charA.name, post.charB.name)
                HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                StatRowComparison("Reaction Speed", post.charA.advancedStats.reactionSpeed, post.charB.advancedStats.reactionSpeed, post.charA.name, post.charB.name)
                HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                StatRowComparison("Destructive Cap.", post.charA.advancedStats.destructiveCapacity, post.charB.advancedStats.destructiveCapacity, post.charA.name, post.charB.name)
                HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                StatRowComparison("Battle IQ", post.charA.advancedStats.battleIQ, post.charB.advancedStats.battleIQ, post.charA.name, post.charB.name)
                HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                StatRowComparison("Hax & Resistances", post.charA.advancedStats.haxLevel, post.charB.advancedStats.haxLevel, post.charA.name, post.charB.name)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Verified Canon Feats
        Text("VERIFIED CANON FEATS & CITATIONS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            (post.charA.keyFeats.take(2) + post.charB.keyFeats.take(2)).forEach { feat ->
                Surface(
                    color = ArenaSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = CyanNeon.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = feat.evidenceType.label,
                                    fontSize = 10.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(feat.sourceReference, fontSize = 11.sp, color = GoldNeon, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(feat.description, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FourRoundsTab(post: BattlePost, language: AppLanguage) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "4-ROUND BATTLE SIMULATION",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = "Simulated across ${post.settings.battlefield} with dynamic turning points",
            fontSize = 12.sp,
            color = TextSecondary
        )

        post.rounds.forEach { round ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = TranslationEngine.translateStoryChapter(round.title, language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldNeon
                        )
                        Surface(
                            color = ArenaSurfaceElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ROUND ${round.roundNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Round HP gauges
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RoundHpBar(name = post.charA.name, hp = round.hpA, color = CrimsonNeon, modifier = Modifier.weight(1f))
                        RoundHpBar(name = post.charB.name, hp = round.hpB, color = CyanNeon, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Abilities deployed
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArenaBgDark, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("Abilities Deployed:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${post.charA.name}: ${round.abilityUsedA}", fontSize = 12.sp, color = CrimsonNeon)
                        Text("${post.charB.name}: ${round.abilityUsedB}", fontSize = 12.sp, color = CyanNeon)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Turning Point
                    Text("Turning Point:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(
                        text = TranslationEngine.translateStoryChapter(round.turningPoint, language),
                        fontSize = 12.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Outcome
                    Surface(
                        color = ArenaSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Outcome: ${TranslationEngine.translateStoryChapter(round.outcome, language)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NovelStoryTab(
    post: BattlePost,
    activeChapterIndex: Int,
    onChapterSelect: (Int) -> Unit,
    ttsManager: AudioTtsManager,
    language: AppLanguage
) {
    val context = LocalContext.current
    val currentChapter = post.chapters.firstOrNull { it.chapterIndex == activeChapterIndex } ?: post.chapters.first()
    val translatedTitle = TranslationEngine.translateStoryChapter(currentChapter.title, language)
    val translatedNarrative = TranslationEngine.translateStoryChapter(currentChapter.narrativeText, language)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Chapter selector chips
        Text("CHAPTER NAVIGATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            post.chapters.forEach { ch ->
                val isSelected = ch.chapterIndex == activeChapterIndex
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onChapterSelect(ch.chapterIndex)
                        if (ttsManager.isPlaying) {
                            ttsManager.playChapter(ch.chapterIndex, ch.title, ch.narrativeText)
                        }
                    },
                    label = { Text("Ch. ${ch.chapterIndex}", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanNeon,
                        selectedLabelColor = ArenaBgDark,
                        containerColor = ArenaSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Novel Story Card
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = translatedTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldNeon
                        )
                        Text(
                            text = "${currentChapter.wordCount} words • Original Chapter Narrative",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    // Copy chapter text
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Chapter Text", "$translatedTitle\n\n$translatedNarrative")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Chapter copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Story", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Chapter Narrative
                Text(
                    text = translatedNarrative,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Audio play prompt button inside card
                Button(
                    onClick = {
                        ttsManager.playChapter(currentChapter.chapterIndex, translatedTitle, translatedNarrative)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (ttsManager.isPlaying) ArenaSurfaceElevated else CrimsonNeon),
                    modifier = Modifier.fillMaxWidth().testTag("listen_chapter_btn")
                ) {
                    Icon(
                        imageVector = if (ttsManager.isPlaying) Icons.Default.VolumeUp else Icons.Default.Headphones,
                        contentDescription = null,
                        tint = if (ttsManager.isPlaying) CrimsonNeon else ArenaBgDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (ttsManager.isPlaying) "Narrating Now..." else "Listen to Narration (AI Voice)",
                        color = if (ttsManager.isPlaying) CrimsonNeon else ArenaBgDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VerdictTab(post: BattlePost, language: AppLanguage) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Winner Banner Card
        val winnerAvatar = if (post.charA.id == post.verdict.winnerId) post.charA.avatarUrl else post.charB.avatarUrl
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldNeon)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CharacterAvatarView(
                    name = post.verdict.winnerName,
                    avatarUrl = winnerAvatar,
                    accentColor = GoldNeon,
                    size = 72.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("OFFICIAL BATTLE WINNER", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldNeon, letterSpacing = 1.sp)
                Text(post.verdict.winnerName, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                PowerTierBadge(post.verdict.winnerTier)

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = ArenaSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Difficulty: ${post.verdict.difficulty.label} • Verdict Confidence: ${post.verdict.verdictConfidence}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Decisive Advantages & Key Feat
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CRITICAL DECISIVE FACTORS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Decisive Ability:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldNeon)
                Text(post.verdict.decisiveAbility, fontSize = 13.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(10.dp))

                Text("Main Advantage:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                Text(post.verdict.mainAdvantage, fontSize = 13.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(10.dp))

                Text("Critical Canon Feat:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonNeon)
                Text("\"${post.verdict.criticalFeat}\"", fontSize = 13.sp, color = TextPrimary)
            }
        }

        // Alternative Scenarios (Speed Equalized, Alternate Battlefield, Composite)
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("WHAT-IF & ALTERNATIVE SCENARIOS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                post.verdict.alternateScenarios.forEach { alt ->
                    Surface(
                        color = ArenaSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(alt.scenarioName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(alt.outcomeDescription, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Analytical Assessment Report
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DETAILED ANALYTICAL REPORT", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.analyticalReport,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun DebateCommunityTab(
    post: BattlePost,
    comments: List<BattleComment>,
    userVotedAgree: Boolean?,
    agreeVotes: Int,
    disagreeVotes: Int,
    onVote: (Boolean) -> Unit,
    newCommentText: String,
    onCommentTextChange: (String) -> Unit,
    onPostComment: () -> Unit
) {
    val totalVotes = agreeVotes + disagreeVotes
    val agreePercent = if (totalVotes > 0) ((agreeVotes.toFloat() / totalVotes) * 100).toInt() else 50
    val disagreePercent = 100 - agreePercent

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Community Poll Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COMMUNITY VERDICT POLL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text("Do you agree with the AI Verdict that ${post.verdict.winnerName} wins?", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onVote(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userVotedAgree == true) HealthGreen else ArenaSurfaceElevated
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Agree ($agreePercent%)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onVote(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userVotedAgree == false) DangerRed else ArenaSurfaceElevated
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Disagree ($disagreePercent%)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$totalVotes community members have voted", fontSize = 10.sp, color = TextMuted)
                }
            }
        }

        // Add Comment Box
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.weight(1f).testTag("comment_input_field"),
                    placeholder = { Text("Write your counter-argument or thoughts...", fontSize = 12.sp, color = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ArenaSurfaceDark,
                        unfocusedContainerColor = ArenaSurfaceDark,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = ArenaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onPostComment,
                    modifier = Modifier
                        .size(48.dp)
                        .background(CrimsonNeon, RoundedCornerShape(12.dp))
                        .testTag("send_comment_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = ArenaBgDark)
                }
            }
        }

        // Comments List
        items(comments) { comment ->
            Surface(
                color = ArenaSurfaceDark,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(comment.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                        Text(comment.timestamp, fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(comment.content, fontSize = 13.sp, color = TextPrimary)

                    if (comment.replies.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        comment.replies.forEach { reply ->
                            Surface(
                                color = ArenaSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(reply.authorName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldNeon)
                                    Text(reply.content, fontSize = 12.sp, color = TextPrimary)
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
private fun StatRowComparison(label: String, valA: String, valB: String, nameA: String, nameB: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.height(3.dp))
        Text("$nameA: $valA", fontSize = 12.sp, color = CrimsonNeon)
        Text("$nameB: $valB", fontSize = 12.sp, color = CyanNeon)
    }
}
