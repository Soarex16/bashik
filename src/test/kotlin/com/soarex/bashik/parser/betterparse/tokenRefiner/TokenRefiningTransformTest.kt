package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class TokenRefiningTransformTest {
    @ParameterizedTest
    @MethodSource("transformTestData")
    fun transform(refiners: List<TokenRefiner>, tokens: Sequence<Token>, expectedTransformationResult: Sequence<Token?>) {
        val transform = TokenRefiningTransform(refiners)

        val transformationResult = transform.transform(tokens)

        assertContentEquals(expectedTransformationResult, transformationResult)
    }

    @Test
    fun throwsOnAmbiguousTransform() {
        val transform = TokenRefiningTransform(listOf(
            MockRefiner(),
            MockRefiner()
        ))

        val tokenSequence = sequenceOf(WordToken("foo"), WordToken("bar"))

        val exception = assertThrows<AmbiguousRefiningException> {
            transform.transform(tokenSequence).toList()
        }

        assertEquals(tokenSequence.first(), exception.token)
    }

    private class MockRefiner: TokenRefiner {
        override fun refine(tok: Token) = tok

        override fun isApplicable(tok: Token) = true
    }

    private class ConstRefiner(val refiningResult: Token?): TokenRefiner {
        override fun refine(tok: Token) = refiningResult

        override fun isApplicable(tok: Token) = true
    }

    private class MockRefinerIsApplicable(private val applicable: Boolean = true): TokenRefiner {
        override fun refine(tok: Token) = tok

        override fun isApplicable(tok: Token) = applicable
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
        fun transformTestData() = listOf(
            Arguments.of(
                listOf(MockRefiner()),
                sequenceOf<Token>(
                    WordToken("HOME=\"/users/some_usr\""),
                ),
                sequenceOf<Token>(
                    WordToken("HOME=\"/users/some_usr\""),
                ),
            ),
            Arguments.of(
                listOf(ConstRefiner(WordToken("transformed")), MockRefinerIsApplicable(false)),
                sequenceOf<Token>(
                    WordToken("HOME=\"/users/some_usr\""),
                    WordToken("SOME_IMPORTANT_CONFIG=true"),
                    WordToken("some_cmd"),
                ),
                sequenceOf<Token>(
                    WordToken("transformed"),
                    WordToken("transformed"),
                    WordToken("transformed"),
                ),
            ),
            Arguments.of(
                listOf(ConstRefiner(null)),
                mockTokenSequence,
                mockTokenSequence,
            ),
            Arguments.of(
                listOf(ConstRefiner(null), ConstRefiner(null), ConstRefiner(null)),
                mockTokenSequence,
                mockTokenSequence,
            ),
            Arguments.of(
                listOf(MockRefinerIsApplicable(false), MockRefinerIsApplicable(false)),
                mockTokenSequence,
                mockTokenSequence,
            ),
            Arguments.of(
                listOf(MockRefinerIsApplicable(false), MockRefinerIsApplicable(false)),
                mockTokenSequence,
                mockTokenSequence,
            ),
        )
    }
}