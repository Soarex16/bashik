package com.soarex.bashik

fun Map<String, String>.env(parent: Environment): Environment = Environment(this, parent)
val Map<String, String>.env: Environment
    get() = Environment(this, null)

/**
 * Represents environment of a program as a key-value collection of strings
 *
 * Environments support inheritance by specifying the parent environment, so variable
 * lookup will search in the parent, if the variable doesn't exist in current environment.
 * If variable doesn't exist in the environment hierarchy, it considered empty.
 */
data class Environment(val variables: Map<String, String> = mapOf(), val parent: Environment?) {
    operator fun get(key: String): String = variables.getOrElse(key) { parent?.get(key) ?: "" }
}