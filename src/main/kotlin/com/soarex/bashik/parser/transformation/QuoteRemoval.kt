package com.soarex.bashik.parser.transformation

import com.soarex.bashik.alternateWith
import com.soarex.bashik.parser.betterparse.stringPattern
import com.soarex.bashik.parser.lexer.WordToken
import com.soarex.bashik.partsBetween

/**
 * Трансформация, которая раскрывает строки в кавычках.
 * Примеры:
 * - "abc" ==> abc
 * - "" ==> <пустая строка>
 * - "'single' 'quotes'" ==> 'single' 'quotes'
 * - '"double" "quotes"' ==> "double" "quotes"
 */
fun QuoteRemoval(): Transform = transformToken<WordToken> {
    val matches = stringPattern
        .toRegex()
        .findAll(it.text)

    val quotedStringPositions = matches
        .map { m -> m.range }
        .toList()

    val otherParts = it.text.partsBetween(quotedStringPositions)
    val unquotedStrings = matches
        .map { m -> m.value.substring(1 until m.value.length - 1) }
        .toList()

    val unquotedText = otherParts.alternateWith(unquotedStrings).joinToString("")

    return@transformToken WordToken(unquotedText)
}
