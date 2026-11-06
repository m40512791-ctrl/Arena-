package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CombatStats
import com.example.ui.theme.CrimsonNeon
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PowerRadarChart(
    nameA: String,
    statsA: CombatStats,
    colorA: Color = CrimsonNeon,
    nameB: String,
    statsB: CombatStats,
    colorB: Color = CyanNeon,
    modifier: Modifier = Modifier.fillMaxWidth().height(260.dp)
) {
    val labels = listOf("STR", "SPD", "DUR", "INT", "SKL", "RNG", "HAX")
    val valuesA = listOf(
        statsA.strength / 100f,
        statsA.speed / 100f,
        statsA.durability / 100f,
        statsA.intelligence / 100f,
        statsA.combatSkill / 100f,
        statsA.range / 100f,
        statsA.specialAbility / 100f
    )
    val valuesB = listOf(
        statsB.strength / 100f,
        statsB.speed / 100f,
        statsB.durability / 100f,
        statsB.intelligence / 100f,
        statsB.combatSkill / 100f,
        statsB.range / 100f,
        statsB.specialAbility / 100f
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).padding(1.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(colorA) }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(nameA, color = colorA, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).padding(1.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(colorB) }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(nameB, color = colorB, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = (size.minDimension / 2f) * 0.78f
                val count = labels.size
                val angleStep = (2 * Math.PI / count).toFloat()

                // Draw background concentric webs (4 steps: 25%, 50%, 75%, 100%)
                for (step in 1..4) {
                    val r = maxRadius * (step / 4f)
                    val gridPath = Path()
                    for (i in 0 until count) {
                        val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                        val x = center.x + r * cos(angle)
                        val y = center.y + r * sin(angle)
                        if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                    }
                    gridPath.close()
                    drawPath(
                        path = gridPath,
                        color = Color(0xFF263043).copy(alpha = 0.6f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw radial spokes
                for (i in 0 until count) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val x = center.x + maxRadius * cos(angle)
                    val y = center.y + maxRadius * sin(angle)
                    drawLine(
                        color = Color(0xFF263043).copy(alpha = 0.8f),
                        start = center,
                        end = Offset(x, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw Polygon A
                val pathA = Path()
                valuesA.forEachIndexed { i, factor ->
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val r = maxRadius * factor.coerceIn(0.1f, 1f)
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) pathA.moveTo(x, y) else pathA.lineTo(x, y)
                }
                pathA.close()
                drawPath(path = pathA, color = colorA.copy(alpha = 0.35f))
                drawPath(path = pathA, color = colorA, style = Stroke(width = 2.dp.toPx()))

                // Draw Polygon B
                val pathB = Path()
                valuesB.forEachIndexed { i, factor ->
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val r = maxRadius * factor.coerceIn(0.1f, 1f)
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) pathB.moveTo(x, y) else pathB.lineTo(x, y)
                }
                pathB.close()
                drawPath(path = pathB, color = colorB.copy(alpha = 0.35f))
                drawPath(path = pathB, color = colorB, style = Stroke(width = 2.dp.toPx()))
            }
        }
    }
}
