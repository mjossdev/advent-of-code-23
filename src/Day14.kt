fun main() {
    fun List<String>.transpose() = first().indices.map { i -> this.map { it[i] }.joinToString("") }
    fun List<String>.load() = mapIndexed { index, line -> line.count { it == 'O' } * (size - index) }.sum()

    fun String.rearrangeSegments(order: List<Char>) = split('#').joinToString("#") { it.rearrange(order) }


    fun List<String>.tiltNorth() = transpose().map { it.rearrangeSegments(listOf('O', '.')) }.transpose()
    fun List<String>.tiltSouth() = transpose().map { it.rearrangeSegments(listOf('.', 'O')) }.transpose()
    fun List<String>.tiltWest() = map { it.rearrangeSegments(listOf('O', '.')) }
    fun List<String>.tiltEast() = map { it.rearrangeSegments(listOf('.', 'O')) }

    fun part1(input: List<String>): Int {
        val tilted = input.tiltNorth()
        return tilted.load()
    }

    fun part2(input: List<String>): Int {
        var current = input
        val states = mutableMapOf<List<String>, Int>()
        var i = 0
        val iterations = 1_000_000_000
        while (i < iterations) {
            states[current]?.let {
                val period = i - it
                val missing = iterations - i
                i += (missing / period) * period
            }
            states[current] = i
            current = current.tiltNorth().tiltWest().tiltSouth().tiltEast()
            ++i
        }
        return current.load()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
