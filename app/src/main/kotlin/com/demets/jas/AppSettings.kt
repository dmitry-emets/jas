package com.demets.jas

import android.content.Context
import com.demets.jas.model.PreviousTrackInfo
import com.demets.jas.utils.TaggedLogger
import com.google.gson.Gson

/**
 * Class for store and retrieve application settings.
 */
object AppSettings {
    const val PREFS_NAME = "com.demets.jas.prefs"

    private const val TOKEN = "key_token"
    private const val USERNAME = "key_username"
    private const val SESSION_KEY = "key_session"

    private const val SCROBBLING_ENABLED = "pref_scrobbling_enabled"
    private const val MIN_TIME_TO_SCROBBLE = "pref_min_time_to_scrobble"
    private const val MIN_PERCENT_TO_SCROBBLE = "pref_min_percent_to_scrobble"
    private const val MIN_TRACK_DURATION_TO_SCROBBLE = "pref_min_track_duration_to_scrobble"
    private const val NOTIFICATIONS_ENABLED = "pref_notifications_enabled"
    private const val MIN_PRIORITY_NOTIFICATIONS_ENABLED = "pref_min_priority_notifications_enabled"
    private const val ENABLE_TOAST_ON_SCROBBLE = "pref_enable_toast_on_scrobble"

    private const val PREVIOUS_TRACK_INFO_KEY = "prev_track_info"
    private const val LAST_LASTFM_COUNT = "last_count"
    private const val LAST_COUNT_UPDATE_TIME = "last_count_update_time"

    fun removeAuth(context: Context?) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .remove(SESSION_KEY)
                        .remove(USERNAME)
                        .apply()
                TaggedLogger.d("Logout complete.")
            }

    fun isAuthorized(context: Context?): Boolean {
        val sessionKey = context?.let {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(SESSION_KEY, null)

        }
        return !sessionKey.isNullOrEmpty()
    }

    fun setToken(context: Context?, token: String) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(TOKEN, token)
                        .apply()
            }

    fun getToken(context: Context?): String =
            if (context != null) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getString(TOKEN, "")
            } else {
                ""
            }

    fun setUsername(context: Context?, username: String) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(USERNAME, username)
                        .apply()
            }

    fun getUsername(context: Context?): String =
            if (context != null) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getString(USERNAME, "")
            } else {
                ""
            }

    fun setSessionKey(context: Context?, sessionKey: String) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(SESSION_KEY, sessionKey)
                        .apply()
            }

    fun getSessionKey(context: Context?): String =
            if (context != null) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getString(SESSION_KEY, "")
            } else {
                ""
            }

    fun setScrobblingEnabled(context: Context?, bool: Boolean) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(SCROBBLING_ENABLED, bool)
                        .apply()
            }

    fun getScrobblingEnabled(context: Context?): Boolean =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getBoolean(SCROBBLING_ENABLED, false)
                    ?: false

    fun setMinTimeToScrobble(context: Context?, timeInSeconds: Int) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(MIN_TIME_TO_SCROBBLE, timeInSeconds)
                        .apply()
            }

    fun getMinTimeToScrobble(context: Context?): Int =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getInt(MIN_TIME_TO_SCROBBLE, 0)
                    ?: 0

    fun setMinPercentToScrobble(context: Context?, percent: Int) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(MIN_PERCENT_TO_SCROBBLE, percent)
                        .apply()
            }

    fun getMinPercentToScrobble(context: Context?): Int =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getInt(MIN_PERCENT_TO_SCROBBLE, 0)
                    ?: 0

    fun setMinDurationToScrobble(context: Context?, durationInSeconds: Int) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(MIN_TRACK_DURATION_TO_SCROBBLE, durationInSeconds)
                        .apply()
            }

    fun getMinDurationToScrobble(context: Context?): Int =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getInt(MIN_TRACK_DURATION_TO_SCROBBLE, 0)
                    ?: 0

    fun setNotificationsEnabled(context: Context?, bool: Boolean) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(NOTIFICATIONS_ENABLED, bool)
                        .apply()
            }

    fun getNotificationsEnabled(context: Context?): Boolean =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getBoolean(NOTIFICATIONS_ENABLED, false)
                    ?: false

    fun setMinPriorityNotificationsEnabled(context: Context?, bool: Boolean) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(MIN_PRIORITY_NOTIFICATIONS_ENABLED, bool)
                        .apply()
            }

    fun getMinPriorityNotificationsEnabled(context: Context?): Boolean =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getBoolean(MIN_PRIORITY_NOTIFICATIONS_ENABLED, false)
                    ?: false

    fun setEnableToastOnScrobble(context: Context?, bool: Boolean) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(ENABLE_TOAST_ON_SCROBBLE, bool)
                        .apply()
            }

    fun getEnableToastOnScrobble(context: Context?): Boolean =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getBoolean(ENABLE_TOAST_ON_SCROBBLE, false)
                    ?: false

    fun setPreviousTrackInfo(context: Context?, prevTrackInfo: PreviousTrackInfo) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(PREVIOUS_TRACK_INFO_KEY, Gson().toJson(prevTrackInfo))
                        .apply()
            }

    fun getPreviousTrackInfo(context: Context?): PreviousTrackInfo? {
        val json = context?.let {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(PREVIOUS_TRACK_INFO_KEY, "")
        }
        return Gson().fromJson(json, PreviousTrackInfo::class.java)
    }

    fun setLastFmCount(context: Context?, count: Int) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(LAST_LASTFM_COUNT, count)
                        .apply()
            }

    fun getLastFmCount(context: Context?): Int =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getInt(LAST_LASTFM_COUNT, -1)
                    ?: -1

    fun setLastFmCountTime(context: Context?, count: Long) =
            context?.let {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putLong(LAST_COUNT_UPDATE_TIME, count)
                        .apply()
            }

    fun getLastFmCountTime(context: Context?): Long =
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getLong(LAST_COUNT_UPDATE_TIME, -1L)
                    ?: -1L
}