package com.personal.neural_network_simulation_android_app.core

class NeuralNetwork(
    inputSize: Int,
    hiddenLayersConfig: List<Int>, // Ví dụ: listOf(4, 4) là 2 lớp ẩn, mỗi lớp 4 neuron
    outputSize: Int,
    var activation: ActivationFunction = Sigmoid // Mặc định là Sigmoid
) {
    val layers: MutableList<Layer> = mutableListOf()

    init {
        // 1. Khởi tạo Input -> Hidden Layer đầu tiên
        layers.add(Layer(inputSize, hiddenLayersConfig[0], activation))

        // 2. Khởi tạo các Hidden Layers tiếp theo (nếu có)
        for (i in 0 until hiddenLayersConfig.size - 1) {
            layers.add(Layer(hiddenLayersConfig[i], hiddenLayersConfig[i + 1], activation))
        }

        // 3. Khởi tạo Hidden -> Output Layer
        layers.add(Layer(hiddenLayersConfig.last(), outputSize, activation))
    }

    // --- FORWARD PROPAGATION ---
    fun predict(inputs: DoubleArray): DoubleArray {
        var currentInput = inputs
        for (layer in layers) {
            currentInput = layer.feedForward(currentInput)
        }
        return currentInput
    }

    // --- BACKPROPAGATION (Training Engine) ---
    // Đây là hàm "ăn tiền" nhất
    fun train(inputs: DoubleArray, targets: DoubleArray, learningRate: Double) {
        // Bước 1: Forward để lấy trạng thái hiện tại (output và hidden states)
        predict(inputs)

        // Bước 2: Tính Delta (Lỗi) cho Output Layer
        // Công thức: δ = (output - target) * f'(output)
        val outputLayer = layers.last()
        for (i in 0 until outputLayer.neuronCount) {
            val neuron = outputLayer.neurons[i]
            val error = neuron.output - targets[i]
            neuron.delta = error * outputLayer.activation.derivative(neuron.output)
        }

        // Bước 3: Lan truyền ngược lỗi về các Hidden Layers
        // Công thức: δ_hidden = (Σ δ_next * w_next) * f'(hidden)
        for (i in layers.size - 2 downTo 0) {
            val currentLayer = layers[i]
            val nextLayer = layers[i + 1]

            for (j in 0 until currentLayer.neuronCount) {
                val neuron = currentLayer.neurons[j]
                var sumError = 0.0
                // Tổng hợp lỗi từ lớp phía sau dội ngược về
                for (nextNeuron in nextLayer.neurons) {
                    sumError += nextNeuron.weights[j] * nextNeuron.delta
                }
                neuron.delta = sumError * currentLayer.activation.derivative(neuron.output)
            }
        }

        // Bước 4: Cập nhật Weights và Bias (Gradient Descent)
        // Công thức: w_new = w_old - learningRate * δ * input_prev
        var inputsForLayer = inputs // Input của layer đầu tiên là input của bài toán

        for (i in 0 until layers.size) {
            val layer = layers[i]

            for (neuron in layer.neurons) {
                // Update Weights
                for (j in neuron.weights.indices) {
                    val inputVal = inputsForLayer[j]
                    val gradient = neuron.delta * inputVal
                    neuron.weights[j] -= learningRate * gradient
                }
                // Update Bias
                neuron.bias -= learningRate * neuron.delta
            }

            // Input của layer tiếp theo chính là output của layer hiện tại (đã tính ở Bước 1)
            // Ta cần lấy lại giá trị output đó để dùng cho layer sau trong vòng lặp này
            inputsForLayer = DoubleArray(layer.neuronCount) { k -> layer.neurons[k].output }
        }
    }
}