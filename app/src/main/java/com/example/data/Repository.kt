package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(
    private val database: AppDatabase
) {
    private val memoryCapsuleDao = database.memoryCapsuleDao()
    private val friendBubbleDao = database.friendBubbleDao()
    private val chatMessageDao = database.chatMessageDao()

    val allCapsules: Flow<List<MemoryCapsule>> = memoryCapsuleDao.getAllCapsules()
    val allFriends: Flow<List<FriendBubble>> = friendBubbleDao.getAllFriends()

    fun getMessagesForFriend(friendId: Int): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesForFriend(friendId)

    suspend fun insertCapsule(capsule: MemoryCapsule) = withContext(Dispatchers.IO) {
        memoryCapsuleDao.insertCapsule(capsule)
    }

    suspend fun deleteCapsule(capsule: MemoryCapsule) = withContext(Dispatchers.IO) {
        memoryCapsuleDao.deleteCapsule(capsule)
    }

    suspend fun updateFriend(friend: FriendBubble) = withContext(Dispatchers.IO) {
        friendBubbleDao.updateFriend(friend)
    }

    suspend fun customizeFriend(id: Int, color: String, shape: String) = withContext(Dispatchers.IO) {
        friendBubbleDao.customizeFriend(id, color, shape)
    }

    suspend fun insertMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatMessageDao.insertMessage(message)
        friendBubbleDao.updateLatestMessage(message.friendId, message.messageText)
    }

    suspend fun clearMessagesForFriend(friendId: Int) = withContext(Dispatchers.IO) {
        chatMessageDao.clearMessagesForFriend(friendId)
    }

    suspend fun checkAndPrepopulateData() = withContext(Dispatchers.IO) {
        val currentCapsules = memoryCapsuleDao.getAllCapsules().first()
        if (currentCapsules.isEmpty()) {
            // Prepopulate Memory Capsules
            val defaultCapsules = listOf(
                MemoryCapsule(
                    title = "همسات الغروب (Sunset Whispers)",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    category = "Sunset",
                    emotionTag = "تأمل ودفء",
                    description = "رحلة بصرية هادئة تمتزج فيها ألوان الغروب الدافئة مع ألحان لو-فاي المريحة للأعصاب لتوفر ملاذاً هادئاً للعقل.",
                    moodPace = "peaceful"
                ),
                MemoryCapsule(
                    title = "رحلة الزمن الجميل (Nostalgia Symphony)",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    category = "Nostalgia",
                    emotionTag = "حنين وسلام",
                    description = "مقطع كلاسيكي ممتع يعيد ذكريات الطفولة البريئة والسلام الداخلي وسط حديقة خلابة من النغمات العذبة.",
                    moodPace = "peaceful"
                ),
                MemoryCapsule(
                    title = "نبض الفضاء (Cosmic Energetic Pulse)",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    category = "Action",
                    emotionTag = "حماس وإثارة",
                    description = "مقطع خيال علمي عالي الحيوية يتناغم فيه الإيقاع البصري مع أضواء محيطة متفاعلة تزيد من دقات القلب والإثارة البصرية.",
                    moodPace = "action"
                ),
                MemoryCapsule(
                    title = "سكينة الطبيعة المفقودة (Lost Forest Sanctuary)",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    category = "Quiet",
                    emotionTag = "هدوء وسكينة",
                    description = "مشهد طبيعي خلاب في أعماق الغابات المطيرة يريح حواسك وينقلك لتجربة غامرة خالية من المشتتات والضغوط.",
                    moodPace = "peaceful"
                )
            )
            for (capsule in defaultCapsules) {
                memoryCapsuleDao.insertCapsule(capsule)
            }
        }

        val currentFriends = friendBubbleDao.getAllFriends().first()
        if (currentFriends.isEmpty()) {
            // Prepopulate Friend Bubbles
            val layla = FriendBubble(
                name = "ليلى",
                avatarUrl = "layla",
                colorHex = "#8A2BE2", // BlueViolet
                shapeType = "Hexagon",
                isOnline = true,
                latestMessage = "شاهدت هذا المقطع المذهل لتأمل الغروب! 🌅",
                channelName = "Zen Oasis"
            )
            val yaseen = FriendBubble(
                name = "ياسين",
                avatarUrl = "yaseen",
                colorHex = "#FF6347", // Tomato
                shapeType = "Circle",
                isOnline = true,
                latestMessage = "هل جربت وضع السكون في التطبيق؟ رائع جداً 🧘‍♂️",
                channelName = "Cosmic Beats"
            )
            val sara = FriendBubble(
                name = "سارة",
                avatarUrl = "sara",
                colorHex = "#00FA9A", // MediumSpringGreen
                shapeType = "Bubble",
                isOnline = false,
                latestMessage = "المساعد الذكي لخص لي فيديو الأكشن هذا في ثوانٍ! ⚡",
                channelName = "Sci-Fi Echo"
            )

            friendBubbleDao.insertFriend(layla)
            friendBubbleDao.insertFriend(yaseen)
            friendBubbleDao.insertFriend(sara)

            // Add default initial messages
            // Leila's chat
            chatMessageDao.insertMessage(ChatMessage(friendId = 1, senderName = "friend", messageText = "مرحباً يا صديقي! كيف حالك اليوم؟ 😊"))
            chatMessageDao.insertMessage(ChatMessage(friendId = 1, senderName = "user", messageText = "أهلاً ليلى، أنا بخير وأستمتع بملاذي الرقمي! 🕊️"))
            chatMessageDao.insertMessage(ChatMessage(friendId = 1, senderName = "friend", messageText = "شاهدت هذا المقطع المذهل لتأمل الغروب! 🌅"))

            // Yaseen's chat
            chatMessageDao.insertMessage(ChatMessage(friendId = 2, senderName = "friend", messageText = "مرحبًا، تطبيق Media Sanctuary غير مفاهيم تصفح الميديا تمامًا! هل جربت وضع السكون في التطبيق؟ رائع جداً 🧘‍♂️"))

            // Sara's chat
            chatMessageDao.insertMessage(ChatMessage(friendId = 3, senderName = "friend", messageText = "مرحباً! لقد قمت للتو بتشغيل المقطع وحللته عبر صدى المشاعر."))
            chatMessageDao.insertMessage(ChatMessage(friendId = 3, senderName = "friend", messageText = "المساعد الذكي لخص لي فيديو الأكشن هذا في ثوانٍ! ⚡"))
        }
    }
}
