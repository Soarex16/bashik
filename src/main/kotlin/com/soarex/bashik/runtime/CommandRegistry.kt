package com.soarex.bashik.runtime

import com.soarex.bashik.commands.*
import com.soarex.bashik.commands.clikt.grep

object CommandRegistry {
    private val commands: MutableMap<String, Command> = mutableMapOf(
        "cat" to cat,
        "echo" to echo,
        "pwd" to pwd,
        "wc" to wc,
        "exit" to exit,
        "grep" to grep,
        "cd" to cd,
        "ls" to ls
    )

    fun register(name: String, cmd: Command) {
        commands[name] = cmd
    }

    // command -> null if command don't registered
    operator fun get(cmdName: String): Command? = commands[cmdName]
}
