package com.amber.sample.utils

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.amber.sample.utils.NetworkUtil.getWeatherAbbrUri

@BindingAdapter("unit", "value", requireAll = true)
fun setUnitString(textView: TextView, unit: String, value: Number) {
    val str = value.toInt().toString() + unit
    textView.text = SpannableStringBuilder(str).apply { setSpan(StyleSpan(Typeface.BOLD), 0, str.length - 1, Spanned.SPAN_COMPOSING) }
}

@BindingAdapter("weatherAbbr")
fun setWeatherAbbr(imageView: ImageView, abbr: String) {
    if(abbr.isEmpty()) return

    Glide.with(imageView)
        .load(getWeatherAbbrUri(abbr))
        .into(imageView)
}