package cit.edu.ulysses.helpers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.*

class UsageStatsHelper(context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private lateinit var packageNames: List<String>
    fun getTotalScreenTime(): Long {
        return getTotalScreenTime(getStartOfDayMillis(), System.currentTimeMillis())
    }

    fun getTotalScreenTime(startTime: Long, endTime: Long): Long {
        var totalScreenOnTime = 0L
        val screenTimes = getScreenOnTimesForApps(null, startTime, endTime)
        for(entry in screenTimes){
            if(entry.value >= 60000L){
                totalScreenOnTime += entry.value
            }
        }
        return totalScreenOnTime
    }


    fun getTotalScreenTimeForRanges(startTimes: List<Long>, endTimes: List<Long>): List<Long> {
        val result = mutableListOf<Long>()

        for (i in startTimes.indices) {
            result.add(getTotalScreenTime(startTimes[i], endTimes[i]))
        }

        return result
    }

    fun getUnlocksForRanges(startTimes: List<Long>, endTimes: List<Long>): List<Long> {
        val result = mutableListOf<Long>()

        for (i in startTimes.indices) {
            val r = getUnlocks(null,startTimes[i], endTimes[i])
            result.add(r["phone_unlocks"] ?: 0)
        }

        return result
    }


    fun getScreenOnTimesForApps(
        packageNames: List<String>?, startTime: Long, endTime: Long
    ): Map<String, Long> {
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val totalScreenOnTimes = mutableMapOf<String, Long>()
        val lastInteractiveTimes = mutableMapOf<String, Long?>()
        val appInForeground = mutableSetOf<String>()
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.SCREEN_INTERACTIVE -> {
                    packageNames?.forEach { pkg ->
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
                    if (packageNames == null || event.packageName in packageNames) {
                        appInForeground.add(event.packageName)
                        lastInteractiveTimes[event.packageName] = event.timeStamp
                    }
                }
                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if ((packageNames == null || event.packageName in packageNames) && event.packageName in appInForeground) {
                        val lastInteractive = lastInteractiveTimes[event.packageName]
                        if (lastInteractive != null) {
                            totalScreenOnTimes[event.packageName] =
                                (totalScreenOnTimes[event.packageName] ?: 0) + (event.timeStamp - lastInteractive)
                            lastInteractiveTimes[event.packageName] = null
                        }
                        appInForeground.remove(event.packageName)
                    }
                }
            }
        }

        return totalScreenOnTimes
    }

    fun getUnlocks(packageNames: List<String>?, startTime: Long,endTime: Long): Map<String, Long>{
        val usageEvents = usageStatsManager.queryEvents(startTime,endTime)
        val event = UsageEvents.Event()
        var unlocks = mutableMapOf<String, Long>()
        var phoneUnlocks = 0
        while(usageEvents.hasNextEvent()){
            usageEvents.getNextEvent(event)
            when(event.eventType){
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if(packageNames?.contains(event.packageName) == true || packageNames == null){
                         unlocks[event.packageName] = (unlocks[event.packageName] ?: 0) + 1
                     }
                }
                UsageEvents.Event.KEYGUARD_HIDDEN -> {
                        phoneUnlocks++
                }
            }
        }
        unlocks["phone_unlocks"] = phoneUnlocks.toLong()
        return unlocks
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
    fun getStartAndEndTimesForWeek(): Pair<List<Long>, List<Long>> {
        val startTimes = mutableListOf<Long>()
        val endTimes = mutableListOf<Long>()

        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        for (i in 0..6) {
            startTimes.add(calendar.timeInMillis)

            val endOfDay = calendar.clone() as Calendar
            endOfDay.set(Calendar.HOUR_OF_DAY, 23)
            endOfDay.set(Calendar.MINUTE, 59)
            endOfDay.set(Calendar.SECOND, 59)
            endOfDay.set(Calendar.MILLISECOND, 999)

            // If it's today, and today isn't finished yet, cap at current time
            if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
            ) {
                endTimes.add(now.timeInMillis)
            } else {
                endTimes.add(endOfDay.timeInMillis)
            }

            // Move to next day
            calendar.add(Calendar.DATE, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        return Pair(startTimes, endTimes)
    }

    fun getEndOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE,59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}
