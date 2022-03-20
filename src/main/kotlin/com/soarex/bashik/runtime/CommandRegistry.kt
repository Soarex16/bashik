package com.soarex.bashik.runtime

import com.soarex.bashik.commands.cat
import com.soarex.bashik.commands.echo
import com.soarex.bashik.commands.exit
import com.soarex.bashik.commands.pwd
import com.soarex.bashik.commands.wc

object CommandRegistry {
    private val commands: MutableMap<String, Command> = mutableMapOf(
        "cat" to cat,
        "echo" to echo,
        "pwd" to pwd,
        "wc" to wc,
        "exit" to exit,
    )

    fun register(name: String, cmd: Command) {
        commands[name] = cmd
    }

    // command -> null if command don't registered
    operator fun get(cmdName: String): Command? = commands[cmdName]
}
