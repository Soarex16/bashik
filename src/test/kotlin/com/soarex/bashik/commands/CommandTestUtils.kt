package com.soarex.bashik.commands

import com.soarex.bashik.MutableEnvironment
import com.soarex.bashik.env
import com.soarex.bashik.runtime.*
import com.soarex.bashik.runtime.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

fun testCommand(
    command: Command,
    args: List<String> = emptyList(),
    envVars: MutableEnvironment = mutableMapOf<String, String>().env,
    inputStdio: List<String> = emptyList(),
    expectedStdout: List<String> = emptyList(),
    expectedStderr: List<String> = emptyList(),
    expectedCommandResult: ProcessResult = 0.exitCode
): ProcessContext {
    val stdin = StringInputStream(inputStdio)
    val stdout = StringOutputStream()
    val stderr = StringOutputStream()

    val mockIo = object : ProcessStreams {
        override val stdin: InputStream<String>
            get() = stdin

        override val stdout: OutputStream<String>
            get() = stdout

        override val stderr: OutputStream<String>
            get() = stderr
    }

    val parentCtx = ProcessContextImpl(
        env = envVars,
        parent = null,
        io = mockIo
    )
    val context = ProcessContextImpl(
        env = envVars,
        args = args,
        parent = parentCtx,
        io = parentCtx.io
    )

    val result = runBlocking {
        command(context)
    }

    assertEquals(expectedCommandResult, result)
    assertEquals(expectedStdout, stdout.content)
    assertEquals(expectedStderr, stderr.content)

    return context
}
