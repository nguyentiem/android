package tools.certification.latency.auto.ext

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import tools.certification.latency.auto.R

fun TextView.setSpanColor(fullText: String, subText: String, keywords: List<String>? = null) {
    text = SpannableString(fullText).apply {
        val i = fullText.indexOf(subText)
        setSpan(StyleSpan(Typeface.BOLD), i, i + subText.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        keywords?.forEach {
            val index = fullText.indexOf(it)
            setSpan(ForegroundColorSpan(Color.BLUE), index, index + it.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun TextView.setDrawableStart(@DrawableRes id: Int) {
    ContextCompat.getDrawable(context, id)?.let { drawable ->
        val size = resources.getDimensionPixelSize(R.dimen.drawable_start_size)
        drawable.setBounds(0, 0, size, size)
        setCompoundDrawables(drawable, null, null, null)
    }
}
