package com.soarex.bashik

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path

internal class EnvironmentKtTest {

    @ParameterizedTest
    @MethodSource("envLookupTestData")
    fun envLookup(ctx: ProcessContext, envVar: String, expectedValue: String) {
        val actualEnvVarValue = ctx.envLookup(envVar)

        assertEquals(expectedValue, actualEnvVarValue)
    }

    @ParameterizedTest
    @MethodSource("collectEnvTestData")
    fun collectEnv(ctx: ProcessContext, expectedValue: Map<String, String>) {
        val actualEnvVarValue = ctx.collectEnv()

        assertEquals(expectedValue, actualEnvVarValue)
    }

    companion object {
        @JvmStatic
        fun envLookupTestData() = listOf(
            Arguments.of(
                ProcessContextImpl(
                    null,
                    mutableMapOf(
                        "HOME" to "/users/some_usr",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "HOME",
                "/users/some_usr",
            ),
            Arguments.of(
                ProcessContextImpl(
                    null,
                    mutableMapOf(
                        "LOGNAME" to "some_usr",
                        "HOME" to "/users/some_usr",
                        "USER" to "some_usr",
                        "SHELL" to "/bin/bashik",
                        "PWD" to "/usr/share/icons",
                        "PAGER" to "less",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "VAR_NOT_EXISTS",
                "",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        null,
                        mutableMapOf(
                            "LOGNAME" to "some_usr",
                            "HOME" to "/users/some_usr",
                            "USER" to "some_usr",
                            "SHELL" to "/bin/bashik",
                            "PWD" to "/usr/share/icons",
                            "PAGER" to "less",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "PWD",
                "/users/some_usr/Downloads",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        null,
                        mutableMapOf(
                            "LOGNAME" to "some_usr",
                            "HOME" to "/users/some_usr",
                            "USER" to "some_usr",
                            "SHELL" to "/bin/bashik",
                            "PWD" to "/usr/share/icons",
                            "PAGER" to "less",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "SHELL",
                "/bin/bashik",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        null,
                        mutableMapOf(
                            "LOGNAME" to "some_usr",
                            "HOME" to "/users/some_usr",
                            "USER" to "some_usr",
                            "SHELL" to "/bin/bashik",
                            "PWD" to "/usr/share/icons",
                            "PAGER" to "less",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "SOME_VAR_THAT_NOT_EXISTS",
                "",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        ProcessContextImpl(
                            null,
                            mutableMapOf(
                                "LOGNAME" to "some_usr",
                                "HOME" to "/users/some_usr",
                                "USER" to "some_usr",
                                "SHELL" to "/bin/bashik",
                                "PWD" to "/usr/share/icons",
                                "PAGER" to "less",
                            ).env,
                            Path.of("/"),
                        ),
                        mutableMapOf(
                            "LOGNAME" to "some_other_usr",
                            "HOME" to "/users/some_other_usr",
                            "USER" to "some_other_usr",
                            "SHELL" to "/bin/bash",
                            "PWD" to "/usr/share/icons",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_other_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "PWD",
                "/users/some_other_usr/Downloads",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        ProcessContextImpl(
                            null,
                            mutableMapOf(
                                "LOGNAME" to "some_usr",
                                "HOME" to "/users/some_usr",
                                "USER" to "some_usr",
                                "SHELL" to "/bin/bashik",
                                "PWD" to "/usr/share/icons",
                                "PAGER" to "less",
                            ).env,
                            Path.of("/"),
                        ),
                        mutableMapOf(
                            "LOGNAME" to "some_other_usr",
                            "HOME" to "/users/some_other_usr",
                            "USER" to "some_other_usr",
                            "SHELL" to "/bin/bash",
                            "PWD" to "/usr/share/icons",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_other_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "PAGER",
                "less",
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        ProcessContextImpl(
                            null,
                            mutableMapOf(
                                "LOGNAME" to "some_usr",
                                "HOME" to "/users/some_usr",
                                "USER" to "some_usr",
                                "SHELL" to "/bin/bashik",
                                "PWD" to "/usr/share/icons",
                                "PAGER" to "less",
                            ).env,
                            Path.of("/"),
                        ),
                        mutableMapOf(
                            "LOGNAME" to "some_other_usr",
                            "HOME" to "/users/some_other_usr",
                            "USER" to "some_other_usr",
                            "SHELL" to "/bin/bash",
                            "PWD" to "/usr/share/icons",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_other_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                "SOME_VAR_THAT_NOT_EXISTS",
                "",
            ),
        )

        @JvmStatic
        fun collectEnvTestData() = listOf(
            Arguments.of(
                ProcessContextImpl(
                    null,
                    mutableMapOf(
                        "HOME" to "/users/some_usr",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                mapOf(
                    "HOME" to "/users/some_usr",
                ),
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        ProcessContextImpl(
                            ProcessContextImpl(
                                ProcessContextImpl(
                                    null,
                                    mutableMapOf(
                                        "HOME" to "/users/usr1",
                                    ).env,
                                    Path.of("/usr/share/icons"),
                                ),
                                mutableMapOf(
                                    "HOME" to "/users/usr2",
                                ).env,
                                Path.of("/usr/share/icons"),
                            ),
                            mutableMapOf(
                                "HOME" to "/users/usr3",
                            ).env,
                            Path.of("/usr/share/icons"),
                        ),
                        mutableMapOf(
                            "HOME" to "/users/usr1",
                        ).env,
                        Path.of("/usr/share/icons"),
                    ),
                    mutableMapOf(
                        "HOME" to "/users/usr4",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                mapOf(
                    "HOME" to "/users/usr4",
                ),
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        null,
                        mutableMapOf(
                            "LOGNAME" to "some_usr",
                            "HOME" to "/users/some_usr",
                            "USER" to "some_usr",
                            "SHELL" to "/bin/bashik",
                            "PWD" to "/usr/share/icons",
                            "PAGER" to "less",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                mapOf(
                    "LOGNAME" to "some_usr",
                    "HOME" to "/users/some_usr",
                    "USER" to "some_usr",
                    "SHELL" to "/bin/bashik",
                    "PWD" to "/usr/share/icons",
                    "PAGER" to "less",
                    "PWD" to "/users/some_usr/Downloads",
                ),
            ),
            Arguments.of(
                ProcessContextImpl(
                    ProcessContextImpl(
                        ProcessContextImpl(
                            null,
                            mutableMapOf(
                                "LOGNAME" to "some_usr",
                                "HOME" to "/users/some_usr",
                                "USER" to "some_usr",
                                "SHELL" to "/bin/bashik",
                                "PWD" to "/usr/share/icons",
                                "PAGER" to "less",
                            ).env,
                            Path.of("/"),
                        ),
                        mutableMapOf(
                            "LOGNAME" to "some_other_usr",
                            "HOME" to "/users/some_other_usr",
                            "USER" to "some_other_usr",
                            "SHELL" to "/bin/bash",
                            "PWD" to "/usr/share/icons",
                        ).env,
                        Path.of("/"),
                    ),
                    mutableMapOf(
                        "PWD" to "/users/some_other_usr/Downloads",
                    ).env,
                    Path.of("/usr/share/icons"),
                ),
                mapOf(
                    "PWD" to "/usr/share/icons",
                    "PAGER" to "less",
                    "LOGNAME" to "some_other_usr",
                    "HOME" to "/users/some_other_usr",
                    "USER" to "some_other_usr",
                    "SHELL" to "/bin/bash",
                    "PWD" to "/users/some_other_usr/Downloads",
                ),
            ),
        )
    }
}
