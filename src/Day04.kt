import kotlin.math.pow

fun main() {
    data class Card(val winningNumbers: Set<Int>, val numbersIHave: Set<Int>, var copies: Int = 1)

    fun Card.winningNumbers() = numbersIHave.count { it in winningNumbers }

    val colonAndSpace = Regex(""":\s+""")
    val space = Regex("""\s+""")
    val pipeAndSpace = Regex("""\s+\|\s+""")

    fun String.toCard(): Card {
        val numbers = split(colonAndSpace).last()
        fun String.toNumberSet() = split(space).map { it.toInt() }.toSet()
        val (winning, iHave) = numbers.split(pipeAndSpace)
        return Card(winning.toNumberSet(), iHave.toNumberSet())
    }

    fun part1(input: List<String>): Int {
        fun Card.points() = 2.0.pow(winningNumbers() - 1.0).toInt()
        return input.sumOf { it.toCard().points() }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { it.toCard() }
        cards.forEachIndexed { index, card ->
            (1..card.winningNumbers()).forEach {
                cards[index + it].copies += card.copies
            }
        }
        return cards.sumOf { it.copies }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
