package com.soarex.bashik

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

val rootEnv = mapOf(
    "A" to "some_val"
).env

class NumberProducerCommand(val n: Int) : Command {
    override suspend fun invoke(ctx: ProcessContext): ProcessResult {
        for (x in 1..n) {
            println("sending $x")
            ctx.stdout.send("$x")
            delay(1.seconds)
        }

        ctx.stdout.close()
        return 0.exitCode
    }
}

class EchoCommand : Command {
    override suspend fun invoke(ctx: ProcessContext): ProcessResult {
        for (x in ctx.stdin) {
            ctx.stdout.send("echoed $x")
        }

        ctx.stdout.close()
        return 0.exitCode
    }
}

fun main() {
    /*val commonStderr = Channel<String>(10)

    val producerIO = StandardStreams(stderr = commonStderr)
    val echoIO = StandardStreams(stdin = producerIO.stdout, stderr = commonStderr)
    val producerCmdEnv = MutableEnvironmentImpl(parent = rootEnv)

    val echoCmdEnv = mapOf(
        "echoArg" to "echoVal"
    ).env(rootEnv)

    val producerCmdCtx = ProcessContext(
        env = producerCmdEnv,
        io = producerIO
    )
    val echoCmdCtx = ProcessContext(
        env = echoCmdEnv,
        io = echoIO
    )

    val producerCmd = NumberProducerCommand(10)
    val echoCmd = EchoCommand()

    runBlocking {
        launch {
            val result = producerCmd.invoke(producerCmdCtx)
            println("producer command finished with exit code ${result.exitCode}")
        }
        launch {
            val result = echoCmd.invoke(echoCmdCtx)
            println("echo command finished with exit code ${result.exitCode}")
        }
        for (x in echoIO.stdout) {
            println(x)
        }
    }*/
}
