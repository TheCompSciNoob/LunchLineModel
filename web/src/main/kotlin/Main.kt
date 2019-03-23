import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.browser.document

@ExperimentalCoroutinesApi
fun main() {
    //app lifecycle listeners
    val app = Application()
    document.addEventListener("DOMContentLoaded", {
        app.start()
    })
    document.addEventListener("unload", {
        app.finish()
    })
}