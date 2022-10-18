package khoa.nv.applocker.ui.background

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.data.AppLockerPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackgroundFragmentViewModel @Inject constructor(private val appLockerPreferences: AppLockerPreferences
) : ViewModel() {
    private val _backgroundState = MutableStateFlow(emptyList<GradientItemViewState>())
    val backgroundState: StateFlow<List<GradientItemViewState>> = _backgroundState

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedBackgroundId = appLockerPreferences.getSelectedBackgroundId()
            GradientBackgroundDataProvider.gradientViewStateList.apply {
                this[selectedBackgroundId].isChecked = true
            }.also {
                _backgroundState.value = it
            }
        }
    }

    fun onSelectedItemChanged(selected: GradientItemViewState) {
        appLockerPreferences.setSelectedBackgroundId(selected.id)
    }
}