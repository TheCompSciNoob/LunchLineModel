import kotlin.js.Date

actual fun currentTimeMillis(): kotlin.Long {
    return Date.now().toLong()
}