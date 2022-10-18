package khoa.nv.applocker.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockerPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setHiddenDrawingMode(hiddenDrawingPatternMode: Boolean) {
        sharedPref.edit() {
            putBoolean(KEY_IS_PATTERN_HIDDEN, hiddenDrawingPatternMode)
        }
    }

    fun getHiddenDrawingMode() = sharedPref.getBoolean(KEY_IS_PATTERN_HIDDEN, false)

    fun setFingerPrintEnable(fingerPrintEnabled: Boolean) {
        sharedPref.edit() {
            putBoolean(KEY_IS_FINGERPRINT_ENABLE, fingerPrintEnabled)
        }
    }

    fun getFingerPrintEnabled() = sharedPref.getBoolean(KEY_IS_FINGERPRINT_ENABLE, false)

    fun setIntrudersCatcherEnable(intrudersCatcherEnabled: Boolean) {
        sharedPref.edit() {
            putBoolean(KEY_IS_INTRUDERS_CATCHER_ENABLE, intrudersCatcherEnabled)
        }
    }

    fun getIntrudersCatcherEnabled() = sharedPref.getBoolean(
        KEY_IS_INTRUDERS_CATCHER_ENABLE, false
    )

    fun setUserRateUs() {
        sharedPref.edit() {
            putBoolean(KEY_RATE_US, true)
        }
    }

    fun isUserRateUs() = sharedPref.getBoolean(KEY_RATE_US, false)

    fun setSelectedBackgroundId(backgroundId: Int) {
        sharedPref.edit() {
            putInt(KEY_BACKGROUND_ID, backgroundId)
        }
    }

    fun getSelectedFakeAppId() = sharedPref.getInt(KEY_SELECTED_FAKE_APP_ID, 0)

    fun setSelectedFakeAppId(fakeAppId: Int) {
        sharedPref.edit() {
            putInt(KEY_SELECTED_FAKE_APP_ID, fakeAppId)
        }
    }

    fun getSelectedBackgroundId() = sharedPref.getInt(KEY_BACKGROUND_ID, 0)

    fun acceptPrivacyPolicy() {
        sharedPref.edit() {
            putBoolean(KEY_ACCEPT_PRIVACY_POLICY, true)
        }
    }

    fun isPrivacyPolicyAccepted() = sharedPref.getBoolean(KEY_ACCEPT_PRIVACY_POLICY, false)

    fun setCalculatorPassword(password: String) {
        sharedPref.edit() {
            putString(KEY_CALCULATOR_PASSWORD, password)
        }
    }

    fun getCalculatorPassword(): String? {
        return sharedPref.getString(KEY_CALCULATOR_PASSWORD, null)
    }

    fun setIsNewAppAlert(newAppAlert: Boolean) {
        sharedPref.edit() {
            putBoolean(KEY_IS_NEW_APP_ALERT, newAppAlert)
        }
    }

    fun getIsNewAppAlert() = sharedPref.getBoolean(KEY_IS_NEW_APP_ALERT, true)

    fun setServiceEnable(serviceEnabled: Boolean) = sharedPref.edit() {
        putBoolean(KEY_IS_SERVICE_ENABLE, serviceEnabled)
    }

    fun getServiceEnabled() = sharedPref.getBoolean(KEY_IS_SERVICE_ENABLE, false)

    companion object {

        private const val PREFERENCES_NAME = "AppLockerPreferences"

        private const val KEY_IS_PATTERN_HIDDEN = "KEY_IS_PATTERN_HIDDEN"

        private const val KEY_IS_FINGERPRINT_ENABLE = "KEY_IS_FINGERPRINT_ENABLE"

        private const val KEY_IS_INTRUDERS_CATCHER_ENABLE = "KEY_IS_INTRUDERS_CATCHER_ENABLE"

        private const val KEY_RATE_US = "KEY_RATE_US"

        private const val KEY_ACCEPT_PRIVACY_POLICY = "KEY_ACCEPT_PRIVACY_POLICY"

        private const val KEY_BACKGROUND_ID = "KEY_BACKGROUND_ID"

        private const val KEY_CALCULATOR_PASSWORD = "KEY_IS_CALCULATOR_PASSWORD_SET"

        private const val KEY_IS_NEW_APP_ALERT = "KEY_IS_NEW_APP_ALERT"

        private const val KEY_SELECTED_FAKE_APP_ID = "KEY_SELECTED_FAKE_APP_ID"

        private const val KEY_IS_SERVICE_ENABLE = "KEY_IS_SERVICE_ENABLE"
    }
}
//
//suspend fun setHiddenDrawingMode(hiddenDrawingPatternMode: Boolean) {
//    context.dataStore.edit { db ->
//        db[KEY_IS_PATTERN_HIDDEN] = hiddenDrawingPatternMode
//    }
//}
//
//fun getHiddenDrawingMode() = context.dataStore.data.map { preferences ->
//    preferences[KEY_IS_PATTERN_HIDDEN] ?: false
//}
//
//suspend fun setFingerPrintEnable(fingerPrintEnabled: Boolean) {
//    context.dataStore.edit { db ->
//        db[KEY_IS_FINGERPRINT_ENABLE] = fingerPrintEnabled
//    }
//}
//
//fun getFingerPrintEnabled() = context.dataStore.data.map { preferences ->
//    preferences[KEY_IS_FINGERPRINT_ENABLE] ?: false
//}
//
//suspend fun setIntrudersCatcherEnable(intrudersCatcherEnabled: Boolean) {
//    context.dataStore.edit { db ->
//        db[KEY_IS_INTRUDERS_CATCHER_ENABLE] = intrudersCatcherEnabled
//    }
//}
//
//fun getIntrudersCatcherEnabled() = context.dataStore.data.map { preferences ->
//    preferences[KEY_IS_INTRUDERS_CATCHER_ENABLE] ?: false
//}
//
//suspend fun setUserRateUs() {
//    context.dataStore.edit { db ->
//        db[KEY_RATE_US] = true
//    }
//}
//
//fun isUserRateUs() = context.dataStore.data.map { preferences ->
//    preferences[KEY_RATE_US] ?: false
//}
//
//suspend fun setSelectedBackgroundId(backgroundId: Int) {
//    context.dataStore.edit { db ->
//        db[KEY_BACKGROUND_ID] = backgroundId
//    }
//}
//
//fun getSelectedBackgroundId() = context.dataStore.data.map { preferences ->
//    preferences[KEY_BACKGROUND_ID] ?: 0
//}
//
//suspend fun acceptPrivacyPolicy() {
//    context.dataStore.edit { db ->
//        db[KEY_ACCEPT_PRIVACY_POLICY] = true
//    }
//}
//
//fun isPrivacyPolicyAccepted() = context.dataStore.data.map { preferences ->
//    preferences[KEY_ACCEPT_PRIVACY_POLICY] ?: false
//}
//
//companion object {
//
//    private val KEY_IS_PATTERN_HIDDEN = booleanPreferencesKey("KEY_IS_PATTERN_HIDDEN")
//
//    private val KEY_IS_FINGERPRINT_ENABLE = booleanPreferencesKey("KEY_IS_FINGERPRINT_ENABLE")
//
//    private val KEY_IS_INTRUDERS_CATCHER_ENABLE =
//        booleanPreferencesKey("KEY_IS_INTRUDERS_CATCHER_ENABLE")
//
//    private val KEY_RATE_US = booleanPreferencesKey("KEY_RATE_US")
//
//    private val KEY_ACCEPT_PRIVACY_POLICY = booleanPreferencesKey("KEY_ACCEPT_PRIVACY_POLICY")
//
//    private val KEY_BACKGROUND_ID = intPreferencesKey("KEY_BACKGROUND_ID")
//}