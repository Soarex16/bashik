package com.soarex.bashik

/**
 * Generic interface for command definition
 */
fun interface Command : Process

fun command(commandDef: suspend ProcessContext.() -> ProcessResult) = Command { commandDef(it) }
