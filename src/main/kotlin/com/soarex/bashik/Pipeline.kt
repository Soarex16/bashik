package com.soarex.bashik

import com.soarex.bashik.io.StandardStreams
import kotlinx.coroutines.channels.Channel

// Pipeline отвечает за формирование последовательности процессов
// и представляет собой композитный процесс
interface Pipeline : Process {
    fun appendStage(proc: Process, ctx: ProcessContext)
}

class PipelineImpl : Pipeline {
    private val stages: MutableList<Pair<Process, ProcessContext>> = mutableListOf()

    override fun appendStage(proc: Process, ctx: ProcessContext) {
        stages.add(Pair(proc, ctx))
    }

    override suspend fun invoke(ctx: ProcessContext): ProcessResult {
        redirectIO(ctx)
            .dropLast(1)
            .forEach { it.first(it.second) }

        val (terminalStage, terminalCtx) = stages.last()
        return terminalStage(terminalCtx)
    }

    private fun redirectIO(ctx: ProcessContext): List<Pair<Process, ProcessContext>> {
        var prevStdout = ctx.stdin as Channel<String>

        return stages.map { (proc, ctx) ->
            val newStdout = StandardStreams.create()
            val newIoConfig = StandardStreams(
                stdin = prevStdout,
                stderr = ctx.io.stderr as Channel<String>,
                stdout = newStdout
            )
            prevStdout = newStdout

            val newCtx = ctx.copy(io = newIoConfig)
            Pair(proc, newCtx)
        }
    }
}
