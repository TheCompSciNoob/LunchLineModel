import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
suspend fun main(): Unit = coroutineScope<Unit> {
    println("Starting kotlin js application.")
    runSimulation(
        refs = createSampleRefs(),
        timeout = 30.s
    )
}