@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

/**
 * Models the flow of people through the line
 * Sources, processes, and delay time can be configured here
 *
 * @return List of QueueRef to connect different channels (stations)
 */
@ExperimentalCoroutinesApi
fun CoroutineScope.createChannels(): List<QueueRef> {
    //TODO: Mathematical model of the flow of people through lines
    val totalWork = 100
    val work: ReceiveChannel<QueueInfo> = produce {
        repeat(totalWork) {
            send(QueueInfo(it))
        }
    }

    //initialize channels (stations) to be injected later
    //TODO: Buffers...?
    val servingStation = Channel<QueueInfo>()
    val selfServingStation1 = Channel<QueueInfo>()
    val selfServingStation2 = Channel<QueueInfo>()
    val cashier1 = Channel<QueueInfo>()
    val cashier2 = Channel<QueueInfo>()

    //creates references between channels (stations)
    //change this to model lunch line
    return listOf(
        QueueRef(work, servingStation, 300.ms, 1),
        QueueRef(servingStation, selfServingStation1, 200.ms, 1),
        QueueRef(servingStation, selfServingStation2, 200.ms, 1),
        QueueRef(selfServingStation1, cashier1, 50.ms, 1),
        QueueRef(selfServingStation2, cashier2, 50.ms, 1)
    )
}

/**
 * Extension function to create a new worker to process QueueInfo from source channels
 * Receives from all sources, and sends them through the process channel after the delay
 *
 * @param queueRef references to sources, process, and process time for each worker
 * @return a background job that contains the asynchronous process of people through the line
 */
fun CoroutineScope.worker(
    queueRef: QueueRef
): Job = launch {
    //parent coroutine to receive work from sources
    queueRef.sources.forEach { receiveChannel ->
        //child coroutine to process work for each source
        launch {
            //this is where the time delay happens
            //adds checkpoint and sends QueueInfo after delay
            for (queueInfo in receiveChannel) {
                delay(queueRef.delay)
                queueInfo.checkPoint()
                queueRef.process.send(queueInfo)
            }

        }
    }
}

/**
 * Chains different channels together and sends QueueInfo through the pipeline
 * Creates background jobs (workers) for every chain
 * Creates ReceiveChannel that merges and sends processed QueueInfo to one channel
 *
 * @param refs references between source channels and process channel
 * @return all processed QueueInfo through one ReceiveChannel<QueueInfo>
 */
@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
fun CoroutineScope.processReferences(
    refs: List<QueueRef>
): ReceiveChannel<QueueInfo> = produce<QueueInfo> {
    //use refs to chain stations
    refs.forEach { queueRef ->
        repeat(queueRef.numWorkers) {
            worker(queueRef)
        }
    }
    //find exits to receive from and sends them to one ReceiveChannel<QueueInfo>
    //type of List<ReceiveChannel<QueueInfo>>, cast later
    refs.getAllExits().forEach {
        launch {
            for (queueInfo in it) {
                this@produce.send(queueInfo)
            }
        }
    }
}

/**
 * Puts everything together: runs line simulation
 */
@ExperimentalCoroutinesApi
fun runLineSimulation(): Unit = runBlocking<Unit> {
    try {
        //The chain does not automatically stop because it does not know how many QueueInfo it has to process
        //In reality the timeout should equal the amount of time the lunch line is open for
        withTimeout<Unit>(35.s) {
            val refs: List<QueueRef> = createChannels()
            val results: ReceiveChannel<QueueInfo> = processReferences(refs)
            for (result in results) """
                ProcessNumber: ${result.processNumber}
                Wait times: ${result.waitTimes}
                Total wait time: ${result.totalWaitTime}
                """.trimIndent().let(::println)
        }
    } catch (e: TimeoutCancellationException) {
        //withTimeout() throws a TimeoutCancellationException
        //when that happens we know that lunch is over
        println("Lunch is over.")
    }
}

