package com.lokavo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.lokavo.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            setError(context.getString(R.string.email_error), null)
        } else {
            error = null
        }
    }
}