@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import org.junit.Test

class SimulationTests {

    @Test
    @ExperimentalCoroutinesApi
    fun `Run sample 20s timeout`(): Unit = runBlocking<Unit> {
        val qc = QueueController(coroutineContext)
        val results: List<QueueInfo> = qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 20.s,
            logging = true
        ).awaitAll()
        val averageTime = results.map { it.totalWaitTime }.average()
        println("\nAverage wait time: $averageTime")
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Run sample 5s timeout`(): Unit = runBlocking<Unit> {
        val qc = QueueController(coroutineContext)
        val results: List<QueueInfo> = qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 5.s,
            logging = true
        ).awaitAll()
        val averageTime = results.map { it.totalWaitTime }.average()
        println("\nAverage wait time: $averageTime")
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Cancel and restart simulation 10s`(): Unit = runBlocking<Unit> {
        val qc = QueueController(coroutineContext)
        qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 20.s,
            logging = true
        )
        delay(5.s)
        qc.clear()
        println("Cleared")
        qc.runSimulation(
            refs = qc.createSampleRefs(),
            timeout = 5.s,
            logging = true
        )
    }
}