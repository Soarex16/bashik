package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token

/**
 * Преобразования, осуществляющее выделение операторов из потока метасимволов
 */
class OperatorRefiner : TokenRefiner {
    private val operatorMapping = OperatorToken.values().associateBy { it.text }

    override fun isApplicable(tok: Token) = tok is MetaChar

    override fun refine(tok: Token): OperatorToken? = operatorMapping[tok.text]
}
