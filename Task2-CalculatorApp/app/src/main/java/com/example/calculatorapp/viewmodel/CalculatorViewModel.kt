package com.example.calculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class CalculatorState(
    val expression: String = "",
    val result: String = "0",
    val isScientificMode: Boolean = false,
    val history: List<String> = emptyList()
)

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private var currentInput = StringBuilder()
    private var lastOperation: String? = null
    private var lastResult: Double? = null
    private var justEvaluated = false

    fun toggleMode() {
        _state.value = _state.value.copy(isScientificMode = !_state.value.isScientificMode)
    }

    fun onDigit(digit: String) {
        if (justEvaluated) {
            currentInput.clear()
            justEvaluated = false
        }
        currentInput.append(digit)
        updateDisplay()
    }

    fun onDecimal() {
        if (justEvaluated) {
            currentInput.clear()
            currentInput.append("0")
            justEvaluated = false
        }
        if (!currentInput.contains('.')) {
            if (currentInput.isEmpty()) currentInput.append("0")
            currentInput.append('.')
        }
        updateDisplay()
    }

    fun onOperator(op: String) {
        val displayOp = when (op) {
            "+" -> " + "
            "-" -> " − "
            "*" -> " × "
            "/" -> " ÷ "
            "%" -> "%"
            else -> " $op "
        }

        if (currentInput.isNotEmpty()) {
            val expr = _state.value.expression
            val newExpr = if (justEvaluated) {
                "${_state.value.result}$displayOp"
            } else {
                "$expr${currentInput}$displayOp"
            }
            _state.value = _state.value.copy(expression = newExpr)
            lastOperation = op
            lastResult = currentInput.toString().toDoubleOrNull() ?: lastResult
            currentInput.clear()
            justEvaluated = false
        } else if (_state.value.expression.isNotEmpty()) {
            // Replace last operator
            val expr = _state.value.expression.trimEnd()
            val trimmed = expr.dropLastWhile { it == '+' || it == '−' || it == '×' || it == '÷' || it == ' ' }
            _state.value = _state.value.copy(expression = "$trimmed$displayOp")
            lastOperation = op
        }
    }

    fun onEquals() {
        val expression = _state.value.expression + currentInput.toString()
        if (expression.isBlank()) return

        try {
            val result = evaluateExpression(expression)
            val resultStr = formatResult(result)
            val historyEntry = "$expression = $resultStr"

            val newHistory = (_state.value.history + historyEntry).takeLast(20)

            _state.value = _state.value.copy(
                expression = expression,
                result = resultStr,
                history = newHistory
            )
            currentInput.clear()
            currentInput.append(resultStr)
            justEvaluated = true
            lastResult = result

        } catch (e: Exception) {
            _state.value = _state.value.copy(result = "Error")
        }
    }

    fun onClear() {
        currentInput.clear()
        lastOperation = null
        lastResult = null
        justEvaluated = false
        _state.value = _state.value.copy(expression = "", result = "0")
    }

    fun onDelete() {
        if (justEvaluated) {
            onClear()
            return
        }
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            updateDisplay()
        } else if (_state.value.expression.isNotEmpty()) {
            val expr = _state.value.expression.trimEnd()
            _state.value = _state.value.copy(expression = expr.dropLast(3).trimEnd() + " ")
        }
    }

    fun onPlusMinus() {
        if (currentInput.isEmpty() && _state.value.result != "0") {
            val value = _state.value.result.toDoubleOrNull() ?: return
            val toggled = if (value < 0) value.absoluteValue else -value
            currentInput.clear()
            currentInput.append(formatResult(toggled))
        } else if (currentInput.isNotEmpty()) {
            val value = currentInput.toString().toDoubleOrNull() ?: return
            val toggled = if (value < 0) value.absoluteValue else -value
            currentInput.clear()
            currentInput.append(formatResult(toggled))
        }
        updateDisplay()
    }

    fun onPercent() {
        val value = currentInput.toString().toDoubleOrNull() ?: return
        val percent = value / 100.0
        currentInput.clear()
        currentInput.append(formatResult(percent))
        updateDisplay()
    }

    // Scientific functions
    fun onScientificFunction(fn: String) {
        val value = currentInput.toString().toDoubleOrNull()
            ?: _state.value.result.toDoubleOrNull()
            ?: return

        val result: Double = when (fn) {
            "sin"  -> sin(Math.toRadians(value))
            "cos"  -> cos(Math.toRadians(value))
            "tan"  -> tan(Math.toRadians(value))
            "asin" -> Math.toDegrees(asin(value))
            "acos" -> Math.toDegrees(acos(value))
            "atan" -> Math.toDegrees(atan(value))
            "log"  -> log10(value)
            "ln"   -> ln(value)
            "sqrt" -> sqrt(value)
            "x²"   -> value.pow(2)
            "x³"   -> value.pow(3)
            "1/x"  -> 1.0 / value
            "e^x"  -> exp(value)
            "10^x" -> 10.0.pow(value)
            "π"    -> { currentInput.clear(); currentInput.append(formatResult(PI)); updateDisplay(); return }
            "e"    -> { currentInput.clear(); currentInput.append(formatResult(E)); updateDisplay(); return }
            "!"    -> factorial(value.toInt()).toDouble()
            else   -> return
        }

        val resultStr = formatResult(result)
        val expr = "$fn($value)"

        val historyEntry = "$expr = $resultStr"
        val newHistory = (_state.value.history + historyEntry).takeLast(20)

        _state.value = _state.value.copy(
            expression = expr,
            result = resultStr,
            history = newHistory
        )
        currentInput.clear()
        currentInput.append(resultStr)
        justEvaluated = true
    }

    private fun updateDisplay() {
        val input = currentInput.toString()
        _state.value = _state.value.copy(
            result = if (input.isEmpty()) "0" else input
        )
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble() && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            "%.10g".format(value).trimEnd('0').trimEnd('.')
        }
    }

    private fun factorial(n: Int): Long {
        if (n < 0) return 0
        if (n == 0 || n == 1) return 1
        var result = 1L
        for (i in 2..minOf(n, 20)) result *= i
        return result
    }

    /**
     * Delegates to ExprParser — a simple recursive-descent evaluator for +, −, ×, ÷
     */
    private fun evaluateExpression(expr: String): Double {
        val normalized = expr
            .replace(" ", "")
            .replace("−", "-")
            .replace("×", "*")
            .replace("÷", "/")
        return ExprParser(tokenize(normalized)).parseExpr()
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i++])
                    }
                    tokens.add(sb.toString())
                }
                c == '-' && (i == 0 || tokens.lastOrNull() in listOf("+", "-", "*", "/", "(")) -> {
                    val sb = StringBuilder("-")
                    i++
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i++])
                    }
                    tokens.add(sb.toString())
                }
                c in "+-*/()" -> { tokens.add(c.toString()); i++ }
                else -> i++
            }
        }
        return tokens
    }

    /** Recursive-descent parser; avoids Kotlin's forward-declaration limitation via a class. */
    private class ExprParser(private val tokens: List<String>) {
        private var pos = 0

        fun parseExpr(): Double {
            var left = parseTerm()
            while (pos < tokens.size && (tokens[pos] == "+" || tokens[pos] == "-")) {
                val op = tokens[pos++]
                val right = parseTerm()
                left = if (op == "+") left + right else left - right
            }
            return left
        }

        private fun parseTerm(): Double {
            var left = parseFactor()
            while (pos < tokens.size && (tokens[pos] == "*" || tokens[pos] == "/")) {
                val op = tokens[pos++]
                val right = parseFactor()
                left = if (op == "*") left * right else left / right
            }
            return left
        }

        private fun parseFactor(): Double {
            if (pos >= tokens.size) return 0.0
            val tok = tokens[pos]
            if (tok == "(") {
                pos++ // consume '('
                val value = parseExpr()
                if (pos < tokens.size && tokens[pos] == ")") pos++ // consume ')'
                return value
            }
            return tokens[pos++].toDouble()
        }
    }
}
