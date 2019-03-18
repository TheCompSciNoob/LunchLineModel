@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

@ExperimentalCoroutinesApi
fun main(): Unit = runBlocking<Unit> {
    val results: List<QueueInfo> = runSimulation(
        refs = createSampleRefs(),
        timeout = 30.s
    ).awaitAll()
    val averageTime = results.map { it.totalWaitTime }.average()
    println("\nAverage wait time: $averageTime")
}

/**
 * Extension function to wait for all QueueInfo until the channel is closed
 *
 * @return List of QueueInfo that is completely processed
 */
private suspend fun ReceiveChannel<QueueInfo>.awaitAll(
): List<QueueInfo> = coroutineScope<List<QueueInfo>> {
    val processedList: MutableList<QueueInfo> = mutableListOf()
    for (result in this@awaitAll) processedList += result
    return@coroutineScope processedList
}