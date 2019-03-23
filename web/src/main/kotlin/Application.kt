import kotlinx.coroutines.ExperimentalCoroutinesApi

class Application {
    val qc: QueueController = QueueController()

    @ExperimentalCoroutinesApi
    fun start() {
        loadIndex()
    }

    fun finish() {
        qc.clear()
    }
}

