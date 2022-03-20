package com.soarex.bashik

import com.soarex.bashik.parser.analysis.CommandDefinition
import com.soarex.bashik.parser.analysis.CommandParser
import com.soarex.bashik.parser.analysis.DefaultParser
import com.soarex.bashik.parser.analysis.createParser
import com.soarex.bashik.parser.betterparse.BetterParseLexer
import com.soarex.bashik.parser.betterparse.tokenRefiner.AssignmentTokenRefiner
import com.soarex.bashik.parser.betterparse.tokenRefiner.OperatorRefiner
import com.soarex.bashik.parser.betterparse.tokenRefiner.TokenRefiningTransform
import com.soarex.bashik.parser.lexer.Lexer
import com.soarex.bashik.parser.transformation.QuoteRemoval
import com.soarex.bashik.parser.transformation.TildeExpansion
import com.soarex.bashik.parser.transformation.VariableExpansion
import com.soarex.bashik.parser.transformation.WordSplitting
import com.soarex.bashik.runtime.ProcessContext
import com.soarex.bashik.runtime.ProcessContextImpl
import com.soarex.bashik.runtime.ProcessExecutor
import com.soarex.bashik.runtime.ProcessExecutorImpl
import com.soarex.bashik.runtime.io.ConsoleInputStream
import com.soarex.bashik.runtime.io.ConsoleOutputStream
import com.soarex.bashik.runtime.io.InputStream
import com.soarex.bashik.runtime.io.OutputStream
import com.soarex.bashik.runtime.io.ProcessStreams

object BashikREPL {
    var prompt = "bashik$ "

    private lateinit var commandParser: (String) -> CommandDefinition

    private lateinit var rootContext: ProcessContext

    private lateinit var executor: ProcessExecutor

    private fun init(args: List<String>) {
        rootContext = createRootContext(args)
        commandParser = createParserPipeline(rootContext)
        executor = ProcessExecutorImpl()
    }

    private fun createRootContext(args: List<String>): ProcessContext = ProcessContextImpl(
        env = System.getenv().toMutableMap().env, // because we return [ProcessContext] it can't be modified
        args = args,
        io = object : ProcessStreams {
            override val stdin: InputStream<String> = ConsoleInputStream(System.`in`)
            override val stdout: OutputStream<String> = ConsoleOutputStream(System.out)
            override val stderr: OutputStream<String> = ConsoleOutputStream(System.err)
        },
    )

    private fun createParserPipeline(ctx: ProcessContext): (String) -> CommandDefinition {
        val lexer: Lexer = BetterParseLexer()
        val parser: CommandParser = DefaultParser()

        return createParser(
            lexer,
            parser,
            listOf(
                TildeExpansion(ctx.workingDirectory),
                VariableExpansion(ctx.env),
                WordSplitting(),
                QuoteRemoval(),
                TokenRefiningTransform(listOf(OperatorRefiner(), AssignmentTokenRefiner())),
            )
        )
    }

    suspend fun serve() {
        while (true) {
            print(prompt)

            val input = readln()
            val commandDefinition = commandParser(input)

            executor.runWithContext(rootContext, commandDefinition)
        }
    }

    suspend fun run(args: List<String>) {
        init(args)
        serve()
    }
}
