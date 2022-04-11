package com.soarex.bashik.runtime

import com.soarex.bashik.Environment
import com.soarex.bashik.MutableEnvironment
import com.soarex.bashik.runtime.io.InputStream
import com.soarex.bashik.runtime.io.OutputStream
import com.soarex.bashik.runtime.io.ProcessStreams
import com.soarex.bashik.runtime.io.StandardStreams
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Represents running process context
 */
interface ProcessContext {
    val parent: ProcessContext?
    val env: Environment
    var workingDirectory: Path
    val args: List<String>
    val io: ProcessStreams

    val stdin: InputStream<String>
        get() = io.stdin

    val stdout: OutputStream<String>
        get() = io.stdout

    val stderr: OutputStream<String>
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
    override var workingDirectory: Path = parent?.workingDirectory ?: Path(System.getProperty("user.dir")),
    override val args: List<String> = emptyList(),
    override var io: ProcessStreams = parent?.io ?: StandardStreams()
) : MutableProcessContext
