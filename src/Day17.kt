import java.util.PriorityQueue

fun main() {
    data class Point(val row: Int, val col: Int)
    fun Point.top() = copy(row = row - 1)
    fun Point.bottom() = copy(row = row + 1)
    fun Point.left() = copy(col = col - 1)
    fun Point.right() = copy(col = col + 1)

    fun List<String>.toGrid() = map { line -> line.map { it.digitToInt() }.toIntArray() }.toTypedArray()
    operator fun Array<IntArray>.get(point: Point) = this[point.row][point.col]
    operator fun Array<IntArray>.set(point: Point, value: Int) {
        this[point.row][point.col] = value
    }

    fun part1(input: List<String>): Int {
        data class State(val curr: Point, val previous3: List<Point> = emptyList()) {
            init {
                require(previous3.size <= 3)
            }
        }
        fun State.next(point: Point) = State(point, listOf(curr) + previous3.take(2))
        fun State.isStraightHorizontal() = previous3.size == 3 && previous3.all { it.row == curr.row }
        fun State.isStraightVertical() = previous3.size == 3 && previous3.all { it.col == curr.col }

        val costs = input.toGrid()

        fun Point.isInBounds() = row in costs.indices && col in costs.first().indices
        suspend fun SequenceScope<State>.yieldIfInBounds(state: State) {
            if (state.curr.isInBounds()) {
                yield(state)
            }
        }

        fun State.neighbors() = sequence {
            val top = curr.top()
            val bottom = curr.bottom()
            val left = curr.left()
            val right = curr.right()
            if (previous3.isEmpty()) {
                yieldIfInBounds(next(top))
                yieldIfInBounds(next(bottom))
                yieldIfInBounds(next(left))
                yieldIfInBounds(next(right))
                return@sequence
            }
            val prev = previous3.first()
            if (prev == top || prev == bottom) {
                yieldIfInBounds(next(left))
                yieldIfInBounds(next(right))
            }
            if (prev == top && !isStraightVertical()) {
                yieldIfInBounds(next(bottom))
            }
            if (prev == bottom && !isStraightVertical()) {
                yieldIfInBounds(next(top))
            }
            if (prev == left || prev == right) {
                yieldIfInBounds(next(top))
                yieldIfInBounds(next(bottom))
            }
            if (prev == left && !isStraightHorizontal()) {
                yieldIfInBounds(next(right))
            }
            if (prev == right && !isStraightHorizontal()) {
                yieldIfInBounds(next(left))
            }
        }

        val start = Point(0, 0)
        val goal = Point(costs.lastIndex, costs.first().lastIndex)
        val startState = State(start)
        val dist = mutableMapOf(startState to 0).withDefault { Int.MAX_VALUE }
        val queue = PriorityQueue<Pair<State, Int>>(Comparator.comparingInt { it.second })
        queue.add(startState to dist.getValue(startState))

        while (queue.isNotEmpty()) {
            val (u, distance) = queue.remove()
            if (distance > dist.getValue(u)) continue
            for (v in u.neighbors()) {
                val alt = dist.getValue(u) + costs[v.curr]
                if (v.curr == goal) return alt
                if (alt < dist.getValue(v)) {
                    dist[v] = alt
                    queue.add(v to alt)
                }
            }
        }
        error("goal not reached")
    }

    fun part2(input: List<String>): Int {
        data class State(val curr: Point, val previous10: List<Point> = emptyList()) {
            init {
                require(previous10.size <= 10)
            }
        }
        fun State.next(point: Point) = State(point, listOf(curr) + previous10.take(9))
        fun State.canGoStraight() = previous10.size < 10 || !(previous10.all { it.row == curr.row} || previous10.all { it.col == curr.col })
        fun State.canTurn() = previous10.size >= 4 && previous10.take(4).let {
            slice -> slice.all { it.row == curr.row } || slice.all { it.col == curr.col }
        }

        val costs = input.toGrid()

        fun Point.isInBounds() = row in costs.indices && col in costs.first().indices
        suspend fun SequenceScope<State>.yieldIfInBounds(state: State) {
            if (state.curr.isInBounds()) {
                yield(state)
            }
        }

        fun State.neighbors() = sequence {
            val top = curr.top()
            val bottom = curr.bottom()
            val left = curr.left()
            val right = curr.right()
            if (previous10.isEmpty()) {
                yieldIfInBounds(next(top))
                yieldIfInBounds(next(bottom))
                yieldIfInBounds(next(left))
                yieldIfInBounds(next(right))
                return@sequence
            }
            val prev = previous10.first()
            if (canGoStraight()) {
                yieldIfInBounds(when (prev) {
                    top -> next(bottom)
                    bottom -> next(top)
                    left -> next(right)
                    right -> next(left)
                    else -> error("Invalid previous")
                })
            }
            if (canTurn()) {
                when (prev) {
                    top, bottom -> {
                        yieldIfInBounds(next(left))
                        yieldIfInBounds(next(right))
                    }
                    left, right -> {
                        yieldIfInBounds(next(top))
                        yieldIfInBounds(next(bottom))
                    }
                }
            }
        }

        val start = Point(0, 0)
        val goal = Point(costs.lastIndex, costs.first().lastIndex)
        val startState = State(start)
        val dist = mutableMapOf(startState to 0).withDefault { Int.MAX_VALUE }
        val queue = PriorityQueue<Pair<State, Int>>(Comparator.comparingInt { it.second })
        queue.add(startState to dist.getValue(startState))

        while (queue.isNotEmpty()) {
            val (u, distance) = queue.remove()
            if (distance > dist.getValue(u)) continue
            for (v in u.neighbors()) {
                val alt = dist.getValue(u) + costs[v.curr]
                if (v.curr == goal && v.canTurn()) return alt
                if (alt < dist.getValue(v)) {
                    dist[v] = alt
                    queue.add(v to alt)
                }
            }
        }
        error("goal not reached")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)
    check(part2(readInput("Day17_test2")) == 71)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
