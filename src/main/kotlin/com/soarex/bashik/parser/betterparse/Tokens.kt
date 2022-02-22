package com.soarex.bashik.parser.betterparse

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

const val metaCharsPattern = " \t\r\n|&;()<>"
const val singleQuotedStringPattern = "\'[^']*\'"
const val doubleQuotedStringPattern = """"(?:[^\\"]|\\.)*""""

const val wordPattern = "(?:$doubleQuotedStringPattern|$singleQuotedStringPattern|[^$metaCharsPattern])+"
const val namePattern = "[a-zA-Z_][\\w]*"

// https://regex101.com/r/F85jjs/2
// "(?:\"(?:[^\"]|\\.)*\"|\'[^']*\'|[^ \t\n|&;()<>])+"
val wordToken = regexToken(name = "word", wordPattern)
val metaCharToken = regexToken(name = "metaChar", "[$metaCharsPattern]+")

val singleQuotedStringToken = regexToken(name = "singleQuotedString", singleQuotedStringPattern)
val doubleQuotedStringToken = regexToken(name = "doubleQuotedString", doubleQuotedStringPattern)

val nameToken = regexToken(name = "name", namePattern)
