import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext
import kotlin.dom.clear

class Application : CoroutineScope {
    //coroutine lifecycle
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    @ExperimentalCoroutinesApi
    fun start() {
        loadIndex()
    }

    fun finish() {
        job.cancel()
        job = Job()
    }
}

