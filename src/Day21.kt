fun main() {
    data class Point(val row: Int, val col: Int)
    fun Point.adjacent() = listOf(
        copy(row = row - 1),
        copy(row = row + 1),
        copy(col = col - 1),
        copy(col = col + 1)
    )
    operator fun List<String>.get(point: Point) = getOrNull(point.row)?.getOrNull(point.col)

    fun List<String>.findStart(): Point {
        forEachIndexed { rowIndex, row ->
            val startCol = row.indexOf('S')
            if (startCol != -1) {
                return Point(rowIndex, startCol)
            }
        }
        throw IllegalArgumentException("No S found")
    }

    fun part1(input: List<String>, steps: Int): Int {
        val start = input.findStart()
        var currentPoints = setOf(start)
        repeat(steps) {
            currentPoints = currentPoints.asSequence()
                .flatMap { it.adjacent() }
                .filter { p -> input[p].let { it == 'S' || it == '.' } }
                .toSet()
        }
        return currentPoints.size
    }

    fun part2(input: List<String>, steps: Int): Long {
        data class Offset(val rowOffset: Int, val colOffset: Int)

        val rows = input.size
        val cols = input.first().length

        val cache = mutableMapOf<Set<Point>, Set<Point>>()
        val start = input.findStart()
        var currentPoints = mapOf(Offset(0, 0) to setOf(start))
        repeat(steps) {

        }
        return currentPoints.values.sumOf { it.size.toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)
    check(part2(testInput, 5000) == 16733044L)

    // 121067705714306 too low
    val input = readInput("Day21")
    part1(input, 64).println()
    part2(input).println()
}
