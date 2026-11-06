package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.model.*
import com.example.ui.components.BattlePostCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: BattleRepository,
    onBattleClick: (BattlePost) -> Unit,
    onNewBattleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val battles by repository.battles.collectAsState()
    var selectedCategory by remember { mutableStateOf("For You") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filterSourceType by remember { mutableStateOf<SourceType?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val categories = listOf("For You", "Trending", "Popular", "Novel VS Anime", "Featured", "Most Discussed")

    val filteredBattles = remember(battles, selectedCategory, searchQuery, filterSourceType) {
        battles.filter { post ->
            val matchesCategory = when (selectedCategory) {
                "For You" -> true
                "Trending" -> post.views > 40000
                "Popular" -> post.likes > 3000
                "Novel VS Anime" -> (post.charA.sourceType == SourceType.WEB_NOVEL || post.charA.sourceType == SourceType.NOVEL || post.charA.sourceType == SourceType.LIGHT_NOVEL) && (post.charB.sourceType == SourceType.ANIME || post.charB.sourceType == SourceType.MANGA)
                "Featured" -> post.isFeatured
                "Most Discussed" -> post.commentsCount > 300
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.charA.name.contains(searchQuery, ignoreCase = true) ||
                post.charB.name.contains(searchQuery, ignoreCase = true) ||
                post.charA.universe.contains(searchQuery, ignoreCase = true) ||
                post.charB.universe.contains(searchQuery, ignoreCase = true)
            }

            val matchesSource = if (filterSourceType == null) true else {
                post.charA.sourceType == filterSourceType || post.charB.sourceType == filterSourceType
            }

            matchesCategory && matchesSearch && matchesSource
        }
    }

    Box(modifier = modifier.fillMaxSize().background(ArenaBgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Filter Toggle
            Surface(
                color = ArenaSurfaceDark,
                modifier = Modifier.fillMaxWidth().border(1.dp, ArenaBorder.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("home_search_field"),
                            placeholder = { Text("Search characters, universes, tiers...", fontSize = 13.sp, color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = ArenaBorder,
                                focusedContainerColor = ArenaSurfaceElevated,
                                unfocusedContainerColor = ArenaSurfaceElevated,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (filterSourceType != null) CyanNeon.copy(alpha = 0.2f) else ArenaSurfaceElevated,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, if (filterSourceType != null) CyanNeon else ArenaBorder, RoundedCornerShape(12.dp))
                                .testTag("home_filter_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (filterSourceType != null) CyanNeon else TextPrimary
                            )
                        }
                    }

                    if (filterSourceType != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Filtered by: ${filterSourceType?.displayName}",
                                fontSize = 11.sp,
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = DangerRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { filterSourceType = null }
                            )
                        }
                    }
                }
            }

            // Categories Horizontal Scroll Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArenaBgDark)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
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
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("feed_tab_$category")
                    )
                }
            }

            // Battles List
            if (filteredBattles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsKabaddi,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No battles found",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your search terms or create a new battle!",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNewBattleClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                        ) {
                            Text("Create New Battle", color = ArenaBgDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredBattles, key = { it.id }) { post ->
                        BattlePostCard(
                            post = post,
                            onClick = { onBattleClick(post) },
                            onLikeClick = { repository.toggleLike(post.id) },
                            onDislikeClick = { repository.toggleDislike(post.id) },
                            onSaveClick = { repository.toggleSave(post.id) },
                            onShareClick = { /* Share dialog */ }
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        AlertDialog(
            onDismissRequest = { showFilterSheet = false },
            title = { Text("Filter by Media Source", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select a fictional universe type to filter battles:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        filterSourceType = null
                                        showFilterSheet = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = filterSourceType == null, onClick = {
                                    filterSourceType = null
                                    showFilterSheet = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("All Media Sources", color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                        items(SourceType.values().toList()) { source ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        filterSourceType = source
                                        showFilterSheet = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = filterSourceType == source, onClick = {
                                    filterSourceType = source
                                    showFilterSheet = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(source.displayName, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterSheet = false }) {
                    Text("Close", color = CyanNeon)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }
}
