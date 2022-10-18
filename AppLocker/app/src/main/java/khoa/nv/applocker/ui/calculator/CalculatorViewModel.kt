package khoa.nv.applocker.ui.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.R
import khoa.nv.applocker.data.AppLockerPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val appLockerPreferences: AppLockerPreferences
) : ViewModel() {

    private val _firstState = MutableStateFlow<Double?>(null)
    private val _secondState = MutableStateFlow<Double?>(null)
    private val _outputState = MutableStateFlow<Double?>(null)
    private val _unaryOperatorState = MutableStateFlow(UnaryOperator.NONE)
    private val _binaryOperatorState = MutableStateFlow(BinaryOperator.NONE)
    private val _commandOperatorState = MutableStateFlow(CommandOperator.NONE)

    val firstState: StateFlow<Double?> = _firstState
    val secondState: StateFlow<Double?> = _secondState
    val outputState: StateFlow<Double?> = _outputState
    val unaryOperatorState: StateFlow<UnaryOperator> = _unaryOperatorState
    val binaryOperatorState: StateFlow<BinaryOperator> = _binaryOperatorState
    val commandOperatorState: StateFlow<CommandOperator> = _commandOperatorState

    private val _passwordState = MutableStateFlow(
        if (isPasswordSet())
            PasswordState.NOT_MATCHED
        else
            PasswordState.INIT
    )
    val passwordState: StateFlow<PasswordState> = _passwordState

    private var firstPassword: String? = null
    private var secondPassword: String? = null

    private fun setFirstPassword(password: String) {
        firstPassword = password
        _passwordState.value = PasswordState.FIRST_COMPLETED
    }

    private fun setSecondPassword(password: String) {
        if (firstPassword == password) {
            appLockerPreferences.setCalculatorPassword(password)
            _passwordState.value = PasswordState.SECOND_COMPLETED
        } else {
            firstPassword = null
            secondPassword = null
            _passwordState.value = PasswordState.ERROR
        }
    }

    fun setNumber(value: Double) {
        if (_binaryOperatorState.value == BinaryOperator.NONE) {
            _firstState.value = (_firstState.value ?: .0) * 10 + value
        } else {
            _secondState.value = (_secondState.value ?: .0) * 10 + value
        }
    }

    fun setUnaryOperator(operator: UnaryOperator) {
        val firstValue = _firstState.value ?: return
        when (operator) {
            UnaryOperator.PERCENT -> _outputState.value = firstValue / 100
            UnaryOperator.INVERSE -> _outputState.value = -firstValue
        }
        _firstState.value = _outputState.value
        _unaryOperatorState.value = UnaryOperator.NONE
    }

    fun setDot() {
        if (_unaryOperatorState.value == UnaryOperator.DOT)
            return
        _unaryOperatorState.value = UnaryOperator.DOT

    }

    fun setBinaryOperator(operator: BinaryOperator) {
        calculate()
        _binaryOperatorState.value = operator
    }

    fun setCommandOperator(operator: CommandOperator) {
        when (operator) {
            CommandOperator.CLEAR -> {
                resetNumbers()
            }
            CommandOperator.EQUAL -> {
                createPassword()
                calculate()
            }
        }
        resetOperators()
    }

    private fun calculate() {
        val firstValue = _firstState.value ?: return
        val secondValue = _secondState.value ?: return
        when (_binaryOperatorState.value) {
            BinaryOperator.MULTI -> _outputState.value = firstValue * secondValue
            BinaryOperator.DIVIDE -> _outputState.value = firstValue / secondValue
            BinaryOperator.ADD -> _outputState.value = firstValue + secondValue
            BinaryOperator.SUB -> _outputState.value = firstValue - secondValue
        }
        _firstState.value = _outputState.value
        _secondState.value = null
    }

    private fun resetOperators() {
        _unaryOperatorState.value = UnaryOperator.NONE
        _binaryOperatorState.value = BinaryOperator.NONE
        _commandOperatorState.value = CommandOperator.NONE
    }

    private fun resetNumbers() {
        _firstState.value = null
        _secondState.value = null
        _outputState.value = null
    }

    private fun createPassword() {
        if (isPasswordSet()) {
            checkPassword()
            return
        }

        if (firstPassword == null) {
            setFirstPassword(_firstState.value.toString())
        } else {
            setSecondPassword(_firstState.value.toString())
        }
        resetNumbers()
    }

    fun isPasswordSet() = appLockerPreferences.getCalculatorPassword() != null

    private fun checkPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            val input = _firstState.value.toString()
            val currentBinaryOperator = binaryOperatorState.value
            val savedPassword = appLockerPreferences.getCalculatorPassword()
            if (savedPassword == input && currentBinaryOperator == BinaryOperator.NONE) {
                _passwordState.value = PasswordState.MATCHED
            }
        }
    }

    enum class PasswordState {
        INIT, FIRST_COMPLETED, SECOND_COMPLETED, ERROR, MATCHED, NOT_MATCHED;

        fun getPromptText(context: Context): String =
            when (this) {
                INIT -> context.getString(R.string.draw_pattern_title)
                FIRST_COMPLETED -> context.getString(R.string.redraw_pattern_title)
                SECOND_COMPLETED -> context.getString(R.string.create_pattern_successful)
                ERROR -> context.getString(R.string.recreate_pattern_error)
                else -> ""
            }

        fun isPasswordCreated() = this == SECOND_COMPLETED

        fun isPasswordMatched() = this == MATCHED
    }

    enum class BinaryOperator(val symbol: String) {
        MULTI("*"), DIVIDE("/"), ADD("+"), SUB("-"), NONE("")
    }

    enum class UnaryOperator(val symbol: String) {
        PERCENT(""), INVERSE(""), DOT("."), NONE("")
    }

    enum class CommandOperator(val symbol: String) {
        CLEAR(""), EQUAL("="), NONE("")
    }
}