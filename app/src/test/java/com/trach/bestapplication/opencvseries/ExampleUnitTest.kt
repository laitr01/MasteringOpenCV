package com.trach.bestapplication.opencvseries

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun hourglassSum() {
        val arr = arrayOf(
            arrayOf(1, 1, 1, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0),
            arrayOf(1, 1, 1, 0, 0, 0),
            arrayOf(0, 0, 2, 4, 4, 0),
            arrayOf(0, 0, 0, 2, 0, 0),
            arrayOf(0, 0, 1, 2, 4, 0)
        )
        var result = Integer.MIN_VALUE
        for (i in 0..arr.size - 3) {
            for (j in 0..arr[i].size - 3) {
                val hourglassSum =
                    arr[i][j] + arr[i][j + 1] + arr[i][j + 2] + arr[i + 1][j + 1] + arr[i + 2][j] + arr[i + 2][j + 1] + arr[i + 2][j + 2]
                if (hourglassSum > result) {
                    result = hourglassSum
                }
            }
        }

        assertEquals(19, result)
    }

    @Test
    fun dynamicArray() {
        val queries = arrayOf(
            arrayOf(1, 0, 5),
            arrayOf(1, 1, 7),
            arrayOf(1, 0, 3),
            arrayOf(2, 1, 0),
            arrayOf(2, 1, 1)
        )
        var lastAnswer1 = 0
        var lastAnswer2 = 0
        for (i in 0 until queries.size) {
            val number = queries[i][2]
            if (number % 2 == 0) {
                lastAnswer1 = number
            } else {
                lastAnswer2 = number
            }
        }

        assertArrayEquals( arrayOf(7,3), arrayOf(lastAnswer1, lastAnswer2))
    }
}
