package com.example.model

/**
 * 15 Supported Fictional Source Types as specified in the PRD.
 */
enum class SourceType(val displayName: String, val badgeColorHex: Long) {
    ANIME("Anime", 0xFFFF5722),
    MANGA("Manga", 0xFFFF9800),
    NOVEL("Novel", 0xFF4CAF50),
    WEB_NOVEL("Web Novel", 0xFF00BCD4),
    LIGHT_NOVEL("Light Novel", 0xFF3F51B5),
    MANHWA("Manhwa", 0xFF9C27B0),
    MANHUA("Manhua", 0xFFE91E63),
    COMIC("Comic", 0xFF2196F3),
    ORIGINAL_CHARACTER("Original Character", 0xFFFFD700),
    GAME("Game", 0xFF00E676),
    ANIMATED_SERIES("Animated Series", 0xFFFF4081),
    MOVIE("Movie", 0xFF7C4DFF),
    TV_SERIES("TV Series", 0xFF536DFE),
    VISUAL_NOVEL("Visual Novel", 0xFFE040FB),
    OTHER("Other Universe", 0xFF78909C)
}

/**
 * Custom 17-Level Power Tier Classification System
 */
enum class PowerTier(val rank: Int, val tierName: String, val description: String) {
    HUMAN(1, "Human", "Normal athletic or peak human capabilities"),
    STREET_LEVEL(2, "Street Level", "Capable of breaking through concrete walls or martial dominance"),
    BUILDING_LEVEL(3, "Building Level", "Capable of collapsing large buildings and structures"),
    CITY_LEVEL(4, "City Level", "Destructive potency spanning entire metropolitan areas"),
    COUNTRY_LEVEL(5, "Country Level", "Can annihilate national landmasses in single bursts"),
    CONTINENTAL_LEVEL(6, "Continental Level", "Shatters tectonic plates and continents"),
    PLANETARY_LEVEL(7, "Planetary Level", "Can decimate or shatter celestial terrestrial planets"),
    STAR_LEVEL(8, "Star Level", "Energy yields comparable to burning stars or supernovas"),
    SOLAR_SYSTEM_LEVEL(9, "Solar System Level", "Capable of obliterating star systems and orbital rings"),
    GALACTIC_LEVEL(10, "Galactic Level", "Spanning destruction across entire galaxies and nebula clusters"),
    UNIVERSAL_LEVEL(11, "Universal Level", "Collapses 4D space-time continuum of an entire universe"),
    UNIVERSAL_PLUS(12, "Universal+", "Affects multiple space-time timelines within a single universe"),
    LOW_MULTIVERSAL(13, "Low Multiversal", "Simultaneously threatens or alters 2 to 1,000 baseline universes"),
    MULTIVERSAL(14, "Multiversal", "Destroys or creates infinite distinct 4D spacetime universes"),
    COMPLEX_MULTIVERSAL(15, "Complex Multiversal", "Transcends 5D to 11D higher dimensional spatial frameworks"),
    HYPERVERSAL(16, "Hyperversal", "Manipulates 12D up to infinite dimensional coordinate axes"),
    OUTERVERSAL(17, "Outerversal", "Transcends all concepts of dimensions, space-time, and logic")
}

data class CombatStats(
    val strength: Int,      // 0-100
    val speed: Int,         // 0-100
    val durability: Int,    // 0-100
    val intelligence: Int,  // 0-100
    val combatSkill: Int,   // 0-100
    val range: Int,         // 0-100
    val specialAbility: Int // 0-100
)

data class AdvancedStats(
    val attackPotency: String,
    val reactionSpeed: String,
    val destructiveCapacity: String,
    val stamina: String,
    val haxLevel: String,
    val battleIQ: String
)

enum class EvidenceType(val label: String) {
    DIRECT_FEAT("Direct Feat"),
    OFFICIAL_STATEMENT("Official Statement"),
    SUPPORTING_EVIDENCE("Supporting Evidence"),
    REASONED_INFERENCE("Reasoned Inference"),
    COMMUNITY_CLAIM("Community Claim"),
    UNVERIFIED_CLAIM("Unverified Claim")
}

data class FeatItem(
    val description: String,
    val sourceReference: String,
    val evidenceType: EvidenceType,
    val confidence: Int // 0-100%
)

data class CharacterProfile(
    val id: String,
    val name: String,
    val universe: String,
    val sourceType: SourceType,
    val version: String,
    val form: String,
    val avatarUrl: String = "",
    val overallTier: PowerTier,
    val baseTier: PowerTier,
    val peakTier: PowerTier,
    val researchConfidence: Int = 95,
    val bio: String,
    val powers: List<String>,
    val combatStats: CombatStats,
    val advancedStats: AdvancedStats,
    val keyFeats: List<FeatItem>,
    val weaknesses: List<String>,
    val isCustomOc: Boolean = false
)

enum class BattleDifficulty(val label: String) {
    NO_DIFFICULTY("No Difficulty"),
    VERY_LOW("Very Low Difficulty"),
    LOW("Low Difficulty"),
    MID("Mid Difficulty"),
    HIGH("High Difficulty"),
    EXTREME("Extreme Difficulty")
}

data class BattleSettings(
    val speedEqualized: Boolean = false,
    val bloodlusted: Boolean = false,
    val fullEquipment: Boolean = true,
    val compositeVersion: Boolean = false,
    val battlefield: String = "Neutral Void Arena",
    val customNotes: String = ""
)

data class BattleRound(
    val roundNumber: Int,
    val title: String,
    val charAStatus: String,
    val charBStatus: String,
    val abilityUsedA: String,
    val abilityUsedB: String,
    val turningPoint: String,
    val outcome: String,
    val hpA: Int, // 0-100
    val hpB: Int  // 0-100
)

data class BattleChapter(
    val chapterIndex: Int,
    val title: String,
    val narrativeText: String,
    val wordCount: Int
)

data class AlternateScenario(
    val scenarioName: String,
    val outcomeDescription: String
)

data class BattleVerdict(
    val winnerId: String,
    val winnerName: String,
    val loserName: String,
    val winnerTier: PowerTier,
    val loserTier: PowerTier,
    val difficulty: BattleDifficulty,
    val decisiveAbility: String,
    val mainAdvantage: String,
    val criticalFeat: String,
    val winningCondition: String,
    val researchConfidence: Int,
    val verdictConfidence: Int,
    val alternateScenarios: List<AlternateScenario>
)

enum class PostVisibility {
    PUBLIC, UNLISTED, PRIVATE
}

data class CommentReply(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: String,
    val likes: Int = 0
)

data class BattleComment(
    val id: String,
    val battleId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<CommentReply> = emptyList()
)

data class BattlePost(
    val id: String,
    val title: String,
    val charA: CharacterProfile,
    val charB: CharacterProfile,
    val settings: BattleSettings,
    val rounds: List<BattleRound>,
    val chapters: List<BattleChapter>,
    val verdict: BattleVerdict,
    val analyticalReport: String,
    val creatorName: String,
    val creatorAvatar: String = "",
    val createdAt: String,
    val likes: Int,
    val dislikes: Int,
    val views: Int,
    val commentsCount: Int,
    val isSaved: Boolean = false,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false
)

data class CreatorProfile(
    val id: String,
    val displayName: String,
    val handle: String,
    val bio: String,
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val location: String = "Multiverse Hub",
    val website: String = "https://deathbattlearena.com",
    val socialLinks: Map<String, String> = emptyMap(),
    val joinDate: String = "November 2026",
    val followersCount: Int,
    val followingCount: Int,
    val totalBattles: Int,
    val totalPosts: Int = 14,
    val totalLikes: Int,
    val isFollowed: Boolean = false,
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val isBlocked: Boolean = false
)

enum class UploadStatus {
    IDLE,
    SELECTING,
    CROPPING,
    UPLOADING,
    SUCCESS,
    ERROR
}

enum class CropAspectRatio(val label: String, val widthRatio: Float, val heightRatio: Float) {
    SQUARE_1_1("1:1 Square", 1f, 1f),
    PORTRAIT_3_4("3:4 Portrait", 3f, 4f),
    LANDSCAPE_16_9("16:9 Landscape", 16f, 9f)
}

data class CharacterImageState(
    val imageUri: String? = null,
    val originalFileName: String? = null,
    val uploadStatus: UploadStatus = UploadStatus.IDLE,
    val progress: Float = 0f,
    val errorMessage: String? = null,
    val sizeBytes: Long = 0L,
    val isCropped: Boolean = false
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String = "",
    val postText: String,
    val attachedMediaUrl: String? = null,
    val battleId: String? = null,
    val battleTitle: String? = null,
    val battleResult: String? = null,
    val timestamp: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0
)

data class ProfilePrivacySettings(
    val isPrivateAccount: Boolean = false,
    val whoCanComment: String = "Everyone",
    val whoCanFollow: String = "Everyone",
    val showActivity: Boolean = true,
    val showLikes: Boolean = true,
    val showFollowersList: Boolean = true
)

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BENGALI("bn", "Bengali", "বাংলা"),
    HINDI("hi", "Hindi", "हिन्दी"),
    JAPANESE("ja", "Japanese", "日本語"),
    KOREAN("ko", "Korean", "한국어"),
    CHINESE("zh", "Chinese", "中文")
}

