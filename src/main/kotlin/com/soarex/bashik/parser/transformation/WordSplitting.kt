package com.soarex.bashik.parser.transformation

import com.soarex.bashik.Environment
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken

class WordSplitting(val envVars: Environment) : Transform {
    override fun transform(input: Sequence<Token>) = input.flatMap {
        when (it) {
            is WordToken -> it.text.split(" ").map { tok -> WordToken(tok) }
            else -> listOf(it)
        }
    }
}