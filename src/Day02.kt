fun main() {
    data class Game (val id: Int, val rounds: List<Map<String, Int>>)

    fun readGames(input: List<String>) = input.map { line ->
        val (title, contents) = line.split(": ")
        val id = title.split(' ').last().toInt()
        val rounds = contents.split("; ").map { round ->
            round.split(", ").associate {
                val (number, color) = it.split(' ')
                color to number.toInt()
            }
        }
        Game(id, rounds)
    }

    fun part1(input: List<String>): Int {
        val testBag = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14
        )
        fun Game.isPossible() = rounds.all {
            it.all { (color, n) ->
                n <= (testBag[color] ?: 0)
            }
        }
        return readGames(input).filter { it.isPossible() }.sumOf { it.id }
    }



    fun part2(input: List<String>): Int {
        fun Game.power() = listOf("red", "green", "blue").map { color ->
            rounds.maxOf { it[color] ?: 0 }
        }.product()
        return readGames(input).sumOf { it.power() }
    }

    val testInput = readInput("Day02_test")
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
