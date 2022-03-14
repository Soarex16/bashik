package com.soarex.bashik.runtime

/**
 * Процесс - это некоторая корутина, принимающая на вход свой контекст исполнения
 */
fun interface Process {
    /**
     * @param ctx контекст исполнения процесса
     * @return результат исполнения процесса (на текущий момент - код возврата)
     */
    suspend operator fun invoke(ctx: ProcessContext): ProcessResult
}
