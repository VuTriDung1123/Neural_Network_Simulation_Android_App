package com.personal.neural_network_simulation_android_app.core

enum class ProblemType(val title: String) {
    XOR("Bài toán XOR (Khó)"),
    AND("Bài toán AND (Dễ)"),
    COMPLEX("Bài toán Phức tạp")
}

object Datasets {
    // 1. XOR: Chỉ đúng khi 1 trong 2 là 1 (0,1 hoặc 1,0)
    val xorData = listOf(
        Pair(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0)),
        Pair(doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0)),
        Pair(doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0)),
        Pair(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0))
    )

    // 2. AND: Chỉ đúng khi cả 2 là 1 (1,1)
    val andData = listOf(
        Pair(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0)),
        Pair(doubleArrayOf(0.0, 1.0), doubleArrayOf(0.0)),
        Pair(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0)),
        Pair(doubleArrayOf(1.0, 1.0), doubleArrayOf(1.0))
    )

    // 3. COMPLEX: Bài toán bịa ra để thử thách (Input > 0.5 thì Output đổi chiều)
    val complexData = listOf(
        Pair(doubleArrayOf(0.1, 0.1), doubleArrayOf(0.0)),
        Pair(doubleArrayOf(0.9, 0.9), doubleArrayOf(1.0)),
        Pair(doubleArrayOf(0.1, 0.9), doubleArrayOf(0.5)), // Output lửng lơ
        Pair(doubleArrayOf(0.8, 0.2), doubleArrayOf(0.8))
    )

    fun get(type: ProblemType) = when(type) {
        ProblemType.XOR -> xorData
        ProblemType.AND -> andData
        ProblemType.COMPLEX -> complexData
    }
}