package com.example.data

import com.example.model.*

object CharacterDatabase {
    val initialCharacters: List<CharacterProfile> = listOf(
        CharacterProfile(
            id = "char_fang_yuan",
            name = "Fang Yuan",
            universe = "Reverend Insanity",
            sourceType = SourceType.WEB_NOVEL,
            version = "Great Love Immortal Venerable (Rank 9)",
            form = "Sovereign Immortal Fetus Body",
            overallTier = PowerTier.COMPLEX_MULTIVERSAL,
            baseTier = PowerTier.PLANETARY_LEVEL,
            peakTier = PowerTier.COMPLEX_MULTIVERSAL,
            researchConfidence = 98,
            bio = "A ruthless, demonic cultivator who traversed 500 years back into the past using the Spring Autumn Cicada. Master of all Gu paths, refined the Sovereign Immortal Fetus, and conquered the River of Time.",
            powers = listOf(
                "Spring Autumn Cicada (Time Reversal)",
                "Sovereign Immortal Body (Non-conflicting Dao Marks)",
                "Steal Heavenly Secret",
                "Great Love Killer Moves",
                "Reverse Flow Protection Seal",
                "Infinite Immortal Essence Synthesis"
            ),
            combatStats = CombatStats(
                strength = 94,
                speed = 96,
                durability = 99,
                intelligence = 100,
                combatSkill = 98,
                range = 97,
                specialAbility = 100
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Complex Multiversal (Controls conceptual Dao Marks across infinite timelines)",
                reactionSpeed = "Immeasurable (Transcends the River of Time causality)",
                destructiveCapacity = "Multiverse Boundary Shattering",
                stamina = "Infinite (Sovereign Aperture self-sustaining)",
                haxLevel = "Absolute Concept Manipulation & Causality Theft",
                battleIQ = "God-Tier Demonic Schemer (500+ years accumulated calculation)"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Refined and controlled the Sovereign Immortal Fetus, allowing simultaneous cultivation of all conflicting Dao paths.",
                    sourceReference = "Reverend Insanity Ch. 1022",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Withstood primordial Chaos tribulations that erase concepts and reality boundaries without taking permanent damage.",
                    sourceReference = "Reverend Insanity Ch. 2215",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 97
                ),
                FeatItem(
                    description = "Used Reverse Flow Protection Seal to reflect all incoming physical and conceptual attacks back onto primordial Venerables.",
                    sourceReference = "Reverend Insanity Ch. 1285",
                    evidenceType = EvidenceType.OFFICIAL_STATEMENT,
                    confidence = 95
                )
            ),
            weaknesses = listOf(
                "Dao mark resonance requires continuous assimilation of apertures",
                "Emotionless pragmatism can be predicted by omniscient wisdom paths under specific setups"
            )
        ),

        CharacterProfile(
            id = "char_goku",
            name = "Son Goku",
            universe = "Dragon Ball Super",
            sourceType = SourceType.ANIME,
            version = "Granolah Arc (True Ultra Instinct)",
            form = "True Ultra Instinct",
            overallTier = PowerTier.LOW_MULTIVERSAL,
            baseTier = PowerTier.PLANETARY_LEVEL,
            peakTier = PowerTier.LOW_MULTIVERSAL,
            researchConfidence = 96,
            bio = "A Saiyan warrior raised on Earth who relentlessly breaks his limits through martial discipline and divine God Ki. Mastered autonomous bodily movement via Ultra Instinct.",
            powers = listOf(
                "Ultra Instinct (Autonomous dodging & reflexive strike)",
                "God Kamehameha",
                "Instant Transmission (Sub-space quantum teleport)",
                "Ki Avatar Projection",
                "Hakai (Destruction Energy Sphere)",
                "Zenkai Boost (In-combat reactive evolution)"
            ),
            combatStats = CombatStats(
                strength = 99,
                speed = 99,
                durability = 96,
                intelligence = 72,
                combatSkill = 100,
                range = 92,
                specialAbility = 88
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Low Multiversal (Punches endangered macrocosm composed of mortal world, Hell, and Heaven)",
                reactionSpeed = "Massively Faster Than Light+ to Incalculable reflexes",
                destructiveCapacity = "Multiple Universal macrocosms",
                stamina = "Superhuman God Ki (Stamina drain mitigated in True UI)",
                haxLevel = "Existence Erasure Resistance & Space-Time Teleportation",
                battleIQ = "Pure Martial Genius (Immediate adaptation to opposing combat styles)"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Clash with Beerus generated shockwaves threatening the complete collapse of Universe 7 macrocosm.",
                    sourceReference = "Dragon Ball Super Episode 12",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Shook the infinite World of Void through mere power ascension into Mastered Ultra Instinct.",
                    sourceReference = "Dragon Ball Super Episode 129",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                ),
                FeatItem(
                    description = "Reacted to Jiren's invisible eye strikes that shattered folded spatial dimensions.",
                    sourceReference = "Dragon Ball Super Ch. 41",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 95
                )
            ),
            weaknesses = listOf(
                "Lacks conceptual and conceptual causality manipulation defenses",
                "Overly trusts fair combat and lets guard down outside bloodlusted focus"
            )
        ),

        CharacterProfile(
            id = "char_klein_moretti",
            name = "Klein Moretti",
            universe = "Lord of the Mysteries",
            sourceType = SourceType.WEB_NOVEL,
            version = "Lord of the Mysteries (Half-Pillar)",
            form = "The Fool / Beacon of Destiny",
            overallTier = PowerTier.COMPLEX_MULTIVERSAL,
            baseTier = PowerTier.STREET_LEVEL,
            peakTier = PowerTier.COMPLEX_MULTIVERSAL,
            researchConfidence = 99,
            bio = "The mysterious Fool who transcended mortal mysteries to claim the Sefirah Castle. Controls the Fool, Error, and Door pathways, capable of fooling time, history, and physical causality.",
            powers = listOf(
                "Fooling (Fooling history, time, perception, and concepts)",
                "Grafting (Connecting separate physical and conceptual realities)",
                "Historical Void Summoning (Projecting past entities)",
                "Error / Theft (Stealing thoughts, abilities, concepts, and time)",
                "Door Pathway Spatial Transmutation",
                "Spirit Body Threads Puppetization"
            ),
            combatStats = CombatStats(
                strength = 82,
                speed = 95,
                durability = 98,
                intelligence = 100,
                combatSkill = 92,
                range = 99,
                specialAbility = 100
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Complex Multiversal (Affects cosmic higher-dimensional Sefirot)",
                reactionSpeed = "Immeasurable (Manipulates temporal historical records)",
                destructiveCapacity = "Conceptual Timeline Reshaping",
                stamina = "Limitless as a Great Old One Sefirah nexus",
                haxLevel = "Supreme (Grafting, Concept Stealing, Fate Manipulation)",
                battleIQ = "Master Strategist & Divination Architect"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Grafted the concept of a supernova explosion directly onto the God of Combat's physical trajectory.",
                    sourceReference = "Lord of the Mysteries Ch. 1290",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                ),
                FeatItem(
                    description = "Fooled history and time to delay the physical arrival of cosmic Outer Deities at the planetary atmosphere.",
                    sourceReference = "Lord of the Mysteries Ch. 1386",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Stole thoughts and conceptual intentions mid-invocation from Amon, the Avatar of Deceit.",
                    sourceReference = "Lord of the Mysteries Ch. 1374",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 96
                )
            ),
            weaknesses = listOf(
                "Requires active mental fortitude to resist Celestial Worthy's will of awakening",
                "Grafting requires conceptual awareness of the linked objects"
            )
        ),

        CharacterProfile(
            id = "char_saitama",
            name = "Saitama",
            universe = "One Punch Man",
            sourceType = SourceType.MANGA,
            version = "Cosmic Fear Garou Fight (Io Arc)",
            form = "Serious Mode (Exponential Growth)",
            overallTier = PowerTier.SOLAR_SYSTEM_LEVEL,
            baseTier = PowerTier.PLANETARY_LEVEL,
            peakTier = PowerTier.LOW_MULTIVERSAL,
            researchConfidence = 94,
            bio = "A hero for fun who removed his biological limiter. His physical statistics multiply exponentially in combat whenever pushed by an adversary, completely defying laws of physics.",
            powers = listOf(
                "Limiter Removal (Infinite potential ceiling)",
                "Serious Punch²",
                "Omni-Directional Serious Sneeze (Blew away Jupiter's gas layers)",
                "Causality Punch (Traveled backward through time without a machine)",
                "Sub-Atomic Portal Kick (Kicking hyperspace gates physically)",
                "Invulnerable Absolute Durability"
            ),
            combatStats = CombatStats(
                strength = 100,
                speed = 98,
                durability = 100,
                intelligence = 60,
                combatSkill = 75,
                range = 88,
                specialAbility = 85
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Multi-Solar System to Low Multiversal (Exponential growth graph)",
                reactionSpeed = "FTL+ to Instantaneous Causality Reversal",
                destructiveCapacity = "Erased visible starlight constellations in serious punch clash",
                stamina = "Infinite (Never exhibited physical fatigue or sweat)",
                haxLevel = "Toon-Physics Interaction & Hyperspace Distortion Manipulation",
                battleIQ = "Instinctive Brawler (Simple yet devastating counter-blows)"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Serious Punch² clash between Saitama and Garou blew an empty void hole into deep cosmos devoid of light.",
                    sourceReference = "One Punch Man Ch. 167",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Sneezed with enough kinetic shockwave to peel away the planetary mantle of Jupiter down to its core.",
                    sourceReference = "One Punch Man Ch. 168",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Learned sub-atomic time travel martial art in seconds and punched Garou before the strike was launched.",
                    sourceReference = "One Punch Man Ch. 169",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 96
                )
            ),
            weaknesses = listOf(
                "Lack of formal martial art versatility or conceptual sealing resistance",
                "Often lets opponents land first strikes out of sheer boredom"
            )
        ),

        CharacterProfile(
            id = "char_wang_lin",
            name = "Wang Lin",
            universe = "Renegade Immortal",
            sourceType = SourceType.WEB_NOVEL,
            version = "4th Step Trampling Heavens",
            form = "Ancient God & Celestial Avatar Unity",
            overallTier = PowerTier.HYPERVERSAL,
            baseTier = PowerTier.PLANETARY_LEVEL,
            peakTier = PowerTier.HYPERVERSAL,
            researchConfidence = 97,
            bio = "A ruthless slaughterer of the heavens who comprehended Life and Death, Karma, and True & False Dao Domains. Stepped beyond the 3rd Step to trample all realms of existence.",
            powers = listOf(
                "Trampling Heavens 9 Bridges",
                "Life and Death Domain",
                "True and False Dao (Transmutes fiction to reality)",
                "Ancient God Star Shatter",
                "Karma Thread Severing",
                "One Finger of Cessation (Freezes time, soul, and motion)"
            ),
            combatStats = CombatStats(
                strength = 98,
                speed = 97,
                durability = 99,
                intelligence = 99,
                combatSkill = 98,
                range = 99,
                specialAbility = 99
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Hyperversal (Tramples 9 bridges beyond space, law, and higher celestial dimensions)",
                reactionSpeed = "Immeasurable (Operates outside cosmic causal flow)",
                destructiveCapacity = "Innumerable Celestial Star Realms and Dao Matrices",
                stamina = "Boundless (Infinite Source Essences)",
                haxLevel = "Supreme (True/False transmutes any attack into nonexistent illusion)",
                battleIQ = "Cold-Blooded Daoist Genius (Decades of isolation & warfare strategy)"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Crossed the 9 Bridges of Trampling Heavens, leaving the universe and entering the boundless boundless void beyond laws.",
                    sourceReference = "Renegade Immortal Ch. 2088",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 99
                ),
                FeatItem(
                    description = "Used True and False Dao to declare the attacks of Sovereign cultivators as purely imaginary, dispersing them instantly.",
                    sourceReference = "Renegade Immortal Ch. 1910",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 97
                )
            ),
            weaknesses = listOf(
                "Deep psychological scars over past lovers and deceased clansmen"
            )
        ),

        CharacterProfile(
            id = "char_sung_jinwoo",
            name = "Sung Jin-woo",
            universe = "Solo Leveling",
            sourceType = SourceType.MANHWA,
            version = "True Shadow Monarch (Ragnarok Post-War)",
            form = "Monarch of Shadows (Death Incarnate)",
            overallTier = PowerTier.LOW_MULTIVERSAL,
            baseTier = PowerTier.STREET_LEVEL,
            peakTier = PowerTier.LOW_MULTIVERSAL,
            researchConfidence = 96,
            bio = "The weakest E-Rank hunter who unlocked the System, inherited the primordial mantle of Ashborn, and commanded a boundless immortal army of shadow soldiers spanning dimensions.",
            powers = listOf(
                "Shadow Extraction (Infinite army reanimation)",
                "Ruler's Authority (Telekinetic force manipulation)",
                "Monarch's Domain (Increases shadow army power by 50%)",
                "Dragon's Fear (Soul paralysis aura)",
                "Shadow Exchange (Instant inter-dimensional teleportation)",
                "Complete Immunity to Poison and Mental Attacks"
            ),
            combatStats = CombatStats(
                strength = 95,
                speed = 97,
                durability = 96,
                intelligence = 89,
                combatSkill = 95,
                range = 94,
                specialAbility = 98
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Planetary to Low Multiversal (Commands primordial mana of creation)",
                reactionSpeed = "Massively Faster Than Light",
                destructiveCapacity = "Dimensional Rifts & Celestial Armies",
                stamina = "Infinite (Regenerates continuously from the World of Darkness)",
                haxLevel = "Death Embodiment, Soul Extraction, Concept Resistance",
                battleIQ = "Adaptive Tactician with High Battlefield Awareness"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Single-handedly fought and defeated all Monarchs of Destruction across a 27-year dimensional war.",
                    sourceReference = "Solo Leveling Ch. 177",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                ),
                FeatItem(
                    description = "Created an internal infinite universe dimension (Realm of the Dead) housing hundreds of millions of shadows.",
                    sourceReference = "Solo Leveling Ch. 162",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 95
                )
            ),
            weaknesses = listOf(
                "Extracting shadows requires souls that haven't been conceptually eradicated"
            )
        ),

        CharacterProfile(
            id = "char_gojo_satoru",
            name = "Gojo Satoru",
            universe = "Jujutsu Kaisen",
            sourceType = SourceType.ANIME,
            version = "Shinjuku Showdown Arc",
            form = "Limitless & Six Eyes Awakened",
            overallTier = PowerTier.CITY_LEVEL,
            baseTier = PowerTier.BUILDING_LEVEL,
            peakTier = PowerTier.CONTINENTAL_LEVEL,
            researchConfidence = 98,
            bio = "The modern world's strongest Jujutsu sorcerer. Possesses the Six Eyes and the Limitless technique, bringing the concept of infinite convergence into physical manifestation.",
            powers = listOf(
                "Infinity Neutral (Achilles and Tortoise paradox shield)",
                "Domain Expansion: Unlimited Void (Infinite sensory information overload)",
                "Cursed Technique Reversal: Red (Repulsive matter beam)",
                "Cursed Technique Amplification: Blue (Attractive spatial vacuum)",
                "Hollow Purple (Imaginary mass space annihilation)",
                "Reverse Cursed Technique (Instant brain and flesh regeneration)"
            ),
            combatStats = CombatStats(
                strength = 78,
                speed = 88,
                durability = 92,
                intelligence = 93,
                combatSkill = 96,
                range = 80,
                specialAbility = 95
            ),
            advancedStats = AdvancedStats(
                attackPotency = "City to Continental (Hollow Purple imaginary mass)",
                reactionSpeed = "Sub-Relativistic to Hypersonic+",
                destructiveCapacity = "Metropolitan obliteration",
                stamina = "Functionally infinite due to Six Eyes atomic cursed energy consumption",
                haxLevel = "Absolute Spatial Division (Neutral Infinity) & Information Mind Crush",
                battleIQ = "Master Jujutsu Strategist and Black Flash specialist"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Unlimited Void overwhelmed 0.2 second exposure with six months' worth of raw cerebral information.",
                    sourceReference = "Jujutsu Kaisen Ch. 89",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 100
                ),
                FeatItem(
                    description = "Surfaced a 200% Hollow Purple that pulverized several city blocks in Shinjuku from kilometers away.",
                    sourceReference = "Jujutsu Kaisen Ch. 223",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                )
            ),
            weaknesses = listOf(
                "Can be bypassed by attacks that target space itself or bypass geometric coordinates",
                "Brain fatigue from continuous Reverse Cursed Technique replenishment"
            )
        ),

        CharacterProfile(
            id = "char_superman",
            name = "Superman",
            universe = "DC Comics",
            sourceType = SourceType.COMIC,
            version = "Post-Crisis / Rebirth (Sun-Dipped)",
            form = "Kryptonian Solar Empowered",
            overallTier = PowerTier.MULTIVERSAL,
            baseTier = PowerTier.PLANETARY_LEVEL,
            peakTier = PowerTier.COMPLEX_MULTIVERSAL,
            researchConfidence = 96,
            bio = "The Last Son of Krypton sent to Earth. Under a yellow sun, his physiology absorbs solar radiation to perform feats that alter laws of physics, shattering boundaries of reality.",
            powers = listOf(
                "Solar Energy Absorption & Sun-Dip Empowerment",
                "Heat Vision (Hotter than core of stellar stars)",
                "Super Breath / Freeze Breath",
                "Invulnerability & Molecular Density Manipulation",
                "Superluminal Flight",
                "Torquasm-Vo & Rao Meditation (Mental barrier)"
            ),
            combatStats = CombatStats(
                strength = 99,
                speed = 99,
                durability = 100,
                intelligence = 90,
                combatSkill = 94,
                range = 92,
                specialAbility = 89
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Multiversal (Punched World Forger and shattered sixth-dimensional hammer)",
                reactionSpeed = "Massively FTL+ to Trans-temporal flight",
                destructiveCapacity = "Galactic clusters to multiversal barriers",
                stamina = "Virtually infinite while nourished by yellow/blue stars",
                haxLevel = "Bio-electric aura manipulation & Story of Superman causality resilience",
                battleIQ = "Kryptonian science genius and skilled boxer/martial artist"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Punched the Sixth-Dimensional World Forger, shattering his newly constructed replacement multiverse.",
                    sourceReference = "Justice League (Vol. 4) #25",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                ),
                FeatItem(
                    description = "Held a book containing infinite pages alongside Captain Marvel.",
                    sourceReference = "Final Crisis: Superman Beyond #1",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 95
                )
            ),
            weaknesses = listOf(
                "Vulnerable to exotic Kryptonite radiation and magic / mystical energy",
                "Morals prevent lethality unless placed in Bloodlusted condition"
            )
        ),

        CharacterProfile(
            id = "char_rimuru",
            name = "Rimuru Tempest",
            universe = "That Time I Got Reincarnated as a Slime",
            sourceType = SourceType.LIGHT_NOVEL,
            version = "Volume 21 (Void God Azathoth)",
            form = "Ultimate Slime / True Dragon Deity",
            overallTier = PowerTier.COMPLEX_MULTIVERSAL,
            baseTier = PowerTier.BUILDING_LEVEL,
            peakTier = PowerTier.COMPLEX_MULTIVERSAL,
            researchConfidence = 98,
            bio = "A corporate worker reincarnated as a slime in a fantasy world who evolved into a Demon Lord and eventually a True Dragon God aided by the Manas Ciel.",
            powers = listOf(
                "Manas: Ciel (Autonomous super-calculation partner)",
                "Void God Azathoth (Turn Null, Imaginary Space, Soul Consumption)",
                "Harvest Lord Shub-Niggurath (Skill Creation & Bestowal)",
                "Space-Time Domination (Instantaneous multi-dimensional teleportation)",
                "Parallel Existences (Infinite clonal avatars)",
                "Absolute Defense (Multidimensional barrier)"
            ),
            combatStats = CombatStats(
                strength = 96,
                speed = 98,
                durability = 99,
                intelligence = 100,
                combatSkill = 95,
                range = 99,
                specialAbility = 100
            ),
            advancedStats = AdvancedStats(
                attackPotency = "Complex Multiversal (Turn Null can recreate the universe tens of thousands of times)",
                reactionSpeed = "Immeasurable (Calculated over billions of years in the End of Time)",
                destructiveCapacity = "Creation and collapse of tens of thousands of separate dimensional worlds",
                stamina = "Infinite (Turn Null primordial reactor)",
                haxLevel = "Supreme (Information King, Soul Eradication, Absolute Nullification)",
                battleIQ = "Infinite with Ciel's tactical processing speed"
            ),
            keyFeats = listOf(
                FeatItem(
                    description = "Stored enough primordial Turn Null energy at the End of Time to recreate the multiverse tens of thousands of times.",
                    sourceReference = "Tensura Light Novel Vol. 21",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 98
                ),
                FeatItem(
                    description = "Absorbed and integrated the power of multiple True Dragons into Imaginary Space.",
                    sourceReference = "Tensura Light Novel Vol. 15",
                    evidenceType = EvidenceType.DIRECT_FEAT,
                    confidence = 99
                )
            ),
            weaknesses = listOf(
                "Relies heavily on Ciel for instantaneous decisions, although Ciel never errs"
            )
        )
    )
}
