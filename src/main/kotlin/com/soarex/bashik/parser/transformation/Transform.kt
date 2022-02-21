package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.Token

/**
 * Трансформация - один из этапов парсинга, например, variable expansion,
 * command execution, word splitting
 *
 * Трансформация осуществляется после лексического анализа, но
 * перед синтасическим анализом (парсингом)
 */
fun interface Transform {
    fun transform(input: Sequence<Token>): Sequence<Token>
}

inline fun <reified TokenType> transformToken(crossinline transform: (token: TokenType) -> Token): Transform {
    return Transform {
        it.map { tok ->
            when (tok) {
                is TokenType -> transform(tok)
                else -> tok
            }
        }
    }
}