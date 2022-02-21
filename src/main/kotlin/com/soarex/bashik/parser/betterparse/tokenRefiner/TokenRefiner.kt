package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.Token

/**
 * Общий интерфейс для всех уточняющих преобразований токенов
 * Полагается, что если результатом refine является null, то токен
 * не может быть уточнен и стоит оставить его as is
 */
interface TokenRefiner {
    fun refine(tok: Token): Token?

    fun isApplicable(tok: Token): Boolean
}
