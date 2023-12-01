fun main() {
    fun part1(input: List<String>): Int = input.sumOf {
        line -> line.first { it.isDigit() }.digitToInt() * 10 + line.last { it.isDigit() }.digitToInt()
    }

    val digitMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

    fun String.tryToDigit(): Int? {
        this[0].let { if (it.isDigit()) return it.digitToInt() }
        return digitMap.entries.firstOrNull { startsWith(it.key) }?.value
    }

    fun part2(input: List<String>): Int = input.sumOf { line ->
        val windows = line.windowed(digitMap.keys.maxOf { it.length }, partialWindows = true)
        windows.firstNotNullOf { it.tryToDigit() } * 10 + windows.lastNotNullOf { it.tryToDigit() }
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_test2")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
