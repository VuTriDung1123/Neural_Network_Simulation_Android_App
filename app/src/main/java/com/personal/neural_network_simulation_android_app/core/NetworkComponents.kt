package com.personal.neural_network_simulation_android_app.core

import kotlin.random.Random

// Neuron chứa trọng số (Weights) kết nối với lớp trước đó
data class Neuron(
    var weights: DoubleArray, // Các đường nối từ layer trước đến neuron này
    var bias: Double = Random.nextDouble() * 2 - 1,
    var output: Double = 0.0, // Giá trị a (activation) hiện tại
    var delta: Double = 0.0   // Giá trị lỗi dùng cho Backprop
) {
    // Để vẽ lên UI: Trả về độ đậm nhạt của đường nối thứ i
    fun getWeight(index: Int): Double = weights.getOrElse(index) { 0.0 }
}

// Layer chứa danh sách các Neuron
class Layer(
    val inputSize: Int, // Số lượng neuron của lớp trước
    val neuronCount: Int, // Số lượng neuron của lớp này
    val activation: ActivationFunction
) {
    val neurons: List<Neuron> = List(neuronCount) {
        // Khởi tạo weights ngẫu nhiên cho từng neuron
        Neuron(weights = DoubleArray(inputSize) { Random.nextDouble() * 2 - 1 })
    }

    // Forward: Tính toán output cho cả layer
    fun feedForward(inputs: DoubleArray): DoubleArray {
        val outputs = DoubleArray(neuronCount)

        neurons.forEachIndexed { i, neuron ->
            // 1. Tính tổng trọng số: z = Σ(w*x) + b
            var z = neuron.bias
            for (j in inputs.indices) {
                z += neuron.weights[j] * inputs[j]
            }
            // 2. Activation: a = f(z)
            neuron.output = activation.activate(z)
            outputs[i] = neuron.output
        }
        return outputs
    }
}