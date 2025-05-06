      package cit.edu.ulysses.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.os.Handler
import android.os.Looper
import android.util.Log
import cit.edu.ulysses.data.NotificationDBHelper
import cit.edu.ulysses.data.NotificationEntry
import com.google.firebase.auth.FirebaseAuth

class NotificationMonitorService : NotificationListenerService() {
    private lateinit var dbHelper: NotificationDBHelper

    companion object {
        val notificationLog = mutableListOf<NotificationEntry>()

        fun getNotificationsForDay(startTime: Long, endTime: Long): List<NotificationEntry> {
            return notificationLog.filter { it.timestamp in startTime..endTime }
        }

        fun getNotificationCountsForDay(startTime: Long, endTime: Long): Map<String, Long> {
            val notifications = getNotificationsForDay(startTime, endTime)
            return notifications.groupBy { it.packageName }.mapValues { it.value.size.toLong() }
        }
    }

    private val syncHandler = Handler(Looper.getMainLooper())
    private var isSyncScheduled = false

    private fun scheduleSync() {
        if (!isSyncScheduled) {
            isSyncScheduled = true
            syncHandler.postDelayed({
                dbHelper.syncToFirebase()
                isSyncScheduled = false
                Log.d("NotificationMonitor", "Batched sync to Firebase executed")
            }, 10 * 60 * 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        dbHelper = NotificationDBHelper(this)

        dbHelper.syncFromFirebase {
            notificationLog.clear()
            notificationLog.addAll(dbHelper.getAllNotifications())
            Log.d("NotificationMonitor", "Synced from Firebase and loaded ${notificationLog.size} notifications")
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val notification = sbn.notification
            val isClearable = sbn.isClearable
            val isOngoing = sbn.isOngoing
            val hasContent = notification.extras?.getCharSequence("android.text")?.isNotEmpty() == true

            if (isClearable && !isOngoing && hasContent) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val entry = NotificationEntry(
                        packageName = sbn.packageName,
                        timestamp = System.currentTimeMillis(),
                        id = sbn.id.toString(),
                        userId = uid
                    )

                    val duplicate = notificationLog.any {
                        it.packageName == entry.packageName &&
                                it.id == entry.id &&
                                (entry.timestamp - it.timestamp) < 60_000
                    }

                    if (!duplicate) {
                        notificationLog.add(entry)
                        dbHelper.insertNotificationIfNotExists(entry)
                        Log.d("NotificationLog", "Saved ${entry.packageName} at ${entry.timestamp}")
                        scheduleSync()
                    } else {
                        Log.d("NotificationLog", "Skipped duplicate ${entry.packageName}")
                    }
                }
            }
        }
    }
}
