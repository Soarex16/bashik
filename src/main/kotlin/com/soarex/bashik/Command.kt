package com.soarex.bashik

/**
 * Generic interface for command definition
 */
fun interface Command : Process

/**
 * Helper function to create commands from suspend function
 *
 * @param commandDef coroutine command
 */
fun command(commandDef: suspend ProcessContext.() -> ProcessResult) = Command { commandDef(it) }
