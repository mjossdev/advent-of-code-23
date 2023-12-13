import kotlin.math.min

fun main() {
    fun List<String>.switch(row: Int, col: Int): List<String> {
        val changed = toMutableList()
        changed[row] = changed[row].let {
            it.replaceRange(col..col, when(it[col]) {
                '#' -> "."
                '.' -> "#"
                else -> error("should not happen")
            })
        }
        return changed
    }

    fun List<String>.reflectionValue(valueToIgnore: Int? = null): Int? {
        for (i in 1 until size) {
            if (valueToIgnore == i * 100) continue
            val size = min(i, size - i)
            val above = slice(i - size until i)
            val below = slice(i until i + size)
            if (above.asReversed() == below) {
                return i * 100
            }
        }
        val lineLength = first().length
        for (i in 1 until lineLength) {
            if (valueToIgnore == i) continue
            val size = min(i, lineLength - i)
            if (all { it.substring(i - size,  i).reversed() == it.substring(i, i + size) }) {
                return i
            }
        }
        return null
    }

    fun part1(input: List<String>): Int = input.split { it.isBlank() }.sumOf { it.reflectionValue()!! }
    fun part2(input: List<String>): Int = input.split { it.isBlank() }.sumOf {
        val originalValue = it.reflectionValue()
        val colIndices = it.first().indices
        for (row in it.indices) {
            for (col in colIndices) {
                val newValue = it.switch(row, col).reflectionValue(originalValue)
                if (newValue != null) {
                    return@sumOf newValue
                }
            }
        }
        it.forEach(::println)
        error("No pattern found")
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
