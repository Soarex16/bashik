package com.soarex.bashik.commands.clikt

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import com.soarex.bashik.env
import com.soarex.bashik.runtime.ProcessContextImpl
import com.soarex.bashik.runtime.exitCode
import com.soarex.bashik.runtime.io.InputStream
import com.soarex.bashik.runtime.io.OutputStream
import com.soarex.bashik.runtime.io.ProcessStreams
import com.soarex.bashik.runtime.io.StringInputStream
import com.soarex.bashik.runtime.io.StringOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

internal class CliktCommandAdapterTest {

    @Test
    fun testCliktCommand() {
        val inputArgs = listOf("counter", "--max=10")
        val stdout = StringOutputStream()
        val stderr = StringOutputStream()

        val context = ProcessContextImpl(
            env = System.getenv().toMutableMap().env, // because we return [ProcessContext] it can't be modified
            args = inputArgs,
            io = object : ProcessStreams {
                override val stdin: InputStream<String> = StringInputStream(listOf())
                override val stdout: OutputStream<String> = stdout
                override val stderr: OutputStream<String> = stderr
            },
        )

        val command = cliktCommand(CounterCommand())
        val commandExecutionResult = runBlocking { command.invoke(context) }

        val expectedStdout = listOf<String>(
            "0\n",
            "1\n",
            "2\n",
            "3\n",
            "4\n",
            "5\n",
            "6\n",
            "7\n",
            "8\n",
            "9\n",
            "10\n",
        )
        val expectedStderr = emptyList<String>()

        assertEquals(0.exitCode, commandExecutionResult)
        assertContentEquals(expectedStderr, stderr.content)
        assertContentEquals(expectedStdout, stdout.content)
    }

    class CounterCommand : CliktCommand(autoCompleteEnvvar = null) {
        private val max by option("-m", "--max", help = "upper bound for counter")
            .int()
            .restrictTo(min = 0)
            .default(10)

        override fun run() {
            for (i in 0..max) {
                echo(i)
            }
        }
    }
}
