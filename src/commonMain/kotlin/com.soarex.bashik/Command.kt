package com.soarex.bashik

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

data class ProcessContext(
    val env: Environment,
    val streams: List<Channel<String>> = listOf(Channel(10), Channel(10), Channel(10))
) {
    val stdin: ReceiveChannel<String>
        get() = streams[0]

    val stdout: SendChannel<String>
        get() = streams[1]

    val stderr: SendChannel<String>
        get() = streams[2]
}

data class ExecutionResult(val exitCode: Int)

interface Process {
    val ctx: ProcessContext

    suspend fun run(): ExecutionResult
}

interface Command {
    fun execute(ctx: ProcessContext): Process
}