package com.demets.jas.preference

import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.pref_dialog_seekbar.view.*

/**
 * Created by dmitr on 26.02.2018.
 */
class SeekBarDialogPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    private lateinit var mSeekBar: SeekBar
    private lateinit var mTextView: TextView

    private var mMin: Int = 0
    private var mMax: Int = 100
    private var mValue: Int = 0
    private var mTemplate: String = "%s"

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        mSeekBar = view.seekbar
        mTextView = view.textview
        if (preference is SeekBarDialogPreference) {
            mValue = (preference as SeekBarDialogPreference).mValue
            mMin = (preference as SeekBarDialogPreference).minValue
            mMax = (preference as SeekBarDialogPreference).maxValue
            mTemplate = (preference as SeekBarDialogPreference).textViewTemplate

            if (mMax < mMin) throw IllegalStateException("SeekBarDialog: max value should be greater than min value!")
            if (mValue < mMin) {
                mValue = mMin
            }
            if (mValue > mMax) {
                mValue = mMax
            }
            mSeekBar.max = mMax - mMin
            mSeekBar.progress = mValue - mMin
            mTextView.text = String.format(mTemplate, mValue)
            mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    mTextView.text = String.format(mTemplate, progress + mMin)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            if (preference is SeekBarDialogPreference) {
                val seekBarDialogPreference = preference as SeekBarDialogPreference
                if (seekBarDialogPreference.callChangeListener(mSeekBar.progress + mMin)) {
                    seekBarDialogPreference.mValue = mSeekBar.progress + mMin
                }
            }
        }
    }

    companion object {
        fun newInstance(key: String): SeekBarDialogPreferenceDialogFragmentCompat {
            val fragment = SeekBarDialogPreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}