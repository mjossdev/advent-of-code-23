fun main() {
    val numberPattern = Regex("""\d+""")
    data class Race (val time: Long, val record: Long)
    fun Race.distance(holdingTime: Long) = holdingTime * (time - holdingTime)
    fun Race.waysToWin() = (1 until time).asSequence()
        .dropWhile { distance(it) <= record }
        .takeWhile { distance(it) > record }
        .count()
        .toLong()

    fun part1(input: List<String>): Long {
        fun List<String>.toRaces(): List<Race> {
            require(size == 2)
            val (times, distances) = map { line -> numberPattern.findAll(line).map { it.value.toLong() }.toList() }
            return times.zip(distances) { time, distance -> Race(time, distance) }
        }
        return input.toRaces().map { it.waysToWin() }.product()
    }

    fun part2(input: List<String>): Long {
        fun List<String>.toRace(): Race {
            require(size == 2)
            val (time, distance) = map { line -> numberPattern.findAll(line).joinToString("") { it.value }.toLong() }
            return Race(time, distance)
        }
        return input.toRace().waysToWin()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288L)
    check(part2(testInput) == 71503L)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
