package com.example

import com.example.data.BattleRepository
import com.example.data.CharacterDatabase
import com.example.engine.BattleEngine
import com.example.engine.TranslationEngine
import com.example.model.*
import com.example.storage.ImageStorageService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifyPowerTiersStructure() {
        assertEquals(17, PowerTier.values().size)
        assertEquals("Human", PowerTier.HUMAN.tierName)
        assertEquals(1, PowerTier.HUMAN.rank)
        assertEquals("Complex Multiversal", PowerTier.COMPLEX_MULTIVERSAL.tierName)
        assertEquals(15, PowerTier.COMPLEX_MULTIVERSAL.rank)
        assertEquals("Outerversal", PowerTier.OUTERVERSAL.tierName)
        assertEquals(17, PowerTier.OUTERVERSAL.rank)
        assertTrue(PowerTier.COMPLEX_MULTIVERSAL.rank > PowerTier.LOW_MULTIVERSAL.rank)
    }

    @Test
    fun verifySupportedSourceTypes() {
        assertEquals(15, SourceType.values().size)
        assertTrue(SourceType.values().contains(SourceType.WEB_NOVEL))
        assertTrue(SourceType.values().contains(SourceType.ANIME))
        assertTrue(SourceType.values().contains(SourceType.MANGA))
        assertTrue(SourceType.values().contains(SourceType.COMIC))
        assertTrue(SourceType.values().contains(SourceType.ORIGINAL_CHARACTER))
    }

    @Test
    fun testBattleSimulationRoundsAndChapters() {
        val chars = CharacterDatabase.initialCharacters
        val fy = chars.first { it.id == "char_fang_yuan" }
        val goku = chars.first { it.id == "char_goku" }

        val battle = BattleEngine.generateBattlePost(
            charA = fy,
            charB = goku,
            settings = BattleSettings(battlefield = "Void Arena", speedEqualized = true)
        )

        // Must generate 4 combat rounds
        assertEquals(4, battle.rounds.size)
        assertEquals("Round 1: Initial Encounter & Probe", battle.rounds[0].title)
        assertEquals("Round 4: Final Clash & Decisive Verdict", battle.rounds[3].title)

        // Must generate 6 novel story chapters
        assertEquals(6, battle.chapters.size)
        assertEquals("Chapter 1: The Beginning", battle.chapters[0].title)
        assertEquals("Chapter 6: The Aftermath", battle.chapters[5].title)

        // Verify verdict
        assertNotNull(battle.verdict.winnerName)
        assertNotNull(battle.verdict.difficulty)
        assertTrue(battle.verdict.alternateScenarios.isNotEmpty())
    }

    @Test
    fun testTranslationEngineBengali() {
        val original = "Chapter 1: The Beginning"
        val translated = TranslationEngine.translateStoryChapter(original, AppLanguage.BENGALI)
        assertEquals("অধ্যায় ১: সূচনা", translated)

        val eng = TranslationEngine.translateStoryChapter(original, AppLanguage.ENGLISH)
        assertEquals(original, eng)
    }

    @Test
    fun testRepositorySocialAndCustomCharacter() {
        val repo = BattleRepository()
        val initialCount = repo.battles.value.size
        assertTrue(initialCount >= 5)

        // Test Like toggle
        val battleId = repo.battles.value.first().id
        val prevLikes = repo.battles.value.first().likes
        repo.toggleLike(battleId)
        assertEquals(prevLikes + 1, repo.battles.value.first { it.id == battleId }.likes)
        assertTrue(repo.battles.value.first { it.id == battleId }.isLiked)

        // Test Comment addition
        repo.addComment(battleId, "Excellent scaling analysis!")
        val comments = repo.comments.value[battleId]
        assertNotNull(comments)
        assertTrue(comments!!.any { it.content == "Excellent scaling analysis!" })
    }

    @Test
    fun testCharacterImageStateIndependence() {
        var stateA = CharacterImageState(
            uploadStatus = UploadStatus.SUCCESS,
            imageUri = "file:///path/to/charA.webp",
            originalFileName = "charA.webp",
            sizeBytes = 1024L * 500
        )
        val stateB = CharacterImageState(
            uploadStatus = UploadStatus.IDLE,
            imageUri = null,
            originalFileName = null
        )

        assertNotEquals(stateA.imageUri, stateB.imageUri)
        assertEquals(UploadStatus.SUCCESS, stateA.uploadStatus)
        assertEquals(UploadStatus.IDLE, stateB.uploadStatus)

        // Resetting stateA must not mutate stateB
        stateA = CharacterImageState()
        assertEquals(UploadStatus.IDLE, stateA.uploadStatus)
        assertNull(stateA.imageUri)
        assertNull(stateB.imageUri)
    }

    @Test
    fun testCropAspectRatioRatios() {
        val square = CropAspectRatio.SQUARE_1_1
        assertEquals(1.0f, square.widthRatio / square.heightRatio, 0.001f)
        assertTrue(square.label.contains("1:1"))

        val portrait = CropAspectRatio.PORTRAIT_3_4
        assertEquals(3f / 4f, portrait.widthRatio / portrait.heightRatio, 0.001f)
        assertTrue(portrait.label.contains("3:4"))

        val landscape = CropAspectRatio.LANDSCAPE_16_9
        assertEquals(16f / 9f, landscape.widthRatio / landscape.heightRatio, 0.001f)
        assertTrue(landscape.label.contains("16:9"))
    }

    @Test
    fun testImageStorageServiceConstants() {
        assertEquals(10 * 1024 * 1024L, ImageStorageService.DEFAULT_MAX_SIZE_BYTES)
        assertTrue(ImageStorageService.SUPPORTED_MIME_TYPES.contains("image/jpeg"))
        assertTrue(ImageStorageService.SUPPORTED_MIME_TYPES.contains("image/png"))
        assertTrue(ImageStorageService.SUPPORTED_MIME_TYPES.contains("image/webp"))
        assertEquals(4, ImageStorageService.SUPPORTED_MIME_TYPES.size)
    }

    @Test
    fun testSocialPostAndPrivacyRepository() {
        val repo = BattleRepository()
        val initialPostsCount = repo.socialPosts.value.size
        assertTrue(initialPostsCount >= 2)

        // Add a new post
        repo.addSocialPost(
            text = "Testing novel vs anime power scaling debate!",
            battleId = repo.battles.value.first().id
        )
        assertEquals(initialPostsCount + 1, repo.socialPosts.value.size)
        val createdPost = repo.socialPosts.value.first()
        assertEquals("Testing novel vs anime power scaling debate!", createdPost.postText)
        assertEquals(0, createdPost.likes)
        assertFalse(createdPost.isLiked)

        // Like the post
        repo.toggleLikeSocialPost(createdPost.id)
        val likedPost = repo.socialPosts.value.first { it.id == createdPost.id }
        assertEquals(1, likedPost.likes)
        assertTrue(likedPost.isLiked)

        // Test privacy settings
        val initialPrivacy = repo.privacySettings.value
        repo.updatePrivacySettings(initialPrivacy.copy(isPrivateAccount = true, showActivity = false))
        assertTrue(repo.privacySettings.value.isPrivateAccount)
        assertFalse(repo.privacySettings.value.showActivity)
    }
}
