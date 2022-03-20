package com.soarex.bashik.runtime.io

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

data class Pipe<T>(val source: OutputStream<T>, val sink: InputStream<T>)

fun <T> createPipe(): Pipe<T> {
    val queue = LinkedBlockingQueue<T>()
    val isOpen = AtomicBoolean(true)

    val source = object : OutputStream<T> {
        override suspend fun write(element: T) {
            if (isOpen.get()) {
                queue.add(element)
            }
        }

        override val isOpen: Boolean
            get() = isOpen.get()

        override suspend fun close() {
            isOpen.set(false)
        }
    }

    val sink = object : InputStream<T> {
        override suspend fun read(): T? = if (isOpen.get()) {
            queue.poll()
        } else {
            null
        }

        override val isOpen: Boolean
            get() = isOpen.get()

        override suspend fun close() {
            isOpen.set(false)
        }
    }

    return Pipe(source, sink)
}

suspend fun <T> pump(from: InputStream<T>, to: OutputStream<T>) {
    while (from.isOpen && to.isOpen) {
        val input: T = from.read() ?: continue
        to.write(input)
    }
}
