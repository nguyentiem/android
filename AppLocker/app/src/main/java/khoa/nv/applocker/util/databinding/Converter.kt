package khoa.nv.applocker.util.databinding

import java.text.DecimalFormat

object Converter {
    private val doubleFormat = DecimalFormat("###,##0.######")

    @JvmStatic
    fun formatDouble(value: Double?): String =
        if (value == null) {
            ""
        } else {
            doubleFormat.format(value)
        }
}