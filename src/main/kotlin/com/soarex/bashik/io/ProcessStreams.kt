package com.soarex.bashik.io

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface ProcessStreams {
    val stdin: ReceiveChannel<String>
    val stdout: SendChannel<String>
    val stderr: SendChannel<String>
}

data class StandardStreams(
    override val stdin: Channel<String> = create(),
    override val stdout: Channel<String> = create(),
    override val stderr: Channel<String> = create()
) : ProcessStreams {
    companion object {
        var DEFAULT_CAPACITY: Int = 10

        fun create(): Channel<String> = Channel(DEFAULT_CAPACITY)
    }
}