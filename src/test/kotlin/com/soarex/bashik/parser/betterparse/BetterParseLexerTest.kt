package com.soarex.bashik.parser.betterparse

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class BetterParseLexerTest {
    private val lexer = BetterParseLexer()

    @ParameterizedTest
    @MethodSource("testData")
    fun tokenize(input: String, expectedTokens: Sequence<Token>) {
        val tokens = lexer.tokenize(input)

        assertContentEquals(expectedTokens, tokens)
    }

    companion object {
        @JvmStatic
        fun testData() = listOf(
            Arguments.of(
                "SOME_VAR=\"string with space and some s/p/e/c/i/a/l symbols\" bash -c 'some_command come_arg | echo \$SOME_VAR'|cmd2 | some pipepipe",
                sequenceOf(
                    WordToken("SOME_VAR=\"string with space and some s/p/e/c/i/a/l symbols\""),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'some_command come_arg | echo \$SOME_VAR'"),
                    MetaChar("|"),
                    WordToken("cmd2"),
                    MetaChar("|"),
                    WordToken("some"),
                    WordToken("pipepipe"),
                ),
            ),
            Arguments.of(
                "echo hello",
                sequenceOf(
                    WordToken("echo"),
                    WordToken("hello"),
                )
            ),
            Arguments.of(
                "e'ch'\"o\" hello",
                sequenceOf(
                    WordToken("e'ch'\"o\""),
                    WordToken("hello"),
                ),
            ),
            Arguments.of(
                "wc -l",
                sequenceOf(
                    WordToken("wc"),
                    WordToken("-l"),
                ),
            ),
            Arguments.of(
                "wc -l --files0-from='file name.txt'",
                sequenceOf(
                    WordToken("wc"),
                    WordToken("-l"),
                    WordToken("--files0-from='file name.txt'"),
                ),
            ),
            Arguments.of(
                "cat /etc/hosts | wc -l",
                sequenceOf(
                    WordToken("cat"),
                    WordToken("/etc/hosts"),
                    MetaChar("|"),
                    WordToken("wc"),
                    WordToken("-l"),
                ),
            ),
            Arguments.of(
                "echo '\$x'\"\$x\"",
                sequenceOf(
                    WordToken("echo"),
                    WordToken("'\$x'\"\$x\""),
                ),
            ),
            Arguments.of(
                "FOO=bar bash -c 'somecommand someargs | somecommand2'",
                sequenceOf(
                    WordToken("FOO=bar"),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'somecommand someargs | somecommand2'"),
                ),
            ),
            Arguments.of(
                "FOO_X=foox bash -c 'echo \$FOO_X'",
                sequenceOf(
                    WordToken("FOO_X=foox"),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'echo \$FOO_X'"),
                ),
            ),
            Arguments.of(
                "cmd1 arg1 || cmd2 arg2 arg3",
                sequenceOf(
                    WordToken("cmd1"),
                    WordToken("arg1"),
                    MetaChar("||"),
                    WordToken("cmd2"),
                    WordToken("arg2"),
                    WordToken("arg3"),
                ),
            ),
        )
    }
}
