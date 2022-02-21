package com.soarex.bashik.parser.lexer

sealed interface Token {
    val text: String
}

data class WordToken(override val text: String) : Token

data class VariableAssignmentToken(val varName: String, val value: String) : Token {
    override val text: String
        get() = "$varName=$value"
}

@JvmInline
value class MetaChar(override val text: String) : Token

enum class Operator(override val text: String) : Token {
    PIPE("|"),
//    AND("&&"),
//    OR("||"),
}