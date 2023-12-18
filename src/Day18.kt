fun main() {
    data class DigInstruction(val direction: Char, val meters: Int)
    data class Point(val x: Int, val y: Int)
    fun Point.left() = copy(x = x - 1)
    fun Point.right() = copy(x = x + 1)
    fun Point.up() = copy(y = y - 1)
    fun Point.down() = copy(y = y + 1)
    fun Point.adjacent() = listOf(
        left(),
        right(),
        up(),
        down()
    )

    fun String.toDigInstruction(): DigInstruction {
        val (direction, meters) = split(' ')
        return DigInstruction(direction.single(), meters.toInt())
    }

    fun Iterable<Point>.sortedByRowThenCol() = sortedWith(Comparator.comparingInt<Point> { it.y }.thenComparingInt { it.x })

    fun printDig(points: Iterable<Point>) {
        val sortedPoints = points.sortedByRowThenCol()
        val minX = sortedPoints.minOf { it.x }
        for (line in sortedPoints.groupBy { it.y }.values) {
            buildString {
                for (point in line) {
                    while (length < point.x - minX) {
                        append('.')
                    }
                    append('#')
                }
            }.println()
        }
    }

    fun countInside(digPoints: Iterable<Point>): Int {
        val xRange = digPoints.minOf { it.x }..digPoints.maxOf { it.x }
        val yRange = digPoints.minOf { it.y }..digPoints.maxOf { it.y }
        val outsidePoints = mutableSetOf<Point>()
        for (x in xRange) {
            for (y in yRange) {
                val root = Point(x, y)
                if (root in digPoints || root in outsidePoints) {
                    continue
                }
                var escaped = false
                val queue = ArrayDeque<Point>()
                val explored = mutableSetOf(root)
                queue.addLast(root)
                while (queue.isNotEmpty()) {
                    val v = queue.removeFirst()
                    if (v.x !in xRange || v.y !in yRange || v in outsidePoints) {
                        escaped = true
                        break
                    }
                    for (w in v.adjacent().filter { it !in digPoints && it !in explored }) {
                        explored.add(w)
                        queue.addLast(w)
                    }
                }
                if (escaped) {
                    outsidePoints.addAll(explored)
                } else {
                    return explored.size
                }
            }
        }
        error("No enclosed points")
    }

    fun part1(input: List<String>): Int {
        val instructions = input.map { it.toDigInstruction() }
        var current = Point(0, 0)
        val digPoints = mutableSetOf(current)
        for (instruction in instructions) {
            repeat (instruction.meters) {
                current = current.let {
                    when (instruction.direction) {
                        'R' -> it.right()
                        'L' -> it.left()
                        'D' -> it.down()
                        'U' -> it.up()
                        else -> error("Invalid direction")
                    }
                }
                digPoints.add(current)
            }
        }
        return digPoints.size + countInside(digPoints)
    }

    fun part2(input: List<String>): Int = 0

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
//    check(part2(testInput) == 94)

    // 45313 too high
    // 26529 too low
    val input = readInput("Day18")
    part1(input).println()
//    part2(input).println()
}
