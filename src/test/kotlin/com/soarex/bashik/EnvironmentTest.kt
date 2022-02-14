package com.soarex.bashik

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class EnvironmentTest {
    @Test
    fun `smoke test`() {
        val env = mapOf(
            "a" to "b",
            "b" to "c",
            "c" to "d"
        ).env

        val varValue = env["a"]
        assertEquals("b", varValue)
    }

    @Test
    fun `variable not exists`() {
        val env = mapOf(
            "b" to "c",
            "c" to "d"
        ).env

        val varValue = env["a"]
        assertEquals("", varValue)
    }

    @Test
    fun `nesting one level variable from parent`() {
        val parentEnv = mapOf(
            "a" to "b",
        ).env

        val childEnv = mapOf(
            "b" to "c",
            "c" to "d"
        ).env(parentEnv)

        val varValue = childEnv["a"]
        assertEquals("b", varValue)
    }

    @Test
    fun `nesting one level variable overriding`() {
        val parentEnv = mapOf(
            "a" to "b",
        ).env

        val childEnv = mapOf(
            "a" to "c"
        ).env(parentEnv)

        val varValue = childEnv["a"]
        assertEquals("c", varValue)
    }

    @Test
    fun `nesting multiple levels overriding`() {
        val env1 = mapOf(
            "a" to "b",
        ).env

        val env2 = mapOf(
            "b" to "c"
        ).env(env1)

        val env3 = mapOf(
            "c" to "d",
            "a" to "z"
        ).env(env2)

        val varValue = env3["a"]
        assertEquals("z", varValue)
    }
}