private enum class BeamDirection {
    UP, DOWN, LEFT, RIGHT
}

fun main() {
    data class Point(val row: Int, val col: Int)
    fun Point.next(direction: BeamDirection) = when(direction) {
        BeamDirection.UP -> copy(row = row - 1)
        BeamDirection.DOWN -> copy(row = row + 1)
        BeamDirection.LEFT -> copy(col = col - 1)
        BeamDirection.RIGHT -> copy(col = col + 1)
    }

    data class Beam(val position: Point, val direction: BeamDirection)

    operator fun List<String>.get(point: Point) = getOrNull(point.row)?.getOrNull(point.col)

    fun Beam.energizedTiles(input: List<String>): Int {
        val beamHistory = mutableSetOf<Beam>()

        fun Beam.next(): List<Beam> {
            val nextPos = position.next(direction)
            return when (input[nextPos]) {
                null -> emptyList()
                '.' -> listOf(copy(position = nextPos))
                '|' -> when (direction) {
                    BeamDirection.UP, BeamDirection.DOWN -> listOf(copy(position = nextPos))
                    BeamDirection.LEFT, BeamDirection.RIGHT -> listOf(Beam(nextPos, BeamDirection.UP), Beam(nextPos, BeamDirection.DOWN))
                }
                '-' -> when (direction) {
                    BeamDirection.LEFT, BeamDirection.RIGHT -> listOf(copy(position = nextPos))
                    BeamDirection.UP, BeamDirection.DOWN -> listOf(Beam(nextPos, BeamDirection.LEFT), Beam(nextPos, BeamDirection.RIGHT))
                }
                '/' -> listOf(Beam(nextPos, when (direction) {
                    BeamDirection.UP -> BeamDirection.RIGHT
                    BeamDirection.DOWN -> BeamDirection.LEFT
                    BeamDirection.LEFT -> BeamDirection.DOWN
                    BeamDirection.RIGHT -> BeamDirection.UP
                }))
                '\\' -> listOf(Beam(nextPos, when (direction) {
                    BeamDirection.UP -> BeamDirection.LEFT
                    BeamDirection.DOWN -> BeamDirection.RIGHT
                    BeamDirection.LEFT -> BeamDirection.UP
                    BeamDirection.RIGHT -> BeamDirection.DOWN
                }))
                else -> error("no")
            }
        }

        var currentBeams = next().toSet()
        while (beamHistory.addAll(currentBeams)) {
            currentBeams = currentBeams.asSequence().flatMap { it.next() }.filter { it !in beamHistory }.toSet()
        }
        return beamHistory.map { it.position }.toSet().size
    }

    fun part1(input: List<String>): Int = Beam(Point(0, -1), BeamDirection.RIGHT).energizedTiles(input)
    fun part2(input: List<String>): Int {
        val rows = input.size
        val cols = input.first().length
        val beams = input.indices.flatMap {
            listOf(
                Beam(Point(it, -1), BeamDirection.RIGHT),
                Beam(Point(it, cols), BeamDirection.LEFT)
            )
        } + input.first().indices.flatMap {
            listOf(
                Beam(Point(-1, it), BeamDirection.DOWN),
                Beam(Point(rows, it), BeamDirection.UP)
            )
        }
        return beams.maxOf { it.energizedTiles(input) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
