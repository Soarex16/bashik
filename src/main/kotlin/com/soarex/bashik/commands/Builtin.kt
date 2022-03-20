package com.soarex.bashik.commands

import com.soarex.bashik.runtime.command
import com.soarex.bashik.runtime.exitCode
import com.soarex.bashik.runtime.io.forEach
import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.io.path.notExists
import kotlin.system.exitProcess

val cat = command {
    if (args.size == 1) {
        stdin.forEach {
            stdout.write(it)
        }
    } else {
        args.drop(1).forEach {
            val filePath = Path(it)

            if (filePath.notExists()) {
                stderr.write("cat: $it: No such file or directory")
                return@command 1.exitCode
            }

            filePath.forEachLine { l -> stdout.write(l) }
        }
    }

    return@command 0.exitCode
}

val echo = command {
    val out = args.drop(1).joinToString(" ")
    stdout.write(out)
    0.exitCode
}

val pwd = command {
    stdout.write(workingDirectory.toString())
    0.exitCode
}

data class WCStats(var lines: Int = 0, var words: Int = 0, var chars: Int = 0) {
    fun processLine(s: String) {
        lines += 1
        words += s.split("""\s+""").size
        chars += s.length
    }
}

val wc = command {
    val wcStats = mutableMapOf<String, WCStats>()

    if (args.size == 1) {
        val stats = WCStats()
        wcStats[""] = stats
        stdin.forEach {
            stats.processLine(it)
        }
    } else {
        args.drop(1).forEach {
            val filePath = Path(it)

            if (filePath.notExists()) {
                stderr.write("wc: $it: No such file or directory")
                return@command 1.exitCode
            }

            val stats = WCStats()
            wcStats[it] = stats
            filePath.forEachLine { l -> stats.processLine(l) }
        }
    }

    wcStats.forEach { (file, stats) ->
        stdout.write(
            stats.lines.toString().padStart(8) +
                stats.words.toString().padStart(8) +
                stats.chars.toString().padStart(8) +
                " " + file
        )
    }

    return@command 0.exitCode
}

val exit = command {
    exitProcess(0)
}
