package com.soarex.bashik

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class StringUtilsTest {

    @ParameterizedTest
    @MethodSource("gapsTestData")
    fun gaps(ranges: List<IntRange>, expectedResult: List<IntRange>) {
        val actualParts = ranges.gaps()

        assertContentEquals(expectedResult, actualParts)
    }

    @ParameterizedTest
    @MethodSource("partsBetweenTestData")
    fun partsBetween(inputString: String, ranges: List<IntRange>, expectedResult: List<String>) {
        val actualParts = inputString.partsBetween(ranges)

        assertContentEquals(expectedResult, actualParts)
    }

    @ParameterizedTest
    @MethodSource("alternateWithTestData")
    fun alternateWith(first: List<String>, second: List<String>, expectedResult: List<String>) {
        val actualResult = first.alternateWith(second).toList()

        assertContentEquals(expectedResult, actualResult)
    }

    companion object {
        @JvmStatic
        fun gapsTestData() = listOf(
            Arguments.of(
                listOf(
                    2..4,
                    5..8,
                    11..14,
                    20..27
                ),
                listOf(
                    @Suppress()
                    5 until 5, // this range should be empty
                    9 until 11,
                    15 until 20
                )
            ),
        )

        @JvmStatic
        fun partsBetweenTestData() = listOf(
            Arguments.of(
                "hello world this is a test data",
                listOf(
                    2..4,
                    5..8,
                    11..14,
                    20..27
                ),
                listOf(
                    "he",
                    "",
                    "ld",
                    "s is ",
                    "ata"
                ),
            ),
            Arguments.of(
                "hellohellohellohello",
                listOf(
                    0 until 5,
                    5 until 10,
                    10 until 15,
                    15 until 20
                ),
                listOf(
                    "",
                    "",
                    "",
                    "",
                    ""
                ),
            ),
        )

        @JvmStatic
        fun alternateWithTestData() = listOf(
            Arguments.of(
                emptyList<String>(),
                listOf(
                    "1",
                    "2",
                    "3",
                    "4"
                ),
                listOf(
                    "1",
                    "2",
                    "3",
                    "4",
                ),
            ),
            Arguments.of(
                listOf(
                    "1",
                    "2",
                    "3",
                    "4"
                ),
                emptyList<String>(),
                listOf(
                    "1",
                    "2",
                    "3",
                    "4",
                ),
            ),
            Arguments.of(
                listOf(
                    "hello",
                    "world",
                ),
                listOf(
                    "1",
                    "2",
                    "3",
                    "4"
                ),
                listOf(
                    "hello",
                    "1",
                    "world",
                    "2",
                    "3",
                    "4",
                ),
            ),
            Arguments.of(
                listOf(
                    "1",
                    "2",
                    "3",
                    "4"
                ),
                listOf(
                    "hello",
                    "world",
                ),
                listOf(
                    "1",
                    "hello",
                    "2",
                    "world",
                    "3",
                    "4",
                ),
            ),
            Arguments.of(
                listOf(
                    "hello",
                    "world",
                    "this",
                    "is",
                    "test"
                ),
                listOf(
                    "1",
                    "2",
                    "3",
                    "4"
                ),
                listOf(
                    "hello",
                    "1",
                    "world",
                    "2",
                    "this",
                    "3",
                    "is",
                    "4",
                    "test"
                ),
            ),
        )
    }
}
