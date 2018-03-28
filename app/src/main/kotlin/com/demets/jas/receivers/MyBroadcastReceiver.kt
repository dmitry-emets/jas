package com.demets.jas.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.demets.jas.model.Track
import com.demets.jas.service.JASService
import com.demets.jas.utils.IntentUtil
import com.demets.jas.utils.TaggedLogger


/**
 * Created by DEmets on 08.02.2018.
 */
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sb = StringBuilder()
        sb.append("Action: " + intent?.action + "\n")
        sb.append("URI: " + intent?.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n")
        val log = sb.toString()
        TaggedLogger.d(log)
        if (intent != null) {
            val intentForService = processIntent(context, intent)
            intentForService?.let {
                intentForService.action = JASService.ACTION_PROCESS_TRACK
                intentForService.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())
                context?.startService(intentForService)
            }
        }
    }

    private fun processIntent(context: Context?, intent: Intent): Intent? {
        val intentToSend = Intent(context, JASService::class.java)
        val rawDuration = IntentUtil.getLong(intent, listOf("duration"))
        val isPlaying = IntentUtil.getBoolean(intent, listOf("playing"), null) ?: return null
        intentToSend.apply {
            putExtra(EXTRA_IS_PLAYING, isPlaying)
            putExtra(EXTRA_PLAYER, intent.action.substring(0, intent.action.lastIndexOf(".")))
            putExtra(EXTRA_TRACK_ID, IntentUtil.getLong(intent, listOf("id")))
            putExtra(EXTRA_TRACK, intent.getStringExtra("track"))
            putExtra(EXTRA_ARTIST, intent.getStringExtra("artist"))
            putExtra(EXTRA_ALBUM, intent.getStringExtra("album"))
            putExtra(EXTRA_DURATION, if (rawDuration < 30000) rawDuration * 1000 else rawDuration)
        }
        return intentToSend
    }

    companion object {
        const val EXTRA_TRACK = "track"
        const val EXTRA_ARTIST = "artist"
        const val EXTRA_ALBUM = "album"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_TIMESTAMP = "timestamp"
        const val EXTRA_IS_PLAYING = "isPlaying"
        const val EXTRA_PLAYER = "player"
        const val EXTRA_TRACK_ID = "trackId"

        fun restoreTrackFromIntent(intent: Intent?): Track? {
            return if (intent == null) {
                null
            } else {
                Track(intent.getStringExtra(EXTRA_TRACK),
                        intent.getStringExtra(EXTRA_ARTIST),
                        intent.getStringExtra(EXTRA_ALBUM),
                        intent.getLongExtra(EXTRA_DURATION, -1L),
                        intent.getLongExtra(EXTRA_TIMESTAMP, -1L))
            }
        }
    }
}