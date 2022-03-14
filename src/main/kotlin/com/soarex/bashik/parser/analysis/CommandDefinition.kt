package com.soarex.bashik.parser.analysis

/**
 * Root interface for command syntax tree
 */
interface CommandDefinition

/**
 * Basic command like `bash -c 'cd some/dir; pwd'"
 */
data class BasicCommand(val envVars: Map<String, String> = emptyMap(), val command: String, val args: List<String>) :
    CommandDefinition

/**
 * Represents pipeline of commands like `cat some_file.txt | sort`
 */
data class Pipeline(val commands: List<BasicCommand>) : CommandDefinition

class UnknownCommandTypeException(command: CommandDefinition) :
    Exception("Unknown command type ${command::class.java.name}")
