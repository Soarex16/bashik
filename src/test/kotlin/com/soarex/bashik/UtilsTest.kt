package com.soarex.bashik

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class UtilsTest {

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

    @ParameterizedTest
    @MethodSource("separateByTestData")
    fun separateBy(
        inputSequence: Sequence<String>,
        separatorPredicate: (String) -> Boolean,
        expectedResult: Sequence<Sequence<String>>
    ) {
        val expectedResultList = expectedResult.toList()

        val actualResult = inputSequence
            .separateBy(separatorPredicate)
            .toList()

        assertEquals(expectedResultList.size, actualResult.size, "Length mismatch")
        expectedResultList
            .zip(actualResult)
            .forEach { (expected, actual) -> assertContentEquals(expected, actual) }
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
            Arguments.of(
                emptyList<IntRange>(),
                emptyList<IntRange>()
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
                emptyList<IntRange>(),
                listOf("hellohellohellohello"),
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

        @JvmStatic
        fun separateByTestData() = listOf(
            Arguments.of(
                sequenceOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "separator",
                    "5",
                    "6",
                    "7",
                    "8",
                ),
                { str: String -> str == "separator" },
                sequenceOf(
                    sequenceOf(
                        "1",
                        "2",
                        "3",
                        "4",
                    ),
                    sequenceOf(
                        "5",
                        "6",
                        "7",
                        "8",
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    "1",
                    "2",
                    "separator",
                    "3",
                    "4",
                    "separator",
                    "5",
                    "6",
                    "separator",
                ),
                { str: String -> str == "separator" },
                sequenceOf(
                    sequenceOf(
                        "1",
                        "2",
                    ),
                    sequenceOf(
                        "3",
                        "4",
                    ),
                    sequenceOf(
                        "5",
                        "6",
                    ),
                    emptySequence(),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    "1",
                    "2",
                    "3",
                ),
                { _: String -> true },
                sequenceOf<Sequence<String>>(
                    emptySequence(),
                    emptySequence(),
                    emptySequence(),
                    emptySequence(),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                ),
                { _: String -> false },
                sequenceOf(
                    sequenceOf(
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6",
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    "separator",
                    "separator",
                    "separator",
                ),
                { str: String -> str == "separator" },
                sequenceOf<Sequence<String>>(
                    emptySequence(),
                    emptySequence(),
                    emptySequence(),
                    emptySequence(),
                ),
            ),
            Arguments.of(
                emptySequence<String>(),
                { str: String -> str == "separator" },
                emptySequence<Sequence<String>>(),
            ),
        )
    }
}
