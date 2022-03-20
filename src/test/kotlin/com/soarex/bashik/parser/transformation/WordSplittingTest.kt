package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class WordSplittingTest {

    @ParameterizedTest
    @MethodSource("transformTestData")
    fun transform(separators: String, inputSequence: Sequence<Token>, expectedRefinedSequence: Sequence<Token>) {
        val transform = WordSplitting(separators)

        val transformationResult = transform.transform(inputSequence)

        assertContentEquals(expectedRefinedSequence, transformationResult)
    }

    companion object {
        @JvmStatic
        fun transformTestData() = listOf(
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("abc def"),
                ),
                sequenceOf(
                    WordToken("abc"),
                    WordToken("def"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("cat 'hello world.txt'"),
                    OperatorToken.PIPE,
                    WordToken("echo"),
                ),
                sequenceOf(
                    WordToken("cat"),
                    WordToken("'hello world.txt'"),
                    OperatorToken.PIPE,
                    WordToken("echo"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    OperatorToken.PIPE,
                    MetaChar("||"),
                    MetaChar("some text with spaces"),
                ),
                sequenceOf(
                    OperatorToken.PIPE,
                    MetaChar("||"),
                    MetaChar("some text with spaces"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("abc"),
                ),
                sequenceOf(
                    WordToken("abc"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("'single quoted'  "),
                ),
                sequenceOf(
                    WordToken("'single quoted'"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("\"double quoted\"  "),
                ),
                sequenceOf(
                    WordToken("\"double quoted\""),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("\"mixed quotes\'  "),
                    WordToken("\'mixed quotes\"  "),
                ),
                sequenceOf(
                    // Эти слова некорректны (непарные кавычки), поэтому не будут обработаны
                    WordToken("\"mixed quotes\'"),
                    WordToken("\'mixed quotes\""),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("\"mixed quotes'    'some text\""),
                ),
                sequenceOf(
                    WordToken("\"mixed quotes'    'some text\""),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("  "),
                ),
                emptySequence<Token>(),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("\n\n\n"),
                ),
                emptySequence<Token>(),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("multi\nline\ntext"),
                ),
                sequenceOf(
                    WordToken("multi"),
                    WordToken("line"),
                    WordToken("text"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("multiple\n\n\nseparators\t\t\tat   once"),
                ),
                sequenceOf(
                    WordToken("multiple"),
                    WordToken("separators"),
                    WordToken("at"),
                    WordToken("once"),
                ),
            ),
            Arguments.of(
                " \t\n",
                sequenceOf(
                    WordToken("M_VAL=\"usr/share/\"'applications'"),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'cd \$M_VAL; ls;'"),
                    MetaChar("&&"),
                    WordToken("echo"),
                    WordToken("\"finished\""),
                ),
                sequenceOf(
                    WordToken("M_VAL=\"usr/share/\"'applications'"),
                    WordToken("bash"),
                    WordToken("-c"),
                    WordToken("'cd \$M_VAL; ls;'"),
                    MetaChar("&&"),
                    WordToken("echo"),
                    WordToken("\"finished\""),
                ),
            ),
        )
    }
}
