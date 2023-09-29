package com.example.composecalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
        }
    }

    // 43:23
    private fun performDeletion() {
        // Check if the state number 2 of is not blank so if we entered something for the second number
        // then we want to remove a character from this from the second number, so then state is stated copy again
        when {
            state.number2.isNotBlank() -> state = state.copy(
                number2 = state.number2.dropLast(1)
            )

            state.operation != null -> state = state.copy(
                operation = null
            )

            state.number1.isNotBlank() -> state.number1.dropLast(1)
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        // Check if number one and number two are not null
        if (number1 != null && number2 != null) {
            val result = when (state.operation) {
                is CalculatorOperation.Add -> number1 + number2
                is CalculatorOperation.Subtract -> number1 - number2
                is CalculatorOperation.Multiply -> number1 * number2
                is CalculatorOperation.Divide -> number1 / number2
                null -> return
            }
            // state is stated copy and now number one will become the result
            state = state.copy(
                number1 = result.toString().take(15)  ,
                // number two gets reset to nothing so we can enter it again
                number2 = "" ,
                // the operation get reset back to null
                operation = null
            )
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        // If we entered something for number one we want to apply the operation to our state
        if (state.number1.isNotBlank()) {
            // state.copy means that basically just create a copy of our state changes the operation the rest remains
            // the same and then it applies this whole new state with the changed operation to our existing one
            // without actually needing to make our single fields of the state mutable which would be not trigger a recomposition here
            state = state.copy(operation = operation)
        }

    }

    private fun enterDecimal() {
        // If the first number does not contain a decimal place already or the first number is not blank
        // then we know we could enter one theoretically
        // we want to check if the first number is now blank, so we cant enter the decimal place as the very first character
        if (state.operation == null && !state.number1.contains(".")
            && state.number1.isNotBlank()
        ) {
            state = state.copy(
                number1 = state.number1 + "."
            )
            return
        }
        if (!state.number2.contains(".") && state.number2.isNotBlank()
        ) {
            state = state.copy(
                number1 = state.number2 + "."
            )

        }
    }

    private fun enterNumber(number: Int) {
        if (state.operation == null) {
            if (state.number1.length >= MAX_NUM_LENGTH) {
                return
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if (state.number2.length >= MAX_NUM_LENGTH) {
            return
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}