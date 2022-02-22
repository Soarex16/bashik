package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class TransformTokenTest {
    @ParameterizedTest
    @MethodSource("transformsTokenOfSpecifiedTypeTestData")
    fun transformsTokenOfSpecifiedType(
        transform: Transform,
        inputSequence: Sequence<Token>,
        expectedRefinedSequence: Sequence<Token>
    ) {
        val transformationResult = transform.transform(inputSequence)

        assertContentEquals(expectedRefinedSequence, transformationResult)
    }

    companion object {
        private val mockTokenSequence = sequenceOf<Token>(
            WordToken("HOME=\"/users/some_usr\""),
            WordToken("SOME_IMPORTANT_CONFIG=true"),
            WordToken("some_cmd"),
            WordToken("--verbosity=2"),
            MetaChar("|"),
            WordToken("remote_log_writer_cmd"),
            WordToken("--remote=127.0.0.1"),
            MetaChar("&&"),
            WordToken("echo"),
            WordToken("'backup complete'"),
        )

        @JvmStatic
        fun transformsTokenOfSpecifiedTypeTestData() = listOf(
            Arguments.of(
                transformToken<WordToken> { WordToken("TRANSFORMED") },
                mockTokenSequence,
                sequenceOf<Token>(
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    MetaChar("|"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    MetaChar("&&"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                ),
            ),
            Arguments.of(
                transformToken<MetaChar> { WordToken("TRANSFORMED") },
                mockTokenSequence,
                sequenceOf<Token>(
                    WordToken("HOME=\"/users/some_usr\""),
                    WordToken("SOME_IMPORTANT_CONFIG=true"),
                    WordToken("some_cmd"),
                    WordToken("--verbosity=2"),
                    WordToken("TRANSFORMED"),
                    WordToken("remote_log_writer_cmd"),
                    WordToken("--remote=127.0.0.1"),
                    WordToken("TRANSFORMED"),
                    WordToken("echo"),
                    WordToken("'backup complete'"),
                ),
            ),
            Arguments.of(
                transformToken<Token> { WordToken("TRANSFORMED") },
                mockTokenSequence,
                sequenceOf<Token>(
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                    WordToken("TRANSFORMED"),
                ),
            ),
        )
    }
}
