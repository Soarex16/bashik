package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.WordToken
import java.nio.file.Path

const val TILDE = "~"

fun TildeExpansion(home: Path): Transform = transformToken<WordToken> {
    if (it.text.startsWith(TILDE))
        WordToken(it.text.replaceFirst(TILDE, home.toAbsolutePath().toString()))
    else
        it
}
