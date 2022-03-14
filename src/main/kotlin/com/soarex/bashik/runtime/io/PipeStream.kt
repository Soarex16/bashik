package com.soarex.bashik.runtime.io

import java.util.concurrent.LinkedBlockingQueue

data class Pipe<T>(val source: OutputStream<T>, val sink: InputStream<T>)

fun <T> createPipe(): Pipe<T> {
    val queue = LinkedBlockingQueue<T>()

    val source = object : OutputStream<T> {
        override suspend fun write(element: T) {
            queue.add(element)
        }
    }

    val sink = object : InputStream<T> {
        override suspend fun read(): T? = queue.poll()
    }

    return Pipe(source, sink)
}

suspend fun <T> pump(from: InputStream<T>, to: OutputStream<T>) {
    var input: T? = from.read()
    while (input != null) {
        to.write(input)
        input = from.read()
    }
}
