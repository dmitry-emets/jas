package com.demets.jas

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import com.demets.jas.preference.SeekBarDialogPreference
import com.demets.jas.preference.SeekBarDialogPreferenceDialogFragmentCompat


/**
 * Created by DEmets on 05.02.2018.
 */
class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is SeekBarDialogPreference) {
            val dialogFragment = SeekBarDialogPreferenceDialogFragmentCompat.newInstance(preference.key)
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference != null) {
            val key = preference.key
            when (key) {
                getString(R.string.pref_key_logout) -> {
                    AppSettings.removeAuth(context)
                    preference.summary = ""
                    preference.isEnabled = false
                    return true
                }
                getString(R.string.pref_key_author) -> {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "dmitriyemets@gmail.com", null))
                    try {
                        startActivity(Intent.createChooser(intent, "Send email..."))
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(context, "Sorry, can't find any email app!", Toast.LENGTH_SHORT).show()
                    }

                    return true
                }
                getString(R.string.pref_key_logo_author) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.freepik.com")
                    }
                    if (intent.resolveActivity(context?.packageManager) != null) {
                        startActivity(intent)
                    }
                    return true
                }
                getString(R.string.pref_key_icons_author) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.flaticon.com/authors/gregor-cresnar")
                    }
                    if (intent.resolveActivity(context?.packageManager) != null) {
                        startActivity(intent)
                    }
                    return true
                }
            }
        }
        return false
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.PREFS_NAME
        addPreferencesFromResource(R.xml.preferences)
        setSummaries()
        setOnPrefChangeListener()
        setOnPrefClickListener()
    }

    private fun setSummaries() {
        var key = getString(R.string.pref_key_scrobbling_enabled)
        val scrobblingEnabledPref = findPreference(key)
        scrobblingEnabledPref.summary = if (AppSettings.getScrobblingEnabled(context)) {
            getString(R.string.pref_summary_scrobbling_enabled_true)
        } else {
            getString(R.string.pref_summary_scrobbling_enabled_false)
        }

        key = getString(R.string.pref_key_min_time_to_scrobble)
        val minTimePref = findPreference(key)
        minTimePref.summary = String.format(getString(R.string.pref_summary_min_time_to_scrobble),
                AppSettings.getMinTimeToScrobble(context).toString())

        key = getString(R.string.pref_key_min_percent_to_scrobble)
        val minPercentPref = findPreference(key)
        minPercentPref.summary = String.format(getString(R.string.pref_summary_min_percent_to_scrobble),
                AppSettings.getMinPercentToScrobble(context))

        key = getString(R.string.pref_key_min_track_duration_to_scrobble)
        val minDurationPref = findPreference(key)
        minDurationPref.summary = String.format(getString(R.string.pref_summary_min_track_duration_to_scrobble),
                AppSettings.getMinDurationToScrobble(context))

        key = getString(R.string.pref_key_logout)
        val logoutPref = findPreference(key)
        if (AppSettings.isAuthorized(context)) {
            logoutPref.summary = String.format(
                    getString(R.string.pref_summary_logout),
                    AppSettings.getUsername(context))
            logoutPref.isEnabled = true
        } else {
            logoutPref.summary = ""
            logoutPref.isEnabled = false
        }
        key = getString(R.string.pref_key_notifications_enabled)
        val notificationsEnabledPref = findPreference(key)
        notificationsEnabledPref.summary = if (AppSettings.getNotificationsEnabled(context)) {
            getString(R.string.pref_summary_notifications_enabled_true)
        } else {
            getString(R.string.pref_summary_notifications_enabled_false)
        }
        key = getString(R.string.pref_key_min_priority_notifications_enabled)
        val minPriorityEnabledPref = findPreference(key)
        minPriorityEnabledPref.summary = if (AppSettings.getMinPriorityNotificationsEnabled(context)) {
            getString(R.string.pref_summary_min_priority_notifications_enabled_true)
        } else {
            getString(R.string.pref_summary_min_priority_notifications_enabled_false)
        }
        key = getString(R.string.pref_key_enable_toast_on_scrobble)
        val enableToastOnScrobblePref = findPreference(key)
        enableToastOnScrobblePref.summary = if (AppSettings.getEnableToastOnScrobble(context)) {
            getString(R.string.pref_summary_enable_toast_on_scrobble_true)
        } else {
            getString(R.string.pref_summary_enable_toast_on_scrobble_false)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        //TODO: Research for right way to iterate over preferences.
        when (key) {
            getString(R.string.pref_key_scrobbling_enabled) -> {
                findPreference(key).summary = if (AppSettings.getScrobblingEnabled(context)) {
                    getString(R.string.pref_summary_scrobbling_enabled_true)
                } else {
                    getString(R.string.pref_summary_scrobbling_enabled_false)
                }
            }
            getString(R.string.pref_key_min_time_to_scrobble) -> {
                findPreference(key).summary = String.format(getString(R.string.pref_summary_min_time_to_scrobble),
                        AppSettings.getMinTimeToScrobble(context))
            }
            getString(R.string.pref_key_min_percent_to_scrobble) -> {
                findPreference(key).summary = String.format(getString(R.string.pref_summary_min_percent_to_scrobble),
                        AppSettings.getMinPercentToScrobble(context))
            }
            getString(R.string.pref_key_min_track_duration_to_scrobble) -> {
                findPreference(key).summary = String.format(getString(R.string.pref_summary_min_track_duration_to_scrobble),
                        AppSettings.getMinDurationToScrobble(context))
            }
            getString(R.string.pref_key_notifications_enabled) -> {
                findPreference(key).summary = if (AppSettings.getNotificationsEnabled(context)) {
                    getString(R.string.pref_summary_notifications_enabled_true)
                } else {
                    getString(R.string.pref_summary_notifications_enabled_false)
                }
            }
            getString(R.string.pref_key_min_priority_notifications_enabled) -> {
                findPreference(key).summary = if (AppSettings.getMinPriorityNotificationsEnabled(context)) {
                    getString(R.string.pref_summary_min_priority_notifications_enabled_true)
                } else {
                    getString(R.string.pref_summary_min_priority_notifications_enabled_false)
                }
            }
            getString(R.string.pref_key_enable_toast_on_scrobble) -> {
                findPreference(key).summary = if (AppSettings.getEnableToastOnScrobble(context)) {
                    getString(R.string.pref_summary_enable_toast_on_scrobble_true)
                } else {
                    getString(R.string.pref_summary_enable_toast_on_scrobble_false)
                }
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return true
    }

    private fun setOnPrefChangeListener() {

        findPreference(getString(R.string.pref_key_scrobbling_enabled)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_min_time_to_scrobble)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_min_percent_to_scrobble)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_min_track_duration_to_scrobble)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_notifications_enabled)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_min_priority_notifications_enabled)).onPreferenceChangeListener = this
        findPreference(getString(R.string.pref_key_enable_toast_on_scrobble)).onPreferenceChangeListener = this
    }

    private fun setOnPrefClickListener() {
        findPreference(getString(R.string.pref_key_logout)).onPreferenceClickListener = this
        findPreference(getString(R.string.pref_key_author)).onPreferenceClickListener = this
        findPreference(getString(R.string.pref_key_logo_author)).onPreferenceClickListener = this
        findPreference(getString(R.string.pref_key_icons_author)).onPreferenceClickListener = this
    }
}