package com.soarex.bashik.runtime

import com.soarex.bashik.parser.analysis.CommandDefinition

fun interface ProcessExecutor {
    suspend fun runWithContext(ctx: ProcessContext, command: CommandDefinition): ProcessResult
}
