package com.soarex.bashik

import java.io.File
import java.io.Reader
import java.util.LinkedList

/**
 * Формирует список диапазонов из промежутков между диапазонами
 *
 * @return список диапазонов следующего вида:
 * ```
 * [range1.r + 1 until range2.l],
 * ...,
 * [rangeN-1.r + 1 until rangeN.l]
 * ```
 */
fun List<IntRange>.gaps(): List<IntRange> = zipWithNext()
    .map { (range, next) -> range.last + 1 until next.first }

/**
 * Нарезает строку на подстроки между диапазонами из [ranges]
 *
 * @param ranges список диапазонов
 * @return список строк следующего вида:
 * ```
 * [0 until range1.l],
 * [range1.r + 1 until range2.l],
 * ...,
 * [rangeN-1.r + 1 until rangeN.l],
 * [rangeN.r + 1 until length(this)]
 * ```
 */
fun String.partsBetween(ranges: List<IntRange>): List<String> {
    if (ranges.isEmpty())
        return listOf(this)

    val parts = mutableListOf<String>()

    parts.add(substring(0, ranges.first().first))

    ranges
        .gaps()
        .forEach { parts.add(substring(it)) }

    parts.add(substring(ranges.last().last + 1, length))

    return parts
}

/**
 * Формирует чередующуюся последовательность элементов
 *
 * @return последовательность вида:
 * ```
 * this[0], other[0], this[1], other[1], this[2], other[2], ...
 * ```
 */
fun <T> List<T>.alternateWith(other: List<T>): Sequence<T> = sequence {
    val first = iterator()
    val second = other.iterator()

    while (first.hasNext() && second.hasNext()) {
        yield(first.next())
        yield(second.next())
    }

    yieldAll(first)
    yieldAll(second)
}

/**
 * Формирует подпоследовательности элементов, разделенные элементами, удовлетворяющими предикату [isSeparator]
 */
fun <T> Sequence<T>.separateBy(isSeparator: (T) -> Boolean): Sequence<Sequence<T>> = sequence {
    var partition = mutableListOf<T>()

    var sequenceIsNotEmpty = false

    forEach {
        sequenceIsNotEmpty = true
        if (isSeparator(it)) {
            yield(partition.asSequence())
            partition = mutableListOf()
        } else {
            partition.add(it)
        }
    }

    if (sequenceIsNotEmpty) yield(partition.asSequence())
}

fun Reader.windowed(linesCount: Int, action: (lines: List<String>) -> Unit) {
    require(linesCount > 0)

    val buffer = LinkedList<String>()
    this.forEachLine {
        if (buffer.size == linesCount) {
            action(buffer.toList())
            buffer.removeFirst()
        }

        buffer.addLast(it)
    }

    for (i in 1..buffer.size) {
        action(buffer.toList())
        buffer.removeFirst()
    }
}

fun File.windowed(linesCount: Int, action: (lines: List<String>) -> Unit) {
    this.bufferedReader().windowed(linesCount, action)
}
