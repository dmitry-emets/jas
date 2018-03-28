package com.demets.jas.utils

import android.util.Log

/**
 * Created by dmitr on 17.02.2018.
 */
object TaggedLogger {
    private const val TAG = "JAS"
    fun d(msg: String) = Log.d(TAG, msg)
}