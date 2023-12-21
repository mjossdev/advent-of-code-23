private enum class PulseType {
    LOW, HIGH
}

private sealed interface PulseModule {
    val name: String
    val destinations: List<String>
    fun send(sender: PulseModule, pulse: PulseType): PulseType?
    fun registerSender(sender: PulseModule) {
    }

    class FlipFlop(
        override val name: String,
        override val destinations: List<String>
    ) : PulseModule {
        private var on: Boolean = false

        override fun send(sender: PulseModule, pulse: PulseType): PulseType? = when (pulse) {
            PulseType.LOW -> {
                on = !on
                if (on) {
                    PulseType.HIGH
                } else {
                    PulseType.LOW
                }
            }
            PulseType.HIGH -> null
        }
    }

    class Conjunction(
        override val name: String,
        override val destinations: List<String>
    ): PulseModule {
        private val memory = mutableMapOf<PulseModule, PulseType>()

        override fun send(sender: PulseModule, pulse: PulseType): PulseType {
            memory[sender] = pulse
            return if (memory.values.all { it == PulseType.HIGH })  {
                PulseType.LOW
            } else {
                PulseType.HIGH
            }
        }

        override fun registerSender(sender: PulseModule) {
            memory[sender] = PulseType.LOW
        }
    }

    class Broadcaster(override val name: String, override val destinations: List<String>): PulseModule {
        override fun send(sender: PulseModule, pulse: PulseType): PulseType = pulse
    }

    class Button(override val name: String, override val destinations: List<String>): PulseModule {
        override fun send(sender: PulseModule, pulse: PulseType): PulseType = PulseType.LOW
    }
}


fun main() {
    data class SendPulse(val sender: PulseModule, val destination: String, val pulse: PulseType)

    fun String.toModule(): PulseModule {
        val (name, destinationsString) = split(" -> ")
        val destinations = destinationsString.split(", ")
        return when {
            name == "broadcaster" -> PulseModule.Broadcaster(name, destinations)
            name.startsWith('%') -> PulseModule.FlipFlop(name.drop(1), destinations)
            name.startsWith('&') -> PulseModule.Conjunction(name.drop(1), destinations)
            else -> throw IllegalArgumentException("Invalid module: $this")
        }
    }
    fun List<String>.toModuleRegistry(): Map<String, PulseModule> {
        val moduleRegistry = associate {
            val module = it.toModule()
            module.name to module
        }

        for (module in moduleRegistry.values) {
            for (destination in module.destinations) {
                moduleRegistry[destination]?.registerSender(module)
            }
        }
        return moduleRegistry
    }

    fun part1(input: List<String>): Int {
        val moduleRegistry = input.toModuleRegistry()
        val button = PulseModule.Button("button", listOf("broadcaster"))


        var lowSent = 0
        var highSent = 0
        val queue = ArrayDeque<SendPulse>()

        repeat(1000) {
            queue.addLast(SendPulse(
                button,
                "broadcaster",
                PulseType.LOW
            ))
            while (queue.isNotEmpty()) {
                val (sender, destination, type) = queue.removeFirst()
                when (type) {
                    PulseType.LOW -> ++lowSent
                    PulseType.HIGH -> ++highSent
                }
                val receiver = moduleRegistry[destination]
                receiver?.send(sender, type)?.let {
                    queue.addAll(
                        receiver.destinations.map { destination ->
                            SendPulse(receiver, destination, it)
                        }
                    )
                }
            }
        }

        return lowSent * highSent
    }

    fun part2(input: List<String>): Long {
        var moduleRegistry = input.toModuleRegistry()
        val button = PulseModule.Button("button", listOf("broadcaster"))

        val relevantModules = mutableSetOf("rx")
        while ("broadcaster" !in relevantModules) {
            relevantModules.addAll(
                moduleRegistry.values.filter { it.destinations.any { it in relevantModules } }.map { it.name }
            )
        }

        moduleRegistry = moduleRegistry.filter { it.key in relevantModules }

        val queue = ArrayDeque<SendPulse>()
        var rxLowPulses = 0
        var buttonPresses = 0L

        while (rxLowPulses != 1) {
            rxLowPulses = 0
            ++buttonPresses
            queue.addLast(SendPulse(
                button,
                "broadcaster",
                PulseType.LOW
            ))
            while (queue.isNotEmpty()) {
                val (sender, destination, type) = queue.removeFirst()
                if (destination == "rx" && type == PulseType.LOW) {
                    ++rxLowPulses
                }
                val receiver = moduleRegistry[destination]
                receiver?.send(sender, type)?.let {
                    queue.addAll(
                        receiver.destinations.map { destination ->
                            SendPulse(receiver, destination, it)
                        }
                    )
                }
            }
            if (rxLowPulses > 0) {
                println(rxLowPulses)
            }
        }

        return buttonPresses
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day20_test")) == 32000000)
    check(part1(readInput("Day20_test2")) == 11687500)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}
