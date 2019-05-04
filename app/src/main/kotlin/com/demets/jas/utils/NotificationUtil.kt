package com.demets.jas.utils

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT
import android.support.v4.app.NotificationCompat.PRIORITY_LOW
import com.demets.jas.AppSettings
import com.demets.jas.R
import com.demets.jas.model.Track
import com.demets.jas.ui.main.MainActivity

/**
 * Util class for status bar notifications.
 */
object NotificationUtil {
    private const val CHANNEL_ID = "JAS channel ID"
    private const val NOW_PLAYING = 1
    private var notificationManager: NotificationManager? = null

    private fun getNotificationManager(context: Context): NotificationManager? {
        if (notificationManager == null) {
            notificationManager = context.applicationContext.getSystemService(NOTIFICATION_SERVICE)
                    as NotificationManager
        }
        return notificationManager
    }

    private fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val priority = if (AppSettings.getMinPriorityNotificationsEnabled(context)) {
                NotificationManager.IMPORTANCE_LOW
            } else {
                NotificationManager.IMPORTANCE_DEFAULT
            }
            if (notificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
                //TODO: check if channel exists
                val notificationChannel = NotificationChannel(CHANNEL_ID,
                        "Now Scrobbling",
                        priority)
                notificationChannel.apply {
                    description = "Channel for JAS Notifications"
                    enableLights(false)
                    enableVibration(false)
                }
                getNotificationManager(context)?.createNotificationChannel(notificationChannel)
            }
        }
    }

    fun setNowPlaying(context: Context, track: Track) {
        val isAuthorized = AppSettings.isAuthorized(context)
        val scrobblingEnabled = AppSettings.getScrobblingEnabled(context)
        val notificationsEnabled = AppSettings.getNotificationsEnabled(context)
        TaggedLogger.d("NP Update: Authorized = $isAuthorized")
        TaggedLogger.d("NP Update: Scrobbling enabled = $scrobblingEnabled")
        TaggedLogger.d("NP Update: Notifications enabled = $notificationsEnabled")

        if (isAuthorized && scrobblingEnabled && notificationsEnabled) {
            TaggedLogger.d("NP Update: notification will be updated")
            initNotificationChannel(context)
            val priority = if (AppSettings.getMinPriorityNotificationsEnabled(context)) PRIORITY_LOW else PRIORITY_DEFAULT

            val resultPendingIntent = TaskStackBuilder.create(context)
                    .addParentStack(MainActivity::class.java)
                    .addNextIntent(Intent(context, MainActivity::class.java))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setPriority(priority)
                    .setSmallIcon(R.drawable.headphones)
                    .setContentTitle("${track.artist} - ${track.title}")
                    .setContentText(context.getString(R.string.notif_now_playing_label))
                    .setContentIntent(resultPendingIntent)
                    .build()
            notification.flags = Notification.FLAG_ONGOING_EVENT
            getNotificationManager(context)?.notify(NOW_PLAYING, notification)
            TaggedLogger.d("NP Update: notification updated")
        }
    }

    fun restoreNowPlaying(context: Context) {
        val prevTrackInfo = AppSettings.getPreviousTrackInfo(context)
        if (prevTrackInfo?.isPlayingState == true) {
            setNowPlaying(context, prevTrackInfo.track)
        }
    }

    fun dismissNowPlaying(context: Context) {
        getNotificationManager(context)?.cancel(NOW_PLAYING)
        TaggedLogger.d("NP notification dismissed")
    }
}