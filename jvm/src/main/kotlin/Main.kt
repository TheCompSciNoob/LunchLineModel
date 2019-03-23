@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main(): Unit = runBlocking<Unit> {
    val qc = QueueController()
    val results: List<QueueInfo> = qc.runSimulation(
        refs = qc.createSampleRefs(), //references between stations
        timeout = 20.s,
        logging = true
    ).awaitAll()
    //prints out stats after simulation is complete
    val averageTime = results.map { it.totalWaitTime }.average()
    println("\nAverage wait time: $averageTime")
}