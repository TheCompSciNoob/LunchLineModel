/**
 * Extension value to conveniently add time units, unified to milliseconds
 * Milliseconds (returns the number itself, but casted to Long)
 */
val Number.ms: Long
    get() = this.toLong()

/**
 * Extension value to conveniently add time units, unified to milliseconds
 * Seconds
 */
val Number.s: Long
    get() = this.toLong() * 1000