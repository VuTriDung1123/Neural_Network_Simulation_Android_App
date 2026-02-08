package com.personal.neural_network_simulation_android_app.core

import kotlin.math.exp

object Activation {
    // Sigmoid function
    val sigmoid: (Double) -> Double = { x ->
        1.0 / (1.0 + exp(-x))
    }

    // Derivative of Sigmoid (y = sigmoid(x))
    val dSigmoid: (Double) -> Double = { y ->
        y * (1.0 - y)
    }
}