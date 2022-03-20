package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.betterparse.namePattern
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
        private val nameRegex = Regex(namePattern)

        private const val eqPattern = "="
    }

    override fun isApplicable(tok: Token) = tok is WordToken

    private fun parseFailed(): VariableAssignmentToken? {
        variablesParsingDone = true
        return null
    }

    override fun refine(tok: Token): VariableAssignmentToken? {
        if (variablesParsingDone)
            return null

        val text = tok.text

        if (!text.contains(eqPattern))
            return parseFailed()

        val tokens = tok.text.split(eqPattern, limit = 2)
        if (tokens.size != 2) return parseFailed()

        if (!nameRegex.matches(tokens[0]))
            return parseFailed()

        return VariableAssignmentToken(tokens[0], tokens[1])
    }
}
