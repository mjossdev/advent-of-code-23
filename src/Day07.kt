private enum class HandType(val counts: List<Int>) {
    HIGH_CARD(List(5) { 1 }),
    ONE_PAIR(listOf(2, 1, 1, 1)),
    TWO_PAIR(listOf(2, 2, 1)),
    THREE_OF_A_KIND(listOf(3, 1, 1)),
    FULL_HOUSE(listOf(3, 2)),
    FOUR_OF_A_KIND(listOf(4, 1)),
    FIVE_OF_A_KIND(listOf(5));

    companion object {
        private val typeByCounts = entries.associateBy { it.counts }

        fun fromCounts(counts: List<Int>): HandType {
            require(counts.sum() == 5 && counts.size <= 5)
            return typeByCounts.getValue(counts.sortedDescending())
        }
    }
}

fun main() {
    data class Hand(val cards: List<Char>, val bid: Int)

    fun String.toHand(): Hand {
        val (cards, bid) = split(' ')
        return Hand(cards.toList(), bid.toInt())
    }

    fun solve(input: List<String>, cardOrder: String, typeFunction: (Hand) -> HandType) = input
        .map { it.toHand() }
        .sortedWith(Comparator
            .comparing<Hand, HandType> { typeFunction(it) }
            .thenComparator { a, b ->
                lexicographicalCompare(a.cards, b.cards, Comparator.comparingInt(cardOrder::indexOf))
            }
        )
        .mapIndexed { index, hand -> (index + 1) * hand.bid }
        .sum()

    fun part1(input: List<String>): Int {
        val cardOrder = "${(2..9).joinToString("")}TJQKA"
        return solve(input, cardOrder) { hand ->
            val counts = hand.cards.groupingBy { it }.eachCount().values.toList()
            HandType.fromCounts(counts)
        }
    }


    fun part2(input: List<String>): Int {
        val cardOrder = "J${(2..9).joinToString("")}TQKA"
        return solve(input, cardOrder) { hand ->
            val countByCard = hand.cards.groupingBy { it }.eachCount().toMutableMap()
            val jokers = countByCard.remove('J')
            if (jokers == 5) {
                return@solve HandType.FIVE_OF_A_KIND
            }
            if (jokers != null) {
                val (card, count) = countByCard.maxBy { it.value }
                countByCard[card] = count + jokers
            }
            val counts = countByCard.values.toList()
            HandType.fromCounts(counts)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
