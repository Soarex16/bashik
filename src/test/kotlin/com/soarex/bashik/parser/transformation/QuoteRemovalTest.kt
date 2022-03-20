package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class QuoteRemovalTest {

    @ParameterizedTest
    @MethodSource("quoteRemovalTestData")
    fun quoteRemoval(inputSequence: Sequence<Token>, expectedTransformedSequence: Sequence<Token>) {
        val transform = QuoteRemoval()

        val transformationResult = transform.transform(inputSequence)

        assertContentEquals(expectedTransformedSequence, transformationResult)
    }

    companion object {
        @JvmStatic
        fun quoteRemovalTestData() = listOf(
            Arguments.of(
                sequenceOf(
                    WordToken("some_cmd"),
                    WordToken("~"),
                    WordToken("\"~/Downloads\""),
                    WordToken("\"~\""),
                    MetaChar("'||'"),
                    WordToken("'~/some_dir'"),
                    WordToken("'double \" quotes \" in single quoted string'"),
                    WordToken("\"single ' quotes ' in double quoted string\""),
                    WordToken("\""),
                    WordToken("'"),
                    WordToken("\"\""),
                    WordToken("''"),
                ),
                sequenceOf(
                    WordToken("some_cmd"),
                    WordToken("~"),
                    WordToken("~/Downloads"),
                    WordToken("~"),
                    MetaChar("'||'"),
                    WordToken("~/some_dir"),
                    WordToken("double \" quotes \" in single quoted string"),
                    WordToken("single ' quotes ' in double quoted string"),
                    WordToken("\""),
                    WordToken("'"),
                    WordToken(""),
                    WordToken(""),
                ),
            ),
        )
    }
}
