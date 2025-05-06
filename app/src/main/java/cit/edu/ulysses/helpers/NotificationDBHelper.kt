package cit.edu.ulysses.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationDBHelper(private val ctx: Context) : SQLiteOpenHelper(
    ctx,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
)
{

    private val firestore = FirebaseFirestore.getInstance()
    private var lastSyncedTimestamp: Long = 0L
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PACKAGE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            );
        """
        db.execSQL(createTable)

        val createIndex = "CREATE INDEX idx_timestamp ON $TABLE_NAME($COLUMN_TIMESTAMP);"
        db.execSQL(createIndex)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertNotification(entry: NotificationEntry) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PACKAGE, entry.packageName)
            put(COLUMN_TIMESTAMP, entry.timestamp)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertNotificationIfNotExists(entry: NotificationEntry) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_PACKAGE = ? AND $COLUMN_TIMESTAMP = ?",
            arrayOf(entry.packageName, entry.timestamp.toString()),
            null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()

        if (!exists) {
            insertNotification(entry)
        }
    }

    fun getAllNotifications(): List<NotificationEntry> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_PACKAGE, COLUMN_TIMESTAMP),
            null, null, null, null,
            "$COLUMN_TIMESTAMP DESC"
        )

        val notifications = mutableListOf<NotificationEntry>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID)).toString()
                val packageName = getString(getColumnIndexOrThrow(COLUMN_PACKAGE))
                val timestamp = getLong(getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                notifications.add(NotificationEntry(packageName, timestamp, id))
            }
            close()
        }
        db.close()
        return notifications
    }
    private fun getLastSyncedTimestamp(context: Context): Long {
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        return prefs.getLong("last_synced_timestamp", 0L)
    }

    private fun setLastSyncedTimestamp(context: Context, timestamp: Long) {
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("last_synced_timestamp", timestamp).apply()
    }

    fun syncToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val lastSynced = getLastSyncedTimestamp(ctx)
        val unsynced = getAllNotifications().filter { it.timestamp > lastSynced }

        if (unsynced.isEmpty()) return

        val batch = firestore.batch()
        val userCollection = firestore.collection("users").document(userId).collection("notifications")

        var latestSynced = lastSynced

        for (entry in unsynced) {
            val entryMap = mapOf("packageName" to entry.packageName, "timestamp" to entry.timestamp)
            val docRef = userCollection.document("${entry.timestamp}_${entry.packageName}")
            batch.set(docRef, entryMap)
            if (entry.timestamp > latestSynced) latestSynced = entry.timestamp
        }

        batch.commit()
            .addOnSuccessListener {
                setLastSyncedTimestamp(ctx, latestSynced)
                Log.d("FirebaseSync", "Synced ${unsynced.size} entries.")
            }
            .addOnFailureListener {
                Log.e("FirebaseSync", "Sync failed: ${it.message}")
            }
    }



    fun syncFromFirebase(onComplete: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete()

        firestore.collection("users").document(userId).collection("notifications")
            .get()
            .addOnSuccessListener { documents ->
                val entries = documents.mapNotNull { doc ->
                    val pkg = doc.getString("packageName")
                    val ts = doc.getLong("timestamp")
                    if (pkg != null && ts != null) {
                        NotificationEntry(pkg, ts, null, userId)
                    } else null
                }

                val dbWritable = writableDatabase
                dbWritable.execSQL("DELETE FROM $TABLE_NAME")

                for (entry in entries) {
                    val values = ContentValues().apply {
                        put(COLUMN_PACKAGE, entry.packageName)
                        put(COLUMN_TIMESTAMP, entry.timestamp)
                    }
                    dbWritable.insert(TABLE_NAME, null, values)
                }

                dbWritable.close()
                onComplete()
            }
            .addOnFailureListener {
                Log.e("FirebaseSync", "Sync from Firebase failed: ${it.message}")
                onComplete()
            }
    }

    companion object {
        private const val DATABASE_NAME = "notifications.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notifications"
        const val COLUMN_ID = "id"
        const val COLUMN_PACKAGE = "packageName"
        const val COLUMN_TIMESTAMP = "timestamp"
        
    }
}
