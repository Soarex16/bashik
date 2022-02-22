package com.soarex.bashik.parser.betterparse.tokenRefiner

import com.soarex.bashik.parser.lexer.Token
import com.soarex.bashik.parser.transformation.Transform

class AmbiguousRefiningException : Exception()

/**
 * Трансформация, которая осуществляет уточнение типов токенов,
 * формируя из слов более сложные синтаксические единицы
 * (например, присваивания, комментарии, операторы).
 * Уточнение типов делегируется конфигурируемому набору [TokenRefiner]-ов
 *
 * В результате уточнения токена возможны следующие ситуации:
 * - все уточнения вернули null - в таком случае токен не преобразуется
 * - ровно одно уточнение вернуло не null - возвращается результат этого уточнения
 * - несколько уточнений вернули не null - в таком случае трансформация выбрасывает
 * исключение о неоднозначной трансформации
 *
 * Трансформация уточнения типов запускается после expansion-ов
 */
class TokenRefiningTransform(private val refiners: List<TokenRefiner> = emptyList()) : Transform {
    override fun transform(input: Sequence<Token>): Sequence<Token> = input.map { tok ->
        val refiningResult = refiners
            .filter { r -> r.isApplicable(tok) }
            .mapNotNull { r -> r.refine(tok) }

        return@map when {
            refiningResult.isEmpty() -> tok
            refiningResult.size == 1 -> refiningResult.single()
            else -> throw AmbiguousRefiningException()
        }
    }
}
