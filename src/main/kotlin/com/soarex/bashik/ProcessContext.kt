package com.soarex.bashik

import com.soarex.bashik.io.ProcessStreams
import com.soarex.bashik.io.StandardStreams
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Represents running process context
 */
interface ProcessContext {
    val parent: ProcessContext?
    val env: Environment
    val workingDirectory: Path
    val args: List<String>
    val io: ProcessStreams

    val stdin: ReceiveChannel<String>
        get() = io.stdin

    val stdout: SendChannel<String>
        get() = io.stdout

    val stderr: SendChannel<String>
        get() = io.stderr
}

/**
 * Like [ProcessContext] but has [MutableEnvironment] as environment variables storage
 */
interface MutableProcessContext : ProcessContext {
    override val env: MutableEnvironment // можем мутировать свой контекст, но не родительский
}

/**
 * Default implementation for [ProcessContext]
 *
 * Note: if [parent] specified by default inherits [workingDirectory] and [io] from parent [ProcessContext]
 */
data class ProcessContextImpl(
    override val parent: ProcessContext? = null,
    override val env: MutableEnvironment,
    override val workingDirectory: Path = parent?.workingDirectory ?: Paths.get("").toAbsolutePath(),
    override val args: List<String> = emptyList(),
    override val io: ProcessStreams = parent?.io ?: StandardStreams()
) : MutableProcessContext
