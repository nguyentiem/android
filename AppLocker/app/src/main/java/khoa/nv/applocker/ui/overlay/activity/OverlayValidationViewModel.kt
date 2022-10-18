package khoa.nv.applocker.ui.overlay.activity

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.*
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.R
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.repository.PatternRepository
import khoa.nv.applocker.ui.overlay.FingerPrintResult
import khoa.nv.applocker.ui.overlay.OverlayValidateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverlayValidationViewModel @Inject constructor(
    private val app: Application,
    private val patternRepository: PatternRepository,
    private val appLockerPreferences: AppLockerPreferences
) : ViewModel() {
    private val _overlayValidateType = MutableLiveData<OverlayValidateType?>(null)
    private val _isDrawnCorrect = MediatorLiveData<Boolean?>().apply { value = null }
    private val _fingerPrintResultData = FingerPrintLiveData(app)
    private val _isHiddenDrawingMode = MutableLiveData(appLockerPreferences.getHiddenDrawingMode())
    private val _isFingerPrintMode = MutableLiveData(appLockerPreferences.getFingerPrintEnabled())
    private val _selectedBackground = MutableLiveData(appLockerPreferences.getSelectedBackgroundId())


    val overlayValidateType: LiveData<OverlayValidateType?> = _overlayValidateType
    val isDrawnCorrect: LiveData<Boolean?> = _isDrawnCorrect
    val isHiddenDrawingMode: LiveData<Boolean> = _isHiddenDrawingMode
    val isFingerPrintMode: LiveData<Boolean> = _isFingerPrintMode
    val selectedBackground: LiveData<Int> = _selectedBackground

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (appLockerPreferences.getFingerPrintEnabled()) {
                _isDrawnCorrect.addSource(_fingerPrintResultData) {
                    _overlayValidateType.postValue(OverlayValidateType.TYPE_FINGERPRINT)
                    _isDrawnCorrect.postValue(it?.isSuccess())
                }
            }
        }
    }

    fun onPatternCompleted(drawnPattern: String) {
        viewModelScope.launch(Dispatchers.IO) {
            patternRepository.getPattern().collect { savedPattern ->
                _isDrawnCorrect.postValue(savedPattern.pattern == drawnPattern)
                _overlayValidateType.postValue(OverlayValidateType.TYPE_PATTERN)
            }
        }
    }

    fun getHiddenDrawingMode() {
        _isHiddenDrawingMode.value = appLockerPreferences.getHiddenDrawingMode()
    }

    fun getFingerPrintMode() {
        _isFingerPrintMode.value = appLockerPreferences.getFingerPrintEnabled()
    }

    fun getSelectedBackgroundId() {
        _selectedBackground.value = appLockerPreferences.getSelectedBackgroundId()
    }

    fun getPromptMessage(context: Context, overlayValidateType: OverlayValidateType?): String {
        return when (overlayValidateType) {
            OverlayValidateType.TYPE_PATTERN -> {
                when (isDrawnCorrect.value) {
                    true -> context.getString(R.string.overlay_prompt_pattern_title_correct)
                    false -> context.getString(R.string.overlay_prompt_pattern_title_wrong)
                    null -> context.getString(R.string.overlay_prompt_pattern_title)
                }
            }
            OverlayValidateType.TYPE_FINGERPRINT -> {
                val fingerPrintData = _fingerPrintResultData.value
                when (fingerPrintData?.fingerPrintResult) {
                    FingerPrintResult.SUCCESS -> context.getString(R.string.overlay_prompt_fingerprint_title_correct)
                    FingerPrintResult.NOT_MATCHED -> context.getString(
                        R.string.overlay_prompt_fingerprint_title_wrong,
                        fingerPrintData.availableTimes.toString()
                    )
                    FingerPrintResult.ERROR -> context.getString(R.string.overlay_prompt_fingerprint_title_error)
                    else -> context.getString(R.string.overlay_prompt_fingerprint_title)
                }
            }
            else -> context.getString(R.string.overlay_prompt_pattern_title)
        }
    }

    fun getFingerPrintIconVisibility(isFingerPrintMode: Boolean?): Int =
        if (isFingerPrintMode == true) View.VISIBLE else View.INVISIBLE
}