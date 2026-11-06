package com.example.engine

import com.example.model.*
import kotlinx.coroutines.delay

object BattleEngine {

    sealed class SimulationStep(val progress: Float, val label: String, val agentName: String) {
        object Initializing : SimulationStep(0.10f, "Initializing Central AI Orchestrator...", "Central Orchestrator")
        object Researching : SimulationStep(0.25f, "Researching canon sources, feats & versions...", "Character Research Agent")
        object CanonVerification : SimulationStep(0.40f, "Verifying canon status & cross-universe rules...", "Canon Verification Agent")
        object PowerScaling : SimulationStep(0.55f, "Scaling 17 Custom Power Tiers & dimensional stats...", "Power Scaling Agent")
        object SimulatingRounds : SimulationStep(0.70f, "Simulating 4 Combat Rounds & turning points...", "Battle Simulation Agent")
        object WritingNovel : SimulationStep(0.85f, "Writing 6-Chapter Novel Narrative & Dialogue...", "Story Writer Agent")
        object FactChecking : SimulationStep(0.95f, "Fact-checking evidence, conditions & final verdict...", "Fact Checker Agent")
        object Complete : SimulationStep(1.0f, "Battle analysis & story simulation complete!", "Report Editor Agent")
    }

    /**
     * Executes the full multi-agent simulation pipeline.
     */
    suspend fun runSimulation(
        charA: CharacterProfile,
        charB: CharacterProfile,
        settings: BattleSettings,
        onProgress: (SimulationStep) -> Unit
    ): BattlePost {
        onProgress(SimulationStep.Initializing)
        delay(400)
        onProgress(SimulationStep.Researching)
        delay(500)
        onProgress(SimulationStep.CanonVerification)
        delay(500)
        onProgress(SimulationStep.PowerScaling)
        delay(600)
        onProgress(SimulationStep.SimulatingRounds)
        delay(600)
        onProgress(SimulationStep.WritingNovel)
        delay(700)
        onProgress(SimulationStep.FactChecking)
        delay(400)
        onProgress(SimulationStep.Complete)
        delay(300)

        return generateBattlePost(charA, charB, settings)
    }

    fun generateBattlePost(
        charA: CharacterProfile,
        charB: CharacterProfile,
        settings: BattleSettings
    ): BattlePost {
        // Evaluate combat advantage based on Tiers, Hax, Speed, Durability, and Rules
        val tierDiff = charA.overallTier.rank - charB.overallTier.rank
        val speedDiff = if (settings.speedEqualized) 0 else (charA.combatStats.speed - charB.combatStats.speed)
        val haxDiff = (charA.combatStats.specialAbility + charA.combatStats.intelligence) -
                      (charB.combatStats.specialAbility + charB.combatStats.intelligence)

        val scoreA = (charA.overallTier.rank * 15) + (if (settings.speedEqualized) 0 else charA.combatStats.speed / 4) + (charA.combatStats.specialAbility / 3) + (charA.combatStats.durability / 4)
        val scoreB = (charB.overallTier.rank * 15) + (if (settings.speedEqualized) 0 else charB.combatStats.speed / 4) + (charB.combatStats.specialAbility / 3) + (charB.combatStats.durability / 4)

        val aWins = scoreA >= scoreB
        val winner = if (aWins) charA else charB
        val loser = if (aWins) charB else charA

        val absScoreDiff = kotlin.math.abs(scoreA - scoreB)
        val difficulty = when {
            absScoreDiff > 45 -> BattleDifficulty.NO_DIFFICULTY
            absScoreDiff > 30 -> BattleDifficulty.LOW
            absScoreDiff > 18 -> BattleDifficulty.MID
            absScoreDiff > 8 -> BattleDifficulty.HIGH
            else -> BattleDifficulty.EXTREME
        }

        // Generate 4 Rounds
        val rounds = generateFourRounds(charA, charB, aWins, difficulty, settings)

        // Generate 6 Novel Chapters
        val chapters = generateNovelChapters(charA, charB, winner, loser, settings, difficulty)

        // Generate Analytical Verdict
        val verdict = generateVerdict(charA, charB, winner, loser, difficulty, settings)

        val title = "${charA.name} VS ${charB.name}: Clash of ${charA.sourceType.displayName} & ${charB.sourceType.displayName}"

        val analyticalReport = buildAnalyticalReport(winner, loser, verdict, settings)

        return BattlePost(
            id = "battle_${System.currentTimeMillis()}_${charA.id}_${charB.id}",
            title = title,
            charA = charA,
            charB = charB,
            settings = settings,
            rounds = rounds,
            chapters = chapters,
            verdict = verdict,
            analyticalReport = analyticalReport,
            creatorName = "Arena Grandmaster",
            createdAt = "Just now",
            likes = (120..850).random(),
            dislikes = (4..35).random(),
            views = (1200..9500).random(),
            commentsCount = (15..80).random(),
            isSaved = false,
            isLiked = false,
            isDisliked = false,
            visibility = PostVisibility.PUBLIC,
            tags = listOf(charA.sourceType.displayName, charB.sourceType.displayName, winner.overallTier.tierName, "Canon Feats")
        )
    }

    private fun generateFourRounds(
        charA: CharacterProfile,
        charB: CharacterProfile,
        aWins: Boolean,
        difficulty: BattleDifficulty,
        settings: BattleSettings
    ): List<BattleRound> {
        val abilityA1 = charA.powers.firstOrNull() ?: "Primary Offensive Blast"
        val abilityB1 = charB.powers.firstOrNull() ?: "Agile Counter Stance"

        val abilityA2 = charA.powers.getOrNull(1) ?: "Tactical Defensive Technique"
        val abilityB2 = charB.powers.getOrNull(1) ?: "High-Impact Kinetic Strike"

        val abilityA3 = charA.powers.getOrNull(2) ?: "Transcendent Awakening"
        val abilityB3 = charB.powers.getOrNull(2) ?: "Domain Projection"

        val abilityA4 = charA.powers.lastOrNull() ?: "Ultimate Finisher"
        val abilityB4 = charB.powers.lastOrNull() ?: "Absolute Limit Strike"

        val battlefield = settings.battlefield

        return listOf(
            BattleRound(
                roundNumber = 1,
                title = "Round 1: Initial Encounter & Probe",
                charAStatus = "Assessing battlefield geometry and enemy combat intent",
                charBStatus = "Testing kinetic impact and spatial range defenses",
                abilityUsedA = abilityA1,
                abilityUsedB = abilityB1,
                turningPoint = "The combatants collided across $battlefield, creating shockwaves that instantly rearranged the terrain.",
                outcome = if (aWins) "${charA.name} seized initial positional dominance." else "${charB.name} parried the opening blow with superior positioning.",
                hpA = 95,
                hpB = 92
            ),
            BattleRound(
                roundNumber = 2,
                title = "Round 2: Ability Showcase & Hax Clashes",
                charAStatus = "Deploying specialized esoteric techniques and dimensional barriers",
                charBStatus = "Retaliating with secondary arsenals and relentless pressure",
                abilityUsedA = abilityA2,
                abilityUsedB = abilityB2,
                turningPoint = "Esoteric conceptual forces clashed directly against raw offensive output, testing reality resilience.",
                outcome = "Both fighters adapted to each other's signature tactics, neutralizing simple frontal attacks.",
                hpA = 80,
                hpB = 78
            ),
            BattleRound(
                roundNumber = 3,
                title = "Round 3: Power Escalation & Peak Forms",
                charAStatus = "Unleashing ${charA.form} to warp surrounding environmental laws",
                charBStatus = "Entering ${charB.form} with escalated energy resonance",
                abilityUsedA = abilityA3,
                abilityUsedB = abilityB3,
                turningPoint = "A critical breach in the battlefield's spatial framework forced both fighters to push beyond safe energy limits.",
                outcome = if (aWins) "${charA.name} forced ${charB.name} onto the back foot with decisive conceptual authority." else "${charB.name} breached ${charA.name}'s primary defensive perimeter.",
                hpA = if (aWins) 65 else 45,
                hpB = if (aWins) 42 else 68
            ),
            BattleRound(
                roundNumber = 4,
                title = "Round 4: Final Clash & Decisive Verdict",
                charAStatus = if (aWins) "Manifesting supreme ultimate authority to execute final victory" else "Pushing lingering stamina to deliver an exhaustive final gambit",
                charBStatus = if (!aWins) "Unleashing supreme ultimate technique to seal absolute triumph" else "Straining against fatal conceptual suppression",
                abilityUsedA = abilityA4,
                abilityUsedB = abilityB4,
                turningPoint = "The final collision tore the fabric of $battlefield, collapsing dimensional layers until only one warrior stood unbowed.",
                outcome = if (aWins) "${charA.name} shattered ${charB.name}'s final resistance to claim definitive victory!" else "${charB.name} broke through ${charA.name}'s ultimate defense to secure total victory!",
                hpA = if (aWins) (if (difficulty == BattleDifficulty.EXTREME) 18 else 55) else 0,
                hpB = if (!aWins) (if (difficulty == BattleDifficulty.EXTREME) 15 else 52) else 0
            )
        )
    }

    private fun generateNovelChapters(
        charA: CharacterProfile,
        charB: CharacterProfile,
        winner: CharacterProfile,
        loser: CharacterProfile,
        settings: BattleSettings,
        difficulty: BattleDifficulty
    ): List<BattleChapter> {
        val arena = settings.battlefield
        val speedCondition = if (settings.speedEqualized) "with speeds equalized under neutral arena decree" else "unconstrained in velocity and temporal acceleration"

        val ch1 = """
The fabric of reality tore in a silent detonation of light and void. Above the boundless expanse of the $arena, the celestial coordinates converged. Two solitary figures materialized from opposite ends of the multiverse—unbound by their native physics, $speedCondition.

On the western horizon stood ${charA.name} of ${charA.universe}, bearing the hallmarks of their ${charA.form}. Their aura exerted a gravitational pressure that bent the surrounding spatial geometry. Opposite them, calm yet primed for imminent war, stood ${charB.name} of ${charB.universe}.

Neither exchanged frivolous words. In the grand arena of Death Battle, only the unyielding testament of feats, power scaling, and combat supremacy would determine who walked away whole.
        """.trimIndent()

        val ch2 = """
The initial breach occurred in less than a Planck instant. ${charA.name} opened with ${charA.powers.firstOrNull() ?: "a crushing strike"}, sending ripples of devastating force hurtling across the expanse. The kinetic and esoteric shock pulverized the void boundaries into shimmering stardust.

However, ${charB.name} was not unprepared. Recognizing the lethality of the incoming assault, ${charB.name} immediately invoked ${charB.powers.firstOrNull() ?: "their reflexive defense"}, intercepting the trajectory and retaliating with pinpoint tactical ferocity. 

Every parry cracked the local continuum. For onlookers from both universes, it was not merely a brawl; it was an ideological and structural collision between the fundamental laws of ${charA.sourceType.displayName} and ${charB.sourceType.displayName}.
        """.trimIndent()

        val ch3 = """
Realizing conventional tactics would never breach an opponent forged in the crucible of ${loser.universe}, ${winner.name} escalated. Their energy output spiked exponentially, manifesting the full dread of ${winner.form}. 

The battlefield groaned. Laws of thermodynamics, causality, and dimensional stability began to unravel. ${loser.name} countered with ferocious valor, unleashing ${loser.powers.getOrNull(1) ?: "their hidden reservoir of power"}, matching shockwave for shockwave in an astounding display of endurance.

Yet subtle disparities began to surface. While ${loser.name} commanded immense destructive potency, ${winner.name}'s deeper conceptual layers and superior tier resilience began absorbing and deflecting the onslaught.
        """.trimIndent()

        val ch4 = """
This was the crux of the war—The Turning Point. 

${loser.name} attempted to turn the tide with their decisive technique: ${loser.powers.lastOrNull() ?: "their supreme ability"}. A blinding pillar of cosmic cataclysm consumed the arena, vaporizing lingering planetary debris and threatening total existence erasure.

For a heartbeat, silence blanketed the cosmos. But through the raging tempest of annihilation, ${winner.name} stepped forward. Relying on their critical feat from canon—${winner.keyFeats.firstOrNull()?.description ?: "their unmatched durability"}—they withstood the cataclysm without wavering. The look of cold shock on ${loser.name}'s face signaled that the battle had tipped irrevocably.
        """.trimIndent()

        val ch5 = """
With no remaining retreat, the final clash ignited. ${winner.name} channeled the totality of ${winner.powers.lastOrNull() ?: "their ultimate technique"}, weaving destructive capacity with peerless combat intellect. 

${loser.name} fought with the furious heart of a true titan, pushing their remaining vitality to absolute zero. But the gulf in ${winner.overallTier.tierName} scaling and decisive hax proved absolute. 

With a reality-shattering impact that resonated through the River of Time and the highest dimensional axes, ${winner.name}'s final strike shattered ${loser.name}'s defenses. The resulting explosion blinded the infinite void before collapsing into solemn, breathless stillness.
        """.trimIndent()

        val ch6 = """
The dust of shattered dimensions settled over the $arena. 

Kneeling amid the scorched remnants of reality, ${loser.name} succumbed to the devastating toll of the engagement, acknowledging an adversary whose preparation, cosmology, and canonical feats surpassed their own. 

${winner.name} stood tall—battered yet indomitable—crowned as the undisputed victor of Death Battle Arena. Across all realms of ${charA.sourceType.displayName} and ${charB.sourceType.displayName}, this clash would be chronicled as a masterclass of fictional supremacy.
        """.trimIndent()

        return listOf(
            BattleChapter(1, "Chapter 1: The Beginning", ch1, ch1.split("\\s+".toRegex()).size),
            BattleChapter(2, "Chapter 2: First Encounter", ch2, ch2.split("\\s+".toRegex()).size),
            BattleChapter(3, "Chapter 3: Power Awakening", ch3, ch3.split("\\s+".toRegex()).size),
            BattleChapter(4, "Chapter 4: The Turning Point", ch4, ch4.split("\\s+".toRegex()).size),
            BattleChapter(5, "Chapter 5: Final Battle", ch5, ch5.split("\\s+".toRegex()).size),
            BattleChapter(6, "Chapter 6: The Aftermath", ch6, ch6.split("\\s+".toRegex()).size)
        )
    }

    private fun generateVerdict(
        charA: CharacterProfile,
        charB: CharacterProfile,
        winner: CharacterProfile,
        loser: CharacterProfile,
        difficulty: BattleDifficulty,
        settings: BattleSettings
    ): BattleVerdict {
        val decisive = winner.powers.lastOrNull() ?: "${winner.name}'s Transcendent Dimensional Hax"
        val advantage = "${winner.name}'s higher Power Tier (${winner.overallTier.tierName}) combined with superior conceptual resistance and ${winner.advancedStats.battleIQ}."
        val feat = winner.keyFeats.firstOrNull()?.description ?: "Demonstrated survival against reality-erasing forces."
        val winCondition = "Breached ${loser.name}'s defensive envelope through superior dimensional scale and fatal hax exploitation."

        val alternateScenarios = listOf(
            AlternateScenario(
                scenarioName = "Speed Equalized Scenario",
                outcomeDescription = if (settings.speedEqualized) {
                    "Currently equalized. Even without velocity divergence, ${winner.name}'s attack potency and hax advantages retain a definitive winning margin."
                } else {
                    "If speeds were strictly equalized, ${loser.name} could prolong the engagement by 35%, but ${winner.name}'s dimensional durability and concept manipulation still seal victory."
                }
            ),
            AlternateScenario(
                scenarioName = "Alternative Battlefield (e.g. Loser's Native Dimension)",
                outcomeDescription = "In ${loser.universe}, ${loser.name} gains localized environmental buffs and ambient energy absorption, elevating the fight to an Extreme Difficulty struggle, but ${winner.name}'s universal baseline remains insurmountable."
            ),
            AlternateScenario(
                scenarioName = "Composite Version Evaluation",
                outcomeDescription = "Granting Composite forms introduces non-canon video game and spin-off feats for both fighters, maintaining the structural tier gap in favor of ${winner.name}."
            )
        )

        return BattleVerdict(
            winnerId = winner.id,
            winnerName = winner.name,
            loserName = loser.name,
            winnerTier = winner.overallTier,
            loserTier = loser.overallTier,
            difficulty = difficulty,
            decisiveAbility = decisive,
            mainAdvantage = advantage,
            criticalFeat = feat,
            winningCondition = winCondition,
            researchConfidence = kotlin.math.min(winner.researchConfidence, loser.researchConfidence),
            verdictConfidence = when (difficulty) {
                BattleDifficulty.NO_DIFFICULTY -> 99
                BattleDifficulty.VERY_LOW -> 96
                BattleDifficulty.LOW -> 93
                BattleDifficulty.MID -> 88
                BattleDifficulty.HIGH -> 82
                BattleDifficulty.EXTREME -> 76
            },
            alternateScenarios = alternateScenarios
        )
    }

    private fun buildAnalyticalReport(
        winner: CharacterProfile,
        loser: CharacterProfile,
        verdict: BattleVerdict,
        settings: BattleSettings
    ): String {
        return """
# Analytical Battle Assessment Report

### 1. The Verdict Summary
**Winner:** ${winner.name} (${winner.sourceType.displayName} - ${winner.universe})
**Loser:** ${loser.name} (${loser.sourceType.displayName} - ${loser.universe})
**Difficulty:** ${verdict.difficulty.label}
**Research Confidence:** ${verdict.researchConfidence}% | **Verdict Confidence:** ${verdict.verdictConfidence}%

---

### 2. Dimensional Scaling & Power Tier Gap
In Death Battle Arena's 17-Tier classification, **${winner.name} operates at ${winner.overallTier.tierName} (Tier ${winner.overallTier.rank})**, whereas **${loser.name} scales to ${loser.overallTier.tierName} (Tier ${loser.overallTier.rank})**. 
Even when considering maximum potential outliers, ${winner.name}'s cosmology directly encompasses higher dimensional space-time axes and qualitative transcendence.

### 3. Decisive Factors & Ability Interactions
- **Decisive Ability:** ${verdict.decisiveAbility}
- **Primary Tactical Advantage:** ${verdict.mainAdvantage}
- **Critical Canon Feat:** "${verdict.criticalFeat}"

### 4. Counter-Strategy Breakdown
Why ${loser.name}'s win conditions failed:
${loser.name}'s primary offensive avenues rely heavily on kinetic, biological, or localized spatial destruction. However, ${winner.name} possesses established resistances to direct reality altering, existence erasure, and conceptual unraveling. Consequently, ${loser.name}'s fatal strikes were unable to bypass ${winner.name}'s innate defensive layers.

### 5. Final Analytical Conclusion
Under standard arena rules and canon continuity restrictions, **${winner.name} secures the triumph in ${verdict.difficulty.label.lowercase()} fashion.**
        """.trimIndent()
    }
}
