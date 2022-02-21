package com.soarex.bashik

@JvmInline
value class ProcessResult(val exitCode: Int)

val Int.exitCode
    get() = ProcessResult(this)
