package com.soarex.bashik.runtime.io

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class ChannelInputStream<T>(private val channel: ReceiveChannel<T>) : InputStream<T> {
    override suspend fun read() = channel.receive()
}

class ChannelOutputStream<T>(private val channel: SendChannel<T>) : OutputStream<T> {
    override suspend fun write(element: T) = channel.send(element)
}

class ChannelIOStream<T>(private val channel: Channel<T>) : IOStream<T> {
    override suspend fun read() = channel.receive()

    override suspend fun write(element: T) = channel.send(element)
}
