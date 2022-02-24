package com.soarex.bashik

/**
 * Represents read-only environment of a program as a key-value collection of strings
 */
interface Environment {
    operator fun get(key: String): String

    fun getAll(): Map<String, String>
}

/**
 * Same as [ProcessContext] but supports mutation
 */
interface MutableEnvironment : Environment {
    operator fun set(key: String, value: String)

    fun setAll(other: Map<String, String>) {
        other.forEach { (k, v) -> set(k, v) }
    }
}

/**
 * Default implementation of [MutableEnvironment]
 * (and [Environment] because [MutableEnvironment] derives from this interface)
 *
 * @param variables initial variables
 */
open class MutableEnvironmentImpl(private val variables: MutableMap<String, String> = mutableMapOf()) :
    MutableEnvironment {
    override operator fun set(key: String, value: String) = variables.set(key, value)

    override fun setAll(other: Map<String, String>) {
        variables.putAll(other)
    }

    override operator fun get(key: String): String = variables.getOrElse(key) { "" }

    override fun getAll(): Map<String, String> = variables
}

/**
 * Wrapper around [variables] immutable map
 *
 * @param variables initial variables
 */
open class EnvironmentImpl(private val variables: Map<String, String>) : Environment {
    override operator fun get(key: String): String = variables.getOrElse(key) { "" }

    override fun getAll(): Map<String, String> = variables
}

/**
 * Creates [Environment] wrapper around Map<String, String>
 */
val Map<String, String>.env: Environment
    get() = EnvironmentImpl(this)

/**
 * Creates [MutableEnvironment] wrapper around MutableMap<String, String>
 */
val MutableMap<String, String>.env: MutableEnvironment
    get() = MutableEnvironmentImpl(this)

/**
 * Performs environment variable lookup by [key] in [ProcessContext] hierarchy
 */
fun ProcessContext.envLookup(key: String): String {
    val varValue = this.env[key]
    return varValue.ifBlank { parent?.envLookup(key) ?: "" }
}

/**
 * Collects variables from all environments in [ProcessContext] hierarchy
 */
fun ProcessContext.collectEnv(): Map<String, String> {
    val parentVars = parent?.collectEnv() ?: emptyMap()
    return parentVars + env.getAll()
}
