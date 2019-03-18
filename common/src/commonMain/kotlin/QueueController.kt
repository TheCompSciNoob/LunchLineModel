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
    timeout: Long
): ReceiveChannel<QueueInfo> {
    val channel = processReferences(refs)
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
    refs: List<QueueRef>
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
                """
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

/**
 * Models the flow of people through the line
 * Sources, processes, and delay time can be configured
 * This is a sample for testing
 *
 * @return List of QueueRef to connect different channels (stations)
 */
@ExperimentalCoroutinesApi
fun CoroutineScope.createSampleRefs(): List<QueueRef> {
    //TODO: Mathematical model of the flow of people through lines
    //assume there is unlimited queuing area and it's always able to send work
    val totalWork = 100
    val work: ReceiveChannel<QueueInfo> = produce(
        capacity = Channel.UNLIMITED //unlimited queuing area
    ) {
        repeat(totalWork) {
            delay(90.ms)
            send(QueueInfo(it))
        }
    }

    //initialize channels (stations) to be injected later
    //unlimited capacity shows the cause of the bottleneck
    //rendezvous capacity shows where people are stuck
    val capacity = Channel.UNLIMITED
    val servingStation = Channel<QueueInfo>(capacity)
    val selfServingStation1 = Channel<QueueInfo>(capacity)
    val selfServingStation2 = Channel<QueueInfo>(capacity)
    val cashier1 = Channel<QueueInfo>(capacity)
    val cashier2 = Channel<QueueInfo>(capacity)

    //creates references between channels (stations)
    //change this to model lunch line
    return listOf(
        QueueRef(work, servingStation, 100.ms, 1),
        QueueRef(servingStation, selfServingStation1, 300.ms, 1),
        QueueRef(servingStation, selfServingStation2, 300.ms, 1),
        QueueRef(selfServingStation1, cashier1, 50.ms, 1),
        QueueRef(selfServingStation2, cashier2, 50.ms, 1)
    )
}