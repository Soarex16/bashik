package com.soarex.bashik.runtime

import com.soarex.bashik.env
import com.soarex.bashik.parser.analysis.BasicCommand
import com.soarex.bashik.parser.analysis.CommandDefinition
import com.soarex.bashik.parser.analysis.Pipeline
import com.soarex.bashik.parser.analysis.UnknownCommandTypeException
import com.soarex.bashik.runtime.io.InputStream
import com.soarex.bashik.runtime.io.OutputStream
import com.soarex.bashik.runtime.io.ProcessStreams
import com.soarex.bashik.runtime.io.createPipe
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ProcessExecutorImpl : ProcessExecutor {
    override suspend fun runWithContext(ctx: ProcessContext, command: CommandDefinition): ProcessResult =
        when (command) {
            is BasicCommand -> runCommand(command, ctx)
            is Pipeline -> runPipeline(command, ctx)
            else -> throw UnknownCommandTypeException(command)
        }

    private suspend fun runCommand(def: BasicCommand, parentCtx: ProcessContext): ProcessResult {
        val ctx = ProcessContextImpl(
            parent = parentCtx,
            env = def.envVars.toMutableMap().env,
            args = def.args,
            io = parentCtx.io,
        )
        val command = CommandRegistry[def.command] ?: ExternalProcess(def)

        return try {
            command.invoke(ctx)
        } catch (e: CommandNotFoundException) {
            parentCtx.stdout.write("command not found: ${def.command}")
            return 127.exitCode
        }
    }

    private suspend fun runPipeline(def: Pipeline, parentCtx: ProcessContext): ProcessResult {
        var sink: InputStream<String> = parentCtx.io.stdin

        val contexts = def.commands.map {
            val (newSource, newSink) = createPipe<String>()

            val pipeElementContext = ProcessContextImpl(
                parent = parentCtx,
                env = it.envVars.toMutableMap().env,
                args = it.args,
                io = object : ProcessStreams {
                    override val stdin: InputStream<String> = sink
                    override val stdout: OutputStream<String> = newSource
                    override val stderr: OutputStream<String> = parentCtx.stderr
                },
            )

            sink = newSink

            return@map pipeElementContext
        }

        val lastPipeCommandContext = contexts.last()
        lastPipeCommandContext.io = object : ProcessStreams {
            override val stdin: InputStream<String> = lastPipeCommandContext.stdin
            override val stdout: OutputStream<String> = parentCtx.stdout
            override val stderr: OutputStream<String> = lastPipeCommandContext.stderr
        }

        return coroutineScope {
            def.commands
                .zip(contexts)
                .map { (cmd, ctx) -> async { runCommand(cmd, ctx) } }
                .last()
                .await()
        }
    }
}
