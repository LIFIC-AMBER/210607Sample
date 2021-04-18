package com.wotosts.recruit_backpacker.utils

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("unit", "value", requireAll = true)
fun setUnitString(textView: TextView, unit: String, value: Number) {
    val str = value.toInt().toString() + unit
    textView.text = SpannableStringBuilder(str).apply { setSpan(StyleSpan(Typeface.BOLD), 0, str.length - 1, Spanned.SPAN_COMPOSING) }
}