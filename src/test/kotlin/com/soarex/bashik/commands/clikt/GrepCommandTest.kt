package com.soarex.bashik.commands.clikt

import com.soarex.bashik.commands.testCommand
import org.junit.jupiter.api.Test
import java.io.File

internal class GrepCommandTest {
    @Test
    fun grepCommandNoFlags() {
        val expectedStdout = listOf(
            "# bash nested if/else\n",
            "if [ \$choice -eq 1 ] ; then\n",
            "        if [ \$choice -eq 2 ] ; then\n",
            "                if [ \$choice -eq 3 ] ; then\n",
        )

        val inputFile = File("src/test/resources/grep-test-data.txt").absolutePath

        testCommand(
            grep,
            listOf("grep", "if", inputFile),
            expectedStdout = expectedStdout
        )
    }

    @Test
    fun grepCommand() {
        val expectedStdout = listOf(
            "# bash nested if/else\n",
            "if [ \$choice -eq 1 ] ; then\n",
            "\n",
            "        echo \"You have chosen word: Bash\"\n",
            "--\n",
            "if [ \$choice -eq 1 ] ; then\n",
            "\n",
            "        echo \"You have chosen word: Bash\"\n",
            "\n",
            "--\n",
            "        if [ \$choice -eq 2 ] ; then\n",
            "                 echo \"You have chosen word: Scripting\"\n",
            "        else\n",
            "\n",
            "--\n",
            "                if [ \$choice -eq 3 ] ; then\n",
            "                        echo \"You have chosen word: Tutorial\"\n",
            "                else\n",
            "                        echo \"Please make a choice between 1-3 !\"\n",
            "--\n",
        )

        val inputFile = File("src/test/resources/grep-test-data.txt").absolutePath

        testCommand(
            grep,
            listOf("grep", "if", "-A", "3", inputFile),
            expectedStdout = expectedStdout
        )
    }
}
