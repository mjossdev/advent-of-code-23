private enum class Direction {
    NORTH, SOUTH, WEST, EAST;

    fun opposite() = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        WEST -> EAST
        EAST -> WEST
    }
}

private enum class PipeSegment(val directions: Set<Direction>) {
    VERTICAL(setOf(Direction.NORTH, Direction.SOUTH)),
    HORIZONTAL(setOf(Direction.WEST, Direction.EAST)),
    L_BEND(setOf(Direction.NORTH, Direction.EAST)),
    J_BEND(setOf(Direction.NORTH, Direction.WEST)),
    SEVEN_BEND(setOf(Direction.SOUTH, Direction.WEST)),
    F_BEND(setOf(Direction.SOUTH, Direction.EAST));
}

private data class Corner(val vertical: Direction, val horizontal: Direction) {
    init {
        check(vertical == Direction.NORTH || vertical == Direction.SOUTH)
        check(horizontal == Direction.WEST || horizontal == Direction.EAST)
    }

    companion object {
        val NORTH_WEST = Corner(Direction.NORTH, Direction.WEST)
        val NORTH_EAST = Corner(Direction.NORTH, Direction.EAST)
        val SOUTH_WEST = Corner(Direction.SOUTH, Direction.WEST)
        val SOUTH_EAST = Corner(Direction.SOUTH, Direction.EAST)

        val all = listOf(
            NORTH_WEST,
            NORTH_EAST,
            SOUTH_WEST,
            SOUTH_EAST
        )
    }
}

fun main() {
    fun Char.toPipeSegment() = when (this) {
        '|' -> PipeSegment.VERTICAL
        '-' -> PipeSegment.HORIZONTAL
        'L' -> PipeSegment.L_BEND
        'J' -> PipeSegment.J_BEND
        '7' -> PipeSegment.SEVEN_BEND
        'F' -> PipeSegment.F_BEND
        else -> throw IllegalArgumentException("Invalid pipe segment: $this")
    }

    fun PipeSegment.exit(entrance: Direction) = (directions subtract setOf(entrance)).single()

    data class Point(val row: Int, val col: Int)

    fun Point.north() = copy(row = row - 1)
    fun Point.south() = copy(row = row + 1)
    fun Point.west() = copy(col = col - 1)
    fun Point.east() = copy(col = col + 1)
    fun Point.next(direction: Direction) = when (direction) {
        Direction.NORTH -> north()
        Direction.SOUTH -> south()
        Direction.WEST -> west()
        Direction.EAST -> east()
    }

    data class Coordinate(val point: Point, val corner: Corner)

    fun Point.allCorners() = Corner.all.map { Coordinate(this, it) }

    fun Coordinate.next(direction: Direction): Coordinate {
        fun e(): Nothing = error("")

        val nextPoint = point.next(direction)
        return when (direction) {
            Direction.NORTH -> when (corner.vertical) {
                Direction.NORTH -> Coordinate(nextPoint, corner.copy(vertical = Direction.SOUTH))
                Direction.SOUTH -> copy(corner = corner.copy(vertical = Direction.NORTH))
                else -> e()
            }

            Direction.SOUTH -> when (corner.vertical) {
                Direction.NORTH -> copy(corner = corner.copy(vertical = Direction.SOUTH))
                Direction.SOUTH -> Coordinate(nextPoint, corner.copy(vertical = Direction.NORTH))
                else -> e()
            }

            Direction.WEST -> when (corner.horizontal) {
                Direction.WEST -> Coordinate(nextPoint, corner.copy(horizontal = Direction.EAST))
                Direction.EAST -> copy(corner = corner.copy(horizontal = Direction.WEST))
                else -> e()
            }

            Direction.EAST -> when (corner.horizontal) {
                Direction.WEST -> copy(corner = corner.copy(horizontal = Direction.EAST))
                Direction.EAST -> Coordinate(nextPoint, corner.copy(horizontal = Direction.WEST))
                else -> e()
            }
        }
    }

    operator fun <T> Array<Array<T>>.get(point: Point) = this[point.row][point.col]

    fun List<String>.parseGrid(startSegment: PipeSegment): Array<Array<PipeSegment?>> = Array(this.size) { row ->
        Array(this[row].length) { col ->
            this[row][col].let {
                when (it) {
                    '.' -> null
                    'S' -> startSegment
                    else -> it.toPipeSegment()
                }
            }
        }
    }

    fun List<String>.findStart(): Point {
        forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, c ->
                if (c == 'S') {
                    return Point(rowIndex, colIndex)
                }
            }
        }
        throw IllegalArgumentException("No start found")
    }

    fun Array<Array<PipeSegment?>>.traverse(start: Point): Sequence<Point> = sequence {
        var current = start
        var direction = get(current)!!.directions.first()
        do {
            yield(current)
            val segment = get(current)!!
            val nextDirection = segment.exit(direction)
            current = current.next(nextDirection)
            direction = nextDirection.opposite()
        } while (current != start)
    }

    fun part1(input: List<String>, startSegment: PipeSegment): Int {
        val start = input.findStart()
        val grid = input.parseGrid(startSegment)
        return grid.traverse(start).count() / 2
    }

    fun part2(input: List<String>, startSegment: PipeSegment): Int {
        val start = input.findStart()
        val grid = input.parseGrid(startSegment)
        val loop = grid.traverse(start).toSet()

        fun Point.isInBounds() = row in grid.indices && col in grid[row].indices
        fun Coordinate.canProceed(direction: Direction): Boolean {
            fun e(): Nothing = error("")

            val segment = grid[point] ?: return true
            return when (direction) {
                Direction.NORTH -> when (corner) {
                    Corner.NORTH_WEST, Corner.NORTH_EAST -> true
                    Corner.SOUTH_WEST -> when (segment) {
                        PipeSegment.VERTICAL, PipeSegment.L_BEND, PipeSegment.F_BEND -> true
                        PipeSegment.HORIZONTAL, PipeSegment.J_BEND, PipeSegment.SEVEN_BEND -> false
                    }

                    Corner.SOUTH_EAST -> when (segment) {
                        PipeSegment.VERTICAL, PipeSegment.J_BEND, PipeSegment.SEVEN_BEND -> true
                        PipeSegment.HORIZONTAL, PipeSegment.L_BEND, PipeSegment.F_BEND -> false
                    }

                    else -> e()
                }

                Direction.SOUTH -> when (corner) {
                    Corner.SOUTH_WEST, Corner.SOUTH_EAST -> true
                    Corner.NORTH_WEST -> when (segment) {
                        PipeSegment.VERTICAL, PipeSegment.L_BEND, PipeSegment.F_BEND -> true
                        PipeSegment.HORIZONTAL, PipeSegment.J_BEND, PipeSegment.SEVEN_BEND -> false
                    }

                    Corner.NORTH_EAST -> when (segment) {
                        PipeSegment.VERTICAL, PipeSegment.J_BEND, PipeSegment.SEVEN_BEND -> true
                        PipeSegment.HORIZONTAL, PipeSegment.L_BEND, PipeSegment.F_BEND -> false
                    }

                    else -> e()
                }

                Direction.WEST -> when (corner) {
                    Corner.NORTH_WEST, Corner.SOUTH_WEST -> true
                    Corner.NORTH_EAST -> when (segment) {
                        PipeSegment.HORIZONTAL, PipeSegment.SEVEN_BEND, PipeSegment.F_BEND -> true
                        PipeSegment.VERTICAL, PipeSegment.J_BEND, PipeSegment.L_BEND -> false
                    }

                    Corner.SOUTH_EAST -> when (segment) {
                        PipeSegment.HORIZONTAL, PipeSegment.J_BEND, PipeSegment.L_BEND -> true
                        PipeSegment.VERTICAL, PipeSegment.SEVEN_BEND, PipeSegment.F_BEND -> false
                    }

                    else -> e()
                }

                Direction.EAST -> when (corner) {
                    Corner.NORTH_EAST, Corner.SOUTH_EAST -> true
                    Corner.NORTH_WEST -> when (segment) {
                        PipeSegment.HORIZONTAL, PipeSegment.SEVEN_BEND, PipeSegment.F_BEND -> true
                        PipeSegment.VERTICAL, PipeSegment.J_BEND, PipeSegment.L_BEND -> false
                    }

                    Corner.SOUTH_WEST -> when (segment) {
                        PipeSegment.HORIZONTAL, PipeSegment.J_BEND, PipeSegment.L_BEND -> true
                        PipeSegment.VERTICAL, PipeSegment.SEVEN_BEND, PipeSegment.F_BEND -> false
                    }

                    else -> e()
                }
            }
        }
        fun Coordinate.adjacentCoordinates() = Direction.entries.filter { canProceed(it) }.map { next(it) }

        var enclosed = 0
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, segment ->
                val root = Point(rowIndex, colIndex)
                if (root in loop) {
                    return@forEachIndexed
                }
                val rootCorners = root.allCorners()
                val explored = rootCorners.toMutableSet()
                val queue = ArrayDeque(rootCorners)
                var escaped = false
                while (queue.isNotEmpty()) {
                    val v = queue.removeFirst()
                    if (!v.point.isInBounds()) {
                        escaped = true
                        break
                    }
                    for (w in v.adjacentCoordinates()) {
                        if (w !in explored) {
                            explored.add(w)
                            queue.addLast(w)
                        }
                    }
                }
                if (!escaped) {
                    ++enclosed
                }
            }
        }
        return enclosed
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day10_test"), PipeSegment.F_BEND) == 8)
    check(part2(readInput("Day10_test4"), PipeSegment.F_BEND) == 4)
    check(part2(readInput("Day10_test2"), PipeSegment.SEVEN_BEND) == 10)
    check(part2(readInput("Day10_test3"), PipeSegment.F_BEND) == 8)


    val input = readInput("Day10")
    part1(input, PipeSegment.J_BEND).println()
    part2(input, PipeSegment.J_BEND).println()
}
