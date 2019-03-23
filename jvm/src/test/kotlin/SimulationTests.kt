@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import org.junit.Test

class SimulationTests {

    @Test
    @ExperimentalCoroutinesApi
    fun runSampleRefsNoTimeout(): Unit = runBlocking<Unit> {
        val qc = QueueController()
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
    fun runSampleRefsTimeout(): Unit = runBlocking<Unit> {
        val qc = QueueController()
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
    fun cancelAndRestartSimulation(): Unit = runBlocking<Unit> {
        val qc = QueueController()
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
        ).awaitAll()
    }
}