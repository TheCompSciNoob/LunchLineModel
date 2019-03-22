import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.browser.document

@ExperimentalCoroutinesApi
fun main() {
    val app = Application()
    document.addEventListener("DOMContentLoaded", {
        println("Starting KotlinJS application.")
        app.start()
    })
    document.addEventListener("unload", {
        println("Finishing KotlinJS application.")
        app.finish()
    })
}