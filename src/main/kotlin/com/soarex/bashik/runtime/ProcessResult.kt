package com.soarex.bashik.runtime

@JvmInline
value class ProcessResult(val exitCode: Int)

val Int.exitCode
    get() = ProcessResult(this)
