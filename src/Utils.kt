import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T, R> List<T>.lastNotNullOf(transform: (T) -> R?): R = asReversed().firstNotNullOf(transform)

fun Iterable<Int>.product() = reduce(Int::times)
fun Iterable<Long>.product() = reduce(Long::times)

fun <T> Iterable<T>.split(predicate: (T) -> Boolean) = buildList<List<T>> {
    var currentList = mutableListOf<T>()
    this@split.forEach {
        if (predicate(it)) {
            add(currentList)
            currentList = mutableListOf<T>()
        } else {
            currentList.add(it)
        }
    }
    if (currentList.isNotEmpty()) {
        add(currentList)
    }
}

fun <T> lexicographicalCompare(left: List<T>, right: List<T>, comparator: Comparator<T>): Int =
    left.zip(right).firstNotNullOfOrNull { (l, r) -> comparator.compare(l, r).takeIf { it != 0 } }
        ?: (left.size compareTo right.size)

fun LongRange.distance() = last - first

fun <T> Iterable<T>.repeat(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@repeat)
    }
}

fun <T> Iterable<T>.repeatWithIndex(): Sequence<IndexedValue<T>> = sequence {
    while (true) {
        this@repeatWithIndex.forEachIndexed { index, item ->
            yield(IndexedValue(index, item))
        }
    }
}

fun Iterable<Long>.lcm(): Long {
    val largest = max()
    var candidate = largest
    while (any { candidate % it != 0L }) {
        candidate += largest
    }
    return candidate
}
