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
import com.demets.jas.MainActivity
import com.demets.jas.R
import com.demets.jas.model.Track


/**
 * Created by dmitr on 21.02.2018.
 */
object NotificationUtil {
    private const val CHANNEL_ID = "JAS channel ID"
    private const val NOW_PLAYING = 1
    private var notificationManager: NotificationManager? = null

    private fun getNotificationManager(context: Context): NotificationManager? {
        if (notificationManager == null) {
            notificationManager = context.applicationContext
                    .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
        if (AppSettings.getNotificationsEnabled(context)) {
            initNotificationChannel(context)
            val priority = if (AppSettings.getMinPriorityNotificationsEnabled(context)) PRIORITY_LOW else PRIORITY_DEFAULT
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setPriority(priority)
                    .setSmallIcon(R.drawable.headphones)
                    .setContentTitle("${track.artist} - ${track.title}")
                    .setContentText("Now scrobbling")
            val resultIntent = Intent(context, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(resultIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            val notification = mBuilder.build()
            notification.flags = Notification.FLAG_ONGOING_EVENT
            getNotificationManager(context)?.notify(NOW_PLAYING, notification)
        }
    }

    fun dismissNowPlaying(context: Context) {
//        initNotificationChannel(context)
        getNotificationManager(context)?.cancel(NOW_PLAYING)
    }
}