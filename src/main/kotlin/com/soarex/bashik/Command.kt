package com.soarex.bashik

import com.soarex.bashik.runtime.CommandRegistry
import com.soarex.bashik.runtime.Process
import com.soarex.bashik.runtime.ProcessContext
import com.soarex.bashik.runtime.ProcessResult

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
