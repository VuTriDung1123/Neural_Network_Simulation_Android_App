package com.personal.neural_network_simulation_android_app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.personal.neural_network_simulation_android_app.core.NeuralNetwork
import kotlin.math.abs

@Composable
fun NeuralNetworkGraph(
    nn: NeuralNetwork,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Cấu hình vẽ: Input(1) + Hidden(3) + Output(1) = 5 cột
        // Lưu ý: nn.layers chỉ chứa Hidden & Output. Input là ảo.
        val totalVisualLayers = nn.layers.size + 1
        val columnWidth = width / (totalVisualLayers)
        val nodeRadius = 18f

        val nodePositions = mutableMapOf<Int, List<Offset>>()

        // --- 1. TÍNH VỊ TRÍ ---

        // A. Input Layer (Cột 0)
        val inputSize = nn.layers[0].inputSize
        // Canh giữa theo chiều dọc
        val inputStartY = (height - (inputSize * 80f)) / 2
        nodePositions[0] = List(inputSize) { i ->
            Offset(columnWidth * 0.5f, inputStartY + i * 80f)
        }

        // B. Hidden & Output Layers (Cột 1 -> n)
        nn.layers.forEachIndexed { index, layer ->
            val visualIndex = index + 1
            val x = columnWidth * (visualIndex + 0.5f)

            // Canh giữa dọc
            val startY = (height - (layer.neuronCount * 80f)) / 2
            nodePositions[visualIndex] = List(layer.neuronCount) { i ->
                Offset(x, startY + i * 80f)
            }
        }

        // --- 2. VẼ DÂY (WEIGHTS) ---
        nn.layers.forEachIndexed { layerIdx, layer ->
            val currentIdx = layerIdx + 1
            val prevIdx = layerIdx

            val currentNodes = nodePositions[currentIdx]!!
            val prevNodes = nodePositions[prevIdx]!!

            layer.neurons.forEachIndexed { neuronIdx, neuron ->
                val endPos = currentNodes[neuronIdx]
                prevNodes.forEachIndexed { prevNodeIdx, startPos ->
                    val weight = neuron.getWeight(prevNodeIdx)

                    // Màu sắc: Xanh/Đỏ
                    val color = if (weight > 0) Color.Green else Color.Red
                    // Độ đậm: Dựa vào giá trị weight
                    val stroke = (abs(weight) * 3).coerceIn(1.0, 8.0).toFloat()
                    // Độ mờ: Dây yếu thì mờ
                    val alpha = (abs(weight)).coerceIn(0.1, 0.8).toFloat()

                    drawLine(
                        color = color.copy(alpha = alpha),
                        start = startPos,
                        end = endPos,
                        strokeWidth = stroke
                    )
                }
            }
        }

        // --- 3. VẼ NODE & NHÃN LAYER ---
        nodePositions.forEach { (layerIdx, offsets) ->
            // Vẽ tên Layer ở trên cùng
            val layerName = when(layerIdx) {
                0 -> "INPUT"
                totalVisualLayers - 1 -> "OUTPUT"
                else -> "HIDDEN ${layerIdx}"
            }

            drawText(
                textMeasurer = textMeasurer,
                text = layerName,
                topLeft = Offset(offsets[0].x - 40f, 20f), // Cách đỉnh 20px
                style = TextStyle(color = Color.White, fontSize = 12.sp)
            )

            offsets.forEachIndexed { index, offset ->
                // Giá trị kích hoạt (Activation)
                val activation = if (layerIdx == 0) 0.5 else nn.layers[layerIdx - 1].neurons[index].output

                // Hiệu ứng "Đang suy nghĩ": Màu vàng rực nếu > 0.5
                val glowColor = if (activation > 0.5) Color.Yellow else Color.Gray

                // Vẽ Viền
                drawCircle(Color.White, radius = nodeRadius, center = offset, style = Stroke(2f))
                // Vẽ Nhân
                drawCircle(glowColor.copy(alpha = activation.toFloat()), radius = nodeRadius - 4f, center = offset)
            }
        }
    }
}