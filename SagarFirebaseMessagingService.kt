package com.sagarwifi.app

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SagarFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val CHANNEL_ID = "sagarwifi_updates"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"] ?: "Sagar WiFi"
        val body = message.notification?.body
            ?: message.data["body"] ?: "Naye offers aur updates dekhein."
        val targetUrl = safeBlogUrl(message.data["url"])

        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (targetUrl != null) putExtra("notification_url", targetUrl)
        }
        val pending = PendingIntent.getActivity(
            this, (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(
                (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification
            )
        } catch (_: SecurityException) {
            // Notifications may be disabled by the user or not yet permitted.
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send this token to your trusted backend if targeting individual devices.
        // For broadcast offers, Firebase Console topic messaging can be used after topic subscription is added.
    }

    private fun safeBlogUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(raw)
            if (uri.scheme == "https" && uri.host == "sagarwifi.blogspot.com") uri.toString() else null
        } catch (_: Exception) { null }
    }
}
