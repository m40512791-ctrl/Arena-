package com.example.data

import com.example.engine.BattleEngine
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BattleRepository {

    private val _characters = MutableStateFlow<List<CharacterProfile>>(CharacterDatabase.initialCharacters)
    val characters: StateFlow<List<CharacterProfile>> = _characters.asStateFlow()

    private val _battles = MutableStateFlow<List<BattlePost>>(emptyList())
    val battles: StateFlow<List<BattlePost>> = _battles.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<BattleComment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<BattleComment>>> = _comments.asStateFlow()

    private val _creators = MutableStateFlow<List<CreatorProfile>>(emptyList())
    val creators: StateFlow<List<CreatorProfile>> = _creators.asStateFlow()

    private val _userProfile = MutableStateFlow(
        CreatorProfile(
            id = "user_me",
            displayName = "Arena Grandmaster",
            handle = "@grandmaster_battles",
            bio = "Multiversal battle theorizer & Tier 17 researcher. Exploring cosmological feats and conceptual limits.",
            avatarUrl = "",
            coverUrl = "",
            location = "Tokyo / Neutral Void",
            website = "https://deathbattlearena.com/@grandmaster_battles",
            socialLinks = mapOf("X" to "x.com/arena_master", "Discord" to "discord.gg/deathbattle"),
            joinDate = "October 2026",
            followersCount = 1240,
            followingCount = 84,
            totalBattles = 28,
            totalPosts = 16,
            totalLikes = 45200,
            isFollowed = false,
            isVerified = true
        )
    )
    val userProfile: StateFlow<CreatorProfile> = _userProfile.asStateFlow()

    private val _privacySettings = MutableStateFlow(ProfilePrivacySettings())
    val privacySettings: StateFlow<ProfilePrivacySettings> = _privacySettings.asStateFlow()

    private val _socialPosts = MutableStateFlow<List<SocialPost>>(emptyList())
    val socialPosts: StateFlow<List<SocialPost>> = _socialPosts.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    init {
        bootstrapInitialData()
    }

    private fun bootstrapInitialData() {
        val chars = CharacterDatabase.initialCharacters
        val fy = chars.first { it.id == "char_fang_yuan" }
        val goku = chars.first { it.id == "char_goku" }
        val klein = chars.first { it.id == "char_klein_moretti" }
        val saitama = chars.first { it.id == "char_saitama" }
        val wanglin = chars.first { it.id == "char_wang_lin" }
        val jinwoo = chars.first { it.id == "char_sung_jinwoo" }
        val gojo = chars.first { it.id == "char_gojo_satoru" }
        val superman = chars.first { it.id == "char_superman" }

        val b1 = BattleEngine.generateBattlePost(
            charA = fy,
            charB = goku,
            settings = BattleSettings(battlefield = "Cosmic Primordial River of Time", bloodlusted = true)
        ).copy(
            id = "curated_battle_1",
            title = "Fang Yuan VS Son Goku: Dao Sovereign vs Saiyan Pinnacle",
            creatorName = "DaoSeeker99",
            createdAt = "2 hours ago",
            likes = 3420,
            views = 42100,
            commentsCount = 286,
            isFeatured = true
        )

        val b2 = BattleEngine.generateBattlePost(
            charA = goku,
            charB = saitama,
            settings = BattleSettings(battlefield = "Empty Space-Time Void", speedEqualized = false)
        ).copy(
            id = "curated_battle_2",
            title = "Son Goku VS Saitama: The Multiversal Fist of Limitless Growth",
            creatorName = "ShonenScholar",
            createdAt = "5 hours ago",
            likes = 8910,
            views = 98500,
            commentsCount = 1420,
            isFeatured = true
        )

        val b3 = BattleEngine.generateBattlePost(
            charA = klein,
            charB = fy,
            settings = BattleSettings(battlefield = "Sefirah Castle Apex", bloodlusted = false)
        ).copy(
            id = "curated_battle_3",
            title = "Klein Moretti VS Fang Yuan: The Lord of Mysteries Meets Great Love Venerable",
            creatorName = "OmniscientReader",
            createdAt = "1 day ago",
            likes = 5120,
            views = 63000,
            commentsCount = 632,
            isFeatured = true
        )

        val b4 = BattleEngine.generateBattlePost(
            charA = jinwoo,
            charB = superman,
            settings = BattleSettings(battlefield = "Kryptonian Solar Core Overlap", bloodlusted = false)
        ).copy(
            id = "curated_battle_4",
            title = "Sung Jin-woo VS Superman: Shadow Monarch vs The Man of Tomorrow",
            creatorName = "MonarchVanguard",
            createdAt = "2 days ago",
            likes = 2140,
            views = 28900,
            commentsCount = 195,
            isFeatured = false
        )

        val b5 = BattleEngine.generateBattlePost(
            charA = gojo,
            charB = saitama,
            settings = BattleSettings(battlefield = "Shinjuku Metropolitan Ruins", speedEqualized = true)
        ).copy(
            id = "curated_battle_5",
            title = "Gojo Satoru VS Saitama: Infinity Barrier vs Exponential Kinetic Shockwave",
            creatorName = "DomainMaster",
            createdAt = "3 days ago",
            likes = 4890,
            views = 54000,
            commentsCount = 412,
            isFeatured = false
        )

        _battles.value = listOf(b1, b2, b3, b4, b5)

        _creators.value = listOf(
            CreatorProfile("c1", "DaoSeeker99", "@daoseeker", "Xianxia & Web Novel power scaling authority. 500+ analyzed chapters.", "", "", "Beijing Realm", "https://daoseeker.net", mapOf("X" to "x.com/daoseeker"), "October 2026", 18500, 42, 38, 52, 92400, isFollowed = true, isVerified = true),
            CreatorProfile("c2", "ShonenScholar", "@shonenscholar", "Anime vs Manga feat researcher. Dedicated to frame-by-frame calculations.", "", "", "Neo Tokyo", "https://shonenscholar.org", mapOf("YouTube" to "youtube.com/@shonenscholar"), "September 2026", 34200, 110, 84, 114, 210000, isFollowed = true, isVerified = true),
            CreatorProfile("c3", "OmniscientReader", "@mysteries_fool", "Conceptual hax, Sefirah logic and outerversal cosmology analyst.", "", "", "Backlund", "https://foolpathway.io", emptyMap(), "August 2026", 12400, 29, 27, 43, 65000, isFollowed = false, isVerified = true),
            CreatorProfile("c4", "MonarchVanguard", "@shadowmonarch", "Manhwa & Solo Leveling / ORV lore specialist.", "", "", "Seoul Hunter Assoc.", "", emptyMap(), "July 2026", 9800, 15, 19, 29, 41000, isFollowed = false, isVerified = false)
        )

        _socialPosts.value = listOf(
            SocialPost(
                id = "sp1",
                authorId = "user_me",
                authorName = "Arena Grandmaster",
                authorHandle = "@grandmaster_battles",
                authorAvatar = "",
                postText = "Breaking down Fang Yuan's Great Love Killer Moves against Mastered Ultra Instinct. The dimensional authority of non-conflicting Dao marks creates an insurmountable conceptual barrier under standard canon rules!",
                battleId = "curated_battle_1",
                battleTitle = "Fang Yuan VS Son Goku",
                battleResult = "Winner: Fang Yuan (High Difficulty)",
                timestamp = "3 hours ago",
                likes = 142,
                commentsCount = 28
            ),
            SocialPost(
                id = "sp2",
                authorId = "c2",
                authorName = "ShonenScholar",
                authorHandle = "@shonenscholar",
                authorAvatar = "",
                postText = "The Serious Punch Squared constellation void feat remains the most visually quantifiable kinetic feat in modern manga history. What an incredible debate matchup!",
                battleId = "curated_battle_2",
                battleTitle = "Son Goku VS Saitama",
                battleResult = "Winner: Son Goku (Extreme Difficulty)",
                timestamp = "6 hours ago",
                likes = 310,
                commentsCount = 89
            )
        )

        _comments.value = mapOf(
            "curated_battle_1" to listOf(
                BattleComment(
                    id = "cm1",
                    battleId = "curated_battle_1",
                    authorName = "GuMaster",
                    authorAvatar = "",
                    content = "Accurate scaling! Fang Yuan's Steal Heavenly Secret and non-conflicting Dao marks are impossible to defend against for pure physical fighters.",
                    timestamp = "1 hour ago",
                    likes = 45,
                    replies = listOf(
                        CommentReply("r1", "SaiyanPride", "", "Goku's Hakai and speed blitz would cause severe trouble if speed wasn't neutralized though!", "45 mins ago", 12)
                    )
                ),
                BattleComment(
                    id = "cm2",
                    battleId = "curated_battle_1",
                    authorName = "VenerableLover",
                    authorAvatar = "",
                    content = "The Chapter 4 Turning Point description gave me chills! Great Love Venerable never disappoints.",
                    timestamp = "30 mins ago",
                    likes = 28
                )
            ),
            "curated_battle_2" to listOf(
                BattleComment(
                    id = "cm3",
                    battleId = "curated_battle_2",
                    authorName = "OnePunchFan",
                    authorAvatar = "",
                    content = "The exponential growth graph from the Io battle showed Saitama's potential has no ceiling!",
                    timestamp = "3 hours ago",
                    likes = 112
                )
            )
        )
    }

    fun addBattle(battle: BattlePost) {
        _battles.update { listOf(battle) + it }
    }

    fun addCustomCharacter(char: CharacterProfile) {
        _characters.update { listOf(char) + it }
    }

    fun toggleLike(battleId: String) {
        _battles.update { list ->
            list.map { post ->
                if (post.id == battleId) {
                    val wasLiked = post.isLiked
                    val newLikes = if (wasLiked) post.likes - 1 else post.likes + 1
                    val newDislikes = if (!wasLiked && post.isDisliked) post.dislikes - 1 else post.dislikes
                    post.copy(isLiked = !wasLiked, isDisliked = false, likes = newLikes, dislikes = newDislikes)
                } else post
            }
        }
    }

    fun toggleDislike(battleId: String) {
        _battles.update { list ->
            list.map { post ->
                if (post.id == battleId) {
                    val wasDisliked = post.isDisliked
                    val newDislikes = if (wasDisliked) post.dislikes - 1 else post.dislikes + 1
                    val newLikes = if (!wasDisliked && post.isLiked) post.likes - 1 else post.likes
                    post.copy(isDisliked = !wasDisliked, isLiked = false, dislikes = newDislikes, likes = newLikes)
                } else post
            }
        }
    }

    fun toggleSave(battleId: String) {
        _battles.update { list ->
            list.map { post ->
                if (post.id == battleId) post.copy(isSaved = !post.isSaved) else post
            }
        }
    }

    fun addComment(battleId: String, text: String, authorName: String = "You (Challenger)") {
        val newComment = BattleComment(
            id = "cm_${System.currentTimeMillis()}",
            battleId = battleId,
            authorName = authorName,
            authorAvatar = "",
            content = text,
            timestamp = "Just now",
            likes = 0
        )
        val currentList = _comments.value[battleId] ?: emptyList()
        _comments.update { map ->
            map + (battleId to (listOf(newComment) + currentList))
        }
        _battles.update { list ->
            list.map { if (it.id == battleId) it.copy(commentsCount = it.commentsCount + 1) else it }
        }
    }

    fun toggleFollowCreator(creatorId: String) {
        _creators.update { list ->
            list.map { c ->
                if (c.id == creatorId) {
                    val newFollowed = !c.isFollowed
                    c.copy(isFollowed = newFollowed, followersCount = if (newFollowed) c.followersCount + 1 else c.followersCount - 1)
                } else c
            }
        }
    }

    fun recordView(battleId: String) {
        _history.update { (listOf(battleId) + it.filter { id -> id != battleId }).take(50) }
        _battles.update { list ->
            list.map { if (it.id == battleId) it.copy(views = it.views + 1) else it }
        }
    }

    fun toggleFeatured(battleId: String) {
        _battles.update { list ->
            list.map { if (it.id == battleId) it.copy(isFeatured = !it.isFeatured) else it }
        }
    }

    fun updateUserProfile(
        displayName: String,
        handle: String,
        bio: String,
        location: String,
        website: String,
        avatarUrl: String,
        coverUrl: String
    ) {
        _userProfile.update {
            it.copy(
                displayName = displayName,
                handle = handle,
                bio = bio,
                location = location,
                website = website,
                avatarUrl = avatarUrl,
                coverUrl = coverUrl
            )
        }
    }

    fun updatePrivacySettings(settings: ProfilePrivacySettings) {
        _privacySettings.value = settings
    }

    fun addSocialPost(
        text: String,
        mediaUrl: String? = null,
        battleId: String? = null,
        battleTitle: String? = null,
        battleResult: String? = null
    ) {
        val user = _userProfile.value
        val newPost = SocialPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = user.id,
            authorName = user.displayName,
            authorHandle = user.handle,
            authorAvatar = user.avatarUrl,
            postText = text,
            attachedMediaUrl = mediaUrl,
            battleId = battleId,
            battleTitle = battleTitle,
            battleResult = battleResult,
            timestamp = "Just now",
            likes = 0
        )
        _socialPosts.update { listOf(newPost) + it }
        _userProfile.update { it.copy(totalPosts = it.totalPosts + 1) }
    }

    fun toggleLikeSocialPost(postId: String) {
        _socialPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val wasLiked = post.isLiked
                    post.copy(
                        isLiked = !wasLiked,
                        likes = if (wasLiked) post.likes - 1 else post.likes + 1
                    )
                } else post
            }
        }
    }

    fun toggleBlockCreator(creatorId: String) {
        _creators.update { list ->
            list.map { c ->
                if (c.id == creatorId) c.copy(isBlocked = !c.isBlocked) else c
            }
        }
    }
}

