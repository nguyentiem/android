package khoa.nv.applocker.ui.newpattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.data.database.pattern.PatternEntity
import khoa.nv.applocker.repository.PatternRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewPatternViewModel @Inject constructor(private val patternRepository: PatternRepository) :
    ViewModel() {

    enum class PatternEvent {
        INITIALIZE, FIRST_COMPLETED, SECOND_COMPLETED, ERROR
    }

    private val _patternEventState =
        MutableStateFlow(CreateNewPatternViewState(PatternEvent.INITIALIZE))
    val patternEventState: StateFlow<CreateNewPatternViewState> = _patternEventState

    private var firstPattern: String? = null
    private var secondPattern: String? = null


    fun setFirstPattern(pattern: String?) {
        pattern?.let {
            firstPattern = pattern
            _patternEventState.value = CreateNewPatternViewState(PatternEvent.FIRST_COMPLETED)
        }
    }

    fun setSecondPattern(pattern: String?) {
        pattern?.let {
            secondPattern = pattern
            if (firstPattern == secondPattern) {
                saveNewCreatedPattern(pattern)
                _patternEventState.value = CreateNewPatternViewState(PatternEvent.SECOND_COMPLETED)
            } else {
                firstPattern = null
                secondPattern = null
                _patternEventState.value = CreateNewPatternViewState(PatternEvent.ERROR)
            }
        }
    }

    fun isFirstPattern(): Boolean = firstPattern == null

    private fun saveNewCreatedPattern(pattern: String) {
        patternRepository.createPattern(PatternEntity(pattern)).launchIn(viewModelScope)
    }
}