package com.soarex.bashik.parser.analysis

import com.soarex.bashik.parser.betterparse.BetterParseLexer
import com.soarex.bashik.parser.lexer.Lexer
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.transformation.Transform

data class CommandDefinition(val envVars: Map<String, String> = emptyMap(), val command: String, val args: List<String>)

fun interface CommandParser {
    fun parse(tokens: Sequence<Token>): Sequence<CommandDefinition>
}

fun parse(
    lexer: Lexer = BetterParseLexer(),
    parser: CommandParser = DefaultParser(),
    transforms: List<Transform> = listOf(),
    input: String
): Sequence<CommandDefinition> {
    // TODO: exception handling
    val tokens = lexer.tokenize(input)
    val transformedTokens = transforms.fold(tokens) { tokenSequence, trans ->
        trans.transform(tokenSequence)
    }

    return parser.parse(transformedTokens)
}
