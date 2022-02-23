package com.soarex.bashik.parser.transformation

import com.soarex.bashik.parser.lexer.MetaChar
import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.lexer.WordToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import kotlin.test.assertContentEquals

internal class TildeExpansionTest {
    @ParameterizedTest
    @MethodSource("tildeExpansionTestData")
    fun tildeExpansion(path: Path, inputSequence: Sequence<Token>, expectedRefinedSequence: Sequence<Token>) {
        val transform = TildeExpansion(path)

        val transformationResult = transform.transform(inputSequence)

        assertContentEquals(expectedRefinedSequence, transformationResult)
    }

    companion object {
        private val mockPath: Path = Path.of("/users/some_usr")

        @JvmStatic
        fun tildeExpansionTestData() = listOf(
            Arguments.of(
                mockPath,
                sequenceOf(
                    WordToken("some_cmd"),
                    WordToken("~"),
                    WordToken("~/Downloads"),
                    WordToken("\"~\""),
                    MetaChar("||"),
                    WordToken("other_cmd"),
                    WordToken("arg1~"),
                    WordToken("ar~g2"),
                    WordToken("'~/some_dir'"),
                ),
                sequenceOf(
                    WordToken("some_cmd"),
                    WordToken("$mockPath"),
                    WordToken("$mockPath/Downloads"),
                    WordToken("\"~\""),
                    MetaChar("||"),
                    WordToken("other_cmd"),
                    WordToken("arg1~"),
                    WordToken("ar~g2"),
                    WordToken("'~/some_dir'"),
                ),
            ),
        )
    }
}
