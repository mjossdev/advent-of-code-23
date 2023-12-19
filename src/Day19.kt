private class Workflow(val name: String, val step: Step) {
    sealed interface Step
    data object AcceptStep : Step
    data object RejectStep : Step
    data class RedirectStep(val workflowName: String) : Step
    data class ConditionStep(
        val category: String,
        val range: IntRange,
        val trueStep: Step,
        val falseStep: Step
    ) : Step
}

private class WorkflowParser private constructor(data: String) {
    private val iterator = data.iterator()
    private var current: Char = ' '

    fun parse(): Workflow {
        next()
        val name = parseName()
        skip('{')
        val step = parseStep()
        check('}')
        check(!iterator.hasNext())
        return Workflow(name, step)
    }

    private fun parseStep(): Workflow.Step {
        val name = parseName()
        when (name) {
            "A" -> return Workflow.AcceptStep
            "R" -> return Workflow.RejectStep
        }
        if (current == '<' || current == '>') {
            return parseConditionStepRest(name)
        }
        return Workflow.RedirectStep(name)
    }

    private fun parseName(): String = buildString {
        check(current.isLetter())
        while (current.isLetter()) {
            append(current)
            next()
        }
    }

    private fun parseConditionStepRest(category: String): Workflow.ConditionStep {
        val operator = current
        next()
        val value = parseInt()
        val range = when (operator) {
            '<' -> Int.MIN_VALUE..<value
            '>' -> value + 1..Int.MAX_VALUE
            else -> error("invalid operator $operator")
        }
        skip(':')
        val trueStep = parseStep()
        skip(',')
        val falseStep = parseStep()
        return Workflow.ConditionStep(
            category,
            range,
            trueStep,
            falseStep
        )
    }

    private fun parseInt(): Int {
        check(current.isDigit())
        var result = 0
        while (current.isDigit()) {
            result = result * 10 + current.digitToInt()
            next()
        }
        return result
    }

    private fun check(char: Char) {
        check(current == char)
    }

    private fun skip(char: Char) {
        check(char)
        next()
    }

    private fun next() {
        current = iterator.nextChar()
    }

    companion object {
        fun parse(data: String) = WorkflowParser(data).parse()
    }
}

fun main() {
    fun String.toPart() = removeSurrounding("{", "}")
        .split(',')
        .associate {
            val (category, rating) = it.split('=')
            category to rating.toInt()
        }

    fun List<String>.toWorkflowRegistry() = associate {
        val workflow = WorkflowParser.parse(it)
        workflow.name to workflow
    }

    fun part1(input: List<String>): Int {
        val workflowRegistry = input.takeWhile { it.isNotBlank() }.toWorkflowRegistry()

        fun Map<String, Int>.runWorkflow(step: Workflow.Step): Boolean = when (step) {
            Workflow.AcceptStep -> true
            Workflow.RejectStep -> false
            is Workflow.RedirectStep -> runWorkflow(workflowRegistry.getValue(step.workflowName).step)
            is Workflow.ConditionStep -> if (getValue(step.category) in step.range) {
                runWorkflow(step.trueStep)
            } else {
                runWorkflow(step.falseStep)
            }
        }

        val parts = input.takeLastWhile { it.isNotBlank() }.map { it.toPart() }
        val firstStep = workflowRegistry.getValue("in").step
        return parts.filter {
            it.runWorkflow(firstStep)
        }.sumOf {
            it.values.sum()
        }
    }

    fun part2(input: List<String>): Long {
        val workflowRegistry = input.takeWhile { it.isNotBlank() }.toWorkflowRegistry()

        fun Workflow.Step.acceptedRanges(
            rangeByCategory: Map<String, IntRange> = listOf(
                "x",
                "m",
                "a",
                "s"
            ).associateWith { 1..4000 }
        ): List<Map<String, IntRange>> = when (this) {
            Workflow.AcceptStep -> listOf(rangeByCategory)
            Workflow.RejectStep -> emptyList()
            is Workflow.RedirectStep -> workflowRegistry.getValue(workflowName).step.acceptedRanges(rangeByCategory)
            is Workflow.ConditionStep -> {
                val trueCase = trueStep.acceptedRanges(
                    rangeByCategory.toMutableMap().apply { compute(category) { _, r -> r!!.intersect(range) } }
                )
                val falseCase = falseStep.acceptedRanges(
                    rangeByCategory.toMutableMap().apply { compute(category) { _, r -> r!!.intersect(range.inverse()) } }
                )
                trueCase + falseCase
            }
        }

        val ranges =  workflowRegistry.getValue("in").step.acceptedRanges()
        return ranges.sumOf { rangeByCategory ->
            rangeByCategory.values.map { (it.last - it.first + 1).toLong() }.product()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000)

    // 121067705714306 too low
    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
