package com.soarex.bashik.parser.betterparse

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.VariableAssignmentToken
import com.soarex.bashik.parser.transformation.Transform

val assignmentParser = nameToken * -eqToken * optional(wordToken) map { (name, value) ->
    VariableAssignmentToken(name.text, value?.text ?: "")
}

/*
преобразование метасимволов в операторы
when (text) {
    Operator.PIPE.text -> Operator.PIPE
    else ->
}*/

class TokenRefiner : Transform {
    override fun transform(input: Sequence<Token>): Sequence<Token> {
        TODO("Not yet implemented")
    }
}