package com.personal.neural_network_simulation_android_app.core

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.tanh

// Interface Strategy Pattern
interface ActivationFunction {
    val name: String
    fun activate(x: Double): Double
    fun derivative(y: Double): Double // y = f(x)
}

object Sigmoid : ActivationFunction {
    override val name = "Sigmoid"
    override fun activate(x: Double) = 1.0 / (1.0 + exp(-x))
    override fun derivative(y: Double) = y * (1.0 - y)
}

object ReLU : ActivationFunction {
    override val name = "ReLU"
    override fun activate(x: Double) = max(0.0, x)
    override fun derivative(y: Double) = if (y > 0) 1.0 else 0.0
}

object Tanh : ActivationFunction {
    override val name = "Tanh"
    override fun activate(x: Double) = tanh(x)
    override fun derivative(y: Double) = 1.0 - (y * y)
}