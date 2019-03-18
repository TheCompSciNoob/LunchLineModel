import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope

suspend fun ReceiveChannel<QueueInfo>.awaitAll(
): List<QueueInfo> = coroutineScope<List<QueueInfo>> {
    val processedList: MutableList<QueueInfo> = mutableListOf()
    for (result in this@awaitAll) processedList += result
    return@coroutineScope processedList
}