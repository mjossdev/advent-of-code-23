fun main() {
    fun List<String>.getChar(row: Int, col: Int) = getOrNull(row)?.getOrNull(col) ?: '.'
    fun String.intPosition(index: Int): IntRange {
        require(this[index].isDigit())
        fun getEdge(range: Iterable<Int>) = range.takeWhile { this[it].isDigit() }.last()
        val start = getEdge(index downTo 0)
        val end = getEdge(index..lastIndex)
        return start..end
    }

    fun readPartNumbers(input: List<String>): List<Int> {
        val numberPattern = Regex("""\d+""")
        return input.flatMapIndexed { index, line ->
            numberPattern.findAll(line).filter { result ->
                val left = result.range.first - 1
                val right = result.range.last + 1
                (sequenceOf(
                    input.getChar(index - 1, left),
                    input.getChar(index, left),
                    input.getChar(index + 1, left),
                    input.getChar(index - 1, right),
                    input.getChar(index, right),
                    input.getChar(index + 1, right),
                ) + result.range.asSequence().flatMap {
                    sequenceOf(
                        input.getChar(index - 1, it),
                        input.getChar(index + 1, it)
                    )
                })
                    .any { it != '.' && !it.isDigit() }
            }.map { it.value.toInt() }
        }
    }

    fun part1(input: List<String>): Int = readPartNumbers(input).sum()


    fun part2(input: List<String>): Int {
        val gearPattern = Regex("""\*""")
        suspend fun SequenceScope<Pair<Int, IntRange>>.yieldIfNumber(row: Int, col: Int) {
            if (input.getChar(row, col).isDigit()) {
                yield(row to input[row].intPosition(col))
            }
        }
        return input.flatMapIndexed { row, line ->
            gearPattern.findAll(line).map { result ->
                val col = result.range.single()
                val adjacentNumbers = sequence {
                    yieldIfNumber(row, col - 1)
                    yieldIfNumber(row, col + 1)
                    yieldIfNumber(row - 1, col - 1)
                    yieldIfNumber(row - 1, col)
                    yieldIfNumber(row - 1, col + 1)
                    yieldIfNumber(row + 1, col - 1)
                    yieldIfNumber(row + 1, col)
                    yieldIfNumber(row + 1, col + 1)
                }.toSet()
                if (adjacentNumbers.size == 2) {
                    adjacentNumbers.map { (row, range) -> input[row].substring(range).toInt() }.product()
                } else {
                    0
                }
            }
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
