package com.soarex.bashik.commands

import com.soarex.bashik.runtime.exitCode
import org.junit.jupiter.api.Test

class LsCommandTest {
    @Test
    fun lsCommandNoArgs() {
        testCommand(
            ls,
            args = listOf("ls"),
            expectedStdout = listOf("build  gradle  gradlew  README.md  build.gradle.kts  src")
        )
    }

    @Test
    fun lsCommandWithOneDirArg() {
        testCommand(
            ls,
            args = listOf("ls", "src"),
            expectedStdout = listOf("test  main")
        )
    }

    @Test
    fun lsCommandWithOneFileArg() {
        testCommand(
            ls,
            args = listOf("ls", "README.md"),
            expectedStdout = listOf("README.md")
        )
    }

    @Test
    fun lsCommandWithTooManyArgs() {
        testCommand(
            ls,
            args = listOf("ls", "src", "build"),
            expectedStderr = listOf("ls: too many arguments"),
            expectedCommandResult = 1.exitCode
        )
    }


    @Test
    fun lsCommandWithWrongPathArgs() {
        testCommand(
            ls,
            args = listOf("ls", "resources"),
            expectedStderr = listOf("ls: resources: No such file or directory"),
            expectedCommandResult = 1.exitCode
        )
    }
}