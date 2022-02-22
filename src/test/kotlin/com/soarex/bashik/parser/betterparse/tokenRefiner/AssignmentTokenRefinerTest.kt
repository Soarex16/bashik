package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.OperatorToken
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.VariableAssignmentToken
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals

internal class AssignmentTokenRefinerTest {
    private lateinit var assignmentTokenRefiner: AssignmentTokenRefiner

    @BeforeEach
    internal fun setUp() {
        assignmentTokenRefiner = AssignmentTokenRefiner()
    }

    @ParameterizedTest
    @MethodSource("isApplicableTestData")
    fun isApplicable(tok: Token, isApplicable: Boolean) {
        val isActualApplicable = assignmentTokenRefiner.isApplicable(tok)

        kotlin.test.assertEquals(isApplicable, isActualApplicable)
    }

    @ParameterizedTest
    @MethodSource("refineTestData")
    fun refine(tok: Token, expectedRefineResult: Token?) {
        val actualResult = assignmentTokenRefiner.refine(tok)

        kotlin.test.assertEquals(expectedRefineResult, actualResult)
    }

    @ParameterizedTest
    @MethodSource("refineSequenceTestData")
    fun refineSequence(inputSequence: Sequence<Token>, expectedRefinedSequence: Sequence<Token?>) {
        val actualRefinedSequence = inputSequence.map { assignmentTokenRefiner.refine(it) }

        assertContentEquals(expectedRefinedSequence, actualRefinedSequence)
    }

    companion object {
        @JvmStatic
        fun isApplicableTestData() = listOf(
            Arguments.of(WordToken("cmd"), true),
            Arguments.of(WordToken("||"), true),
            Arguments.of(MetaChar("||"), false),
            Arguments.of(MetaChar(""), false),
            Arguments.of(MetaChar("alsjdhakjshd"), false),
            Arguments.of(OperatorToken.PIPE, false),
        )

        @JvmStatic
        fun refineTestData() = listOf(
            Arguments.of(WordToken("VAR=value"), VariableAssignmentToken("VAR", "value")),
            Arguments.of(
                WordToken("VAR='single quoted value'"),
                VariableAssignmentToken("VAR", "'single quoted value'")
            ),
            Arguments.of(
                WordToken("VAR=\"double quoted value\""),
                VariableAssignmentToken("VAR", "\"double quoted value\"")
            ),
            Arguments.of(
                WordToken("var=val=val"),
                VariableAssignmentToken("var", "val=val")
            ),
            Arguments.of(WordToken("VAR NAME WITH SPACES=value"), null),
            Arguments.of(WordToken("cmd"), null),
            Arguments.of(WordToken("||"), null),
            Arguments.of(WordToken(""), null),
            Arguments.of(MetaChar("||"), null),
            Arguments.of(MetaChar(""), null),
            Arguments.of(MetaChar("alsjdhakjshd"), null),
            Arguments.of(MetaChar("|"), null),
        )

        @JvmStatic
        fun refineSequenceTestData() = listOf(
            Arguments.of(
                sequenceOf<Token>(WordToken("VAR_WITHOUT_VALUE=")),
                sequenceOf<Token?>(VariableAssignmentToken("VAR_WITHOUT_VALUE", ""))
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("VAR=value")),
                sequenceOf<Token?>(VariableAssignmentToken("VAR", "value"))
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("VAR='single quoted value'")),
                sequenceOf<Token?>(VariableAssignmentToken("VAR", "'single quoted value'"))
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("VAR=\"double quoted value\"")),
                sequenceOf<Token?>(VariableAssignmentToken("VAR", "\"double quoted value\""))
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("VAR NAME WITH SPACES=value")),
                sequenceOf<Token?>(null)
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("cmd")),
                sequenceOf<Token?>(null)
            ),
            Arguments.of(
                sequenceOf<Token>(WordToken("||")),
                sequenceOf<Token?>(null)
            ),
            Arguments.of(
                sequenceOf<Token>(MetaChar("||")),
                sequenceOf<Token?>(null)
            ),
            Arguments.of(
                sequenceOf<Token>(MetaChar("")),
                sequenceOf<Token?>(null)
            ),
            Arguments.of(
                sequenceOf<Token>(
                    WordToken("HOME=/users/SomeUser"),
                    WordToken("SHELL=/bin/zsh"),
                    WordToken("LANG=ru_RU.UTF-8"),
                    WordToken("PAGER=less"),
                    WordToken("PATH='/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"),
                ),
                sequenceOf<Token?>(
                    VariableAssignmentToken("HOME", "/users/SomeUser"),
                    VariableAssignmentToken("SHELL", "/bin/zsh"),
                    VariableAssignmentToken("LANG", "ru_RU.UTF-8"),
                    VariableAssignmentToken("PAGER", "less"),
                    VariableAssignmentToken(
                        "PATH",
                        "'/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"
                    ),
                )
            ),
            Arguments.of(
                sequenceOf<Token>(
                    WordToken("HOME=/users/SomeUser"),
                    WordToken("SHELL=/bin/zsh"),
                    WordToken("LANG=ru_RU.UTF-8"),
                    WordToken("is not var assignment"),
                    WordToken("PAGER=less"),
                    WordToken("PATH='/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"),
                ),
                sequenceOf<Token?>(
                    VariableAssignmentToken("HOME", "/users/SomeUser"),
                    VariableAssignmentToken("SHELL", "/bin/zsh"),
                    VariableAssignmentToken("LANG", "ru_RU.UTF-8"),
                    null,
                    null,
                    null,
                )
            ),
            Arguments.of(
                sequenceOf<Token>(
                    WordToken("echo"),
                    WordToken("HOME=/users/SomeUser"),
                    WordToken("SHELL=/bin/zsh"),
                    WordToken("LANG=ru_RU.UTF-8"),
                    WordToken("is not var assignment"),
                    WordToken("PAGER=less"),
                    WordToken("PATH='/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"),
                ),
                sequenceOf<Token?>(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                )
            ),
            Arguments.of(
                sequenceOf<Token>(
                    WordToken("HOME=/users/SomeUser"),
                    WordToken("SHELL=/bin/zsh"),
                    WordToken("LANG="),
                    WordToken("PAGER=less"),
                    WordToken("PATH='/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"),
                ),
                sequenceOf<Token?>(
                    VariableAssignmentToken("HOME", "/users/SomeUser"),
                    VariableAssignmentToken("SHELL", "/bin/zsh"),
                    VariableAssignmentToken("LANG", ""),
                    VariableAssignmentToken("PAGER", "less"),
                    VariableAssignmentToken(
                        "PATH",
                        "'/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"
                    ),
                )
            ),
        )
    }
}
