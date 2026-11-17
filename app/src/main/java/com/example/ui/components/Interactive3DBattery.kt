package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.DeepVoltGreen
import com.example.ui.theme.ElevatedSteel
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MetallicWhite
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonVoltGreen
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TerminalCrimson
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class Vec3(val x: Float, val y: Float, val z: Float)

private data class QuadFace(
    val p0: Vec3,
    val p1: Vec3,
    val p2: Vec3,
    val p3: Vec3,
    val fill: Color,
    val stroke: Color,
    val highlightAlpha: Float = 0f
)

private data class BatteryCallout(
    val title: String,
    val metric: String,
    val detail: String
)

private val BATTERY_CALLOUTS = listOf(
    BatteryCallout(
        title = "Long Life",
        metric = "60-Mo Warranty",
        detail = "High-purity silver alloy grids built for extreme Maharashtra summers."
    ),
    BatteryCallout(
        title = "High Cranking Power",
        metric = "Up to 920 CCA",
        detail = "Instant cold & hot ignition with low internal resistance."
    ),
    BatteryCallout(
        title = "Zero Maintenance",
        metric = "Factory Sealed",
        detail = "Spill-proof heat-sealed cover with minimal electrolyte loss."
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Interactive3DBatteryShowcase(
    scrollProgress: Float,
    modifier: Modifier = Modifier
) {
    var targetYawDeg by remember { mutableFloatStateOf(-28f) }
    var targetPitchDeg by remember { mutableFloatStateOf(20f) }
    var autoSpin by remember { mutableStateOf(true) }
    var selectedCalloutIndex by remember { mutableIntStateOf(1) }

    val infiniteTransition = rememberInfiniteTransition(label = "battery_3d_motion")
    val spinPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_spin"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    val animatedYaw by animateFloatAsState(
        targetValue = targetYawDeg + (if (autoSpin) spinPhase else 0f) + scrollProgress * 22f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "yaw"
    )
    val animatedPitch by animateFloatAsState(
        targetValue = (targetPitchDeg + scrollProgress * 10f).coerceIn(-55f, 65f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pitch"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ObsidianSurface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.TouchApp,
                        contentDescription = null,
                        tint = NeonVoltGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "DRAG TO ROTATE 3D MODEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonVoltGreen
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (autoSpin) NeonGreenGlow else ElevatedSteel,
                    border = BorderStroke(
                        1.dp,
                        if (autoSpin) NeonVoltGreen else GlassBorder
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { autoSpin = !autoSpin }
                        .testTag("toggle_3d_spin_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Autorenew,
                            contentDescription = "Toggle 3D rotation",
                            tint = if (autoSpin) NeonVoltGreen else BrushedSilver,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (autoSpin) "SPIN ON" else "PAUSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (autoSpin) NeonVoltGreen else BrushedSilver
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            autoSpin = false
                            targetYawDeg += dragAmount.x * 0.55f
                            targetPitchDeg = (targetPitchDeg - dragAmount.y * 0.45f).coerceIn(-50f, 60f)
                        }
                    }
                    .testTag("interactive_3d_battery_canvas"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawEnergyField(
                        pulseAlpha = pulseAlpha,
                        spinDeg = animatedYaw
                    )
                    draw3DBattery(
                        yawDeg = animatedYaw,
                        pitchDeg = animatedPitch,
                        pulseAlpha = pulseAlpha,
                        floatOffsetPx = floatOffset * density,
                        highlightTerminals = selectedCalloutIndex == 1
                    )
                }

                // Floating top-left badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = DeepCharcoal.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = NeonVoltGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "12V PRO • AMARON SPEC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetallicWhite
                        )
                    }
                }

                // Floating bottom-right live voltage badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = DeepCharcoal.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, NeonVoltGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonVoltGreen.copy(alpha = pulseAlpha))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "12.8V OPTIMAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonVoltGreen
                        )
                    }
                }
            }

            // Camera angle preset pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                PresetAngleChip("Isometric") {
                    autoSpin = false
                    targetYawDeg = -32f
                    targetPitchDeg = 22f
                }
                PresetAngleChip("Front") {
                    autoSpin = false
                    targetYawDeg = 0f
                    targetPitchDeg = 10f
                }
                PresetAngleChip("Terminals") {
                    autoSpin = false
                    targetYawDeg = 24f
                    targetPitchDeg = 48f
                }
                PresetAngleChip("Reset") {
                    autoSpin = true
                    targetYawDeg = -28f
                    targetPitchDeg = 20f
                }
            }

            Spacer(Modifier.height(14.dp))

            // Floating spec callout pills
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BATTERY_CALLOUTS.forEachIndexed { index, callout ->
                    val isSelected = selectedCalloutIndex == index
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonGreenGlow else ElevatedSteel,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) NeonVoltGreen else GlassBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCalloutIndex = index }
                            .testTag("battery_callout_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.Verified
                                    1 -> Icons.Filled.Bolt
                                    else -> Icons.Filled.BatteryChargingFull
                                },
                                contentDescription = null,
                                tint = if (isSelected) NeonVoltGreen else BrushedSilver,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = callout.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MetallicWhite else BrushedSilver
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val active = BATTERY_CALLOUTS[selectedCalloutIndex]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepCharcoal.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${active.title} • ${active.metric}",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonVoltGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = active.detail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrushedSilver
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetAngleChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ElevatedSteel.copy(alpha = 0.75f),
        border = BorderStroke(1.dp, GlassBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = BrushedSilver,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

private fun DrawScope.drawEnergyField(
    pulseAlpha: Float,
    spinDeg: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxR = size.minDimension * 0.56f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonVoltGreen.copy(alpha = 0.22f * pulseAlpha),
                NeonVoltGreen.copy(alpha = 0.06f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = maxR
        ),
        radius = maxR,
        center = Offset(cx, cy)
    )

    // Orbital energy rings & floating particles
    drawCircle(
        color = NeonVoltGreen.copy(alpha = 0.16f),
        radius = maxR * 0.78f,
        center = Offset(cx, cy + 18f),
        style = Stroke(width = 1.5f)
    )

    for (i in 0 until 8) {
        val angle = ((spinDeg * 0.8f) + i * 45f) * (PI / 180.0)
        val px = cx + (cos(angle) * maxR * 0.78f).toFloat()
        val py = cy + 18f + (sin(angle) * maxR * 0.26f).toFloat()
        drawCircle(
            color = if (i % 2 == 0) NeonVoltGreen.copy(alpha = 0.75f) else AmberGold.copy(alpha = 0.6f),
            radius = if (i % 2 == 0) 4.5f else 3f,
            center = Offset(px, py)
        )
    }
}

private fun DrawScope.draw3DBattery(
    yawDeg: Float,
    pitchDeg: Float,
    pulseAlpha: Float,
    floatOffsetPx: Float,
    highlightTerminals: Boolean
) {
    val yaw = (yawDeg * PI / 180.0).toFloat()
    val pitch = (pitchDeg * PI / 180.0).toFloat()
    val cx = size.width / 2f
    val cy = size.height / 2f + floatOffsetPx
    val unit = size.minDimension * 0.34f
    val camDist = unit * 5.2f

    fun transform(v: Vec3): Vec3 {
        val x1 = v.x * cos(yaw) - v.z * sin(yaw)
        val z1 = v.x * sin(yaw) + v.z * cos(yaw)
        val y2 = v.y * cos(pitch) - z1 * sin(pitch)
        val z2 = v.y * sin(pitch) + z1 * cos(pitch)
        return Vec3(x1, y2, z2)
    }

    fun project(v: Vec3): Offset {
        val scale = camDist / (camDist - v.z)
        return Offset(cx + v.x * scale, cy - v.y * scale)
    }

    fun addBoxFaces(
        list: MutableList<QuadFace>,
        minX: Float, maxX: Float,
        minY: Float, maxY: Float,
        minZ: Float, maxZ: Float,
        frontColor: Color,
        sideColor: Color,
        topColor: Color,
        bottomColor: Color,
        strokeColor: Color,
        highlight: Float = 0f
    ) {
        val c000 = transform(Vec3(minX, minY, minZ))
        val c100 = transform(Vec3(maxX, minY, minZ))
        val c110 = transform(Vec3(maxX, maxY, minZ))
        val c010 = transform(Vec3(minX, maxY, minZ))
        val c001 = transform(Vec3(minX, minY, maxZ))
        val c101 = transform(Vec3(maxX, minY, maxZ))
        val c111 = transform(Vec3(maxX, maxY, maxZ))
        val c011 = transform(Vec3(minX, maxY, maxZ))

        // Front (+Z)
        list += QuadFace(c001, c101, c111, c011, frontColor, strokeColor, highlight)
        // Back (-Z)
        list += QuadFace(c100, c000, c010, c110, frontColor.copy(alpha = 0.92f), strokeColor)
        // Right (+X)
        list += QuadFace(c101, c100, c110, c111, sideColor, strokeColor)
        // Left (-X)
        list += QuadFace(c000, c001, c011, c010, sideColor, strokeColor)
        // Top (+Y)
        list += QuadFace(c011, c111, c110, c010, topColor, strokeColor, highlight)
        // Bottom (-Y)
        list += QuadFace(c000, c100, c101, c001, bottomColor, strokeColor)
    }

    val faces = mutableListOf<QuadFace>()

    // 1. Main lower charcoal industrial casing
    val w = unit * 1.15f
    val hBody = unit * 0.62f
    val d = unit * 0.72f
    addBoxFaces(
        list = faces,
        minX = -w, maxX = w,
        minY = -hBody, maxY = hBody * 0.52f,
        minZ = -d, maxZ = d,
        frontColor = Color(0xFF172133),
        sideColor = Color(0xFF111827),
        topColor = Color(0xFF1E293B),
        bottomColor = Color(0xFF0A0E17),
        strokeColor = Color(0xFF334155)
    )

    // 2. Amaron-inspired neon green top lid
    addBoxFaces(
        list = faces,
        minX = -w * 1.03f, maxX = w * 1.03f,
        minY = hBody * 0.52f, maxY = hBody * 0.85f,
        minZ = -d * 1.03f, maxZ = d * 1.03f,
        frontColor = DeepVoltGreen,
        sideColor = Color(0xFF009E44),
        topColor = NeonVoltGreen,
        bottomColor = Color(0xFF007A33),
        strokeColor = Color(0xFF69F0AE),
        highlight = 0.15f
    )

    // 3. Vent cap rail on lid
    addBoxFaces(
        list = faces,
        minX = -w * 0.72f, maxX = w * 0.72f,
        minY = hBody * 0.85f, maxY = hBody * 0.96f,
        minZ = -d * 0.22f, maxZ = d * 0.22f,
        frontColor = Color(0xFF1E293B),
        sideColor = Color(0xFF0F172A),
        topColor = Color(0xFF334155),
        bottomColor = Color(0xFF0F172A),
        strokeColor = NeonVoltGreen.copy(alpha = 0.6f)
    )

    // 4. Positive (+) Terminal Post (Left)
    val termW = unit * 0.12f
    val termX = -w * 0.68f
    val termZ = d * 0.52f
    addBoxFaces(
        list = faces,
        minX = termX - termW, maxX = termX + termW,
        minY = hBody * 0.85f, maxY = hBody * 1.24f,
        minZ = termZ - termW, maxZ = termZ + termW,
        frontColor = TerminalCrimson,
        sideColor = Color(0xFFC62828),
        topColor = Color(0xFFFF8A80),
        bottomColor = Color(0xFF8E0000),
        strokeColor = AmberGold,
        highlight = if (highlightTerminals) pulseAlpha else 0.25f
    )

    // 5. Negative (-) Terminal Post (Right)
    val negX = w * 0.68f
    addBoxFaces(
        list = faces,
        minX = negX - termW, maxX = negX + termW,
        minY = hBody * 0.85f, maxY = hBody * 1.24f,
        minZ = termZ - termW, maxZ = termZ + termW,
        frontColor = Color(0xFFCBD5E1),
        sideColor = Color(0xFF94A3B8),
        topColor = MetallicWhite,
        bottomColor = Color(0xFF64748B),
        strokeColor = NeonVoltGreen,
        highlight = if (highlightTerminals) pulseAlpha else 0.25f
    )

    // Painter's algorithm sort (back to front by average Z)
    val sorted = faces.sortedBy { (it.p0.z + it.p1.z + it.p2.z + it.p3.z) * 0.25f }

    sorted.forEach { face ->
        val a = project(face.p0)
        val b = project(face.p1)
        val c = project(face.p2)
        val dPt = project(face.p3)

        val path = Path().apply {
            moveTo(a.x, a.y)
            lineTo(b.x, b.y)
            lineTo(c.x, c.y)
            lineTo(dPt.x, dPt.y)
            close()
        }
        drawPath(path = path, color = face.fill)
        if (face.highlightAlpha > 0f) {
            drawPath(path = path, color = NeonVoltGreen.copy(alpha = face.highlightAlpha * 0.18f))
        }
        drawPath(
            path = path,
            color = face.stroke,
            style = Stroke(width = 1.6f)
        )
    }

    // Draw glowing voltage spark / terminal aura on top of the terminals
    val posTop = project(transform(Vec3(termX, hBody * 1.30f, termZ)))
    val negTop = project(transform(Vec3(negX, hBody * 1.30f, termZ)))

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TerminalCrimson.copy(alpha = 0.55f * pulseAlpha),
                Color.Transparent
            ),
            center = posTop,
            radius = unit * 0.36f
        ),
        radius = unit * 0.36f,
        center = posTop
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonVoltGreen.copy(alpha = 0.65f * pulseAlpha),
                Color.Transparent
            ),
            center = negTop,
            radius = unit * 0.36f
        ),
        radius = unit * 0.36f,
        center = negTop
    )

    // Subtle energy arc between terminals when "High Cranking Power" is active
    if (highlightTerminals) {
        val mid = Offset(
            (posTop.x + negTop.x) * 0.5f,
            minOf(posTop.y, negTop.y) - unit * 0.22f
        )
        val arcPath = Path().apply {
            moveTo(posTop.x, posTop.y)
            quadraticTo(mid.x, mid.y, negTop.x, negTop.y)
        }
        drawPath(
            path = arcPath,
            color = NeonVoltGreen.copy(alpha = 0.55f * pulseAlpha),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
    }
}
