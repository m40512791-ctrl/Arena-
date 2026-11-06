package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.BattleRepository
import com.example.model.*
import com.example.storage.ImageStorageService
import com.example.storage.ImageValidationResult
import com.example.ui.components.BattlePostCard
import com.example.ui.components.CharacterAvatarView
import com.example.ui.components.ImageCropEditorModal
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: BattleRepository,
    onBattleClick: (BattlePost) -> Unit,
    targetCreator: CreatorProfile? = null, // null means viewing own profile
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val storageService = remember { ImageStorageService(context) }

    val myProfile by repository.userProfile.collectAsState()
    val privacySettings by repository.privacySettings.collectAsState()
    val battles by repository.battles.collectAsState()
    val socialPosts by repository.socialPosts.collectAsState()
    val creators by repository.creators.collectAsState()
    val historyIds by repository.history.collectAsState()

    val isOwnProfile = (targetCreator == null || targetCreator.id == myProfile.id)
    val activeProfile = if (isOwnProfile) myProfile else (creators.firstOrNull { it.id == targetCreator?.id } ?: targetCreator!!)

    var selectedTab by remember { mutableStateOf("Posts") }
    val tabs = listOf("Posts", "Battles", "Replies", "Media", "Likes", "About")

    // Image Upload Modals for Profile
    var pendingBitmapForCrop by remember { mutableStateOf<Bitmap?>(null) }
    var cropMode by remember { mutableStateOf<String?>(null) } // "avatar" or "cover"

    val avatarGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                when (val res = storageService.validateAndDecode(uri)) {
                    is ImageValidationResult.Success -> {
                        pendingBitmapForCrop = res.bitmap
                        cropMode = "avatar"
                    }
                    is ImageValidationResult.Error -> Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val coverGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                when (val res = storageService.validateAndDecode(uri)) {
                    is ImageValidationResult.Success -> {
                        pendingBitmapForCrop = res.bitmap
                        cropMode = "cover"
                    }
                    is ImageValidationResult.Error -> Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Dialog States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showFollowersDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }
    var showNewPostDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaBgDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 18.1 Profile Header (Cover Photo & Avatar & Bio)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    // Cover Photo
                    if (activeProfile.coverUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(activeProfile.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(CrimsonNeonDark, ArenaSurfaceElevated, ArenaBgDark)
                                    )
                                )
                        )
                    }

                    // Cover overlay gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color.Transparent, ArenaBgDark.copy(alpha = 0.85f))
                                )
                            )
                    )

                    // Change Cover Photo button (for own profile)
                    if (isOwnProfile) {
                        IconButton(
                            onClick = { coverGalleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(36.dp)
                                .background(ArenaBgDark.copy(alpha = 0.7f), CircleShape)
                                .testTag("change_cover_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Change Cover", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(36.dp)
                                .background(ArenaBgDark.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Avatar overlapping bottom of cover
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp)
                            .offset(y = 36.dp)
                    ) {
                        CharacterAvatarView(
                            name = activeProfile.displayName,
                            avatarUrl = activeProfile.avatarUrl,
                            accentColor = CyanNeon,
                            size = 80.dp
                        )

                        // Change Avatar Badge
                        if (isOwnProfile) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonNeon)
                                    .clickable { avatarGalleryLauncher.launch("image/*") }
                                    .testTag("change_avatar_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Upload Avatar", tint = ArenaBgDark, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(42.dp))

                // Profile Info & Action Row
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeProfile.displayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                if (activeProfile.isVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = CyanNeon, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = activeProfile.handle,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        // Profile Actions (Own vs Other)
                        if (isOwnProfile) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showEditProfileDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceElevated),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("edit_profile_btn")
                                ) {
                                    Text("Edit Profile", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { showPrivacyDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ArenaSurfaceElevated, CircleShape)
                                        .border(1.dp, ArenaBorder, CircleShape)
                                        .testTag("profile_privacy_btn")
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = "Privacy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { repository.toggleFollowCreator(activeProfile.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (activeProfile.isFollowed) ArenaSurfaceElevated else CyanNeon
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("follow_creator_toggle_btn")
                                ) {
                                    Text(
                                        text = if (activeProfile.isFollowed) "Following" else "Follow",
                                        color = if (activeProfile.isFollowed) TextSecondary else ArenaBgDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = { showMessageDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ArenaSurfaceElevated, CircleShape)
                                        .border(1.dp, ArenaBorder, CircleShape)
                                ) {
                                    Icon(Icons.Default.MailOutline, contentDescription = "Message", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Profile URL", "deathbattlearena.com/${activeProfile.handle}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Profile link copied: deathbattlearena.com/${activeProfile.handle}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ArenaSurfaceElevated, CircleShape)
                                        .border(1.dp, ArenaBorder, CircleShape)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share Profile", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio
                    Text(
                        text = activeProfile.bio,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location, Website & Join Date Meta Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeProfile.location.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(activeProfile.location, fontSize = 11.sp, color = TextMuted)
                            }
                        }
                        if (activeProfile.website.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = activeProfile.website.removePrefix("https://").take(22),
                                    fontSize = 11.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Joined ${activeProfile.joinDate}", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 18.5 Followers & Following Counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.clickable { showFollowingDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${activeProfile.followingCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Following", fontSize = 12.sp, color = TextMuted)
                        }

                        Row(
                            modifier = Modifier.clickable { showFollowersDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${activeProfile.followersCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Followers", fontSize = 12.sp, color = TextMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${activeProfile.totalPosts}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Posts", fontSize = 12.sp, color = TextMuted)
                        }
                    }

                    // Post Dispatch Button (for own profile)
                    if (isOwnProfile) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showNewPostDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_post_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish Arena Thought / Dispatch", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 18.3 Profile Navigation Tabs (Posts, Battles, Replies, Media, Likes, About)
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
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("profile_tab_$tab")
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                "Posts" -> {
                    val userPosts = socialPosts.filter { it.authorId == activeProfile.id || it.authorName == activeProfile.displayName }
                    if (userPosts.isEmpty()) {
                        item {
                            EmptyStateMessage("No dispatches posted yet.")
                        }
                    } else {
                        items(userPosts, key = { it.id }) { post ->
                            SocialPostCard(post = post, onLike = { repository.toggleLikeSocialPost(post.id) })
                        }
                    }
                }
                "Battles" -> {
                    val userBattles = battles.filter { it.creatorName.contains(activeProfile.displayName) || it.creatorName.contains("Grandmaster") }
                    if (userBattles.isEmpty()) {
                        item {
                            EmptyStateMessage("No published battles yet.")
                        }
                    } else {
                        items(userBattles, key = { it.id }) { battle ->
                            BattlePostCard(
                                post = battle,
                                onClick = { onBattleClick(battle) },
                                onLikeClick = { repository.toggleLike(battle.id) },
                                onDislikeClick = { repository.toggleDislike(battle.id) },
                                onSaveClick = { repository.toggleSave(battle.id) },
                                onShareClick = {}
                            )
                        }
                    }
                }
                "Replies" -> {
                    item {
                        Surface(
                            color = ArenaSurfaceDark,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Recent Debate Replies", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Replying to @mysteries_fool on 'Klein Moretti VS Fang Yuan': 'Grafting can indeed bypass physical defenses, but Great Love's sovereign aperture possesses non-conflicting Dao marks that neutralize foreign spiritual corruption.'",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
                "Media" -> {
                    val mediaPosts = socialPosts.filter { !it.attachedMediaUrl.isNullOrBlank() }
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Uploaded Fictional Art & Media", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (mediaPosts.isEmpty()) {
                                Text("No uploaded battle media yet.", fontSize = 12.sp, color = TextMuted)
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.height(240.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(mediaPosts) { p ->
                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ArenaSurfaceElevated)
                                        ) {
                                            AsyncImage(
                                                model = p.attachedMediaUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Likes" -> {
                    val likedBattles = battles.filter { it.isLiked }
                    if (likedBattles.isEmpty()) {
                        item { EmptyStateMessage("No liked battles to display.") }
                    } else {
                        items(likedBattles, key = { it.id }) { battle ->
                            BattlePostCard(
                                post = battle,
                                onClick = { onBattleClick(battle) },
                                onLikeClick = { repository.toggleLike(battle.id) },
                                onDislikeClick = { repository.toggleDislike(battle.id) },
                                onSaveClick = { repository.toggleSave(battle.id) },
                                onShareClick = {}
                            )
                        }
                    }
                }
                "About" -> {
                    item {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // 18.9 Creator Analytics Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("CREATOR ANALYTICS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldNeon)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                        ProfileStatItem(count = "${activeProfile.totalBattles}", label = "Published Battles")
                                        ProfileStatItem(count = "84.2K", label = "Total Views")
                                        ProfileStatItem(count = "${activeProfile.totalLikes}", label = "Total Likes")
                                        ProfileStatItem(count = "+18%", label = "Monthly Growth")
                                    }
                                }
                            }

                            // Details Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("COMMUNITY CREDENTIALS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Cosmological Tier: Tier 17 Outerversal Researcher", fontSize = 12.sp, color = CyanNeon)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Platform Role: Senior Lorekeeper & Battle Judge", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Account Status: Verified Creator", fontSize = 12.sp, color = HealthGreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Crop Modal for Profile Avatar or Cover
    if (pendingBitmapForCrop != null && cropMode != null) {
        val isCover = cropMode == "cover"
        ImageCropEditorModal(
            rawBitmap = pendingBitmapForCrop!!,
            initialAspectRatio = if (isCover) CropAspectRatio.LANDSCAPE_16_9 else CropAspectRatio.SQUARE_1_1,
            onConfirm = { cropped ->
                coroutineScope.launch {
                    val meta = storageService.saveImage(cropped, categoryTag = if (isCover) "cover" else "avatar")
                    if (isCover) {
                        repository.updateUserProfile(
                            displayName = activeProfile.displayName,
                            handle = activeProfile.handle,
                            bio = activeProfile.bio,
                            location = activeProfile.location,
                            website = activeProfile.website,
                            avatarUrl = activeProfile.avatarUrl,
                            coverUrl = meta.fileUriString
                        )
                        Toast.makeText(context, "Cover photo updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        repository.updateUserProfile(
                            displayName = activeProfile.displayName,
                            handle = activeProfile.handle,
                            bio = activeProfile.bio,
                            location = activeProfile.location,
                            website = activeProfile.website,
                            avatarUrl = meta.fileUriString,
                            coverUrl = activeProfile.coverUrl
                        )
                        Toast.makeText(context, "Avatar updated!", Toast.LENGTH_SHORT).show()
                    }
                    pendingBitmapForCrop = null
                    cropMode = null
                }
            },
            onCancel = {
                pendingBitmapForCrop = null
                cropMode = null
            }
        )
    }

    // 18.2 Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(activeProfile.displayName) }
        var editHandle by remember { mutableStateOf(activeProfile.handle) }
        var editBio by remember { mutableStateOf(activeProfile.bio) }
        var editLocation by remember { mutableStateOf(activeProfile.location) }
        var editWebsite by remember { mutableStateOf(activeProfile.website) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editDialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = editHandle,
                        onValueChange = { editHandle = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editDialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editDialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editDialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = editWebsite,
                        onValueChange = { editWebsite = it },
                        label = { Text("Website Link") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editDialogTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateUserProfile(
                            displayName = editName,
                            handle = editHandle,
                            bio = editBio,
                            location = editLocation,
                            website = editWebsite,
                            avatarUrl = activeProfile.avatarUrl,
                            coverUrl = activeProfile.coverUrl
                        )
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                ) {
                    Text("Save Changes", color = ArenaBgDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }

    // 18.8 Privacy Settings Dialog
    if (showPrivacyDialog) {
        var isPrivate by remember { mutableStateOf(privacySettings.isPrivateAccount) }
        var showActivity by remember { mutableStateOf(privacySettings.showActivity) }
        var showLikes by remember { mutableStateOf(privacySettings.showLikes) }

        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Profile Privacy Settings", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Private Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Only approved followers can view your dispatches", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(checked = isPrivate, onCheckedChange = { isPrivate = it }, colors = SwitchDefaults.colors(checkedThumbColor = CrimsonNeon))
                    }
                    HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show Activity Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Let others see when you are simulating battles", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(checked = showActivity, onCheckedChange = { showActivity = it }, colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon))
                    }
                    HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Public Likes Visibility", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Display your liked battles in the Likes tab", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(checked = showLikes, onCheckedChange = { showLikes = it }, colors = SwitchDefaults.colors(checkedThumbColor = GoldNeon))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updatePrivacySettings(
                            privacySettings.copy(
                                isPrivateAccount = isPrivate,
                                showActivity = showActivity,
                                showLikes = showLikes
                            )
                        )
                        showPrivacyDialog = false
                        Toast.makeText(context, "Privacy settings saved", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                ) {
                    Text("Apply", color = ArenaBgDark, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }

    // 18.5 Followers Dialog
    if (showFollowersDialog) {
        AlertDialog(
            onDismissRequest = { showFollowersDialog = false },
            title = { Text("Followers (${activeProfile.followersCount})", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(creators) { creator ->
                        Surface(
                            color = ArenaSurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CharacterAvatarView(name = creator.displayName, avatarUrl = creator.avatarUrl, accentColor = CyanNeon, size = 36.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(creator.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(creator.handle, fontSize = 11.sp, color = TextMuted)
                                }
                                Button(
                                    onClick = { repository.toggleFollowCreator(creator.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (creator.isFollowed) ArenaSurfaceDark else CyanNeon),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(if (creator.isFollowed) "Following" else "Follow", fontSize = 10.sp, color = if (creator.isFollowed) TextSecondary else ArenaBgDark)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFollowersDialog = false }) { Text("Close", color = CyanNeon) } },
            containerColor = ArenaSurfaceDark
        )
    }

    // 18.5 Following Dialog
    if (showFollowingDialog) {
        val followedCreators = creators.filter { it.isFollowed }
        AlertDialog(
            onDismissRequest = { showFollowingDialog = false },
            title = { Text("Following (${activeProfile.followingCount})", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                if (followedCreators.isEmpty()) {
                    Text("Not following any creators yet.", fontSize = 12.sp, color = TextSecondary)
                } else {
                    LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(followedCreators) { creator ->
                            Surface(
                                color = ArenaSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CharacterAvatarView(name = creator.displayName, avatarUrl = creator.avatarUrl, accentColor = CyanNeon, size = 36.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(creator.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(creator.handle, fontSize = 11.sp, color = TextMuted)
                                    }
                                    Button(
                                        onClick = { repository.toggleFollowCreator(creator.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceDark),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Unfollow", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFollowingDialog = false }) { Text("Close", color = CyanNeon) } },
            containerColor = ArenaSurfaceDark
        )
    }

    // New Post / Thought Dialog
    if (showNewPostDialog) {
        var postContent by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewPostDialog = false },
            title = { Text("New Arena Dispatch", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    placeholder = { Text("Share an analytical feat discovery or cross-universe theory...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    colors = editDialogTextFieldColors()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (postContent.isNotBlank()) {
                            repository.addSocialPost(text = postContent)
                            showNewPostDialog = false
                            Toast.makeText(context, "Dispatch published to timeline!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                ) {
                    Text("Publish", color = ArenaBgDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPostDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = ArenaSurfaceDark
        )
    }

    // Message Dialog (for other creators)
    if (showMessageDialog) {
        var msgText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showMessageDialog = false },
            title = { Text("Message ${activeProfile.displayName}", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = msgText,
                    onValueChange = { msgText = it },
                    placeholder = { Text("Send a private debate challenge or research inquiry...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = editDialogTextFieldColors()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMessageDialog = false
                        Toast.makeText(context, "Message sent to ${activeProfile.handle}!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                ) {
                    Text("Send", color = ArenaBgDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMessageDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = ArenaSurfaceDark
        )
    }
}

@Composable
private fun SocialPostCard(post: SocialPost, onLike: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
        shape = RoundedCornerShape(14.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterAvatarView(name = post.authorName, avatarUrl = post.authorAvatar, accentColor = CyanNeon, size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(post.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${post.authorHandle} • ${post.timestamp}", fontSize = 11.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(post.postText, fontSize = 13.sp, lineHeight = 20.sp, color = TextPrimary)

            if (!post.battleTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ArenaSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(post.battleTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldNeon)
                        post.battleResult?.let { Text(it, fontSize = 11.sp, color = TextSecondary) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) CrimsonNeon else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text("${post.likes}", fontSize = 12.sp, color = if (post.isLiked) CrimsonNeon else TextSecondary)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${post.commentsCount}", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun EmptyStateMessage(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun editDialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = ArenaSurfaceElevated,
    unfocusedContainerColor = ArenaSurfaceElevated,
    focusedBorderColor = CyanNeon,
    unfocusedBorderColor = ArenaBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = CyanNeon,
    unfocusedLabelColor = TextMuted
)
