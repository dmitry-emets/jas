package com.demets.jas.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.demets.jas.AppSettings
import com.demets.jas.R
import com.demets.jas.androidx.moxy.MvpAppCompatFragment
import com.demets.jas.ui.main.authorised.AuthorizedFragment
import com.demets.jas.ui.main.unauthorised.UnauthorizedFragment
import com.demets.jas.ui.settings.SettingsActivity
import com.demets.jas.ui.tracks.TracksActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(
                this,
                AppSettings.PREFS_NAME,
                Context.MODE_PRIVATE,
                R.xml.preferences,
                false
        )

        val hasSessionKey = AppSettings.getSessionKey(this).isNotEmpty()
        val fragment: MvpAppCompatFragment = if (hasSessionKey) {
            AuthorizedFragment()
        } else {
            UnauthorizedFragment()
        }
        supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_tracks -> {
                val intent = Intent(this, TracksActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
