package com.soarex.bashik.runtime

import com.soarex.bashik.collectEnv
import com.soarex.bashik.parser.analysis.BasicCommand
import com.soarex.bashik.runtime.io.ConsoleInputStream
import com.soarex.bashik.runtime.io.ConsoleOutputStream
import com.soarex.bashik.runtime.io.ProcessStreams
import com.soarex.bashik.runtime.io.pump
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintStream
import java.lang.Process as JProcess

class ExternalProcess(cmd: BasicCommand) : Process {
    private suspend fun redirectIO(proc: JProcess, io: ProcessStreams) = coroutineScope {
        launch {
            val procIn = ConsoleOutputStream(PrintStream(proc.outputStream))
            pump(io.stdin, procIn)
        }
        launch {
            val procOut = ConsoleInputStream(proc.inputStream)
            pump(procOut, io.stdout)
        }
        launch {
            val procErr = ConsoleInputStream(proc.errorStream)
            pump(procErr, io.stderr)
        }
    }

    override suspend fun invoke(ctx: ProcessContext): ProcessResult = withContext(Dispatchers.IO) {
        val builder = ProcessBuilder(ctx.args)

        builder.directory(ctx.workingDirectory.toFile())

        val allVariables = ctx.collectEnv()
        val envMap = builder.environment()
        allVariables.forEach {
            envMap[it.key] = it.value
        }

        val process = try {
            builder.start()
        } catch (e: RuntimeException) {
            throw ExternalProcessStartException(e)
        }

        redirectIO(process, ctx.io)

        process.waitFor().exitCode
    }
}

class ExternalProcessStartException(e: RuntimeException) : Throwable()
