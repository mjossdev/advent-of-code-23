fun main() {
    data class Record(val springs: String, val numbers: List<Int>)

    fun String.toRecord(factor: Int): Record {
        val (springs, numberString) = split(' ')
        val numbers = buildList {
            val initial = numberString.split(',').map { it.toInt() }
            repeat(factor) {
                addAll(initial)
            }
        }
        return Record(springs + "?$springs".repeat(factor - 1), numbers)
    }

    class ArrangementCounter(val record: Record) {
        private val cache = mutableMapOf<Pair<List<Int>, Int>, Long>()
        private val springs get() = record.springs

        fun count() = count(record.numbers)
        private fun count(remainingNumbers: List<Int>, startIndex: Int = 0): Long =
            cache.getOrPut(remainingNumbers to startIndex) {
                if (remainingNumbers.isEmpty()) {
                    return@getOrPut if (springs.indexOf('#', startIndex) == -1) 1L else 0L
                }
                if (remainingNumbers.sum() + remainingNumbers.size - 1 > springs.length - startIndex) {
                    return@getOrPut 0L
                }
                val next = remainingNumbers.first()
                val regex = Regex("[#?]{$next}([.?]|$)")
                val result = regex.find(springs, startIndex) ?: return@getOrPut 0L
                if (springs.indexOf('#', startIndex) in 0 until result.range.first) {
                    return@getOrPut 0L
                }
                val countIfBroken = count(remainingNumbers.tailView(), result.range.last + 1)
                if (result.value.startsWith('#')) {
                    countIfBroken
                } else {
                    countIfBroken + count(remainingNumbers, result.range.first + 1)
                }
            }
    }

    fun solve(input: List<String>, factor: Int): Long = input.sumOf {
        ArrangementCounter(it.toRecord(factor)).count()
    }

    fun part1(input: List<String>): Long = solve(input, 1)
    fun part2(input: List<String>): Long = solve(input, 5)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
