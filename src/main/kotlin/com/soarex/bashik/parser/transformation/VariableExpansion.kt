package com.soarex.bashik.parser.transformation

import com.soarex.bashik.Environment
import com.soarex.bashik.parser.lexer.Token

class VariableExpansion(val envVars: Environment) : Transform {
    override fun transform(input: Sequence<Token>): Sequence<Token> = TODO("Not yet implemented")
}
