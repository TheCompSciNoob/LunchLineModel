@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main(): Unit = runBlocking<Unit> {
    val qc = QueueController()
    qc.runSimulation(
        refs = qc.createSampleRefs(),
        timeout = 20.s
    )
    delay(2.s)
    qc.clear()
    println("Cleared.")
    qc.runSimulation(
        refs = qc.createSampleRefs(),
        timeout = 3.s
    )
}