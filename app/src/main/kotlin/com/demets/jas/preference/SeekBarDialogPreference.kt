package com.demets.jas.preference

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.demets.jas.R

/**
 * Created by dmitr on 25.02.2018.
 */
class SeekBarDialogPreference @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null,
                                                        defStyleAttr: Int = R.attr.preferenceStyle,
                                                        defStyleRes: Int = defStyleAttr)
    : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mDialogLayoutResId = R.layout.pref_dialog_seekbar
    var minValue: Int = 0
    var maxValue: Int = 100
    var textViewTemplate = "%s"
    var mValue: Int = 0
        set(value) {
            field = value
            persistInt(value)
        }

    init {
        attrs?.let {
            minValue = attrs.getAttributeIntValue(null, "min", 0)
            maxValue = attrs.getAttributeIntValue(null, "max", 100)
            textViewTemplate = try {
                val textViewTemplateRes = attrs.getAttributeResourceValue(null, "textFormTemplate", 0)
                context.getString(textViewTemplateRes)
            } catch (e: Resources.NotFoundException) {
                attrs.getAttributeValue(null, "textFormTemplate")
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any = a.getInt(index, 0)

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        mValue = if (restorePersistedValue) getPersistedInt(mValue) else defaultValue as Int
    }

    override fun getDialogLayoutResource(): Int = mDialogLayoutResId
}