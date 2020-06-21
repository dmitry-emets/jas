package com.demets.jas.utils

import android.content.Intent

/**
 * Created by dmitr on 09.02.2018.
 */
object IntentUtil {
    fun getLong(
            intent: Intent,
            extraNames: List<String>,
            defaultValue: Long = -1L
    ): Long {
        val extras = intent.extras
        extraNames.forEach {key ->
            if (extras?.containsKey(key) == true) {
                return when (val extraValue = extras[key]) {
                    is Short -> extraValue.toLong()
                    is Byte -> extraValue.toLong()
                    is Int -> extraValue.toLong()
                    is Long -> extraValue
                    else -> defaultValue
                }
            }
        }
        return defaultValue
    }

    fun getBoolean(
            intent: Intent,
            extraNames: List<String>,
            defaultValue: Boolean?
    ): Boolean? {
        val extras = intent.extras
        extraNames.forEach {
            if (extras?.containsKey(it) == true) {
                return when (val extraValue = extras[it]) {
                    is Boolean -> extraValue
                    is Short -> extraValue.toLong() > 0
                    is Byte -> extraValue.toLong() > 0
                    is Int -> extraValue.toLong() > 0
                    is Long -> extraValue > 0
                    else -> defaultValue
                }
            }
        }
        return null
    }

    fun getString(intent: Intent, extraNames: List<String>, defaultValue: String): String {
        val extras = intent.extras
        extraNames.forEach {
            if (extras?.containsKey(it) == true) {
                return extras[it] as? String ?: defaultValue
            }
        }
        return defaultValue
    }
}
