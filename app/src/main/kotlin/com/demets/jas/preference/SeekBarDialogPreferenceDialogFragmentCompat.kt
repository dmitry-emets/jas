package com.demets.jas.preference

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.PreferenceDialogFragmentCompat
import kotlinx.android.synthetic.main.pref_dialog_seekbar.view.*

/**
 * Created by dmitr on 26.02.2018.
 */
class SeekBarDialogPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    private lateinit var seekBar: SeekBar
    private lateinit var valueView: TextView

    private var mMin: Int = 0
    private var mMax: Int = 100
    private var mValue: Int = 0
    private var mTemplate: String = "%s"

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        seekBar = view.seekbar
        valueView = view.valueView
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
            seekBar.max = mMax - mMin
            seekBar.progress = mValue - mMin
            valueView.text = String.format(mTemplate, mValue)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    valueView.text = String.format(mTemplate, progress + mMin)
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
                if (seekBarDialogPreference.callChangeListener(seekBar.progress + mMin)) {
                    seekBarDialogPreference.mValue = seekBar.progress + mMin
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
