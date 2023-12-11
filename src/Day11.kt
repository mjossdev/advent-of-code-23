fun main() {
    data class Point(val row: Int, val col: Int)

    operator fun Array<LongArray>.set(point: Point, value: Long) {
        this[point.row][point.col] = value
    }

    operator fun Array<LongArray>.get(point: Point) = this[point.row][point.col]

    operator fun <T> Array<Array<T>>.set(point: Point, value: T) {
        this[point.row][point.col] = value
    }

    operator fun <T> Array<Array<T>>.get(point: Point) = this[point.row][point.col]

    fun Point.adjacent() = listOf(
        copy(row = row - 1),
        copy(row = row + 1),
        copy(col = col - 1),
        copy(col = col + 1),
    )

    fun List<String>.parseGalaxyPositions(): List<Point> = flatMapIndexed { rowIndex, row ->
        row.mapIndexedNotNull { colIndex, c -> if (c == '#') Point(rowIndex, colIndex) else null }
    }

    fun solve(input: List<String>, expansionFactor: Long): Long {
        fun Point.isInBounds() = row in input.indices && col in input.first().indices

        val galaxies = input.parseGalaxyPositions().toSet()
        val expandedRows = input.indices.filter { index -> input[index].all { it == '.' } }.toSet()
        val expandedCols = input.first().indices.filter { index -> input.all { it[index] == '.' } }.toSet()

        val pathLengths = mutableMapOf<Set<Point>, Long>()
        for (root in galaxies) {
            val parents = mutableMapOf<Point, Pair<Point, Long>>()
            val explored = mutableSetOf(root)
            val queue = ArrayDeque(listOf(root))
            while (queue.isNotEmpty()) {
                val v = queue.removeFirst()
                if (v != root && v in galaxies) {
                    var current = v
                    var length = 0L
                    while (current != root) {
                        val (point, cost) = parents.getValue(current)
                        length += cost
                        current = point
                    }
                    pathLengths[setOf(root, v)] = length
                }
                for (w in v.adjacent().filter { it.isInBounds() }) {
                    if (w !in explored) {
                        explored.add(w)
                        val cost =
                            if (w.row != v.row && w.row in expandedRows || w.col != v.col && w.col in expandedCols) expansionFactor
                            else 1L
                        queue.addLast(w)
                        parents[w] = Pair(v, cost)
                    }
                }
            }
        }
        return pathLengths.values.sum()
    }

    fun part1(input: List<String>): Long = solve(input, 2)
    fun part2(input: List<String>, factor: Long): Long = solve(input, factor)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input, 1000000).println()
}
