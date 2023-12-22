fun main() {
    data class Point(val x: Int, val y: Int, val z: Int)

    fun Point.below() = copy(z = z - 1)
    fun Point.above() = copy(z = z + 1)
    data class Brick(val points: List<Point>)

    fun String.toPoint(): Point {
        val (x, y, z) = split(',').map { it.toInt() }
        return Point(x, y, z)
    }

    fun String.toBrick(): Brick {
        val (start, end) = split('~').map { it.toPoint() }
        val points = when {
            start.x != end.x -> (start.x..end.x).map { Point(it, start.y, start.z) }
            start.y != end.y -> (start.y..end.y).map { Point(start.x, it, start.z) }
            start.z != end.z -> (start.z..end.z).map { Point(start.x, start.y, it) }
            else -> listOf(start)
        }
        require(points.isNotEmpty())
        return Brick(points)
    }

    operator fun <T> Array<Array<Array<T>>>.get(point: Point) = this[point.x][point.y][point.z]
    operator fun <T> Array<Array<Array<T>>>.set(point: Point, value: T) {
        this[point.x][point.y][point.z] = value
    }

    fun getInitialGrid(bricks: List<Brick>): Array<Array<Array<Brick?>>> {
        val allPoints = bricks.flatMap { it.points }
        val width = allPoints.maxOf { it.x } + 1
        val height = allPoints.maxOf { it.y } + 1
        val depth = allPoints.maxOf { it.z } + 1
        val grid = Array(width) { Array(height) { arrayOfNulls<Brick>(depth) } }
        bricks.forEach { brick ->
            brick.points.forEach {
                grid[it] = brick
            }
        }
        return grid
    }

    fun fall(bricks: MutableList<Brick>, grid: Array<Array<Array<Brick?>>>) {
        bricks.sortBy { it.points.first().z }
        do {
            var changed = false
            for (i in bricks.indices) {
                val brick = bricks[i]
                if (brick.points.first().z == 1) {
                    continue
                }
                if (brick.points.all { point -> grid[point.below()].let { it == brick || it == null } }) {
                    changed = true
                    val newBrick = Brick(brick.points.map { it.below() })
                    brick.points.forEach { grid[it] = null }
                    newBrick.points.forEach { grid[it] = newBrick }
                    bricks[i] = newBrick
                }
            }
        } while (changed)
    }

    fun part1(input: List<String>): Int {
        val bricks = input.map { it.toBrick() }.toMutableList()
        val grid = getInitialGrid(bricks)
        fall(bricks, grid)

        return bricks.count { brick ->
            val supportedBricks =
                brick.points.mapNotNull { point -> grid[point.above()].takeIf { it != brick } }.distinct()
            supportedBricks.all { supported ->
                supported.points.any { point -> grid[point.below()].let { it != brick && it != supported && it != null } }
            }
        }
    }


    fun part2(input: List<String>): Int {
        val bricks = input.map { it.toBrick() }.toMutableList()
        val grid = getInitialGrid(bricks)
        fall(bricks, grid)

        fun Brick.countFallingBricks(): Int {
            val supportedBricks = mutableSetOf(this)
            do {
                val newBricks = supportedBricks.asSequence()
                    .flatMap { it.points }
                    .mapNotNull { point -> grid[point.above()].takeIf { it !in supportedBricks } }
                    .filter { supported -> supported.points.none { point -> grid[point.below()].let { it != null && it != supported && it !in supportedBricks } } }
                    .toSet()
            } while (supportedBricks.addAll(newBricks))
            return supportedBricks.size - 1
        }


        return bricks.sumOf { it.countFallingBricks() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    part1(input).println()
    // 43045 too low
    part2(input).println()
}
