package tools.certification.latency.auto.ui.main.view

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import tools.certification.latency.auto.ext.setDrawableStart

@BindingAdapter("drawableStart")
fun setDrawableStart(view: TextView, @DrawableRes id: Int) {
    view.setDrawableStart(id)
}

@BindingAdapter("showIf")
fun setShowIf(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("hideIf")
fun setHideIf(view: View, invisible: Boolean) {
    view.visibility = if (invisible) View.GONE else View.VISIBLE
}
