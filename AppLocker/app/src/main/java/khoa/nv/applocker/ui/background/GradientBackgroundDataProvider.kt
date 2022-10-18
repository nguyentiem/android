package khoa.nv.applocker.ui.background

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import khoa.nv.applocker.R

data class BackgroundsViewState(@DrawableRes val selectedDrawable: Int)

interface BackgroundItem

data class GradientItemViewState(
    val id: Int, @DrawableRes val gradientBackgroundRes: Int,
    var isChecked: Boolean = false
) : BackgroundItem {

    fun getGradientDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, gradientBackgroundRes)
    }

    fun isCheckedVisible(): Int = if (isChecked) View.VISIBLE else View.INVISIBLE

}

object GradientBackgroundDataProvider {
    val gradientViewStateList = arrayListOf(
        GradientItemViewState(0, R.drawable.gradient_one),
        GradientItemViewState(1, R.drawable.gradient_two),
        GradientItemViewState(2, R.drawable.gradient_three),
        GradientItemViewState(3, R.drawable.gradient_four),
        GradientItemViewState(4, R.drawable.gradient_five),
        GradientItemViewState(5, R.drawable.gradient_six),
        GradientItemViewState(6, R.drawable.gradient_seven),
        GradientItemViewState(7, R.drawable.gradient_eight),
        GradientItemViewState(8, R.drawable.gradient_nine),
        GradientItemViewState(9, R.drawable.gradient_ten),
        GradientItemViewState(10, R.drawable.gradient_eleven),
        GradientItemViewState(11, R.drawable.gradient_twelwe)
    )
}