package com.soarex.bashik

import com.soarex.bashik.io.ProcessStreams
import com.soarex.bashik.io.StandardStreams
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.nio.file.Path
import java.nio.file.Paths

interface Process {
    suspend operator fun invoke(ctx: ProcessContext): ProcessResult
}

/**
 * Represents running process context
 */
data class ProcessContext(
    val env: Environment,
    val workingDirectory: Path = Paths.get("").toAbsolutePath(),
    val args: List<String> = emptyList(),
    val io: ProcessStreams = StandardStreams()
) {
    val stdin: ReceiveChannel<String> = io.stdin
    val stdout: SendChannel<String> = io.stdout
    val stderr: SendChannel<String> = io.stderr
}