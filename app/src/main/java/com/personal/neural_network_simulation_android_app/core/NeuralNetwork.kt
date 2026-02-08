package com.personal.neural_network_simulation_android_app.core

class NeuralNetwork(inputNodes: Int, hiddenNodes: Int, outputNodes: Int) {

    private val inputNodes = inputNodes
    private val hiddenNodes = hiddenNodes
    private val outputNodes = outputNodes

    private var weights_ih: Matrix = Matrix(hiddenNodes, inputNodes)
    private var weights_ho: Matrix = Matrix(outputNodes, hiddenNodes)

    private var bias_h: Matrix = Matrix(hiddenNodes, 1)
    private var bias_o: Matrix = Matrix(outputNodes, 1)

    var learningRate = 0.1

    init {
        weights_ih.randomize()
        weights_ho.randomize()
        bias_h.randomize()
        bias_o.randomize()
    }

    fun feedForward(inputArray: DoubleArray): List<Double> {
        val inputs = Matrix.fromArray(inputArray)
        var hidden = Matrix.multiply(weights_ih, inputs)
        hidden.add(bias_h)
        hidden.map(Activation.sigmoid)

        var output = Matrix.multiply(weights_ho, hidden)
        output.add(bias_o)
        output.map(Activation.sigmoid)

        return Matrix.toArray(output).toList()
    }

    fun train(inputArray: DoubleArray, targetArray: DoubleArray) {
        val inputs = Matrix.fromArray(inputArray)

        var hidden = Matrix.multiply(weights_ih, inputs)
        hidden.add(bias_h)
        hidden.map(Activation.sigmoid)

        var outputs = Matrix.multiply(weights_ho, hidden)
        outputs.add(bias_o)
        outputs.map(Activation.sigmoid)

        val targets = Matrix.fromArray(targetArray)
        val outputErrors = Matrix.subtract(targets, outputs)

        val gradients = Matrix.map(outputs, Activation.dSigmoid)
        gradients.multiply(outputErrors)
        gradients.multiply(learningRate)

        val hiddenT = Matrix.transpose(hidden)
        val weight_ho_deltas = Matrix.multiply(gradients, hiddenT)

        weights_ho.add(weight_ho_deltas)
        bias_o.add(gradients)

        val weights_ho_t = Matrix.transpose(weights_ho)
        val hiddenErrors = Matrix.multiply(weights_ho_t, outputErrors)

        val hiddenGradient = Matrix.map(hidden, Activation.dSigmoid)
        hiddenGradient.multiply(hiddenErrors)
        hiddenGradient.multiply(learningRate)

        val inputsT = Matrix.transpose(inputs)
        val weight_ih_deltas = Matrix.multiply(hiddenGradient, inputsT)

        weights_ih.add(weight_ih_deltas)
        bias_h.add(hiddenGradient)
    }
}