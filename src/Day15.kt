fun main() {
    data class Lens(val label: String, val focalLength: Int)

    fun String.hash() = fold(0) { hash, c -> ((hash + c.code) * 17) % 256 }
    fun String.toLens(): Lens {
        val (label, focalLength) = split('=')
        return Lens(label, focalLength.toInt())
    }
    fun part1(input: List<String>): Int = input.single().split(',').sumOf { it.hash() }
    fun part2(input: List<String>): Int {
        val instructions = input.single().split(',')
        val boxes = List(256) { mutableListOf<Lens>() }
        for (instruction in instructions) {
            if (instruction.endsWith('-')) {
                val label = instruction.dropLast(1)
                boxes[label.hash()].removeIf { it.label == label }
            } else {
                val lens = instruction.toLens()
                val box = boxes[lens.label.hash()]
                val index = box.indexOfFirst { it.label == lens.label }
                if (index != -1) {
                    box[index] = lens
                } else {
                    box.add(lens)
                }
            }
        }
        return boxes.flatMapIndexed { boxNumber, box ->
            box.mapIndexed { slot, lens -> (boxNumber + 1) * (slot + 1) * lens.focalLength }
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
