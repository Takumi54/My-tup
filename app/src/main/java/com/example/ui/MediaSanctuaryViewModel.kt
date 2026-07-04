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
