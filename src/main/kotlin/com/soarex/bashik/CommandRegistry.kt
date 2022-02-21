package com.soarex.bashik

object CommandRegistry {
    private val commands: MutableMap<String, Command> = mutableMapOf()

    fun register(name: String, cmd: Command) {
        commands[name] = cmd
    }

    // command -> alias -> null
    operator fun get(cmdName: String): Command? = commands[cmdName]
}
