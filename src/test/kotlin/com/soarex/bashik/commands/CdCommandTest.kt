package com.soarex.bashik.commands

import com.soarex.bashik.runtime.exitCode
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.test.assertEquals

class CdCommandTest {
    @Test
    fun lsCommandNoArgs() {
        val context = testCommand(
            cd,
            args = listOf("cd"),
        )

        assertEquals(Path(System.getProperty("user.home")), context.parent?.workingDirectory)
    }

    @Test
    fun cdCommandWithOneArg() {
        val context = testCommand(
            cd,
            args = listOf("cd", "src/test/resources"),
        )

        assertEquals(context.workingDirectory.resolve(Path("src/test/resources")), context.parent?.workingDirectory)
    }

    @Test
    fun cdCommandWithTooManyArgs() {
        testCommand(
            cd,
            args = listOf("cd", "src/test/resources", "src/test/kotlin"),
            expectedStderr = listOf("cd: too many arguments"),
            expectedCommandResult = 1.exitCode
        )
    }


    @Test
    fun cdCommandWithWrongPathArgs() {
        testCommand(
            cd,
            args = listOf("cd", "src/resources"),
            expectedStderr = listOf("cd: src/resources: No such file or directory"),
            expectedCommandResult = 1.exitCode
        )
        testCommand(
            cd,
            args = listOf("cd", "README.md"),
            expectedStderr = listOf("cd: README.md: Not a directory"),
            expectedCommandResult = 1.exitCode
        )
    }
}