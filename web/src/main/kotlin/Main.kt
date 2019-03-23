import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.browser.document

@ExperimentalCoroutinesApi
fun main() {
    //app lifecycle listeners
    document.addEventListener("DOMContentLoaded", {
        Application.start()
    })
    document.addEventListener("unload", {
        Application.finish()
    })
}