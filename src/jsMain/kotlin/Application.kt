import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class Application : CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    @ExperimentalCoroutinesApi
    fun start() {
        runSimulation(
            refs = createSampleRefs(),
            timeout = 30.s
        )
    }

    fun onClear() {
        job.cancel()
        job = Job()
    }
}