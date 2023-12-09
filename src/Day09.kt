fun main() {
    fun String.toHistory() = split(' ').map { it.toInt() }

    fun getDifferences(history: List<Int>): List<ArrayDeque<Int>> = buildList{
        add(ArrayDeque(history))
        while (last().any { it != 0 }) {
            add(ArrayDeque(last().zipWithNext { a, b -> b - a }))
        }
    }

    fun part1(input: List<String>): Int {
        val histories = input.map { it.toHistory() }
        return histories.sumOf {
            val differences = getDifferences(it)
            val reversed = differences.asReversed()
            reversed.forEachIndexed { index, difference ->
                difference.add(difference.last() + (reversed.getOrNull(index - 1)?.last() ?: 0))
            }
            differences.first().last()
        }
    }


    fun part2(input: List<String>): Int {
        val histories = input.map { it.toHistory() }
        return histories.sumOf {
            val differences = getDifferences(it)
            val reversed = differences.asReversed()
            reversed.forEachIndexed { index, difference ->
                difference.addFirst(difference.first() - (reversed.getOrNull(index - 1)?.first() ?: 0))
            }
            differences.first().first()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
