import kotlin.math.min

fun main() {
    data class Mapping(val sourceRange: LongRange, val targetRange: LongRange)


    fun String.toMapping(): Mapping {
        val (target, source, length) = split(' ').map { it.toLong() }
        return Mapping(source until source + length, target until target + length)
    }

    fun part1(input: List<String>): Long {
        data class Almanac(val seeds: List<Long>, val mappingsList: List<List<Mapping>>)
        fun List<String>.toAlmanac(): Almanac {
            val seeds = first().split(": ").last().split(' ').map { it.toLong() }
            val maps = drop(2).split { it.isBlank() }.map { mappings ->
                mappings.drop(1).map { it.toMapping() }
            }
            return Almanac(seeds, maps)
        }

        val almanac = input.toAlmanac()
        var currentValues = almanac.seeds
        for (mappings in almanac.mappingsList) {
            currentValues = currentValues.map { value ->
                val mapping = mappings.firstOrNull { value in it.sourceRange }
                if (mapping != null) {
                    mapping.targetRange.first + (value - mapping.sourceRange.first)
                } else {
                    value
                }
            }
        }
        return currentValues.min()
    }

    fun part2(input: List<String>): Long {
        data class Almanac(val seedRanges: List<LongRange>, val mappingsList: List<List<Mapping>>)
        fun List<String>.toAlmanac(): Almanac {
            val seeds = first().split(": ").last().split(' ').map { it.toLong() }.chunked(2) { (start, length) -> start until start + length }.sortedBy { it.first }
            val maps = drop(2).split { it.isBlank() }.map { mappings ->
                mappings.drop(1).map { it.toMapping() }.sortedBy { it.sourceRange.first }
            }
            return Almanac(seeds, maps)
        }
        val almanac = input.toAlmanac()
        var currentRanges = almanac.seedRanges
        for (mappings in almanac.mappingsList) {
            currentRanges = currentRanges.flatMap { range ->
                sequence {
                    var remaining = range
                    while (!remaining.isEmpty()) {
                        val mapping = mappings.firstOrNull { remaining.first in it.sourceRange }
                        val matchedRange = if (mapping == null) {
                            val next = mappings.filter { it.sourceRange.first > remaining.first }
                                .minOfOrNull { it.sourceRange.first }
                            if (next == null) {
                                remaining
                            } else {
                                remaining.first until next
                            }
                        } else {
                            val (source, target) = mapping
                            val start = target.first + (remaining.first - source.first)
                            val end = start + min(target.last - start, remaining.distance())
                            start .. end
                        }
                        yield(matchedRange)
                        remaining = remaining.first + matchedRange.distance() + 1..remaining.last
                    }
                }
            }
        }
        return currentRanges.minOf { it.first }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
