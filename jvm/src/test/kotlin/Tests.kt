@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class Tests {

    @Test
    @ExperimentalCoroutinesApi
    fun testWithSampleRefs(): Unit = runBlocking<Unit> {
        val qc = QueueController(coroutineContext)
        val results: List<QueueInfo> = qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 20.s
        ).awaitAll()
        val averageTime = results.map { it.totalWaitTime }.average()
        println("\nAverage wait time: $averageTime")
    }
}