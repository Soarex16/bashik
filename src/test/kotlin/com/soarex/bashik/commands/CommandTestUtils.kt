package com.soarex.bashik.commands

import com.soarex.bashik.MutableEnvironment
import com.soarex.bashik.env
import com.soarex.bashik.runtime.Command
import com.soarex.bashik.runtime.ProcessContextImpl
import com.soarex.bashik.runtime.ProcessResult
import com.soarex.bashik.runtime.exitCode
import com.soarex.bashik.runtime.io.InputStream
import com.soarex.bashik.runtime.io.OutputStream
import com.soarex.bashik.runtime.io.ProcessStreams
import com.soarex.bashik.runtime.io.StringInputStream
import com.soarex.bashik.runtime.io.StringOutputStream
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

fun testCommand(
    command: Command,
    args: List<String> = emptyList(),
    envVars: MutableEnvironment = mutableMapOf<String, String>().env,
    inputStdio: List<String> = emptyList(),
    expectedStdout: List<String>,
    expectedStderr: List<String> = emptyList(),
    expectedCommandResult: ProcessResult = 0.exitCode
) {
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

    val context = ProcessContextImpl(
        args = args,
        env = envVars,
        parent = null,
        io = mockIo
    )

    val result = runBlocking {
        command(context)
    }

    assertEquals(expectedCommandResult, result)
    assertEquals(expectedStdout, stdout.content)
    assertEquals(expectedStderr, stderr.content)
}
