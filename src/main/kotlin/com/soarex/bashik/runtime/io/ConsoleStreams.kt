package com.soarex.bashik.runtime.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.InputStream as JavaInputStream
import java.io.PrintStream as JavaPrintStream

class ConsoleInputStream(inputStream: JavaInputStream) : InputStream<String> {
    private val input = BufferedReader(InputStreamReader(inputStream))

    override suspend fun read() = withContext(Dispatchers.IO) {
        try {
            input.readLine()
        } catch (e: IOException) {
            null
        }
    }
}

class ConsoleOutputStream(private val out: JavaPrintStream) : OutputStream<String> {
    override suspend fun write(element: String) {
        out.println(element)
    }
}
