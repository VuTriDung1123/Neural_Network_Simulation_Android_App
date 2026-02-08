package com.personal.neural_network_simulation_android_app.core

import java.util.Random

data class Matrix(val rows: Int, val cols: Int) {
    var data: Array<DoubleArray> = Array(rows) { DoubleArray(cols) }

    init {
        randomize()
    }

    // Khởi tạo ngẫu nhiên từ -1 đến 1
    fun randomize() {
        val random = Random()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] = random.nextDouble() * 2 - 1
            }
        }
    }

    // Phép cộng với một số (Scalar)
    fun add(scaler: Double) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] += scaler
            }
        }
    }

    // Phép cộng ma trận (Element-wise)
    fun add(m: Matrix) {
        if (cols != m.cols || rows != m.rows) {
            println("Shape Mismatch in Add")
            return
        }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] += m.data[i][j]
            }
        }
    }

    // Nhân từng phần tử (Hadamard product) - Dùng cho Gradient
    fun multiply(m: Matrix) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] *= m.data[i][j]
            }
        }
    }

    // Nhân với một số (Scalar) - Dùng cho Learning Rate
    fun multiply(scaler: Double) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] *= scaler
            }
        }
    }

    // Áp dụng hàm số cho từng phần tử (Dùng cho Activation)
    fun map(func: (Double) -> Double) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val value = data[i][j]
                data[i][j] = func(value)
            }
        }
    }

    companion object {
        // Phép nhân ma trận (Dot Product) - Cốt lõi
        fun multiply(a: Matrix, b: Matrix): Matrix {
            if (a.cols != b.rows) {
                throw IllegalArgumentException("Cols A must match Rows B")
            }
            val result = Matrix(a.rows, b.cols)
            for (i in 0 until result.rows) {
                for (j in 0 until result.cols) {
                    var sum = 0.0
                    for (k in 0 until a.cols) {
                        sum += a.data[i][k] * b.data[k][j]
                    }
                    result.data[i][j] = sum
                }
            }
            return result
        }

        // Chuyển mảng thành ma trận
        fun fromArray(arr: DoubleArray): Matrix {
            val m = Matrix(arr.size, 1)
            for (i in arr.indices) {
                m.data[i][0] = arr[i]
            }
            return m
        }

        // Chuyển ma trận thành mảng
        fun toArray(m: Matrix): DoubleArray {
            val temp = DoubleArray(m.rows * m.cols)
            var k = 0
            for (i in 0 until m.rows) {
                for (j in 0 until m.cols) {
                    temp[k++] = m.data[i][j]
                }
            }
            return temp
        }

        // Phép trừ ma trận (Tính sai số Error)
        fun subtract(a: Matrix, b: Matrix): Matrix {
            val result = Matrix(a.rows, a.cols)
            for (i in 0 until a.rows) {
                for (j in 0 until a.cols) {
                    result.data[i][j] = a.data[i][j] - b.data[i][j]
                }
            }
            return result
        }

        // Chuyển vị ma trận (Transpose) - Cần cho Backpropagation
        fun transpose(m: Matrix): Matrix {
            val result = Matrix(m.cols, m.rows)
            for (i in 0 until m.rows) {
                for (j in 0 until m.cols) {
                    result.data[j][i] = m.data[i][j]
                }
            }
            return result
        }

        // Map function trả về ma trận mới (Static)
        fun map(m: Matrix, func: (Double) -> Double): Matrix {
            val result = Matrix(m.rows, m.cols)
            for (i in 0 until m.rows) {
                for (j in 0 until m.cols) {
                    result.data[i][j] = func(m.data[i][j])
                }
            }
            return result
        }
    }
}