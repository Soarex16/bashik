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
    override fun parse(tokens: Sequence<Token>): CommandDefinition {
        if (!tokens.any())
            throw IllegalArgumentException()

        val childCommands = tokens
            .separateBy { it == OperatorToken.PIPE }
            .map { tokenSequence ->
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

                BasicCommand(commandEnvironment, cmd, args)
            }.toList()

        return if (childCommands.size == 1) childCommands.first() else Pipeline(childCommands)
    }
}
