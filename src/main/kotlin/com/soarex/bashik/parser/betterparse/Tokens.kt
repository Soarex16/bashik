package com.soarex.bashik.parser.betterparse

import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

/**
 * Описание основных синтаксических единиц грамматики BASH
 *
 * Первичная обработка предполагает разбиение входной строки на токены двух типов:
 * - метасимволы - специальный набор символов (space, tab, newline, "|", "&", ";", "(", ")", "<", ">")
 * - слова - последовательность непробельных символов, которая не содержит неэкранированных метасимволов
 * (исключение составляют строки - набор символов, заключенные в одиночные или двойные кавычки)
 *
 * DISCLAIMER: я знаю, что ущербно и богомерзко описывать грамматику регулярными выражениями,
 * но bash сам по себе убогий и богомерзкий
 */

const val metaChars = " \t\r\n|&;()<>"
const val singleQuotedString = "\'[^']*\'"
const val doubleQuotedString = "\"(?:[^\"]|\\.)*\""

// https://regex101.com/r/F85jjs/2
// "(?:\"(?:[^\"]|\\.)*\"|\'[^']*\'|[^ \t\n|&;()<>])+"
val wordToken = regexToken(name = "word", "(?:${doubleQuotedString}|${singleQuotedString}|[^${metaChars}])+")
val metaCharToken = regexToken(name = "metaChar", "[${metaChars}]+")

val singleQuotedStringToken = regexToken(name = "singleQuotedString", singleQuotedString)
val doubleQuotedStringToken = regexToken(name = "doubleQuotedString", doubleQuotedString)

val nameToken = regexToken(name = "name", "[a-zA-Z_][\\w]*")
val eqToken = literalToken("=")