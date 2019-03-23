import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.ClassCastException

/**
 * Queue Reference
 *
 * @property sources channels to receive QueueInfo from
 * @property process channel to send updated QueueInfo to
 * @property delay time it takes for a QueueInfo to complete processing
 * @property numWorkers number workers to process work from the sources
 * @constructor Creates references between source channels and process channel
 */
data class QueueRef(
    val sources: List<ReceiveChannel<QueueInfo>>,
    val process: SendChannel<QueueInfo>,
    val delay: Long,
    val numWorkers: Int
) {
    constructor(
        source: ReceiveChannel<QueueInfo>,
        process: SendChannel<QueueInfo>,
        delay: Long,
        workers: Int
    ) : this(listOf(source), process, delay, workers)
}

/**
 * @receiver Iterable of QueueRef
 * @return all channels to receive from from an Iterable of QueueRef
 */
fun Iterable<QueueRef>.getAllSources(): List<ReceiveChannel<QueueInfo>> {
    val list: MutableList<ReceiveChannel<QueueInfo>> = mutableListOf()
    forEach { list += it.sources }
    return list.distinct()
}

/**
 * @receiver Iterable of QueueRef
 * @return all channels to send to from an Iterable of QueueRef
 */
fun Iterable<QueueRef>.getAllProcesses(): List<SendChannel<QueueInfo>> {
    return map { it.process }.distinct()
}

/**
 * @receiver Iterable of QueueRef
 * @throws ClassCastException when any exit is not of type ReceiveChannel<QueueInfo>
 * @return channels that are not received by any channel, i.e. exits
 */
@Suppress("UNCHECKED_CAST")
fun Iterable<QueueRef>.getAllExits(): List<ReceiveChannel<QueueInfo>> = try {
    val exits = getAllProcesses() - getAllSources()
    exits as List<ReceiveChannel<QueueInfo>>
} catch (e: ClassCastException) {
    throw ClassCastException("Not all exits are ReceiveChannel<QueueInfo>.")
}