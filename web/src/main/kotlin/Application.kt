import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Application : CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    @ExperimentalCoroutinesApi
    fun start(): Job = launch {
        runSimulation(
            refs = createSampleRefs(),
            timeout = 20.s
        )
    }

    fun clear() {
        job.cancel()
        job = Job()
    }
}