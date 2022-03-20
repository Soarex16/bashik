package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken

/**
 * Трансформация, осуществляющая разделение слов по пробельным символам
 *
 * @param fieldSeparator строка, содержащая пробельные символы (и только их)
 */
class WordSplitting(fieldSeparator: String = " \t\n") : Transform {
    private val outsideSingleQuotesPredicate = "(?=(?:[^']|'[^']*')*\$)"
    private val outsideDoubleQuotesPredicate = "(?=(?:[^\"]|\"[^\"]*\")*\$)"
    private val separatorsOutsideQuotesPattern =
        "([$fieldSeparator]+)$outsideDoubleQuotesPredicate$outsideSingleQuotesPredicate"

    private val outsideQuotesRegex = separatorsOutsideQuotesPattern.toRegex()

    override fun transform(input: Sequence<Token>) = input.flatMap {
        when (it) {
            is WordToken -> {
                it.text
                    .trim()
                    .splitToSequence(outsideQuotesRegex)
                    .filter { w -> w.isNotBlank() }
                    .map { w -> WordToken(w) }
                    .toList()
            }
            else -> listOf(it)
        }
    }
}
