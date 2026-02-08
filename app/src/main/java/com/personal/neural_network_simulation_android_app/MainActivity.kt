package com.personal.neural_network_simulation_android_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.neural_network_simulation_android_app.core.NeuralNetwork
import com.personal.neural_network_simulation_android_app.ui.theme.Neural_Network_Simulation_Android_AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.random.Random

// Dữ liệu training nằm ở đây (CHỈ ĐƯỢC KHAI BÁO 1 LẦN)
val trainingData = listOf(
    Pair(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0)),
    Pair(doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0)),
    Pair(doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0)),
    Pair(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val nn = NeuralNetwork(2, 4, 1)

        setContent {
            Neural_Network_Simulation_Android_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NeuralNetworkScreen(nn, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NeuralNetworkScreen(nn: NeuralNetwork, modifier: Modifier = Modifier) {
    var epoch by remember { mutableIntStateOf(0) }
    var p00 by remember { mutableDoubleStateOf(0.0) }
    var p01 by remember { mutableDoubleStateOf(0.0) }
    var p10 by remember { mutableDoubleStateOf(0.0) }
    var p11 by remember { mutableDoubleStateOf(0.0) }

    val scope = rememberCoroutineScope()
    var isTraining by remember { mutableStateOf(false) }

    fun startTraining() {
        isTraining = true
        scope.launch(Dispatchers.Default) {
            while (isTraining) {
                val data = trainingData[Random.nextInt(trainingData.size)]
                nn.train(data.first, data.second)

                if (epoch % 50 == 0) {
                    val r00 = nn.feedForward(doubleArrayOf(0.0, 0.0))[0]
                    val r01 = nn.feedForward(doubleArrayOf(0.0, 1.0))[0]
                    val r10 = nn.feedForward(doubleArrayOf(1.0, 0.0))[0]
                    val r11 = nn.feedForward(doubleArrayOf(1.0, 1.0))[0]
                    withContext(Dispatchers.Main) {
                        p00 = r00; p01 = r01; p10 = r10; p11 = r11
                    }
                }
                epoch++
                if (epoch % 1000 == 0) delay(1)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("XOR Neural Network", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Epoch: $epoch", fontSize = 18.sp)
        Spacer(Modifier.height(20.dp))

        ResultRow("0, 0", 0.0, p00)
        ResultRow("0, 1", 1.0, p01)
        ResultRow("1, 0", 1.0, p10)
        ResultRow("1, 1", 0.0, p11)

        Spacer(Modifier.height(20.dp))
        Button(onClick = { if (isTraining) isTraining = false else startTraining() }) {
            Text(if (isTraining) "Stop" else "Start Training")
        }
    }
}

@Composable
fun ResultRow(input: String, target: Double, predicted: Double) {
    val error = abs(target - predicted)
    val color = Color((error * 2).coerceAtMost(1.0).toFloat(), ((1.0 - error) * 2).coerceAtMost(1.0).toFloat(), 0f, 0.3f)
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(input)
            Text("Target: ${target.toInt()}")
            Text("Pred: ${String.format("%.4f", predicted)}")
        }
    }
}