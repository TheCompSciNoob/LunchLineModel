@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

/**
 * Puts everything together: runs line simulation with timeout
 *
 * @param refs references between source channels and process channel
 * @param timeout time allowed to process work, i.e. for how long the line is open
 * @return ReceiveChannel<QueueInfo> that sends processed QueueInfo
 */
@ExperimentalCoroutinesApi
fun CoroutineScope.runSimulation(
    refs: List<QueueRef>,
    timeout: Long,
    logging: Boolean = true
): ReceiveChannel<QueueInfo> {
    val channel = processReferences(refs, logging)
    launch {
        delay(timeout)
        channel.cancel()
        println("Lunch is over.")
    }
    return channel
}

/**
 * Chains different channels together and sends QueueInfo through the pipeline
 * Creates background jobs (workers) for every chain
 * Creates ReceiveChannel that merges and sends processed QueueInfo to one channel
 *
 * @param refs references between source channels and process channel
 * @return ReceiveChannel<QueueInfo> that sends processed QueueInfo
 */
@ExperimentalCoroutinesApi
private fun CoroutineScope.processReferences(
    refs: List<QueueRef>,
    logging: Boolean = true
): ReceiveChannel<QueueInfo> = produce<QueueInfo>(
    capacity = Channel.UNLIMITED //unlimited because it's the final exit
) {
    //use refs to chain stations
    refs.forEach { queueRef ->
        repeat(queueRef.numWorkers) {
            worker(queueRef)
        }
    }
    //find exits to receive from and sends them to one ReceiveChannel<QueueInfo>
    refs.getAllExits().forEach { exit ->
        launch {
            for (queueInfo in exit) {
                //Log for debugging
                if (logging) """
                    ProcessNumber: ${queueInfo.processNumber}
                    Wait times: ${queueInfo.waitTimes}
                    Total wait time: ${queueInfo.totalWaitTime}
                    """.trimIndent().let(::println)
                this@produce.send(queueInfo)
            }
        }
    }
}

/**
 * Extension function to create a new worker to process QueueInfo from source channels
 * Receives from all sources, and sends them through the process channel after the delay
 *
 * @param queueRef one reference to sources, process, and process time for each worker
 * @return a background job that contains the asynchronous process of people through the line
 */
@ExperimentalCoroutinesApi
private fun CoroutineScope.worker(
    queueRef: QueueRef
): Job = launch {
    //parent coroutine to receive work from sources
    val merged: ReceiveChannel<QueueInfo> = produce<QueueInfo> {
        queueRef.sources.forEach { receiveChannel ->
            //merges work to one channel
            launch {
                for (queueInfo in receiveChannel)
                    this@produce.send(queueInfo)
            }
        }
    }
    //this is where the time delay happens
    //adds checkpoint and sends QueueInfo after delay
    for (queueInfo in merged) {
        delay(queueRef.delay)
        queueInfo.checkPoint()
        queueRef.process.send(queueInfo)
    }
}