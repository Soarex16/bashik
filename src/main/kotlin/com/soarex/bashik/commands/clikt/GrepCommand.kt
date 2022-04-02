package com.soarex.bashik.commands.clikt

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.defaultStdin
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import com.soarex.bashik.windowed
import java.nio.file.FileSystems
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

val grep = cliktCommand(GrepCommand())

class GrepCommand : CliktCommand(autoCompleteEnvvar = null) {
    private val caseInsensitive by option("-i").flag(default = false)

    private val wordsOnly by option("-w").flag(default = false)

    private val printNextLines by option("-A")
        .int()
        .restrictTo(min = 0)
        .default(0)

    private val regex by argument()
        .validate {
            try {
                Pattern.compile(it)
            } catch (e: PatternSyntaxException) {
                fail("Invalid regular expression")
            }
        }

    private val file by argument()
        .inputStream(FileSystems.getDefault())
        .defaultStdin()

    override fun run() {
        val re = prepareRegex()

        file.reader().windowed(1 + printNextLines) { lines ->
            val firstLine = lines.first()
            if (re.containsMatchIn(firstLine)) {
                lines.forEach { echo(it) }
                if (printNextLines > 0) {
                    echo("--")
                }
            }
        }
    }

    private fun prepareRegex(): Regex {
        val flags = if (caseInsensitive)
            setOf(RegexOption.UNIX_LINES, RegexOption.IGNORE_CASE)
        else
            setOf(RegexOption.UNIX_LINES)

        val pattern = if (wordsOnly) "\b${regex}\b" else regex

        return Regex(pattern, flags)
    }
}
