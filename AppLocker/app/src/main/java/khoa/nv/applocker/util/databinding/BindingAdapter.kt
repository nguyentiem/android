package khoa.nv.applocker.util.databinding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @BindingAdapter("hideIf")
    @JvmStatic
    fun hideIf(view: View, invisible: Boolean) {
        view.visibility = if (invisible) View.GONE else View.VISIBLE
    }

    @BindingAdapter("hideUnless")
    @JvmStatic
    fun hideUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("firstNumber", "secondNumber", "binaryOperator")
    @JvmStatic
    fun setInput(view: TextView, firstNumber: Double?, secondNumber: Double?, binaryOperator: String, ) {
        if (firstNumber == null)
            view.text = null
        else {
            view.text = "${Converter.formatDouble(firstNumber)}${binaryOperator}${Converter.formatDouble(secondNumber)}"
        }
    }
}