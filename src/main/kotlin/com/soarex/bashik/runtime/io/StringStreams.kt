package com.soarex.bashik.runtime.io

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class StringOutputStream : OutputStream<String> {
    private val outputStorage = mutableListOf<String>()
    private val streamOpened = AtomicBoolean(true)

    val content: List<String>
        get() = outputStorage

    override suspend fun write(element: String) {
        if (!streamOpened.get()) return
        outputStorage.add(element)
    }

    override val isOpen: Boolean
        get() = streamOpened.get()

    override suspend fun close() {
    }
}

class StringInputStream(val input: List<String>) : InputStream<String> {
    private val currentIndex = AtomicInteger(0)
    private val streamOpened = AtomicBoolean(true)

    override suspend fun read(): String? {
        if (!streamOpened.get() || currentIndex.get() >= input.size)
            return null

        return input[currentIndex.getAndIncrement()]
    }

    override val isOpen: Boolean
        get() = streamOpened.get()

    override suspend fun close() {
        streamOpened.set(false)
    }
}
