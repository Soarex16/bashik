package com.soarex.bashik

import kotlin.system.exitProcess

val echo = command {
    val out = args.joinToString(" ")
    stdout.send(out)
    0.exitCode
}

val pwd = command {
    stdout.send(workingDirectory.toString())
    0.exitCode
}

val exit = command {
    exitProcess(0)
}
