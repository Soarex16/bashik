package com.soarex.bashik.parser.analysis

import com.soarex.bashik.parser.betterparse.BetterParseLexer
import com.soarex.bashik.parser.lexer.Lexer
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.transformation.Transform

fun interface CommandParser {
    fun parse(tokens: Sequence<Token>): CommandDefinition
}

fun createParser(
    lexer: Lexer = BetterParseLexer(),
    parser: CommandParser = DefaultParser(),
    transforms: List<Transform> = listOf(),
): (String) -> CommandDefinition = { input ->
    val tokens = lexer.tokenize(input)
    val transformedTokens = transforms.fold(tokens) { tokenSequence, trans ->
        trans.transform(tokenSequence)
    }

    parser.parse(transformedTokens)
}
