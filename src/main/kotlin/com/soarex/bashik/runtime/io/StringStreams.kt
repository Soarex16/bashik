package com.soarex.bashik.runtime.io

import java.util.concurrent.atomic.AtomicInteger

class StringOutputStream : OutputStream<String> {
    private val outputStorage = mutableListOf<String>()

    val content: List<String>
        get() = outputStorage

    override suspend fun write(element: String) {
        outputStorage.add(element)
    }
}

class StringInputStream(val input: List<String>) : InputStream<String> {
    private val currentIndex = AtomicInteger(0)

    override suspend fun read(): String? {
        if (currentIndex.get() >= input.size)
            return null

        return input[currentIndex.getAndIncrement()]
    }
}
