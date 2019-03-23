import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlin.coroutines.suspendCoroutine

/**
 * Models the flow of people through the line
 * Sources, processes, and delay time can be configured
 * This is a sample for testing
 *
 * @return List of QueueRef to connect different channels (stations)
 */
@ExperimentalCoroutinesApi
fun QueueController.createSampleRefs(): List<QueueRef> {
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

/**
 * Extension function to wait for all QueueInfo until the channel is closed
 *
 * @return List of QueueInfo that is completely processed
 */
suspend fun ReceiveChannel<QueueInfo>.awaitAll(
): List<QueueInfo> = coroutineScope<List<QueueInfo>> {
    val processedList: MutableList<QueueInfo> = mutableListOf()
    for (result in this@awaitAll) processedList += result
    return@coroutineScope processedList
}