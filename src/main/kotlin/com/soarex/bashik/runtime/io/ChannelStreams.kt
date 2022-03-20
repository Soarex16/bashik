package com.soarex.bashik.runtime.io

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class ChannelInputStream<T>(private val channel: ReceiveChannel<T>) : InputStream<T> {
    override suspend fun read() = channel.receive()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isOpen: Boolean
        get() = !channel.isClosedForReceive

    override suspend fun close() {}
}

class ChannelOutputStream<T>(private val channel: SendChannel<T>) : OutputStream<T> {
    override suspend fun write(element: T) = channel.send(element)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isOpen: Boolean
        get() = channel.isClosedForSend

    override suspend fun close() {
        channel.close()
    }
}

class ChannelIOStream<T>(private val channel: Channel<T>) : IOStream<T> {
    override suspend fun read() = channel.receive()

    override suspend fun write(element: T) = channel.send(element)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isOpen: Boolean
        get() = channel.isClosedForReceive

    override suspend fun close() {
        channel.close()
    }
}
