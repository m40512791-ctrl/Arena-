package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleRepository
import com.example.engine.BattleEngine
import com.example.model.*
import com.example.storage.ImageStorageService
import com.example.ui.components.CharacterAvatarView
import com.example.ui.components.CharacterImageUploader
import com.example.ui.components.PowerTierBadge
import com.example.ui.components.SourceTypeBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBattleScreen(
    repository: BattleRepository,
    onBattleCreated: (BattlePost) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val characters by repository.characters.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val storageService = remember { ImageStorageService(context) }

    var selectedCharA by remember { mutableStateOf<CharacterProfile?>(characters.firstOrNull { it.id == "char_fang_yuan" } ?: characters.firstOrNull()) }
    var selectedCharB by remember { mutableStateOf<CharacterProfile?>(characters.firstOrNull { it.id == "char_goku" } ?: characters.getOrNull(1)) }

    // Independent Character Image Upload States
    var imageStateA by remember { mutableStateOf(CharacterImageState(imageUri = selectedCharA?.avatarUrl?.takeIf { it.isNotBlank() })) }
    var imageStateB by remember { mutableStateOf(CharacterImageState(imageUri = selectedCharB?.avatarUrl?.takeIf { it.isNotBlank() })) }

    // Independent Source Type selectors
    var sourceTypeA by remember { mutableStateOf(selectedCharA?.sourceType ?: SourceType.WEB_NOVEL) }
    var sourceTypeB by remember { mutableStateOf(selectedCharB?.sourceType ?: SourceType.ANIME) }


    // Battle Rules
    var speedEqualized by remember { mutableStateOf(false) }
    var bloodlusted by remember { mutableStateOf(true) }
    var fullEquipment by remember { mutableStateOf(true) }
    var battlefield by remember { mutableStateOf("Neutral Primordial Void Arena") }
    val battlefieldOptions = listOf(
        "Neutral Primordial Void Arena",
        "Cosmic River of Time Apex",
        "Sefirah Castle Divine Realm",
        "Shinjuku Metropolitan Ruins",
        "Namek Wasteland Core",
        "Hyperspace Dimensional Rift"
    )

    // Selection Dialog states
    var selectingForSide by remember { mutableStateOf<String?>(null) } // "A" or "B"
    var showSourceTypePickerFor by remember { mutableStateOf<String?>(null) }

    // Simulation Execution states
    var isSimulating by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf<BattleEngine.SimulationStep?>(null) }

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize().background(ArenaBgDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SportsKabaddi,
                    contentDescription = null,
                    tint = CrimsonNeon,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NEW ARENA BATTLE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configure cross-universe characters & simulated rules",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Selection Preview Header
            Surface(
                color = ArenaSurfaceDark,
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Matchup Banner
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        CharacterAvatarView(
                            name = selectedCharA?.name ?: "A",
                            avatarUrl = imageStateA.imageUri ?: selectedCharA?.avatarUrl,
                            accentColor = CrimsonNeon,
                            size = 46.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sourceTypeA.displayName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CrimsonNeon
                        )
                        Text(
                            text = selectedCharA?.name ?: "Select Fighter A",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                brush = Brush.radialGradient(listOf(GoldNeon.copy(alpha = 0.3f), Color.Transparent)),
                                shape = CircleShape
                            )
                            .border(1.dp, GoldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldNeon)
                    }

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        CharacterAvatarView(
                            name = selectedCharB?.name ?: "B",
                            avatarUrl = imageStateB.imageUri ?: selectedCharB?.avatarUrl,
                            accentColor = CyanNeon,
                            size = 46.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sourceTypeB.displayName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanNeon
                        )
                        Text(
                            text = selectedCharB?.name ?: "Select Fighter B",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CHARACTER A PANEL
            CharacterSelectionPanel(
                panelTitle = "CHARACTER A (CHALLENGER)",
                sideColor = CrimsonNeon,
                character = selectedCharA,
                currentSourceType = sourceTypeA,
                imageState = imageStateA,
                storageService = storageService,
                onImageStateChange = { imageStateA = it },
                onSelectCharacter = { selectingForSide = "A" },
                onChangeSourceType = { showSourceTypePickerFor = "A" },
                tagPrefix = "char_a"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // CHARACTER B PANEL
            CharacterSelectionPanel(
                panelTitle = "CHARACTER B (OPPONENT)",
                sideColor = CyanNeon,
                character = selectedCharB,
                currentSourceType = sourceTypeB,
                imageState = imageStateB,
                storageService = storageService,
                onImageStateChange = { imageStateB = it },
                onSelectCharacter = { selectingForSide = "B" },
                onChangeSourceType = { showSourceTypePickerFor = "B" },
                tagPrefix = "char_b"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // BATTLE CONFIGURATION SETTINGS
            Text(
                text = "BATTLE CONFIGURATION",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Speed Equalization
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Speed Equalization", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Neutralizes speed blitz; focuses outcome on Hax and AP.", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = speedEqualized,
                            onCheckedChange = { speedEqualized = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonNeon, checkedTrackColor = CrimsonNeon.copy(alpha = 0.3f))
                        )
                    }

                    HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Bloodlusted
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bloodlusted Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Characters fight with zero morals and maximum lethal intent.", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = bloodlusted,
                            onCheckedChange = { bloodlusted = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonNeon, checkedTrackColor = CrimsonNeon.copy(alpha = 0.3f))
                        )
                    }

                    HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Full Equipment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Full Equipment & Arsenal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Allows peak canonical weapons, artifacts, and tools.", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = fullEquipment,
                            onCheckedChange = { fullEquipment = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon, checkedTrackColor = CyanNeon.copy(alpha = 0.3f))
                        )
                    }

                    HorizontalDivider(color = ArenaBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Battlefield selection
                    Column {
                        Text("Battlefield Environment", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        battlefieldOptions.take(4).forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { battlefield = option }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = battlefield == option,
                                    onClick = { battlefield = option },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldNeon)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(option, fontSize = 12.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIMULATE BUTTON
            Button(
                onClick = {
                    val charA = selectedCharA
                    val charB = selectedCharB
                    if (charA != null && charB != null) {
                        isSimulating = true
                        coroutineScope.launch {
                            val settings = BattleSettings(
                                speedEqualized = speedEqualized,
                                bloodlusted = bloodlusted,
                                fullEquipment = fullEquipment,
                                battlefield = battlefield
                            )
                            val finalCharA = if (!imageStateA.imageUri.isNullOrBlank()) {
                                charA.copy(avatarUrl = imageStateA.imageUri!!)
                            } else charA

                            val finalCharB = if (!imageStateB.imageUri.isNullOrBlank()) {
                                charB.copy(avatarUrl = imageStateB.imageUri!!)
                            } else charB

                            val post = BattleEngine.runSimulation(
                                charA = finalCharA,
                                charB = finalCharB,
                                settings = settings,
                                onProgress = { step -> currentStep = step }
                            )
                            repository.addBattle(post)
                            isSimulating = false
                            onBattleCreated(post)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_simulation_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                shape = RoundedCornerShape(14.dp),
                enabled = selectedCharA != null && selectedCharB != null && !isSimulating
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = ArenaBgDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LAUNCH AI BATTLE SIMULATION",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaBgDark,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // SIMULATION PROGRESS OVERLAY
        if (isSimulating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
                    shape = RoundedCornerShape(20.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(CrimsonNeon)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = { currentStep?.progress ?: 0.1f },
                            modifier = Modifier.size(68.dp),
                            color = CrimsonNeon,
                            trackColor = ArenaSurfaceElevated,
                            strokeWidth = 6.dp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currentStep?.agentName ?: "Central AI Orchestrator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldNeon
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentStep?.label ?: "Processing battle matrix...",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { currentStep?.progress ?: 0.1f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyanNeon,
                            trackColor = ArenaSurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${((currentStep?.progress ?: 0.1f) * 100).toInt()}% COMPLETED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    // Character Selection Modal
    if (selectingForSide != null) {
        val side = selectingForSide
        val targetSourceType = if (side == "A") sourceTypeA else sourceTypeB
        val available = characters.filter { it.sourceType == targetSourceType }

        AlertDialog(
            onDismissRequest = { selectingForSide = null },
            title = {
                Text(
                    text = "Select Character (${targetSourceType.displayName})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (available.isEmpty()) {
                        Text(
                            "No pre-configured characters for ${targetSourceType.displayName}. You can create an Original Character or choose another source type.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(280.dp)) {
                            items(available) { char ->
                                Surface(
                                    color = ArenaSurfaceElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (side == "A") selectedCharA = char else selectedCharB = char
                                            selectingForSide = null
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(char.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text("${char.universe} • ${char.form}", fontSize = 11.sp, color = TextMuted)
                                        }
                                        PowerTierBadge(char.overallTier)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectingForSide = null }) {
                    Text("Close", color = CyanNeon)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }

    // Source Type Picker Modal
    if (showSourceTypePickerFor != null) {
        val side = showSourceTypePickerFor
        AlertDialog(
            onDismissRequest = { showSourceTypePickerFor = null },
            title = { Text("Choose Source Type", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(SourceType.values().toList()) { st ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (side == "A") {
                                        sourceTypeA = st
                                        selectedCharA = characters.firstOrNull { it.sourceType == st }
                                    } else {
                                        sourceTypeB = st
                                        selectedCharB = characters.firstOrNull { it.sourceType == st }
                                    }
                                    showSourceTypePickerFor = null
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).background(Color(st.badgeColorHex), CircleShape))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(st.displayName, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourceTypePickerFor = null }) {
                    Text("Done", color = CyanNeon)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }
}

@Composable
private fun CharacterSelectionPanel(
    panelTitle: String,
    sideColor: Color,
    character: CharacterProfile?,
    currentSourceType: SourceType,
    imageState: CharacterImageState,
    storageService: ImageStorageService,
    onImageStateChange: (CharacterImageState) -> Unit,
    onSelectCharacter: () -> Unit,
    onChangeSourceType: () -> Unit,
    tagPrefix: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceDark),
        shape = RoundedCornerShape(14.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(sideColor.copy(alpha = 0.5f))
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = panelTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = sideColor,
                    letterSpacing = 0.5.sp
                )
                // Source Type Selector button
                Surface(
                    color = Color(currentSourceType.badgeColorHex).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(currentSourceType.badgeColorHex))
                    ),
                    modifier = Modifier.clickable(onClick = onChangeSourceType)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentSourceType.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(currentSourceType.badgeColorHex)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(currentSourceType.badgeColorHex), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (character != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ArenaSurfaceElevated, RoundedCornerShape(10.dp))
                        .clickable(onClick = onSelectCharacter)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterAvatarView(
                        name = character.name,
                        avatarUrl = imageState.imageUri ?: character.avatarUrl,
                        accentColor = sideColor,
                        size = 44.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(character.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${character.universe} • ${character.form}", fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        PowerTierBadge(character.overallTier)
                        Text(
                            text = "Tap to switch",
                            fontSize = 10.sp,
                            color = sideColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onSelectCharacter,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = sideColor)
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Character from ${currentSourceType.displayName}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Professional Image Upload Component
            CharacterImageUploader(
                characterName = character?.name ?: "Character",
                sourceType = currentSourceType,
                accentColor = sideColor,
                imageState = imageState,
                storageService = storageService,
                onImageStateChange = onImageStateChange,
                tagPrefix = tagPrefix
            )
        }
    }
}

