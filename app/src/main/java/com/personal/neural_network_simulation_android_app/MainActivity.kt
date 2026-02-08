package com.personal.neural_network_simulation_android_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import bộ icon cơ bản
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.neural_network_simulation_android_app.core.*
import com.personal.neural_network_simulation_android_app.ui.theme.Neural_Network_Simulation_Android_AppTheme
import com.personal.neural_network_simulation_android_app.ui.NeuralNetworkGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

// --- 1. ĐỊNH NGHĨA DỮ LIỆU CÁC BÀI LAB (ĐÃ SỬA ICON CƠ BẢN) ---
data class LabModule(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

// Sử dụng icon cơ bản (Share, Star, Lock...) thay vì icon nâng cao để tránh lỗi
val labModules = listOf(
    LabModule("neural_sim", "Neural Network Playground", "Quan sát não bộ suy nghĩ, trực quan hóa Weights & Biases.", Icons.Default.Share, Color(0xFF4CAF50)), // Share nhìn giống mạng lưới
    LabModule("activation", "Activation Functions", "So sánh Sigmoid, Tanh, ReLU, Leaky ReLU tác động đến hội tụ.", Icons.Default.Star, Color(0xFF2196F3)),
    LabModule("overfitting", "Overfitting & Regularization", "Thí nghiệm L1/L2 Regularization và Early Stopping.", Icons.Default.Lock, Color(0xFFE91E63)),
    LabModule("gradient", "Gradient Descent 3D", "Trực quan hóa Batch, Mini-batch, SGD trên bề mặt Loss.", Icons.Default.Refresh, Color(0xFFFF9800)),
    LabModule("scaling", "Feature Scaling", "Tại sao cần Standardization & Min-Max Scaling?", Icons.Default.Menu, Color(0xFF9C27B0)),
    LabModule("tuning", "Hyperparameter Tuning", "Grid Search vs Random Search để tìm tham số tối ưu.", Icons.Default.Settings, Color(0xFF00BCD4)),
    LabModule("bias_variance", "Bias-Variance Tradeoff", "Cân bằng giữa Underfitting và Overfitting.", Icons.Default.Warning, Color(0xFF607D8B)),
    LabModule("xai", "Explainable AI (XAI)", "Phân tích Feature Importance & Sensitivity.", Icons.Default.Search, Color(0xFFFF5722)),
    LabModule("unsupervised", "Unsupervised Learning", "K-means & Hierarchical Clustering gom cụm dữ liệu.", Icons.Default.List, Color(0xFF795548)),
    LabModule("rl", "Reinforcement Learning", "Q-learning Agent tự học cách đi trong mê cung.", Icons.Default.Face, Color(0xFF3F51B5)),
    LabModule("comparison", "Model Comparison", "Dashboard so sánh Accuracy, Loss, Time.", Icons.Default.Info, Color(0xFF009688)),
    LabModule("noise", "Noise & Robustness", "Test độ bền của Model với nhiễu Gaussian & Adversarial.", Icons.Default.Notifications, Color(0xFF9E9E9E)),
    LabModule("fairness", "Dataset Bias & Fairness", "Phát hiện Bias và phân phối lệch trong dữ liệu.", Icons.Default.Person, Color(0xFFE040FB))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val nn = NeuralNetwork(2, listOf(4, 5, 4), 1, Tanh)

        setContent {
            Neural_Network_Simulation_Android_AppTheme {
                var currentScreen by remember { mutableStateOf("HOME") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF101010)
                ) { innerPadding ->
                    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                        when (screen) {
                            "HOME" -> HomeScreen(
                                onNavigate = { id -> currentScreen = id },
                                modifier = Modifier.padding(innerPadding)
                            )
                            "neural_sim" -> PlaygroundScreen(
                                nn = nn,
                                onBack = { currentScreen = "HOME" },
                                modifier = Modifier.padding(innerPadding)
                            )
                            else -> ComingSoonScreen(
                                title = labModules.find { it.id == screen }?.title ?: "Unknown",
                                onBack = { currentScreen = "HOME" },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 2. MÀN HÌNH CHÍNH ---
@Composable
fun HomeScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "AI LABORATORY",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select a module to start experimenting",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(labModules) { module ->
                LabModuleCard(module, onClick = { onNavigate(module.id) })
            }
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }
}

@Composable
fun LabModuleCard(module: LabModule, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(module.color.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = module.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = module.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            // Đã đổi ChevronRight -> ArrowForward
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

// --- 3. MÀN HÌNH CHỜ ---
@Composable
fun ComingSoonScreen(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Đã đổi Construction -> Build
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Module Under Construction", color = Color.Gray, fontSize = 18.sp)
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBack) {
            Text("Back to Dashboard")
        }
    }
}

// --- 4. PLAYGROUND SCREEN ---
@Composable
fun PlaygroundScreen(
    nn: NeuralNetwork,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentProblem by remember { mutableStateOf(ProblemType.XOR) }
    var epoch by remember { mutableIntStateOf(0) }
    var refreshTrigger by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            withContext(Dispatchers.Default) {
                while (isRunning) {
                    val dataSet = Datasets.get(currentProblem)
                    repeat(50) {
                        val sample = dataSet[Random.nextInt(dataSet.size)]
                        val noisyInput = sample.first.map { value ->
                            val noise = Random.nextDouble() * 0.1 - 0.05
                            value + noise
                        }.toDoubleArray()
                        nn.train(noisyInput, sample.second, learningRate = 0.05)
                    }
                    epoch += 50
                    if (epoch > 500) {
                        delay(500)
                        currentProblem = when(currentProblem) {
                            ProblemType.XOR -> ProblemType.AND
                            ProblemType.AND -> ProblemType.COMPLEX
                            ProblemType.COMPLEX -> ProblemType.XOR
                        }
                        resetBrain(nn)
                        epoch = 0
                    }
                    refreshTrigger = System.currentTimeMillis()
                    delay(20)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (true) {
                if (!isRunning) {
                    val dataSet = Datasets.get(currentProblem)
                    val sample = dataSet[Random.nextInt(dataSet.size)]
                    nn.predict(sample.first)
                    refreshTrigger = System.currentTimeMillis()
                    delay(200)
                } else {
                    delay(1000)
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFF101010))) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI BRAIN SIMULATION", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Task:", color = Color.Gray, fontSize = 12.sp)
                    Text(currentProblem.title, color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Epoch", color = Color.Gray, fontSize = 12.sp)
                    Text("$epoch / 500", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)
                .background(Color.Black, shape = MaterialTheme.shapes.medium)
        ) {
            key(refreshTrigger) {
                NeuralNetworkGraph(nn)
            }
            Text(
                "3 Hidden Layers Structure",
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp),
                fontSize = 10.sp
            )
        }

        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.Red else Color.Green),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isRunning) "PAUSE" else "START SIMULATION")
            }
            Button(
                onClick = { resetBrain(nn); epoch = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.weight(0.5f)
            ) {
                Text("RESET")
            }
        }
    }
}

fun resetBrain(nn: NeuralNetwork) {
    nn.layers.forEach { layer ->
        layer.neurons.forEach { neuron ->
            for (i in neuron.weights.indices) {
                neuron.weights[i] = Random.nextDouble() * 2 - 1
            }
            neuron.bias = Random.nextDouble() * 2 - 1
        }
    }
}