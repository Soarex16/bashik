package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class OperatorRefinerTest {
    private val operatorRefiner = OperatorRefiner()

    @ParameterizedTest
    @MethodSource("isApplicableTestData")
    fun isApplicable(tok: Token, isApplicable: Boolean) {
        val isActualApplicable = operatorRefiner.isApplicable(tok)

        assertEquals(isApplicable, isActualApplicable)
    }

    @ParameterizedTest
    @MethodSource("refineTestData")
    fun refine(tok: Token, expectedRefineResult: Token?) {
        val actualResult = operatorRefiner.refine(tok)

        assertEquals(expectedRefineResult, actualResult)
    }

    companion object {
        @JvmStatic
        fun isApplicableTestData() = listOf(
            Arguments.of(WordToken("cmd"), false),
            Arguments.of(WordToken("||"), false),
            Arguments.of(MetaChar("||"), true),
            Arguments.of(MetaChar(""), true),
            Arguments.of(MetaChar("alsjdhakjshd"), true),
            Arguments.of(OperatorToken.PIPE, false),
        )

        @JvmStatic
        fun refineTestData() = listOf(
            Arguments.of(WordToken("cmd"), null),
            Arguments.of(WordToken("||"), null),
            Arguments.of(MetaChar("||"), null),
            Arguments.of(MetaChar(""), null),
            Arguments.of(MetaChar("alsjdhakjshd"), null),
            Arguments.of(MetaChar("|"), OperatorToken.PIPE),
        )
    }
}
