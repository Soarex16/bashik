package com.soarex.bashik.runtime.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.io.InputStream as JavaInputStream
import java.io.PrintStream as JavaPrintStream

class ConsoleInputStream(underlyingStream: JavaInputStream) : InputStream<String> {
    private val input = underlyingStream.bufferedReader()
    private val streamOpened = AtomicBoolean(true)

    override suspend fun read() = withContext(Dispatchers.IO) {
        try {
            input.readLine()
        } catch (e: IOException) {
            null
        }
    }

    override val isOpen: Boolean
        get() = streamOpened.get()

    override suspend fun close() {
        streamOpened.set(false)
        withContext(Dispatchers.IO) {
            input.close()
        }
    }
}

class ConsoleOutputStream(private val out: JavaPrintStream) : OutputStream<String> {
    private val streamOpened = AtomicBoolean(true)

    override suspend fun write(element: String) {
        if (out.checkError())
            return

        out.println(element)
    }

    override val isOpen: Boolean
        get() = streamOpened.get()

    override suspend fun close() {
        streamOpened.set(false)
        out.close()
    }
}
