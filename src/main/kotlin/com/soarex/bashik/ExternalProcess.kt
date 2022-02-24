package com.soarex.bashik

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExternalProcess(private val cmd: String) : Process {
    private fun configureRedirects(builder: ProcessBuilder) {
        // TODO
        builder.redirectInput()
        builder.redirectOutput()
        builder.redirectError()
    }

    override suspend fun invoke(ctx: ProcessContext): ProcessResult {
        val builder = ProcessBuilder(cmd, *ctx.args.toTypedArray())

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

        return code.exitCode
    }
}
