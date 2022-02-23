package com.soarex.bashik.parser.transformation

import com.soarex.bashik.Environment
import com.soarex.bashik.env
import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class VariableExpansionTest {

    @ParameterizedTest
    @MethodSource("variableExpansionTestData")
    fun variableExpansion(env: Environment, inputSequence: Sequence<Token>, expectedRefinedSequence: Sequence<Token>) {
        val transform = VariableExpansion(env)

        val transformationResult = transform.transform(inputSequence)

        assertContentEquals(expectedRefinedSequence, transformationResult)
    }

    companion object {
        @JvmStatic
        fun variableExpansionTestData() = listOf(
            Arguments.of(
                emptyMap<String, String>().env,
                sequenceOf(
                    WordToken("\${HOME:-\"default val\"}"),
                    WordToken(":- ?: :? := \${HOME:-\"default val\"}"),
                    WordToken("\${HOME}$"),
                    WordToken("$ some other token text"),
                    WordToken("\${SHELL}"),
                    WordToken("'\$HOME'"),
                ),
                sequenceOf(
                    WordToken("\"default val\""),
                    WordToken(":- ?: :? := \"default val\""),
                    WordToken("$"),
                    WordToken("$ some other token text"),
                    WordToken(""),
                    WordToken("'\$HOME'"),
                ),
            ),
            Arguments.of(
                mapOf(
                    "HOME" to "/users/user1",
                    "PWD" to "/usr/bin/share/",
                    "SHELL" to "/bin/bashik",
                ).env,
                sequenceOf(
                    WordToken("\$HOME"),
                    WordToken("\$HOME$"),
                    WordToken("\$HOME\$HOME\$HOME\$HOME$ some other token text"),
                    WordToken("\${SHELL}"),
                    WordToken("'\$HOME'"),
                ),
                sequenceOf(
                    WordToken("/users/user1"),
                    WordToken("/users/user1$"),
                    WordToken("/users/user1/users/user1/users/user1/users/user1$ some other token text"),
                    WordToken("/bin/bashik"),
                    WordToken("'\$HOME'"),
                ),
            ),
            Arguments.of(
                mapOf(
                    "HOME" to "/users/user1",
                    "PWD" to "/usr/bin/share/",
                    "SHELL" to "/bin/bashik",
                ).env,
                sequenceOf(
                    WordToken("--work_dir=\${PWD}"),
                    WordToken("--output=some/path\${PWD}some/sub/dir"),
                    WordToken("\$HOME"),
                    WordToken("\${SHELL}"),
                ),
                sequenceOf(
                    WordToken("--work_dir=/usr/bin/share/"),
                    WordToken("--output=some/path/usr/bin/share/some/sub/dir"),
                    WordToken("/users/user1"),
                    WordToken("/bin/bashik"),
                ),
            ),
            Arguments.of(
                mapOf(
                    "HOME" to "/users/user1",
                    "PWD" to "/usr/bin/share/",
                    "SHELL" to "/bin/bashik",
                ).env,
                sequenceOf(
                    WordToken("HOME=\"/users/some_usr\""),
                    WordToken("SOME_IMPORTANT_CONFIG=true"),
                    WordToken("some_cmd"),
                    WordToken("--verbosity=2"),
                    WordToken("--work_dir=\${PWD}"),
                    WordToken("--output=some/path\${PWD}some/sub/dir"),
                    MetaChar("&&"),
                    WordToken("echo"),
                    WordToken("hello"),
                    WordToken("from"),
                    WordToken("\$HOME"),
                    WordToken("\${SHELL}"),
                ),
                sequenceOf(
                    WordToken("HOME=\"/users/some_usr\""),
                    WordToken("SOME_IMPORTANT_CONFIG=true"),
                    WordToken("some_cmd"),
                    WordToken("--verbosity=2"),
                    WordToken("--work_dir=/usr/bin/share/"),
                    WordToken("--output=some/path/usr/bin/share/some/sub/dir"),
                    MetaChar("&&"),
                    WordToken("echo"),
                    WordToken("hello"),
                    WordToken("from"),
                    WordToken("/users/user1"),
                    WordToken("/bin/bashik"),
                ),
            ),
        )
    }
}
