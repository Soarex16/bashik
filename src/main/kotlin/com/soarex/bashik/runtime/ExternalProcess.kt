package com.soarex.bashik.runtime

import com.soarex.bashik.collectEnv
import com.soarex.bashik.parser.analysis.BasicCommand
import com.soarex.bashik.runtime.io.ProcessStreams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ExternalProcess(cmd: BasicCommand) : Process {
    private val stdin: File = File.createTempFile("${cmd.command}_stdin", null)
    private val stdout: File = File.createTempFile("${cmd.command}_stdout", null)
    private val stderr: File = File.createTempFile("${cmd.command}_stderr", null)

    private fun configureRedirects(builder: ProcessBuilder) {
        builder.redirectInput(ProcessBuilder.Redirect.from(stdin))
        builder.redirectOutput(ProcessBuilder.Redirect.to(stdout))
        builder.redirectError(ProcessBuilder.Redirect.to(stderr))
    }

    private fun mirrorOutput(io: ProcessStreams) {
        // TODO: make async io
        stdout.forEachLine { line -> runBlocking { io.stdout.write(line) } }
        stderr.forEachLine { line -> runBlocking { io.stderr.write(line) } }
    }

    override suspend fun invoke(ctx: ProcessContext): ProcessResult {
        val builder = ProcessBuilder(ctx.args)

        builder.directory(ctx.workingDirectory.toFile())

        val allVariables = ctx.collectEnv()
        val envMap = builder.environment()
        allVariables.forEach {
            envMap[it.key] = it.value
        }

        configureRedirects(builder)

        val code = withContext(Dispatchers.IO) {
            val process = builder.start()
            process.waitFor()
        }

        mirrorOutput(ctx.io)

        return code.exitCode
    }
}
