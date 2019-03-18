/**
 * Contains information about a job to be processed at the Queue
 * Includes the process number and wait times
 *
 * @property processNumber Identifies this object for logging purposes
 * @constructor Create a new QueueInfo with process number and no wait times yet
 */
data class QueueInfo(val processNumber: Int) {
    var baseTime: Long = currentTimeMillis()
    val waitTimes: MutableList<Long> = mutableListOf()

    /**
     * Adds time elapsed since base time to record wait time in each station
     */
    fun checkPoint() {
        val currentTime = currentTimeMillis()
        waitTimes += (currentTime - baseTime)
        baseTime = currentTime
    }
}

/**
 * Extension to calculate the total wait time from the list of times
 */
val QueueInfo.totalWaitTime: Long
    get() = waitTimes.sum()