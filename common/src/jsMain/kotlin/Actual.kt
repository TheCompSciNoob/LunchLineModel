import kotlin.browser.window
import kotlin.js.Date

actual fun currentTimeMillis(): kotlin.Long = try {
    window.performance.now().toLong()
} catch (e: Exception) {
    println("window.performance.now() is not supported.")
    Date.now().toLong()
}
