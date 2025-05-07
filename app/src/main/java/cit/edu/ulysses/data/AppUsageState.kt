package cit.edu.ulysses.data

data class AppUsageState(
    var startTime: Long = 0L,
    var elapsedTime: Long = 0L,
    var isRunning: Boolean = false
) {
    fun reset() {
        startTime = 0L
        elapsedTime = 0L
        isRunning = false
    }
}

