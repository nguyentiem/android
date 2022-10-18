package khoa.nv.applocker.ui.fakeapps

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import khoa.nv.applocker.R
import khoa.nv.applocker.ui.background.BackgroundItem

data class FakeAppItemViewState(
    val id: Int,
    val appName: String,
    val content: String,
    val alias: String,
    @DrawableRes val appIconRes: Int,
    var isChecked: Boolean = false
) : BackgroundItem {

    fun getIconDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, appIconRes)
    }

    fun isCheckedVisible(): Int = if (isChecked) View.VISIBLE else View.INVISIBLE
}

object FakeAppDataProvider {
    val fakeApps = listOf(
        FakeAppItemViewState(
            0,
            "App Locker",
            "App Locker is running",
            ".ui.calculator.CalculatorActivity",
            R.drawable.ic_locked_24px
        ),
        FakeAppItemViewState(
            1,
            "Calculator",
            "Calculator is running",
            ".ui.calculator.Calculator",
            R.drawable.ic_calculator
        ),
        FakeAppItemViewState(
            2,
            "Weather",
            "Weather is running",
            ".ui.calculator.Weather",
            R.drawable.ic_weather
        )
    )
}