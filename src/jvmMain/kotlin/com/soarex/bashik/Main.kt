package com.soarex.bashik

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

val rootEnv = mapOf(
    "A" to "some_val"
).env

class NumberProducerCommand(val n: Int): Command {
    override fun execute(ctx: ProcessContext) = object : Process {
        override val ctx: ProcessContext
            get() = ctx

        override suspend fun run(): ExecutionResult {
            for (x in 1..n) {
                println("sending $x")
                ctx.stdout.send("$x")
                delay(1.seconds)
            }

            ctx.stdout.close()
            return ExecutionResult(0)
        }
    }
}

class EchoCommand: Command {
    override fun execute(ctx: ProcessContext) = object : Process {
        override val ctx: ProcessContext
            get() = ctx

        override suspend fun run(): ExecutionResult {
            for (x in ctx.stdin) {
                ctx.stdout.send("echoed $x")
            }

            ctx.stdout.close()
            return ExecutionResult(0)
        }
    }
}

fun main() {
    val commonStderr = Channel<String>(10)

    val producerStdin = Channel<String>(10)
    val producerStdout = Channel<String>(10)
    val producerStderr = commonStderr

    val echoStdin = producerStdout
    val echoStdout = Channel<String>(10)
    val echoStderr = commonStderr

    val producerCmdEnv = Environment(parent = rootEnv)

    val echoCmdEnv = mapOf(
        "echoArg" to "echoVal"
    ).env(rootEnv)

    val producerCmdCtx = ProcessContext(producerCmdEnv, listOf(producerStdin, producerStdout, producerStderr))
    val echoCmdCtx = ProcessContext(echoCmdEnv, listOf(echoStdin, echoStdout, echoStderr))

    val producerCmd = NumberProducerCommand(10)
    val echoCmd = EchoCommand()

    val producerProcess = producerCmd.execute(producerCmdCtx)
    val echoProcess = echoCmd.execute(echoCmdCtx)

    runBlocking {
        launch {
            val result = producerProcess.run()
            println("producer command finished with exit code ${result.exitCode}")
        }
        launch {
            val result = echoProcess.run()
            println("echo command finished with exit code ${result.exitCode}")
        }
        for (x in echoStdout) {
            println(x)
        }
    }
}