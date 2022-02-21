package com.soarex.bashik.parser.betterparse

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Tokenizer
import com.soarex.bashik.parser.lexer.Lexer
import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.UnknownTokenException
import com.soarex.bashik.parser.lexer.WordToken

/**
 * Лексический анализатор на основе парсер-комбинаторов
 */
class BetterParseLexer : Lexer {
    private val tokenizer: Tokenizer = DefaultTokenizer(
        listOf(
            wordToken,
            metaCharToken
        )
    )

    override fun tokenize(input: String): Sequence<Token> {
        val tokenMatches = tokenizer.tokenize(input)
        return tokenMatches.map {
            val text = it.text.trim()

            when (it.type) {
                wordToken -> WordToken(text)
                metaCharToken -> {
                    if (text.isBlank())
                        return@map null

                    MetaChar(text)
                }
                else -> throw UnknownTokenException(text, it.row, it.column)
            }
        }.filterNotNull()
    }
}
