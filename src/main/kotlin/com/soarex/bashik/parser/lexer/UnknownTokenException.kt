package com.soarex.bashik.parser.lexer

class UnknownTokenException(token: String, row: Int, column: Int) : Exception("Unknown token $token at $row:$column")