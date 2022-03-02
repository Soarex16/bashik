package com.soarex.bashik.parser.analysis

import com.soarex.bashik.parser.betterparse.BetterParseLexer
import com.soarex.bashik.parser.lexer.Lexer
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.transformation.Transform

fun interface CommandParser {
    fun parse(tokens: Sequence<Token>): CommandDefinition
}

fun parse(
    lexer: Lexer = BetterParseLexer(),
    parser: CommandParser = DefaultParser(),
    transforms: List<Transform> = listOf(),
    input: String
): CommandDefinition {
    // TODO: exception handling
    val tokens = lexer.tokenize(input)
    val transformedTokens = transforms.fold(tokens) { tokenSequence, trans ->
        trans.transform(tokenSequence)
    }

    return parser.parse(transformedTokens)
}
