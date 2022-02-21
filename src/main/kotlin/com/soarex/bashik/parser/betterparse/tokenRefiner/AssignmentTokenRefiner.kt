package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.soarex.bashik.parser.betterparse.namePattern
import com.soarex.bashik.parser.betterparse.wordPattern
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.VariableAssignmentToken
import com.soarex.bashik.parser.lexer.WordToken

/**
 * Преобразует слова, пытаясь выделить присваивание переменных.
 * Данное преобразование является statefull и не может быть переиспользовано
 * между разными последовательностями токенов. Данное поведение позволяет
 * прекратить последующие преобразования при попытке обработать токен,
 * который не является присваиванием. Это сделано с целью соблюдения структуры команды:
 * ```
 * <NAME1>=<VAL1> <NAME2>=<VAL2> some_cmd arg1 arg2=some_val
 * ```
 *
 * В данном примере преобразование останавливается при попытке обработать слово `some_cmd`
 * и соответственно не интерпретирует слово `arg2=some_val` как присваивание
 */
class AssignmentTokenRefiner : TokenRefiner {
    private var variablesParsingDone = false

    companion object {
        private val assignmentGrammar = object : Grammar<VariableAssignmentToken>() {
            val nameToken by regexToken(namePattern)
            val wordToken by regexToken(wordPattern)
            val eqToken by literalToken("=")

            override val rootParser = nameToken * -eqToken * optional(wordToken) map { (name, value) ->
                VariableAssignmentToken(name.text, value?.text ?: "")
            }
        }
    }

    override fun isApplicable(tok: Token) = tok is WordToken

    override fun refine(tok: Token): VariableAssignmentToken? {
        if (variablesParsingDone)
            return null

        return when (val parseResult = assignmentGrammar.tryParseToEnd(tok.text)) {
            is Parsed -> parseResult.value
            is ErrorResult -> {
                variablesParsingDone = true
                null
            }
        }
    }
}
