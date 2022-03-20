package com.soarex.bashik.runtime.io

import kotlinx.coroutines.channels.Channel

interface InputStream<T> {
    val isOpen: Boolean
    suspend fun read(): T?
    suspend fun close()
}

interface OutputStream<T> {
    val isOpen: Boolean
    suspend fun write(element: T)
    suspend fun close()
}

interface IOStream<T> : InputStream<T>, OutputStream<T>

interface ProcessStreams {
    val stdin: InputStream<String>
    val stdout: OutputStream<String>
    val stderr: OutputStream<String>
}

data class StandardStreams(
    override val stdin: IOStream<String> = create(),
    override val stdout: IOStream<String> = create(),
    override val stderr: IOStream<String> = create()
) : ProcessStreams {
    companion object {
        var DEFAULT_CAPACITY: Int = 10

        fun create(): IOStream<String> = ChannelIOStream(Channel(DEFAULT_CAPACITY))
    }
}

suspend fun <T> InputStream<T>.forEach(block: suspend (elem: T) -> Unit) {
    var current = read()
    while (current != null) {
        block(current)
        current = read()
    }
}
