package com.soarex.bashik.runtime

/**
 * Generic interface for command definition
 */
fun interface Command : Process

/**
 * Helper function to create commands from suspend function and register in [CommandRegistry]
 *
 * @param commandDef coroutine command
 */
fun command(commandDef: suspend ProcessContext.() -> ProcessResult): Command {
    return Command { commandDef(it) }
}
