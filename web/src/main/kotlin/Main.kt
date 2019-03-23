import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.browser.document

@ExperimentalCoroutinesApi
fun main() {
    //app lifecycle listeners
    val qc = QueueController()
    document.addEventListener("DOMContentLoaded", {
        document.start(qc)
    })
    document.addEventListener("unload", {
        qc.clear()
    })
}