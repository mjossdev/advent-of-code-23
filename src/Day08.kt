private enum class Instruction {
    L, R
}

fun main() {
    val pattern = Regex("""(\w+) = \((\w+), (\w+)\)""")


    fun parseMap(map: List<String>) = map.associate {
        val (_, position, left, right) = pattern.matchEntire(it)!!.groupValues
        position to Pair(left, right)
    }

    fun part1(input: List<String>): Int {
        val instructions = input.first().map { Instruction.valueOf(it.toString()) }
        val map = parseMap(input.drop(2))
        var current = "AAA"
        var count = 0
        for (instruction in instructions.repeat()) {
            val (left, right) = map.getValue(current)
            current = when (instruction) {
                Instruction.L -> left
                Instruction.R -> right
            }
            ++count
            if (current == "ZZZ") {
                break
            }
        }
        return count
    }

    fun findPeriod(start: String, instructions: List<Instruction>, map: Map<String, Pair<String, String>>): Long {
        var current = start
        var count = 0L
        val encountered = mutableMapOf<Pair<String, Int>, Long>()
        val goalStates = mutableListOf<Pair<String, Long>>()
        for ((index, instruction) in instructions.repeatWithIndex()) {
            val state = Pair(current, index)
            if (current.endsWith('Z')) {
                goalStates.add(Pair(current, count))
            }
            encountered[state]?.let {
                return count - it
            }
            encountered[state] = count
            ++count
            val (left, right) = map.getValue(current)
            current = when (instruction) {
                Instruction.L -> left
                Instruction.R -> right
            }
        }
        error("unreachable")
    }


    fun part2(input: List<String>): Long {
        val instructions = input.first().map { Instruction.valueOf(it.toString()) }
        val map = parseMap(input.drop(2))
        val startPositions = map.keys.filter { it.endsWith('A') }
        val periods = startPositions.map { findPeriod(it, instructions, map) }
        return periods.lcm()
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day08_test")) == 6)
    check(part2(readInput("Day08_test2")) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
