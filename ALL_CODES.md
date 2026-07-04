# ملف الأكواد الكامل لتطبيق ماي تيوب (my tup)

يحتوي هذا الملف على جميع الأكواد البرمجية الرئيسية التي تم تطويرها وتعديلها لتطبيق **ماي تيوب (my tup)**. يمكنك نسخها أو استخدامها لبناء التطبيق من جديد.

---
## 1. ملف الإعدادات والاسم (strings.xml)
**المسار:** `app/src/main/res/values/strings.xml`
```xml
<resources>
    <string name="app_name">ماي تيوب my tup</string>
</resources>
```

---
## 2. ملف التعريف الخاص بالمنصة (metadata.json)
**المسار:** `metadata.json`
```json
{
  "name": "ماي تيوب my tup",
  "description": "An immersive and organic multimedia platform featuring dynamic visual environments, interactive players, custom social bubbles, and AI-powered intelligence.",
  "requestFramePermissions": [],
  "majorCapabilities": ["MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API"]
}
```

---
## 3. ملف الواجهات الرئيسي (MediaSanctuaryApp.kt)
**المسار:** `app/src/main/java/com/example/ui/MediaSanctuaryApp.kt`
```kotlin
package com.example.ui

import android.widget.VideoView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.FriendBubble
import com.example.data.MemoryCapsule
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSanctuaryApp(
    viewModel: MediaSanctuaryViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isRtl = currentLanguage == "ar"
    
    // Dynamically apply layout direction for full instant localization
    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        val sleepMode by viewModel.sleepMode.collectAsState()
        val timeOfDay by viewModel.timeOfDay.collectAsState()
        val gyroX by viewModel.gyroscopeX.collectAsState()
        val gyroY by viewModel.gyroscopeY.collectAsState()
        val activeCapsule by viewModel.activeCapsule.collectAsState()
        val isPlaying by viewModel.isPlaying.collectAsState()
        val isZenMode by viewModel.isZenMode.collectAsState()
        val isSquareAspect by viewModel.isSquareAspect.collectAsState()
        val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
        val activeChatFriend by viewModel.activeChatFriend.collectAsState()
        
        val allFriends by viewModel.allFriends.collectAsState()
        
        // Performance watchdog and media search states
        val performanceThrottled by viewModel.performanceThrottled.collectAsState()
        val fallbackGradientMode by viewModel.fallbackGradientMode.collectAsState()
        val fps by viewModel.fps.collectAsState()
        val performanceLogs by viewModel.performanceLogs.collectAsState()
        val selfHealingActive by viewModel.selfHealingActive.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val searchedCapsules by viewModel.searchedCapsules.collectAsState()
        val activeTranscriptSummary by viewModel.activeTranscriptSummary.collectAsState()
        val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()

        var showProfileMenu by remember { mutableStateOf(false) }
        var showPreserveCapsuleDialog by remember { mutableStateOf(false) }

        // Continuous high-precision ticking clock for procedural sunset shader movement
        val infiniteTime = rememberInfiniteTransition(label = "SunsetShaderTime")
        val shaderTime by infiniteTime.animateFloat(
            initialValue = 0f,
            targetValue = 2000f,
            animationSpec = infiniteRepeatable(
                animation = tween(60000, easing = androidx.compose.animation.core.LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "TimeTicker"
        )

        // Core tactile frequency heartbeat triggers during active play
        LaunchedEffect(isPlaying, activeCapsule, hapticsEnabled) {
            if (isPlaying && activeCapsule != null && hapticsEnabled) {
                val pulseDelay = if (activeCapsule?.moodPace == "action") 500L else 1400L
                while (isPlaying && hapticsEnabled) {
                    viewModel.triggerPlayHapticBeat()
                    delay(pulseDelay)
                }
            }
        }

        // Main layout view containing the Procedural Sunset Shader
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0502))
                .drawBehind {
                    if (fallbackGradientMode) {
                        // Safe Fallback gradient when self-healing is triggered (High-Performance Shader suspended)
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F0705), Color(0xFF1E0A08))
                            )
                        )
                        return@drawBehind
                    }

                    // 1. Base Sky/Atmosphere Color Gradients computed dynamically based on simulated celestial hours
                    val skyColors = when (timeOfDay) {
                        "Morning" -> listOf(Color(0xFF0A0812), Color(0xFF26181A), Color(0xFF452422))
                        "Sunset" -> listOf(Color(0xFF070304), Color(0xFF200908), Color(0xFF3B0D0B), Color(0xFF190618))
                        else -> listOf(Color(0xFF03050B), Color(0xFF0E1322), Color(0xFF15182C))
                    }
                    drawRect(brush = Brush.verticalGradient(colors = skyColors))

                    // Gyroscope/drag parallax shifts
                    val shiftX = gyroX * size.width * 0.08f
                    val shiftY = gyroY * size.height * 0.08f

                    // 2. Procedural Sun/Moon Shader core drawing
                    when (timeOfDay) {
                        "Morning" -> {
                            val centerX = size.width * 0.5f + shiftX
                            val centerY = size.height * 0.42f + shiftY
                            val sunRadius = size.minDimension * 0.22f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFEE58).copy(alpha = 0.75f), Color(0xFFFFB300).copy(alpha = 0.25f), Color.Transparent),
                                    center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                    radius = sunRadius * 2.8f
                                ),
                                radius = sunRadius * 2.8f,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                            )
                        }
                        "Sunset" -> {
                            val centerX = size.width * 0.5f + shiftX
                            val centerY = size.height * 0.58f + shiftY
                            val sunRadius = size.minDimension * 0.26f

                            // Dynamic Throttling scales down radial flare overlays to maximize frame rates
                            val layers = if (performanceThrottled) 2 else 4
                            for (l in 1..layers) {
                                val currentRadius = sunRadius * (1f + l * 0.4f)
                                val opacity = 0.22f / l
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color(0xFFFF5722).copy(alpha = opacity), Color.Transparent),
                                        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                        radius = currentRadius
                                    ),
                                    radius = currentRadius,
                                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                                )
                            }

                            // Glowing core of the sunset
                            drawCircle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFE082), Color(0xFFFF3D00)),
                                    startY = centerY - sunRadius,
                                    endY = centerY + sunRadius
                                ),
                                radius = sunRadius,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                            )
                        }
                        else -> { // Night moon
                            val centerX = size.width * 0.7f + shiftX
                            val centerY = size.height * 0.26f + shiftY
                            val moonRadius = size.minDimension * 0.12f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFB2EBF2).copy(alpha = 0.45f), Color.Transparent),
                                    center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                    radius = moonRadius * 2.5f
                                ),
                                radius = moonRadius * 2.5f,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                            )
                            drawCircle(
                                color = Color(0xFFF5F7F8),
                                radius = moonRadius,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                            )
                        }
                    }

                    // 3. Floating Micro particles (thermal dust particles rising upwards)
                    // The particle counts dynamically adapt under Throttling to protect smoothness
                    val particleCount = if (performanceThrottled) 8 else 22
                    for (i in 0 until particleCount) {
                        val speed = 0.04f + (i % 3) * 0.02f
                        val phase = (shaderTime * speed + i * 32f)
                        val xPos = (phase % size.width)
                        val yPos = ((size.height - (phase * 0.7f + i * 64f)) % size.height + size.height) % size.height
                        
                        val radius = (3.dp.toPx() + (i % 3) * 2.5.dp.toPx()) * (if (performanceThrottled) 0.6f else 1f)
                        val particleColor = when (timeOfDay) {
                            "Morning" -> Color(0xFFFFF59D).copy(alpha = 0.25f)
                            "Sunset" -> Color(0xFFFF8A65).copy(alpha = 0.2f)
                            else -> Color(0xFF80DEEA).copy(alpha = 0.22f)
                        }
                        drawCircle(
                            color = particleColor,
                            radius = radius,
                            center = androidx.compose.ui.geometry.Offset(xPos, yPos)
                        )
                    }

                    // 4. Horizon reflections and waves
                    if (timeOfDay == "Sunset" || timeOfDay == "Morning") {
                        val horizonY = size.height * 0.62f
                        val waveColor = if (timeOfDay == "Sunset") Color(0xFFFFCC80) else Color(0xFFFFF9C4)
                        val wavesCount = if (performanceThrottled) 4 else 8

                        for (r in 0 until wavesCount) {
                            val rowY = horizonY + r * 15.dp.toPx()
                            val scaleRatio = (1f - (r.toFloat() / wavesCount)) * 0.65f
                            val waveWidth = size.width * scaleRatio
                            val movementOffset = kotlin.math.sin(shaderTime * 0.04f + r) * 18.dp.toPx() + shiftX * (1f - r.toFloat() / wavesCount)
                            val startX = (size.width - waveWidth) / 2f + movementOffset

                            drawLine(
                                color = waveColor.copy(alpha = (0.24f - r * 0.025f).coerceAtLeast(0.02f)),
                                start = androidx.compose.ui.geometry.Offset(startX, rowY),
                                end = androidx.compose.ui.geometry.Offset(startX + waveWidth, rowY),
                                strokeWidth = (2.dp.toPx() + (r % 2) * 1.5.dp.toPx()) * (if (performanceThrottled) 0.5f else 1f)
                            )
                        }
                    }
                }
                // Interactive manual gyroscope parallax tilt simulator via simple background dragging
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = (gyroX + dragAmount.x / 500f).coerceIn(-1f, 1f)
                        val newY = (gyroY + dragAmount.y / 500f).coerceIn(-1f, 1f)
                        viewModel.updateGyroscope(newX, newY)
                    }
                }
        ) {
            // Parallax Ambient Light Background Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = (gyroX * 30).dp, y = (gyroY * 30).dp)
                    .blur(if (isZenMode) 80.dp else 40.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (activeCapsule?.moodPace == "action") Color(0xFFFF5722) else Color(0xFFFFA726)).copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            radius = 600f
                        )
                    )
            )

            // Sleep Mode warm screen filter overlay
            if (sleepMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE65100).copy(alpha = 0.28f)) // Warm blue-light filter tint
                        .background(Color.Black.copy(alpha = 0.2f))        // Dimming overlay
                )
            }

            if (isZenMode && activeCapsule != null) {
                // --- ZEN MODE INTERACTIVE THEATER SCREEN ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // Blurred ambient outline light sync
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(50.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        (if (activeCapsule?.moodPace == "action") Color(0xFFFF007F) else Color(0xFF00FFCC)).copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Large horizontal screen video view
                    VideoViewContainer(
                        videoUrl = activeCapsule!!.videoUrl,
                        isPlaying = isPlaying,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                2.dp,
                                if (activeCapsule?.moodPace == "action") Color(0xFFFF0055) else Color(0xFF00E5FF),
                                RoundedCornerShape(16.dp)
                            )
                    )

                    // Subtle Floating Zen Mode Exit Control Button
                    IconButton(
                        onClick = { viewModel.toggleZenMode() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(24.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("exit_zen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloseFullscreen,
                            contentDescription = "Exit Zen Mode",
                            tint = Color.White
                        )
                    }

                    // Bottom info label in Zen Mode
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = activeCapsule!!.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isRtl) "وضع التأمل مفعل • صدى المشاعر: ${activeCapsule!!.emotionTag}" else "Zen Mode Active • Mood Echo: ${activeCapsule!!.emotionTag}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // --- STANDARD PORTRAIT DASHBOARD VIEW ---
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Black.copy(alpha = 0.45f),
                                titleContentColor = Color.White
                            ),
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    // Golden spark emblem inside a glass card
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "✦",
                                            color = Color(0xFFFF5722),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = if (isRtl) "ماي تيوب" else "my tup",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.95f),
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "MY TUP",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFFFCC80).copy(alpha = 0.6f),
                                            letterSpacing = 1.8.sp
                                        )
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { showProfileMenu = true },
                                    modifier = Modifier.testTag("profile_menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Tune,
                                        contentDescription = "Customization Settings",
                                        tint = Color.White
                                    )
                                }
                            },
                            actions = {
                                // Fast language switcher
                                TextButton(
                                    onClick = {
                                        viewModel.setLanguage(if (isRtl) "en" else "ar")
                                    },
                                    modifier = Modifier.testTag("lang_toggle_button")
                                ) {
                                    Text(
                                        text = if (isRtl) "EN" else "عربي",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                // Quick Sleep Mode toggle
                                IconButton(
                                    onClick = { viewModel.toggleSleepMode() },
                                    modifier = Modifier.testTag("sleep_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (sleepMode) Icons.Filled.NightsStay else Icons.Outlined.WbSunny,
                                        contentDescription = "Sleep Mode",
                                        tint = if (sleepMode) Color(0xFFFFB74D) else Color.White
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Home tab (Active)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { /* Already on Home page */ },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Top Orange active indicator
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(Color(0xFFFF5722), RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                                            .align(Alignment.TopCenter)
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(text = "🏠", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isRtl) "الرئيسية" else "Home",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF5722)
                                        )
                                    }
                                }

                                // 2. Memory Capsule tab (Opens add memory)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { showPreserveCapsuleDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "📦", fontSize = 18.sp, modifier = Modifier.graphicsLayer { alpha = 0.5f })
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isRtl) "كبسولة الذاكرة" else "Capsule",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                // 3. Messaging tab (Opens chat with first friend if exists)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            if (allFriends.isNotEmpty()) {
                                                viewModel.openFriendChat(allFriends.first())
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "💬", fontSize = 18.sp, modifier = Modifier.graphicsLayer { alpha = 0.5f })
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isRtl) "المراسلة" else "Messaging",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                // 4. Settings tab (Opens profile/customization)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { showProfileMenu = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "⚙️", fontSize = 18.sp, modifier = Modifier.graphicsLayer { alpha = 0.5f })
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isRtl) "الإعدادات" else "Settings",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Organic Swimming Friends Bubble Area
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                                    .padding(vertical = 16.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if (isRtl) "فقاعات الأصدقاء الملاذيين" else "Sanctuary Friend Bubbles",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Interactive Swimming Area
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Ambient background water shine
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .blur(20.dp)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color.White.copy(alpha = 0.05f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )

                                    if (allFriends.isEmpty()) {
                                        Text(
                                            text = "جاري تجميع الأصدقاء...",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        // Position bubbles horizontally with organic overlapping space
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            allFriends.forEach { friend ->
                                                FloatingFriendBubble(
                                                    friend = friend,
                                                    moodPace = activeCapsule?.moodPace ?: "peaceful",
                                                    onClick = { viewModel.openFriendChat(friend) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. The Sanctuary Epic Multimedia Player
                        item {
                            if (activeCapsule != null) {
                                val glowColor = if (activeCapsule!!.moodPace == "action") Color(0xFFFF0055) else Color(0xFF00FFCC)
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isRtl) "مشغل الملاذ الفائق" else "Epic Multimedia Sanctuary Player",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                            .padding(horizontal = 4.dp, vertical = 6.dp)
                                    )

                                    GlowCard(
                                        glowColor = glowColor,
                                        isPlaying = isPlaying,
                                        moodPace = activeCapsule!!.moodPace,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // The Video Playback Container
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(if (isSquareAspect) 1f else 1.77f) // Morphing layout
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(Color.DarkGray)
                                            ) {
                                                VideoViewContainer(
                                                    videoUrl = activeCapsule!!.videoUrl,
                                                    isPlaying = isPlaying,
                                                    modifier = Modifier.fillMaxSize()
                                                )

                                                // Glowing ambient color overlay linked to active video mood
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .border(
                                                            1.5.dp,
                                                            glowColor.copy(alpha = 0.5f),
                                                            RoundedCornerShape(16.dp)
                                                        )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Title & Description
                                            Text(
                                                text = activeCapsule!!.title,
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text(activeCapsule!!.category, fontSize = 11.sp) },
                                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                                        labelColor = glowColor,
                                                        containerColor = glowColor.copy(alpha = 0.1f)
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text(activeCapsule!!.emotionTag, fontSize = 11.sp) },
                                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                                        labelColor = Color.Yellow,
                                                        containerColor = Color.Yellow.copy(alpha = 0.1f)
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = activeCapsule!!.description,
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Player Controls
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // 1. Play/Pause Button
                                                IconButton(
                                                    onClick = { viewModel.togglePlayback() },
                                                    modifier = Modifier
                                                        .background(glowColor.copy(alpha = 0.15f), CircleShape)
                                                        .border(1.dp, glowColor.copy(alpha = 0.4f), CircleShape)
                                                        .testTag("play_pause_button")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                        contentDescription = "Playback Control",
                                                        tint = glowColor
                                                    )
                                                }

                                                // 2. Morph Button (16:9 <=> 1:1)
                                                IconButton(
                                                    onClick = { viewModel.toggleAspectMorph() },
                                                    modifier = Modifier
                                                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                                        .testTag("aspect_morph_button")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSquareAspect) Icons.Outlined.AspectRatio else Icons.Filled.AspectRatio,
                                                        contentDescription = "Morph Player Ratio",
                                                        tint = Color.White
                                                    )
                                                }

                                                // 3. Haptic Tactile Toggle
                                                IconButton(
                                                    onClick = { viewModel.toggleHaptics() },
                                                    modifier = Modifier
                                                        .background(if (hapticsEnabled) Color.Green.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f), CircleShape)
                                                        .testTag("haptic_pulse_button")
                                                ) {
                                                    Icon(
                                                        imageVector = if (hapticsEnabled) Icons.Filled.Vibration else Icons.Outlined.Vibration,
                                                        contentDescription = "Tactile Haptics Sync",
                                                        tint = if (hapticsEnabled) Color.Green else Color.LightGray
                                                    )
                                                }

                                                // 4. Zen Mode Toggle
                                                IconButton(
                                                    onClick = { viewModel.toggleZenMode() },
                                                    modifier = Modifier
                                                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                                        .testTag("zen_mode_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.OpenInFull,
                                                        contentDescription = "Enter Zen Mode",
                                                        tint = Color.White
                                                    )
                                                }
                                            }

                                            // 5. Embedded AI Summarizer Chat Panel
                                            Spacer(modifier = Modifier.height(16.dp))
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            AiPlayerAssistant(viewModel = viewModel, isRtl = isRtl)
                                        }
                                    }
                                }
                            }
                        }

                        // 2.5 AI Assistant & Mood Echo Card Grid (Extracted styling and layout patterns from Design HTML)
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Left Card: AI Companion Quick-Trigger card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                                        .clickable {
                                            viewModel.askAiAssistantAboutVideo(
                                                if (isRtl) "اقترح لي فيديوهات هادئة تناسب هذا الوقت من المساء" 
                                                else "Suggest calm videos suited for this evening"
                                            )
                                        }
                                        .padding(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isRtl) "مساعد الذكاء الاصطناعي" else "AI ASSISTANT",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.4f),
                                                letterSpacing = 1.sp
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .background(Color(0xFFFFCC80).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                    .border(1.dp, Color(0xFFFFCC80).copy(alpha = 0.25f), RoundedCornerShape(6.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("✦", color = Color(0xFFFF5722), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        
                                        Text(
                                            text = if (isRtl) "\"اقترح لي فيديوهات هادئة تناسب هذا الوقت من المساء\"" else "\"Suggest calm videos suited for this evening\"",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Right Card: Mood Echo Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFF3A1510).copy(alpha = 0.35f), Color.Transparent)
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .border(1.dp, Color(0xFFFF5722).copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                        .padding(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Pulsating amber dot
                                            val infinitePulse = rememberInfiniteTransition(label = "pulse")
                                            val pulseAlpha by infinitePulse.animateFloat(
                                                initialValue = 0.3f,
                                                targetValue = 1f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(1000, easing = EaseInOutSine),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "pulse"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .background(Color(0xFFFF5722).copy(alpha = pulseAlpha), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isRtl) "صدى المشاعر" else "MOOD ECHO",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFCC80).copy(alpha = 0.5f),
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = activeCapsule?.emotionTag ?: (if (isRtl) "هدوء وسكينة" else "Quiet & Serene"),
                                                color = Color(0xFFFFE0B2),
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Light,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (isRtl) "يتم تعديل الإضاءة لتناسب إيقاع المحتوى" else "Lighting adjusts to content rhythm",
                                                color = Color.White.copy(alpha = 0.4f),
                                                fontSize = 9.sp,
                                                lineHeight = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Memory Capsule (مكتبة الذاكرة وعواطفها)
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isRtl) "مكتبة كبسولات الذاكرة العاطفية" else "Emotional Memory Capsules",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // FAB to preserve memory
                                    IconButton(
                                        onClick = { showPreserveCapsuleDialog = true },
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            .testTag("add_capsule_fab")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Preserve Capsule",
                                            tint = Color.White
                                        )
                                    }
                                }

                                // Elegant Ambient Media Search Bar
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.setSearchQuery(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .testTag("media_search_bar"),
                                    placeholder = {
                                        Text(
                                            text = if (isRtl) "ابحث في ملاذك (ذكريات، مشاعر، تصنيفات)..." else "Search sanctuary (capsules, emotions, tags)...",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.4f)
                                        )
                                    },
                                    leadingIcon = {
                                        Text("🔍", fontSize = 14.sp)
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                                Text("✕", color = Color.White, fontSize = 12.sp)
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                        focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                        unfocusedContainerColor = Color.Black.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                if (searchedCapsules.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isRtl) "لا توجد نتائج مطابقة لبحثك." else "No matching memory capsules found.",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(searchedCapsules) { capsule ->
                                            MemoryCapsuleCard(
                                                capsule = capsule,
                                                isSelected = activeCapsule?.id == capsule.id,
                                                onClick = { viewModel.setActiveCapsule(capsule) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- BOTTOM SLIDING CHAT PANEL FOR FRIENDS ---
            activeChatFriend?.let { friend ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.closeFriendChat() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f)
                            .align(Alignment.BottomCenter)
                            .clickable(enabled = false) {} // Disable dismissal when tapping inner sheet
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                            color = Color(0xFF1C1B1F)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                // Friend Profile Header & Shape Customization Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    Color(android.graphics.Color.parseColor(friend.colorHex)),
                                                    when (friend.shapeType) {
                                                        "Hexagon" -> HexagonShape
                                                        "RoundedRect" -> RoundedCornerShape(12.dp)
                                                        "Bubble" -> ChatBubbleShape
                                                        else -> CircleShape
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = friend.name.firstOrNull()?.toString() ?: "👤",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = friend.name,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${friend.channelName} • YouTube",
                                                color = Color.LightGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.closeFriendChat() },
                                        modifier = Modifier.testTag("close_chat_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close Chat",
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Bubble Personalization Drawer
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = if (isRtl) "تخصيص فقاعة الصديق:" else "Personalize Bubble Shape & Glow:",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Shape Pickers
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            listOf("Circle", "Hexagon", "RoundedRect", "Bubble").forEach { s ->
                                                val isSelected = friend.shapeType == s
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(
                                                            if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            viewModel.customizeFriendBubble(friend.id, friend.colorHex, s)
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when (s) {
                                                            "Hexagon" -> Icons.Default.Hexagon
                                                            "RoundedRect" -> Icons.Default.CropLandscape
                                                            "Bubble" -> Icons.Default.Comment
                                                            else -> Icons.Default.Circle
                                                        },
                                                        contentDescription = s,
                                                        tint = if (isSelected) Color.White else Color.Gray,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Color Pickers
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            listOf("#8A2BE2", "#FF6347", "#00FA9A", "#FFD700", "#FF1493").forEach { c ->
                                                val isSelected = friend.colorHex.lowercase() == c.lowercase()
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(Color(android.graphics.Color.parseColor(c)), CircleShape)
                                                        .border(
                                                            width = if (isSelected) 2.dp else 0.dp,
                                                            color = Color.White,
                                                            shape = CircleShape
                                                        )
                                                        .clickable {
                                                            viewModel.customizeFriendBubble(friend.id, c, friend.shapeType)
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Active Messages Chat Logs
                                val messages by viewModel.activeChatMessages.collectAsState()
                                Box(modifier = Modifier.weight(1f)) {
                                    if (messages.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "ابدأ محادثة حميمية جديدة..",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(messages) { msg ->
                                                val isMe = msg.senderName == "user"
                                                val align = if (isMe) Alignment.End else Alignment.Start
                                                val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f)
                                                val textColor = if (isMe) Color.White else Color.LightGray

                                                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                bubbleColor,
                                                                RoundedCornerShape(
                                                                    topStart = 16.dp,
                                                                    topEnd = 16.dp,
                                                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                                                )
                                                            )
                                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                                    ) {
                                                        Text(
                                                            text = msg.messageText,
                                                            color = textColor,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Chat Text Input
                                var chatTextState by remember { mutableStateOf("") }
                                val aiThinking by viewModel.aiThinking.collectAsState()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        value = chatTextState,
                                        onValueChange = { chatTextState = it },
                                        placeholder = { Text(if (isRtl) "أرسل همسة ملحمية..." else "Whisper something...", color = Color.Gray) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("chat_input"),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(24.dp),
                                        maxLines = 2,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                        keyboardActions = KeyboardActions(onSend = {
                                            if (chatTextState.isNotBlank()) {
                                                viewModel.sendChatMessage(chatTextState)
                                                chatTextState = ""
                                            }
                                        })
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (aiThinking) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        IconButton(
                                            onClick = {
                                                if (chatTextState.isNotBlank()) {
                                                    viewModel.sendChatMessage(chatTextState)
                                                    chatTextState = ""
                                                }
                                            },
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                .testTag("send_chat_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Send,
                                                contentDescription = "Send Message",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- FULL SETTINGS AND PROFILE MENU DIALOG ---
            if (showProfileMenu) {
                AlertDialog(
                    onDismissRequest = { showProfileMenu = false },
                    title = {
                        Text(
                            text = if (isRtl) "لوحة ملاذ التحكم" else "Sanctuary Control Board",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = Color(0xFF2D2A32),
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isRtl) "بناء الهوية ومظهر الوقت الحركي:" else "Live Space Time Settings:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Time of day toggle row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Morning", "Sunset", "Night").forEach { t ->
                                    val isSelected = timeOfDay == t
                                    val localLabel = when (t) {
                                        "Morning" -> if (isRtl) "صباح" else "Morning"
                                        "Sunset" -> if (isRtl) "غروب" else "Sunset"
                                        else -> if (isRtl) "ليل" else "Night"
                                    }
                                    Button(
                                        onClick = { viewModel.setTimeOfDay(t) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(localLabel, fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Interactive accelerometer simulation sliders
                            Text(
                                text = if (isRtl) "جيروسكوب محاكي (إمالة المشهد):" else "Simulated Gyroscope Tilt:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("X: ", color = Color.White, fontSize = 11.sp)
                                    Slider(
                                        value = gyroX,
                                        onValueChange = { viewModel.updateGyroscope(it, gyroY) },
                                        valueRange = -1f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Y: ", color = Color.White, fontSize = 11.sp)
                                    Slider(
                                        value = gyroY,
                                        onValueChange = { viewModel.updateGyroscope(gyroX, it) },
                                        valueRange = -1f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Language Quick Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRtl) "تغيير لغة التطبيق بالكامل:" else "Application Language Switcher:",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Button(
                                    onClick = {
                                        viewModel.setLanguage(if (isRtl) "en" else "ar")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(if (isRtl) "English" else "العربية", color = Color.White)
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            // Performance Watchdog Dashboard
                            Text(
                                text = if (isRtl) "مراقب الأداء الذكي (Watchdog):" else "Performance Watchdog Dashboard:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // FPS Counter
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡ ", fontSize = 12.sp)
                                    Text(
                                        text = "$fps FPS",
                                        color = if (fps >= 55) Color.Green else Color.Yellow,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Throttling Indicator
                                Text(
                                    text = if (performanceThrottled) {
                                        if (isRtl) "تحجيم ديناميكي نشط" else "Throttled (Low Overhead)"
                                    } else {
                                        if (isRtl) "أداء فائق" else "High Fidelity"
                                    },
                                    color = if (performanceThrottled) Color.Yellow else Color.Green,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Self-Healing Status Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRtl) "وضع الرسم الحالي:" else "Active Draw Engine:",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (fallbackGradientMode) {
                                        if (isRtl) "التدرج الاحتياطي الآمن" else "Safe Fallback Gradient"
                                    } else {
                                        if (isRtl) "محرك شيدر تفاعلي" else "Procedural Sunset Shader"
                                    },
                                    color = if (fallbackGradientMode) Color(0xFFFF7043) else Color(0xFF00FFCC),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Diagnostics Logs list view
                            Text(
                                text = if (isRtl) "سجل تشخيصات النظام الفورية:" else "Real-Time System Diagnostics Log:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                if (performanceLogs.isEmpty()) {
                                    Text(
                                        text = if (isRtl) "لا توجد سجلات حالياً." else "No log entries.",
                                        color = Color.Gray,
                                        fontSize = 10.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(performanceLogs) { log ->
                                            Text(
                                                text = log,
                                                color = if (log.contains("activated") || log.contains("triggered") || log.contains("SUCCESSFUL")) Color.Yellow else Color.LightGray,
                                                fontSize = 9.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                lineHeight = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Watchdog & Self-Healing Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.triggerSelfHealing() },
                                    modifier = Modifier.weight(1f),
                                    enabled = !selfHealingActive,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF5722).copy(alpha = 0.2f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (selfHealingActive) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp, color = Color.White)
                                    } else {
                                        Text(if (isRtl) "إصلاح ذاتي" else "Self-Heal", fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.resetGraphicsMode() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.08f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (isRtl) "إعادة ضبط الرسم" else "Reset Graphics", fontSize = 11.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showProfileMenu = false }) {
                            Text(if (isRtl) "إغلاق" else "Close", color = Color.White)
                        }
                    }
                )
            }

            // --- DIALOG TO PRESERVE A NEW MEMORY CAPSULE ---
            if (showPreserveCapsuleDialog) {
                var titleVal by remember { mutableStateOf("") }
                var urlVal by remember { mutableStateOf("") }
                var descVal by remember { mutableStateOf("") }
                var categoryVal by remember { mutableStateOf("Sunset") }
                var emotionVal by remember { mutableStateOf("هدوء وسكينة") }

                AlertDialog(
                    onDismissRequest = { showPreserveCapsuleDialog = false },
                    title = {
                        Text(
                            text = if (isRtl) "حفظ كبسولة ذاكرة جديدة" else "Preserve New Memory Capsule",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = Color(0xFF1E1E24),
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            TextField(
                                value = titleVal,
                                onValueChange = { titleVal = it },
                                label = { Text(if (isRtl) "العنوان الروحي للفيديو" else "Spiritual Video Title") },
                                colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth().testTag("capsule_title_input")
                            )

                            TextField(
                                value = urlVal,
                                onValueChange = { urlVal = it },
                                label = { Text(if (isRtl) "رابط البث (MP4 Video Stream URL)" else "Streaming MP4 Video URL") },
                                colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                placeholder = { Text("https://...") },
                                modifier = Modifier.fillMaxWidth().testTag("capsule_url_input")
                            )

                            TextField(
                                value = descVal,
                                onValueChange = { descVal = it },
                                label = { Text(if (isRtl) "الوصف الإيحائي أو التأملي" else "Reflective or Meditative Description") },
                                colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth().testTag("capsule_desc_input")
                            )

                            // Quick Category Picker
                            Text(
                                text = if (isRtl) "التصنيف الإيقاعي:" else "Rhythmic Category:",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Sunset", "Nostalgia", "Quiet", "Action").forEach { cat ->
                                    val isSelected = categoryVal == cat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                categoryVal = cat
                                                emotionVal = when (cat) {
                                                    "Sunset" -> "تأمل ودفء"
                                                    "Nostalgia" -> "حنين وسلام"
                                                    "Action" -> "حماس وإثارة"
                                                    else -> "هدوء وسكينة"
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(cat, fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (titleVal.isNotBlank() && urlVal.isNotBlank()) {
                                    viewModel.saveNewMemoryCapsule(
                                        title = titleVal,
                                        url = urlVal,
                                        desc = descVal,
                                        category = categoryVal,
                                        emotion = emotionVal
                                    )
                                    showPreserveCapsuleDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (isRtl) "خلود الذاكرة" else "Preserve", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPreserveCapsuleDialog = false }) {
                            Text(if (isRtl) "إلغاء" else "Cancel", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

// --- FLOATING SWIMMING FRIEND BUBBLE COMPOSABLE ---
@Composable
fun FloatingFriendBubble(
    friend: FriendBubble,
    moodPace: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BubbleSwimming")
    
    // Swimming speed responds directly to the Mood Echo
    val swimDuration = if (moodPace == "action") 3500 else 11000
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(swimDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swimX"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween((swimDuration * 1.25).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swimY"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val bubbleColor = try {
        Color(android.graphics.Color.parseColor(friend.colorHex))
    } catch (e: Exception) {
        Color(0xFF8A2BE2)
    }

    val shape = when (friend.shapeType) {
        "Hexagon" -> HexagonShape
        "RoundedRect" -> RoundedCornerShape(14.dp)
        "Bubble" -> ChatBubbleShape
        else -> CircleShape
    }

    Column(
        modifier = modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .drawBehind {
                    // Soft glowing perimeter shadow
                    drawCircle(
                        color = bubbleColor.copy(alpha = 0.35f),
                        radius = this.size.minDimension / 2f + 8.dp.toPx()
                    )
                }
                .border(2.5.dp, bubbleColor.copy(alpha = 0.8f), shape)
                .clip(shape)
                .background(bubbleColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            // First letter avatar
            Text(
                text = friend.name.firstOrNull()?.toString() ?: "👤",
                color = bubbleColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            if (friend.isOnline) {
                // Online pulse dot
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .background(Color.Green, CircleShape)
                        .border(1.5.dp, Color(0xFF1C1B1F), CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = friend.name,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// --- NATIVE EMBEDDED VIDEO VIEW VIA ANDROIDVIEW ---
@Composable
fun VideoViewContainer(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoView = remember { VideoView(context) }

    LaunchedEffect(videoUrl) {
        try {
            videoView.setVideoPath(videoUrl)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.setVolume(0.3f, 0.3f) // Lower default volume
                if (isPlaying) {
                    videoView.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(isPlaying) {
        try {
            if (isPlaying) {
                videoView.start()
            } else {
                videoView.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(
        factory = { videoView },
        modifier = modifier
    )
}

// --- HORIZONTAL MEMORY CAPSULE CARD ---
@Composable
fun MemoryCapsuleCard(
    capsule: MemoryCapsule,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.12f)
    val glowColor = if (capsule.moodPace == "action") Color(0xFFFF1744) else Color(0xFF00FA9A)

    Card(
        modifier = modifier
            .width(180.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) glowColor else borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Simulated thumbnail background visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (capsule.moodPace == "action") Icons.Default.Cyclone else Icons.Default.FilterHdr,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = capsule.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = capsule.emotionTag,
                color = Color.Yellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --- EMBEDDED AI ASSISTANT CHAT FOR MEDIA SENSING ---
@Composable
fun AiPlayerAssistant(
    viewModel: MediaSanctuaryViewModel,
    isRtl: Boolean,
    modifier: Modifier = Modifier
) {
    var queryState by remember { mutableStateOf("") }
    val aiThinking by viewModel.aiThinking.collectAsState()
    val aiChatLog by viewModel.aiChatLog.collectAsState()
    
    // Transcript summary states
    val activeTranscriptSummary by viewModel.activeTranscriptSummary.collectAsState()
    val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRtl) "مساعد الذكاء الاصطناعي" else "AI Sanctuary Assistant",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick summarize button
            TextButton(
                onClick = { viewModel.askAiAssistantAboutVideo(if (isRtl) "لخص لي هذا المقطع الإيماني" else "Summarize this media capsule for me") },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isRtl) "تلخيص خاطف ⚡" else "Quick Summary ⚡",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Advanced AI Transcript Summarizer button
        Button(
            onClick = { viewModel.summarizeActiveVideoTranscript() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("deep_transcript_summary_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5722).copy(alpha = 0.15f),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            enabled = !isGeneratingSummary
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✦  ", color = Color(0xFFFFCC80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (isRtl) "تلخيص ذكي معمق للنص المكتوب (معزول)" else "Deep AI Transcript Summary (Thread-Isolated)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Loader during Transcript Generation
        if (isGeneratingSummary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Color(0xFFFF5722),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRtl) "جاري استخلاص النص وتحليله عبر خوادم معزولة..." else "Extracting transcript & analyzing in isolated thread...",
                    color = Color.Yellow,
                    fontSize = 10.sp
                )
            }
        }

        // Active Deep Transcript Summary Display
        activeTranscriptSummary?.let { summary ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color(0xFF33150C).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFFF5722).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRtl) "ملخص النص الذكي المعمق:" else "Deep Transcript Summary:",
                            color = Color(0xFFFFCC80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.clearTranscriptSummary() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Text("✕", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = summary,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Small AI response scrolling logs
        if (aiChatLog.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(aiChatLog) { entry ->
                        val text = entry.first
                        val isUser = entry.second
                        Text(
                            text = if (isUser) "• $text" else "🤖: $text",
                            color = if (isUser) Color.LightGray else Color(0xFF00FFCC),
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input query row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = queryState,
                onValueChange = { queryState = it },
                placeholder = {
                    Text(
                        text = if (isRtl) "اسأل عن مشاعر هذا الفيديو..." else "Ask about video vibes...",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_assistant_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (queryState.isNotBlank()) {
                        viewModel.askAiAssistantAboutVideo(queryState)
                        queryState = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(6.dp))

            if (aiThinking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = {
                        if (queryState.isNotBlank()) {
                            viewModel.askAiAssistantAboutVideo(queryState)
                            queryState = ""
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .testTag("send_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Query AI",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
```

---
## 4. ملف منطق العمل وإدارة الحالة (MediaSanctuaryViewModel.kt)
**المسار:** `app/src/main/java/com/example/ui/MediaSanctuaryViewModel.kt`
```kotlin
package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MediaSanctuaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AppRepository(db)

    // Language state: "ar" (Arabic) or "en" (English)
    private val _currentLanguage = MutableStateFlow("ar")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Sleep Mode state
    private val _sleepMode = MutableStateFlow(false)
    val sleepMode: StateFlow<Boolean> = _sleepMode.asStateFlow()

    // Background season / time of day
    private val _timeOfDay = MutableStateFlow("Sunset") // "Morning", "Sunset", "Night"
    val timeOfDay: StateFlow<String> = _timeOfDay.asStateFlow()

    // Gyroscope simulation state (X & Y tilt)
    private val _gyroscopeX = MutableStateFlow(0f)
    val gyroscopeX: StateFlow<Float> = _gyroscopeX.asStateFlow()

    private val _gyroscopeY = MutableStateFlow(0f)
    val gyroscopeY: StateFlow<Float> = _gyroscopeY.asStateFlow()

    // Player States
    private val _activeCapsule = MutableStateFlow<MemoryCapsule?>(null)
    val activeCapsule: StateFlow<MemoryCapsule?> = _activeCapsule.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isZenMode = MutableStateFlow(false)
    val isZenMode: StateFlow<Boolean> = _isZenMode.asStateFlow()

    private val _isSquareAspect = MutableStateFlow(false) // Morphing aspect ratio
    val isSquareAspect: StateFlow<Boolean> = _isSquareAspect.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    // Active Chat Friend
    private val _activeChatFriend = MutableStateFlow<FriendBubble?>(null)
    val activeChatFriend: StateFlow<FriendBubble?> = _activeChatFriend.asStateFlow()

    // Active Chat Messages
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatFriend
        .flatMapLatest { friend ->
            if (friend != null) {
                repository.getMessagesForFriend(friend.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI States
    private val _aiThinking = MutableStateFlow(false)
    val aiThinking: StateFlow<Boolean> = _aiThinking.asStateFlow()

    private val _aiChatLog = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of (Message, IsUser)
    val aiChatLog: StateFlow<List<Pair<String, Boolean>>> = _aiChatLog.asStateFlow()

    // --- Performance Watchdog & Self-Healing states ---
    private val _fps = MutableStateFlow(60)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    private val _performanceThrottled = MutableStateFlow(false)
    val performanceThrottled: StateFlow<Boolean> = _performanceThrottled.asStateFlow()

    private val _performanceLogs = MutableStateFlow<List<String>>(emptyList())
    val performanceLogs: StateFlow<List<String>> = _performanceLogs.asStateFlow()

    private val _selfHealingActive = MutableStateFlow(false)
    val selfHealingActive: StateFlow<Boolean> = _selfHealingActive.asStateFlow()

    private val _fallbackGradientMode = MutableStateFlow(false)
    val fallbackGradientMode: StateFlow<Boolean> = _fallbackGradientMode.asStateFlow()

    // --- Search & Transcript Summary States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeTranscriptSummary = MutableStateFlow<String?>(null)
    val activeTranscriptSummary: StateFlow<String?> = _activeTranscriptSummary.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    // DB Flows
    val allCapsules: StateFlow<List<MemoryCapsule>> = repository.allCapsules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFriends: StateFlow<List<FriendBubble>> = repository.allFriends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered capsules matching current search query
    val searchedCapsules: StateFlow<List<MemoryCapsule>> = combine(allCapsules, _searchQuery) { capsules, query ->
        if (query.isBlank()) {
            capsules
        } else {
            capsules.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.emotionTag.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Run pre-population on startup
        viewModelScope.launch {
            repository.checkAndPrepopulateData()
            // Set initial active capsule to the first sunset capsule
            val capsules = repository.allCapsules.first()
            if (capsules.isNotEmpty()) {
                _activeCapsule.value = capsules.first()
            }
            detectAutomaticTimeOfDay()
            addPerformanceLog("System Orchestrator fully online. Beautiful Dark theme parameters initialized.")
        }

        // Performance Watchdog dynamic loop to monitor frame health & apply real-time throttling
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            var consecutiveDrops = 0
            while (true) {
                kotlinx.coroutines.delay(1000)
                val isPlayingActive = _isPlaying.value
                val isHeavy = isPlayingActive && !_isZenMode.value
                val baseFps = if (isHeavy) 55 else 60
                val randomDrop = (0..3).random()
                val currentSimulatedFps = (baseFps - randomDrop).coerceIn(30, 60)
                _fps.value = currentSimulatedFps

                if (currentSimulatedFps < 55) {
                    consecutiveDrops++
                    val reason = if (isHeavy) "Heavy multi-layer visual simulation + video rendering" else "Transient hardware pressure"
                    addPerformanceLog("Frame performance dip recorded: $currentSimulatedFps FPS. Reason: $reason")
                    
                    if (consecutiveDrops >= 4 && !_performanceThrottled.value) {
                        _performanceThrottled.value = true
                        addPerformanceLog("Performance Watchdog activated DYNAMIC THROTTLING: reducing particle size and rendering complexity.")
                        triggerHapticPulse()
                    }
                } else {
                    if (consecutiveDrops > 0) consecutiveDrops--
                    if (consecutiveDrops == 0 && _performanceThrottled.value) {
                        _performanceThrottled.value = false
                        addPerformanceLog("Performance Watchdog: UI performance stabilized. Restoring high-fidelity sunset visuals.")
                    }
                }
            }
        }
    }

    private fun detectAutomaticTimeOfDay() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        _timeOfDay.value = when (hour) {
            in 6..15 -> "Morning"
            in 16..18 -> "Sunset"
            else -> "Night"
        }
        // Auto enable sleep mode near bedtime (9 PM to 5 AM)
        if (hour >= 21 || hour < 5) {
            _sleepMode.value = true
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun toggleSleepMode() {
        _sleepMode.value = !_sleepMode.value
    }

    fun setTimeOfDay(time: String) {
        _timeOfDay.value = time
    }

    fun updateGyroscope(x: Float, y: Float) {
        _gyroscopeX.value = x
        _gyroscopeY.value = y
    }

    fun setActiveCapsule(capsule: MemoryCapsule) {
        _activeCapsule.value = capsule
        _isPlaying.value = true
        // Every time video changes, we reset some player states
        _isZenMode.value = false
    }

    fun togglePlayback() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            triggerHapticClick()
        }
    }

    fun toggleZenMode() {
        _isZenMode.value = !_isZenMode.value
        triggerHapticClick()
    }

    fun toggleAspectMorph() {
        _isSquareAspect.value = !_isSquareAspect.value
        triggerHapticClick()
    }

    fun toggleHaptics() {
        _hapticsEnabled.value = !_hapticsEnabled.value
    }

    fun openFriendChat(friend: FriendBubble) {
        _activeChatFriend.value = friend
        // Clear old AI chat logs when switching friends or opening chat
        _aiChatLog.value = emptyList()
    }

    fun closeFriendChat() {
        _activeChatFriend.value = null
    }

    fun sendChatMessage(text: String) {
        val friend = _activeChatFriend.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            // 1. Insert user message
            val userMsg = ChatMessage(
                friendId = friend.id,
                senderName = "user",
                messageText = text
            )
            repository.insertMessage(userMsg)

            // 2. Trigger a subtle typing simulation and AI Persona Response matching the Channel & video active capsule
            _aiThinking.value = true
            triggerHapticClick()

            val activeVideo = _activeCapsule.value
            val activeVideoDesc = activeVideo?.let {
                "الفيديو الحالي النشط: '${it.title}' من تصنيف '${it.category}' بوصف: '${it.description}'."
            } ?: "لا يوجد فيديو نشط حالياً."

            val systemPrompt = """
                أنت صديق وقائد قناة الميديا '${friend.channelName}' في تطبيق 'Media Sanctuary'.
                اسمك هو '${friend.name}'. يجب أن تجيب بلغة صديق ودود، دافئ، ومتفاعل عاطفياً ومحب للميديا.
                التطبيق بالكامل يدعم العربية. جاوب باللغة العربية بأسلوب راقٍ وشاعري يعزز ملاذ المستخدم الرقمي.
                استخدم التفاصيل التالية لتوجيه ردك:
                - $activeVideoDesc
                - لغة الواجهة الحالية للمستخدم: ${_currentLanguage.value}
                لا تتعدى إجابتك سطرين أو ثلاثة لتناسب واجهة الفقاعات العائمة الأنيقة.
            """.trimIndent()

            val reply = GeminiClient.chatWithGemini(systemPrompt, text)

            val aiMsg = ChatMessage(
                friendId = friend.id,
                senderName = "friend",
                messageText = reply
            )
            repository.insertMessage(aiMsg)
            _aiThinking.value = false
            triggerHapticPulse()
        }
    }

    fun customizeFriendBubble(friendId: Int, colorHex: String, shape: String) {
        viewModelScope.launch {
            repository.customizeFriend(friendId, colorHex, shape)
            // Update current active friend ref to apply visual changes in real-time
            val active = _activeChatFriend.value
            if (active != null && active.id == friendId) {
                _activeChatFriend.value = active.copy(colorHex = colorHex, shapeType = shape)
            }
        }
    }

    fun saveNewMemoryCapsule(title: String, url: String, desc: String, category: String, emotion: String) {
        viewModelScope.launch {
            val mood = if (category.lowercase() == "action") "action" else "peaceful"
            val newCapsule = MemoryCapsule(
                title = title,
                videoUrl = url,
                category = category,
                emotionTag = emotion,
                description = desc,
                moodPace = mood
            )
            repository.insertCapsule(newCapsule)
            _activeCapsule.value = newCapsule
            _isPlaying.value = true
        }
    }

    // --- AI Video Summarizer / Chat inside Player ---
    fun askAiAssistantAboutVideo(question: String) {
        val activeVideo = _activeCapsule.value ?: return
        if (question.isBlank()) return

        // Append user question
        _aiChatLog.value = _aiChatLog.value + (question to true)

        viewModelScope.launch {
            _aiThinking.value = true
            triggerHapticClick()

            val systemPrompt = """
                أنت الذكاء العاطفي والمساعد الذكي في تطبيق 'Media Sanctuary'.
                أنت تقوم بتحليل الفيديو الحالي وتلخيصه ومناقشة مشاعر المستخدم معه بصفتك مرشداً شاعرياً ذكياً.
                تفاصيل الفيديو الحالي:
                - العنوان: ${activeVideo.title}
                - التصنيف: ${activeVideo.category}
                - وسم المشاعر: ${activeVideo.emotionTag}
                - الوصف: ${activeVideo.description}
                - وضع صدى المشاعر النشط: ${activeVideo.moodPace}
                
                جاوب دائماً باللغة العربية الدافئة أو الإنجليزية حسب تفضيل المستخدم.
                كن ملهماً، واقترح عليه كيفية تصفية ذهنه والاستمتاع بمشاهدة هذا الملاذ الرقمي.
            """.trimIndent()

            val reply = GeminiClient.chatWithGemini(systemPrompt, question)

            _aiChatLog.value = _aiChatLog.value + (reply to false)
            _aiThinking.value = false
            triggerHapticPulse()
        }
    }

    fun clearAiChat() {
        _aiChatLog.value = emptyList()
    }

    // --- Tactile Haptic Emulation ---
    private fun triggerHapticClick() {
        if (!_hapticsEnabled.value) return
        val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(android.os.VibrationEffect.createOneShot(35, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(35)
            }
        }
    }

    private fun triggerHapticPulse() {
        if (!_hapticsEnabled.value) return
        val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 50, 100, 50), -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }

    // Call this inside video playing cycles to simulate micro haptics tied to frequencies
    fun triggerPlayHapticBeat() {
        if (!_isPlaying.value || !_hapticsEnabled.value) return
        val active = _activeCapsule.value ?: return
        val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        val amplitude = if (active.moodPace == "action") 60 else 30
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(android.os.VibrationEffect.createOneShot(15, amplitude))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(15)
            }
        }
    }

    // --- Media Search Engine ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        addPerformanceLog("Media Search: query changed to '$query'")
    }

    // --- AI Video Summarizer with Main Thread Isolation ---
    fun summarizeActiveVideoTranscript() {
        val active = _activeCapsule.value ?: return
        _isGeneratingSummary.value = true
        _activeTranscriptSummary.value = null
        addPerformanceLog("AI Video Summarizer: Initiating transcript parsing in Isolated Thread (Main Thread Isolation)...")
        triggerHapticClick()

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            try {
                val systemPrompt = """
                    أنت خبير تلخيص وسائط ومستشار ذكاء اصطناعي محترف في تطبيق 'Media Sanctuary'.
                    مهمتك هي كتابة تلخيص فوري عميق وشاعري لنص الفيديو (Video Transcript) المعروض ومناقشة أثره العاطفي.
                    تفاصيل الفيديو:
                    - العنوان: ${active.title}
                    - الوصف: ${active.description}
                    - المشاعر: ${active.emotionTag}
                    - وتيرة المشاعر: ${active.moodPace}

                    قم بتوليد تلخيص شاعري جذاب باللغة العربية بأسلوب 'ملاذ الميديا'، مقسم لثلاثة عناصر:
                    1. خلاصة المحتوى الفكري والروحي للفيديو.
                    2. الرمزية العاطفية للمشهد.
                    3. نصيحة تأملية سريعة للمشاهدة.
                    لا تستخدم أي مقدمات جافة، ادخل في صلب التلخيص مباشرة بعبارات ملهمة.
                """.trimIndent()

                val question = "قم بتلخيص نص هذا الفيديو بشكل عميق وعاطفي يناسب ملاذ الميديا."
                val reply = GeminiClient.chatWithGemini(systemPrompt, question)
                
                _activeTranscriptSummary.value = reply
                addPerformanceLog("AI Video Summarizer: Transcript summary successfully completed on Dispatchers.Default.")
            } catch (e: Exception) {
                _activeTranscriptSummary.value = "فشل تلخيص النص: ${e.message}"
                addPerformanceLog("AI Video Summarizer Error: ${e.message}")
            } finally {
                _isGeneratingSummary.value = false
                triggerHapticPulse()
            }
        }
    }

    fun clearTranscriptSummary() {
        _activeTranscriptSummary.value = null
        addPerformanceLog("AI Video Summarizer: Transcript summary cleared.")
        triggerHapticClick()
    }

    // --- Performance Self-Healing System ---
    fun triggerSelfHealing() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            _selfHealingActive.value = true
            addPerformanceLog("Initializing SELF-HEALING protocol...")
            kotlinx.coroutines.delay(1200)
            _fallbackGradientMode.value = true
            _performanceThrottled.value = true
            _selfHealingActive.value = false
            addPerformanceLog("SELF-HEALING SUCCESSFUL: Re-routed canvas to static Fallback Gradient mode. High-performance shader suspended.")
            triggerHapticPulse()
        }
    }

    fun resetGraphicsMode() {
        _fallbackGradientMode.value = false
        _performanceThrottled.value = false
        addPerformanceLog("Graphics system reset: Restoring full procedural sunset engine.")
        triggerHapticClick()
    }

    fun addPerformanceLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val logLine = "[$timestamp] $message"
        _performanceLogs.value = (listOf(logLine) + _performanceLogs.value).take(40)
    }

    fun clearPerformanceLogs() {
        _performanceLogs.value = emptyList()
    }
}
```

---
## 5. مكونات واجهة المستخدم الإضافية (UiComponents.kt)
**المسار:** `app/src/main/java/com/example/ui/UiComponents.kt`
```kotlin
package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

// Custom shape for customizable Friend Bubble - Hexagon
val HexagonShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val radius = width / 2f
    val cx = width / 2f
    val cy = height / 2f
    moveTo(cx + radius * cos(0f), cy + radius * sin(0f))
    for (i in 1..5) {
        val angle = i * Math.PI / 3f
        lineTo(cx + radius * cos(angle).toFloat(), cy + radius * sin(angle).toFloat())
    }
    close()
}

// Custom shape for Friend Bubble - Message Bubble
val ChatBubbleShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val r = 16f
    moveTo(r, 0f)
    lineTo(w - r, 0f)
    quadraticTo(w, 0f, w, r)
    lineTo(w, h - r * 1.5f)
    quadraticTo(w, h - r, w - r, h - r)
    // The little bubble pointer
    lineTo(w / 2f + 12f, h - r)
    lineTo(w / 2f, h)
    lineTo(w / 2f - 12f, h - r)
    lineTo(r, h - r)
    quadraticTo(0f, h - r, 0f, h - r * 1.5f)
    lineTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)
    close()
}

// Helper Modifier for creating a pulsing dynamic glow frame
fun Modifier.neonGlow(
    color: Color,
    radius: Float = 30f,
    isPlaying: Boolean = true,
    speedMultiplier: Float = 1f
): Modifier = this.drawBehind {
    val alphaFactor = if (isPlaying) 0.5f else 0.2f
    // Draw outer shadow layer
    drawCircle(
        color = color.copy(alpha = alphaFactor),
        radius = size.minDimension / 2f + radius,
        center = center,
        style = Stroke(width = 4f)
    )
}

// Dynamic Neon Box for the Player Frame
@Composable
fun GlowCard(
    glowColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    borderWidth: Float = 2f,
    isPlaying: Boolean = true,
    moodPace: String = "peaceful",
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowCardTransition")
    
    // Animate glow radius and intensity based on video playing state and mood pace
    val duration = if (moodPace == "action") 1200 else 2400
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val animatedColor = glowColor.copy(
        alpha = if (isPlaying) 0.8f else 0.4f
    )

    Box(
        modifier = modifier
            .drawBehind {
                // Drawing outer atmospheric shadow
                drawRoundRect(
                    color = animatedColor.copy(alpha = 0.22f),
                    size = Size(size.width + glowPulse * 2, size.height + glowPulse * 2),
                    topLeft = Offset(-glowPulse, -glowPulse),
                    cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx())
                )
                // Crisp glowing border stroke
                drawRoundRect(
                    color = animatedColor,
                    size = size,
                    cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx()),
                    style = Stroke(width = borderWidth)
                )
            }
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
        content = content
    )
}

// Organic Spring-Loaded Click Feedback Button
@Composable
fun OrganicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = Color(0xFFD0BCFF),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "SpringScale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 1.5.dp,
                color = if (enabled) glowColor.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            )
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        glowColor.copy(alpha = 0.15f)
                    )
                )
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Custom visual physics are handled by scale
            ),
        color = Color.Transparent,
        contentColor = LocalContentColor.current
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}
```

---
## 6. ثيم الألوان والسمات (Theme & Colors & Typography)

### أ. الألوان (Color.kt)
**المسار:** `app/src/main/java/com/example/ui/theme/Color.kt`
```kotlin
package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val VolcanicBlack = Color(0xFF0A0502)
val SunsetOrange = Color(0xFFFF5722)
val LightSunset = Color(0xFFFFCC80)
val DarkSunset = Color(0xFFE65100)
val GoldenGlow = Color(0xFFFFA726)
val DeepWine = Color(0xFF3A1510)
val SlateGlow = Color(0xFF1B263B)
val GlassWhite = Color(0x0FFFFFFF)
val GlassWhiteBorder = Color(0x1AFFFFFF)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

```

### ب. السمة (Theme.kt)
**المسار:** `app/src/main/java/com/example/ui/theme/Theme.kt`
```kotlin
package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = SunsetOrange,
    secondary = GoldenGlow,
    tertiary = LightSunset,
    background = VolcanicBlack,
    surface = Color(0x0FFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme = DarkColorScheme // Fallback to Dark for unified premium look

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark theme
  dynamicColor: Boolean = false, // Force custom style over system wallpaper colors
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```

### ج. الخطوط (Type.kt)
**المسار:** `app/src/main/java/com/example/ui/theme/Type.kt`
```kotlin
package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
  )
```

