package com.soarex.bashik.commands.clikt

import com.github.ajalt.clikt.core.Abort
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.PrintCompletionMessage
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktConsole
import com.soarex.bashik.envLookup
import com.soarex.bashik.runtime.Command
import com.soarex.bashik.runtime.ProcessContext
import com.soarex.bashik.runtime.exitCode
import kotlinx.coroutines.runBlocking

/**
 * Wrapper around Clikt commands to adapt to bashik API
 *
 * Note: you should pass autoCompleteEnvvar = null for compatibility with bashik API
 */
fun cliktCommand(cmd: CliktCommand): Command {
    return Command { ctx ->
        cmd.context {
            console = BashikConsoleAdapter(ctx)
            envvarReader = { ctx.envLookup(it) }
        }

        try {
            cmd.parse(ctx.args.drop(1))
            return@Command 0.exitCode
        } catch (e: ProgramResult) {
            return@Command e.statusCode.exitCode
        } catch (e: PrintHelpMessage) {
            ctx.stdout.write(e.command.getFormattedHelp())
            val code = if (e.error) 1 else 0
            return@Command code.exitCode
        } catch (e: PrintCompletionMessage) {
            throw UnsupportedOperationException()
        } catch (e: PrintMessage) {
            if (e.message != null) ctx.stdout.write(e.message!!)
            val code = if (e.error) 1 else 0
            return@Command code.exitCode
        } catch (e: UsageError) {
            ctx.stderr.write(e.helpMessage())
            return@Command e.statusCode.exitCode
        } catch (e: CliktError) {
            if (e.message != null) ctx.stderr.write(e.message!!)
            return@Command 1.exitCode
        } catch (e: Abort) {
            val code = if (e.error) 1 else 0
            return@Command code.exitCode
        }
    }
}

/**
 * Helper class for replacing default IO in Clikt commands
 */
class BashikConsoleAdapter(val ctx: ProcessContext) : CliktConsole {
    override fun promptForLine(prompt: String, hideInput: Boolean): String? {
        if (hideInput) throw UnsupportedOperationException()

        return runBlocking {
            ctx.stdout.write(prompt)
            ctx.io.stdin.read()
        }
    }

    override fun print(text: String, error: Boolean) {
        val outputStream = if (error) ctx.stderr else ctx.stdout
        return runBlocking {
            outputStream.write(text)
        }
    }

    override val lineSeparator: String get() = "\n"
}
