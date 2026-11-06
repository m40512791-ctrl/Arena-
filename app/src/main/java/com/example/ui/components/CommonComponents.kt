package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaTopBar(
    title: String = "DEATH BATTLE ARENA",
    onSearchClick: () -> Unit = {},
    onCreateOcClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(CrimsonNeon, CyanNeon)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Arena Icon",
                        tint = ArenaBgDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "AI MULTI-AGENT ARENA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon,
                        letterSpacing = 1.sp
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_search_button")
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
            }
            IconButton(
                onClick = onCreateOcClick,
                modifier = Modifier.testTag("top_create_oc_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Create Custom Character", tint = GoldNeon)
            }
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("top_admin_button")
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel", tint = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArenaBgDark,
            titleContentColor = TextPrimary
        ),
        modifier = Modifier.border(
            width = 1.dp,
            color = ArenaBorder.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun SourceTypeBadge(sourceType: SourceType, modifier: Modifier = Modifier) {
    Surface(
        color = Color(sourceType.badgeColorHex).copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(sourceType.badgeColorHex).copy(alpha = 0.6f))
        ),
        modifier = modifier
    ) {
        Text(
            text = sourceType.displayName.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(sourceType.badgeColorHex),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PowerTierBadge(tier: PowerTier, modifier: Modifier = Modifier) {
    val tierColor = when {
        tier.rank >= 16 -> PurpleNeon
        tier.rank >= 13 -> GoldNeon
        tier.rank >= 10 -> CyanNeon
        tier.rank >= 7 -> CrimsonNeon
        else -> TextSecondary
    }

    Surface(
        color = tierColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(tierColor.copy(alpha = 0.8f))
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(tierColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "T${tier.rank}: ${tier.tierName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = tierColor
            )
        }
    }
}

@Composable
fun CharacterAvatarView(
    name: String,
    avatarUrl: String?,
    accentColor: Color,
    size: androidx.compose.ui.unit.Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)),
                shape = CircleShape
            )
            .border(1.5.dp, accentColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name portrait",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                fontSize = (size.value * 0.32f).sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}

@Composable
fun BattlePostCard(
    post: BattlePost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (post.isFeatured) CrimsonNeon.copy(alpha = 0.5f) else ArenaBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("battle_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Creator & Featured tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(ArenaSurfaceElevated, CircleShape)
                            .border(1.dp, CyanNeon.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.creatorName.take(1).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = post.creatorName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = post.createdAt,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                if (post.isFeatured) {
                    Surface(
                        color = CrimsonNeon.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(CrimsonNeon)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("FEATURED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CrimsonNeon)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Character VS Visual Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArenaBgDark, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character A
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CharacterAvatarView(
                        name = post.charA.name,
                        avatarUrl = post.charA.avatarUrl,
                        accentColor = CrimsonNeon,
                        size = 52.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.charA.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    SourceTypeBadge(post.charA.sourceType)
                }

                // VS Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ArenaSurfaceElevated, CircleShape)
                            .border(1.dp, GoldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldNeon
                        )
                    }
                    Text(
                        text = "${post.charA.sourceType.displayName.take(3)} v ${post.charB.sourceType.displayName.take(3)}",
                        fontSize = 9.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Character B
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CharacterAvatarView(
                        name = post.charB.name,
                        avatarUrl = post.charB.avatarUrl,
                        accentColor = CyanNeon,
                        size = 52.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.charB.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    SourceTypeBadge(post.charB.sourceType)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title
            Text(
                text = post.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Verdict Snapshot Box
            Surface(
                color = ArenaSurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Winner: ${post.verdict.winnerName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldNeon
                        )
                    }
                    Text(
                        text = post.verdict.difficulty.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Social Interaction Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like / Dislike
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(32.dp).testTag("like_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                            contentDescription = "Like",
                            tint = if (post.isLiked) CrimsonNeon else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${post.likes}",
                        fontSize = 12.sp,
                        color = if (post.isLiked) CrimsonNeon else TextSecondary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDislikeClick,
                        modifier = Modifier.size(32.dp).testTag("dislike_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                            contentDescription = "Dislike",
                            tint = if (post.isDisliked) DangerRed else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.commentsCount}", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(Icons.Default.Visibility, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.views}", fontSize = 12.sp, color = TextMuted)
                }

                // Bookmark & Share
                Row {
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp).testTag("save_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isSaved) CyanNeon else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp).testTag("share_btn_${post.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoundHpBar(
    name: String,
    hp: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("$hp% HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (hp / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = ArenaSurfaceElevated
        )
    }
}
