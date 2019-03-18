@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main(): Unit = runBlocking<Unit> {
    val results: List<QueueInfo> = runSimulation(
        refs = createSampleRefs(),
        timeout = 30.s
    ).awaitAll()
    val averageTime = results.map { it.totalWaitTime }.average()
    println("\nAverage wait time: $averageTime")
}