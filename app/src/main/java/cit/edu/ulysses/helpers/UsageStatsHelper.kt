package cit.edu.ulysses.helpers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.*

class UsageStatsHelper(context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun getTotalScreenTime(): Long{
        val startTime = getStartOfDayMillis()
        val endTime = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        var totalScreenOnTime = 0L
        var lastScreenInteractiveTime: Long? = null

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.SCREEN_INTERACTIVE -> {
                    lastScreenInteractiveTime = event.timeStamp
                }
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> {
                    if (lastScreenInteractiveTime != null) {
                        totalScreenOnTime += event.timeStamp - lastScreenInteractiveTime
                        lastScreenInteractiveTime = null
                    }
                }
                UsageEvents.Event.DEVICE_SHUTDOWN -> {
                    if (lastScreenInteractiveTime != null) { totalScreenOnTime += event.timeStamp - lastScreenInteractiveTime
                            lastScreenInteractiveTime = null
                    }
                }
            }
        }

        if (lastScreenInteractiveTime != null) {
            totalScreenOnTime += endTime - lastScreenInteractiveTime
        }
        return totalScreenOnTime
    }

    fun getScreenOnTimesForAppsToday(
        packageNames: List<String>
    ): Map<String, Long> {
        val startTime = getStartOfDayMillis()
        val endTime = getEndOfDaymillis()
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

        val totalScreenOnTimes = mutableMapOf<String, Long>()
        val lastInteractiveTimes = mutableMapOf<String, Long?>()
        val appInForeground = mutableSetOf<String>()
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.SCREEN_INTERACTIVE -> {
                    packageNames.forEach { pkg ->
                        if (pkg !in lastInteractiveTimes) {
                            lastInteractiveTimes[pkg] = null
                        }
                    }
                }

                UsageEvents.Event.SCREEN_NON_INTERACTIVE, UsageEvents.Event.DEVICE_SHUTDOWN -> {
                    appInForeground.forEach { pkg ->
                        val lastInteractive = lastInteractiveTimes[pkg]
                        if (lastInteractive != null) {
                            totalScreenOnTimes[pkg] = (totalScreenOnTimes[pkg] ?: 0) + (event.timeStamp - lastInteractive)
                            lastInteractiveTimes[pkg] = null
                        }
                    }
                    appInForeground.clear()
                }

                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if (event.packageName in packageNames) {
                        appInForeground.add(event.packageName)
                        lastInteractiveTimes[event.packageName] = event.timeStamp
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if (event.packageName in packageNames && event.packageName in appInForeground) {
                        val lastInteractive = lastInteractiveTimes[event.packageName]
                        if (lastInteractive != null) {
                            totalScreenOnTimes[event.packageName] =
                                (totalScreenOnTimes[event.packageName]
                                    ?: 0) + (event.timeStamp - lastInteractive)
                            lastInteractiveTimes[event.packageName] = null
                        }
                        appInForeground.remove(event.packageName)
                    }
                }

            }
        }

        return totalScreenOnTimes
    }


    fun formatMilliseconds(milliseconds: Long): String {
        if (milliseconds <= 0) return "0 min"

        val minutes = (milliseconds / 60000).toInt()
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return when {
            hours > 0 -> "${hours}hr ${remainingMinutes} min"
            remainingMinutes > 0 -> "${remainingMinutes} min"
            else -> "less than 1 minute"
        }
    }

    fun getStartOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfDaymillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE,59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}
