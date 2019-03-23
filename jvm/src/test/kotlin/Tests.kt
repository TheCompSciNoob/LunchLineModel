@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import org.junit.Test

class Tests {

    @Test
    @ExperimentalCoroutinesApi
    fun testWithSampleRefs(): Unit = runBlocking<Unit> {
        val qc = QueueController()
        val results: List<QueueInfo> = qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 20.s,
            logging = true
        ).awaitAll()
        qc.clear() //Clears all intermediate channels
        val averageTime = results.map { it.totalWaitTime }.average()
        println("\nAverage wait time: $averageTime")
    }
}