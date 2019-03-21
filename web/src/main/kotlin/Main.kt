import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.browser.document

@ExperimentalCoroutinesApi
fun main() {
    val application = Application()
    document.addEventListener("DOMContentLoaded", {
        application.start()
    })
}