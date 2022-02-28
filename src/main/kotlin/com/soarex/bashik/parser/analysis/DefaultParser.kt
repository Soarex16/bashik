package com.soarex.bashik.parser.analysis

import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.VariableAssignmentToken
import com.soarex.bashik.separateBy

class UnparsedRemainderException : Exception()

/**
 * Default implementation of [CommandParser]
 */
class DefaultParser : CommandParser {
    override fun parse(tokens: Sequence<Token>): Sequence<CommandDefinition> = sequence {
        tokens
            .separateBy { it == OperatorToken.PIPE }
            .forEach { tokenSequence ->
                var commandTokens = tokenSequence.toList()

                if (commandTokens.isEmpty()) throw UnparsedRemainderException()

                // first parse env variables
                val commandEnvironment = commandTokens
                    .takeWhile { it is VariableAssignmentToken }
                    .map { it as VariableAssignmentToken }
                    .associate { Pair(it.varName, it.value) }

                commandTokens = commandTokens.drop(commandEnvironment.size)

                val cmd = commandTokens.firstOrNull()?.text ?: ""

                val args = commandTokens
                    .map { it.text }
                    .toList()

                yield(CommandDefinition(commandEnvironment, cmd, args))
            }
    }
}
