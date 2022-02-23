package com.soarex.bashik.parser.transformation

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.soarex.bashik.Environment
import com.soarex.bashik.alternateWith
import com.soarex.bashik.parser.betterparse.doubleQuotedStringPattern
import com.soarex.bashik.parser.betterparse.namePattern
import com.soarex.bashik.parser.betterparse.singleQuotedStringPattern
import com.soarex.bashik.parser.lexer.WordToken
import com.soarex.bashik.partsBetween

/**
 * Трансформация, осуществляющая подстановку значений переменных и обработку поведения по-умолчанию в случае,
 * если переменная отсутствует в окружении
 */
fun VariableExpansion(envVars: Environment): Transform = transformToken<WordToken> {
    val tokens = variableSubstitutionGrammar.tokenizer.tokenize(it.text)

    when (val substitutions = variableSubstitutionGrammar.tryParse(tokens, 0)) {
        is Parsed -> {
            val ranges = substitutions.value.map { subs -> subs.range }
            val partsBetween = it.text.partsBetween(ranges)
            val substitutionValues = substitutions.value.map { subs -> expandParameter(envVars, subs) }

            val substitutedText = partsBetween.alternateWith(substitutionValues).joinToString("")

            WordToken(substitutedText)
        }
        is ErrorResult -> it
    }
}

/**
 * Тип поведения при подстановке
 *
 * @param type оператор, соответствующий типу подстановки
 */
private enum class SubstitutionKind(val type: String) {
    /**
     * Подстановка значения, указанного справа от ":-" в случае, если переменная пустая
     */
    DEFAULT(":-"),

    /**
     * В случае, если переменная пустая, то осуществляется запись значения по-умолчанию в окружение и подстановка
     * В текущий момент не поддерживается
     */
    ASSIGN(":="),

    /**
     * В случае, если переменная пустая, то в stderr выводится сообщение, указанное справа от ":?"
     * В текущий момент не поддерживается
     */
    LOG_STDERR(":?")
}

/**
 * Описание подстановки
 *
 * @param varName имя переменной
 * @param range диапазон в исходной строке, занимаемый описанием подстановки
 * @param defaultValue опциональное описание поведения при отсутствии значения переменной
 */
private data class VariableExpansionDefinition(
    val varName: String,
    val range: IntRange,
    val defaultValue: DefaultValue?
)

/**
 * Описание поведения по-умолчанию
 *
 * @param value значение, указанное справа от оператора ":-"
 * @param substitutionKind поведение при подстановке
 */
private data class DefaultValue(val value: String, val substitutionKind: SubstitutionKind)

/**
 * Сколько символов занимает описание поведения по-умолчанию
 */
private val DefaultValue?.length
    inline get() = if (this == null) 0 else substitutionKind.type.length + value.length

/**
 * Формирует подставляемое значение на основе окружения и описания подстановки
 *
 * @param envVars окружение
 * @param subs описание подстановки
 *
 * @return значение, которым будет заменено описание подстановки
 */
private fun expandParameter(envVars: Environment, subs: VariableExpansionDefinition): String {
    val variableValue = envVars[subs.varName]
    return if (variableValue.isNotBlank() || subs.defaultValue == null) {
        variableValue
    } else {
        when (subs.defaultValue.substitutionKind) {
            SubstitutionKind.DEFAULT -> subs.defaultValue.value
            SubstitutionKind.ASSIGN -> throw NotImplementedError()
            SubstitutionKind.LOG_STDERR -> throw NotImplementedError()
        }
    }
}

/**
 * Грамматика для описания синтаксиса подстановки параметров.
 * Аналогична следующему набору правил в формате BNF:
 * <SUBSTITUTION> ::= "$" <NAME> | "${" <NAME> [<DEFAULT_BEHAVIOUR_OPERATOR> <WORD>] "}"
 * <DEFAULT_BEHAVIOUR_OPERATOR> ::= ":-" | ":=" | ":?"
 */
private val variableSubstitutionGrammar = object : Grammar<List<VariableExpansionDefinition>>() {
    val singleQuotedString by regexToken(singleQuotedStringPattern)

    val dollar by literalToken("$")
    val lBrace by literalToken("{")
    val rBrace by literalToken("}")

    val defaultValueOperator by literalToken(":-")
    val assignValueOperator by literalToken(":=")
    val logStderrOperator by literalToken(":?")

    val defaultBehaviorOperator by
    (defaultValueOperator map { SubstitutionKind.DEFAULT }) or
        (assignValueOperator map { SubstitutionKind.ASSIGN }) or
        (logStderrOperator map { SubstitutionKind.LOG_STDERR })

    val word by regexToken("(?:$doubleQuotedStringPattern|$singleQuotedStringPattern)+")
    val name by regexToken(namePattern)

    val defaultValueSubstitution by defaultBehaviorOperator * optional(word) map { (op, value) ->
        DefaultValue(value?.text ?: "", op)
    }

    val other by regexToken("[^{}$]+")
    val separator by name or word or other or singleQuotedString or defaultValueOperator

    val withBraces by -(dollar * lBrace) * name * optional(defaultValueSubstitution) * -rBrace map
        { (name, defaultVal) ->
            VariableExpansionDefinition(
                name.text,
                // len(name) - len("${") .. len(name) + optional(len(":-") + len(value)) + len("}")
                name.offset - 2 until name.offset + name.length + defaultVal.length + 1,
                defaultVal
            )
        }

    val withoutBraces by -dollar * name use {
        VariableExpansionDefinition(
            text,
            this.offset - 1 until this.offset + this.length,
            null
        )
    }

    val trash by zeroOrMore(separator)

    override val rootParser by -trash * separatedTerms(
        withBraces or withoutBraces,
        trash
    ) * -trash
}
