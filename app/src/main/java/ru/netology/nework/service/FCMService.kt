package ru.netology.nework.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d("MyAppLog", "FCMService * onCreate: $channel")
            manager.createNotificationChannel(channel)
        }
    }

    //Вызывается при получении сообщения
    override fun onMessageReceived(message: RemoteMessage) {

        val authId = appAuth.authStateFlow.value.id
        Log.d("MyAppLog", "FCMService * authId: $authId")
        if (message.data[action] == null) {
            Log.d("MyAppLog", "FCMService * null: ${message.data[action]} / $action")
            val pushJson = message.data.values.firstOrNull()?.let { JSONObject(it) }
            val recipientId: String? = pushJson?.optString("recipientId")
            val content: String? = pushJson?.optString("content")
            Log.d("MyAppLog", "FCMService * recipientId: $recipientId / $content / $pushJson")

            when (recipientId) {
                "null", authId.toString() -> handlePush(content)
                "0" -> appAuth.sendPushToken()
                else -> appAuth.sendPushToken()
            }
        } else {
            Log.d("MyAppLog", "FCMService * !null: $action / $content")
            message.data[action]?.let {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(message.data["content"], Like::class.java)
                    )
                }
            }
        }

    }

    private fun handlePush(content: String?) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        Log.d("MyAppLog", "FCMService * handlePush: $content")
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    @SuppressLint("StringFormatInvalid")
    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        Log.d("MyAppLog", "FCMService * handleLike: $content")
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    //Вызывается при создании нового токена для проекта Firebase по умолчанию
    override fun onNewToken(token: String) {
        println(token)
        appAuth.sendPushToken(token)
    }

    //исходящее сообщение которое было успешно отправлено на сервер подключения
    override fun onMessageSent(msgId: String) {
        Log.d("MyAppLog", "FCMService * onMessageSent: $msgId")
        println(msgId)
    }


}

enum class Action {
    LIKE,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)
