package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.model.*
import com.example.storage.ImageStorageService
import com.example.ui.components.CharacterImageUploader
import com.example.ui.components.PowerTierBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreatorScreen(
    repository: BattleRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val storageService = remember { ImageStorageService(context) }
    var imageState by remember { mutableStateOf(CharacterImageState()) }


    var name by remember { mutableStateOf("") }
    var universe by remember { mutableStateOf("") }
    var form by remember { mutableStateOf("Peak Form") }
    var bio by remember { mutableStateOf("") }
    var powersText by remember { mutableStateOf("") }
    var signatureFeat by remember { mutableStateOf("") }
    var weakness by remember { mutableStateOf("") }

    var selectedSourceType by remember { mutableStateOf(SourceType.ORIGINAL_CHARACTER) }
    var selectedTier by remember { mutableStateOf(PowerTier.CONTINENTAL_LEVEL) }

    var strength by remember { mutableFloatStateOf(85f) }
    var speed by remember { mutableFloatStateOf(88f) }
    var durability by remember { mutableFloatStateOf(82f) }
    var intelligence by remember { mutableFloatStateOf(90f) }
    var skill by remember { mutableFloatStateOf(86f) }
    var specialAbility by remember { mutableFloatStateOf(92f) }

    var showTierPicker by remember { mutableStateOf(false) }
    var showSourceTypePicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Original Character Creator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "CREATE FICTIONAL FIGHTER",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GoldNeon
            )
            Text(
                text = "Define your own Original Character (OC) or add missing fighters with custom stats & 17-tier scaling.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            // Basic Info
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Character Name") },
                placeholder = { Text("e.g. Void Reaper Thorne") },
                modifier = Modifier.fillMaxWidth().testTag("oc_name_field"),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = universe,
                onValueChange = { universe = it },
                label = { Text("Universe / World Origin") },
                placeholder = { Text("e.g. Chrono Nexus") },
                modifier = Modifier.fillMaxWidth().testTag("oc_universe_field"),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = form,
                onValueChange = { form = it },
                label = { Text("Version / Transformation Form") },
                placeholder = { Text("e.g. Absolute Zero Awakening") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            // Custom OC Image Upload Component
            CharacterImageUploader(
                characterName = if (name.isNotBlank()) name else "Original Character",
                sourceType = selectedSourceType,
                accentColor = GoldNeon,
                imageState = imageState,
                storageService = storageService,
                onImageStateChange = { imageState = it },
                tagPrefix = "oc_char"
            )

            // Source Type & Power Tier Selector Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showSourceTypePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceElevated),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SOURCE TYPE", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(selectedSourceType.displayName, fontSize = 12.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { showTierPicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceElevated),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("POWER TIER", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("T${selectedTier.rank}: ${selectedTier.tierName}", fontSize = 12.sp, color = GoldNeon, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sliders for Stats
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("CORE COMBAT ATTRIBUTES (0-100)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    StatSlider("Strength", strength, CrimsonNeon) { strength = it }
                    StatSlider("Speed", speed, CyanNeon) { speed = it }
                    StatSlider("Durability", durability, GoldNeon) { durability = it }
                    StatSlider("Intelligence", intelligence, PurpleNeon) { intelligence = it }
                    StatSlider("Special Ability / Hax", specialAbility, CrimsonNeon) { specialAbility = it }
                }
            }

            OutlinedTextField(
                value = powersText,
                onValueChange = { powersText = it },
                label = { Text("Abilities / Powers (comma-separated)") },
                placeholder = { Text("Quantum Severance, Absolute Barrier, Time Step") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = signatureFeat,
                onValueChange = { signatureFeat = it },
                label = { Text("Signature Canon Feat") },
                placeholder = { Text("Severed an entire planetary moon in Chapter 45") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Character Lore & Bio") },
                placeholder = { Text("A silent wandering chronomancer who escaped temporal collapse...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = outlinedTextFieldColors()
            )

            Button(
                onClick = {
                    if (name.isBlank() || universe.isBlank()) {
                        Toast.makeText(context, "Please enter character name and universe", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val powersList = if (powersText.isNotBlank()) powersText.split(",").map { it.trim() } else listOf("Universal Reality Distortion", "Temporal Surge")
                    val newChar = CharacterProfile(
                        id = "oc_${System.currentTimeMillis()}",
                        name = name,
                        universe = universe,
                        sourceType = selectedSourceType,
                        version = form,
                        form = form,
                        avatarUrl = imageState.imageUri ?: "",
                        overallTier = selectedTier,
                        baseTier = PowerTier.BUILDING_LEVEL,
                        peakTier = selectedTier,
                        researchConfidence = 92,
                        bio = if (bio.isNotBlank()) bio else "A custom-crafted combatant created for the Death Battle Arena.",
                        powers = powersList,
                        combatStats = CombatStats(
                            strength = strength.toInt(),
                            speed = speed.toInt(),
                            durability = durability.toInt(),
                            intelligence = intelligence.toInt(),
                            combatSkill = skill.toInt(),
                            range = 85,
                            specialAbility = specialAbility.toInt()
                        ),
                        advancedStats = AdvancedStats(
                            attackPotency = "${selectedTier.tierName} destructive threshold",
                            reactionSpeed = if (speed > 90) "Massively Faster Than Light" else "Sub-Relativistic",
                            destructiveCapacity = selectedTier.description,
                            stamina = "High superhuman combat reserves",
                            haxLevel = "Custom reality distortion & concept manipulation",
                            battleIQ = "Tactical Mastermind"
                        ),
                        keyFeats = listOf(
                            FeatItem(
                                description = if (signatureFeat.isNotBlank()) signatureFeat else "Demonstrated power reaching ${selectedTier.tierName}.",
                                sourceReference = "$universe Canon Records",
                                evidenceType = EvidenceType.DIRECT_FEAT,
                                confidence = 95
                            )
                        ),
                        weaknesses = if (weakness.isNotBlank()) listOf(weakness) else listOf("Stamina consumption during peak output"),
                        isCustomOc = true
                    )
                    repository.addCustomCharacter(newChar)
                    Toast.makeText(context, "${newChar.name} added to character roster!", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_oc_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldNeon),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE CHARACTER TO ARENA", color = ArenaBgDark, fontWeight = FontWeight.Black)
            }
        }
    }

    // Source Type Picker
    if (showSourceTypePicker) {
        AlertDialog(
            onDismissRequest = { showSourceTypePicker = false },
            title = { Text("Select Source Media Type", color = TextPrimary) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(SourceType.values().toList()) { st ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSourceType = st
                                    showSourceTypePicker = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).background(Color(st.badgeColorHex), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(st.displayName, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourceTypePicker = false }) { Text("Done", color = CyanNeon) }
            },
            containerColor = ArenaSurfaceDark
        )
    }

    // Tier Picker
    if (showTierPicker) {
        AlertDialog(
            onDismissRequest = { showTierPicker = false },
            title = { Text("Select 17-Level Power Tier", color = TextPrimary) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(PowerTier.values().toList()) { tier ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTier = tier
                                    showTierPicker = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedTier == tier, onClick = {
                                selectedTier = tier
                                showTierPicker = false
                            })
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Tier ${tier.rank}: ${tier.tierName}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(tier.description, color = TextMuted, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTierPicker = false }) { Text("Done", color = CyanNeon) }
            },
            containerColor = ArenaSurfaceDark
        )
    }
}

@Composable
private fun StatSlider(label: String, value: Float, color: Color, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = TextPrimary)
            Text("${value.toInt()}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 10f..100f,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = ArenaSurfaceDark,
    unfocusedContainerColor = ArenaSurfaceDark,
    focusedBorderColor = CyanNeon,
    unfocusedBorderColor = ArenaBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = CyanNeon,
    unfocusedLabelColor = TextMuted
)
