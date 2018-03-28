package com.demets.jas.utils

import android.content.Intent

/**
 * Created by dmitr on 09.02.2018.
 */
object IntentUtil {
    fun getLong(intent: Intent, extraNames: List<String>, defaultValue: Long = -1L): Long {
        val extras = intent.extras
        extraNames.forEach {
            if (extras.containsKey(it)) {
                val extraValue = extras[it]
                return when (extraValue) {
                    is Short,
                    is Byte,
                    is Int,
                    is Long -> extraValue as Long
                    else -> defaultValue
                }
            }
        }
        return defaultValue
    }

    fun getBoolean(intent: Intent, extraNames: List<String>, defaultValue: Boolean?): Boolean? {
        val extras = intent.extras
        extraNames.forEach {
            if (extras.containsKey(it)) {
                val extraValue = extras[it]
                return when (extraValue) {
                    is Boolean -> extraValue
                    is Short,
                    is Byte,
                    is Int,
                    is Long -> extraValue as Long > 0
                    else -> defaultValue
                }
            }
        }
        return null
    }

    fun getString(intent: Intent, extraNames: List<String>, defaultValue: String): String {
        val extras = intent.extras
        extraNames.forEach {
            if (extras.containsKey(it)) {
                return extras[it] as? String ?: defaultValue
            }
        }
        return defaultValue
    }
}