package com.soarex.bashik.parser.analysis

import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.VariableAssignmentToken
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class DefaultParserTest {

    val parser: CommandParser = DefaultParser()

    @ParameterizedTest
    @MethodSource("parseTestData")
    fun parse(tokens: Sequence<Token>, expectedResult: Sequence<CommandDefinition>) {
        val commands = parser.parse(tokens)

        assertContentEquals(expectedResult, commands)
    }

    @ParameterizedTest
    @MethodSource("parseThrowsOnIncorrectInputTestData")
    fun parseThrowsOnIncorrectInput(tokens: Sequence<Token>) {
        assertThrows<UnparsedRemainderException> {
            parser.parse(tokens).toList()
        }
    }

    companion object {
        @JvmStatic
        fun parseTestData() = listOf(
            Arguments.of(
                emptySequence<Token>(),
                emptySequence<CommandDefinition>(),
            ),
            Arguments.of(
                sequenceOf(
                    WordToken("cat"),
                    WordToken("test.txt"),
                ),
                sequenceOf(
                    CommandDefinition(
                        emptyMap(),
                        "cat",
                        listOf(
                            "cat",
                            "test.txt",
                        ),
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    WordToken("cat"),
                    WordToken("test.txt"),
                    OperatorToken.PIPE,
                    WordToken("grep"),
                    WordToken("some text"),
                ),
                sequenceOf(
                    CommandDefinition(
                        emptyMap(),
                        "cat",
                        listOf(
                            "cat",
                            "test.txt",
                        ),
                    ),
                    CommandDefinition(
                        emptyMap(),
                        "grep",
                        listOf(
                            "grep",
                            "some text",
                        ),
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    VariableAssignmentToken("HOME", "/some_volume/users/some_usr"),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'cd \$HOME; ls;'"),
                ),
                sequenceOf(
                    CommandDefinition(
                        mapOf(
                            "HOME" to "/some_volume/users/some_usr",
                        ),
                        "bash",
                        listOf(
                            "bash",
                            "-c",
                            "'cd \$HOME; ls;'",
                        ),
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    WordToken("echo"),
                    WordToken("-e"),
                    WordToken("\\nRemoving \\t backslash \\t characters\\n"),
                ),
                sequenceOf(
                    CommandDefinition(
                        emptyMap(),
                        "echo",
                        listOf(
                            "echo",
                            "-e",
                            "\\nRemoving \\t backslash \\t characters\\n",
                        ),
                    ),
                ),
            ),
            Arguments.of(
                sequenceOf(
                    WordToken("cat"),
                    WordToken("file1.txt"),
                    OperatorToken.PIPE,
                    WordToken("sort"),
                ),
                sequenceOf(
                    CommandDefinition(
                        emptyMap(),
                        "cat",
                        listOf(
                            "cat",
                            "file1.txt",
                        ),
                    ),
                    CommandDefinition(
                        emptyMap(),
                        "sort",
                        listOf(
                            "sort",
                        ),
                    ),
                ),
            ),
        )

        @JvmStatic
        fun parseThrowsOnIncorrectInputTestData() = listOf(
            Arguments.of(
                sequenceOf(
                    WordToken("cat"),
                    WordToken("file1.txt"),
                    OperatorToken.PIPE,
                    OperatorToken.PIPE,
                ),
            ),
        )
    }
}
