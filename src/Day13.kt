import kotlin.math.min

fun main() {
    fun List<String>.reflectionValue(): Int {
        for (i in 1 until size) {
            val size = min(i, size - i)
            val above = slice(i - size until i)
            val below = slice(i until i + size)
            if (above.asReversed() == below) {
                return i * 100
            }
        }
        val lineLength = first().length
        for (i in 1 until lineLength) {
            val size = min(i, lineLength - i)
            if (all { it.substring(i - size,  i).reversed() == it.substring(i, i + size) }) {
                return i
            }
        }
        forEach(::println)
        error("Pattern has no reflection")
    }

    fun part1(input: List<String>): Int = input.split { it.isBlank() }.sumOf { it.reflectionValue() }
    fun part2(input: List<String>): Int = 0


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
//    check(part2(testInput) == 525152)

    val input = readInput("Day13")
    part1(input).println()
//    part2(input).println()
}
