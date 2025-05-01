package cit.edu.ulysses.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PACKAGE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            );
        """
        db.execSQL(createTable)
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

    fun syncToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val notifications = getAllNotifications()

        for (entry in notifications) {
            val entryMap = mapOf(
                "packageName" to entry.packageName,
                "timestamp" to entry.timestamp,
            )
            db.collection("users").document(userId)
                .collection("notifications")
                .document("${entry.timestamp}_${entry.packageName}") // Use composite key
                .set(entryMap)
        }
    }

    fun syncFromFirebase(onComplete: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete()
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("notifications")
            .get()
            .addOnSuccessListener { documents ->
                val entries = documents.map { doc ->
                    NotificationEntry(
                        packageName = doc.getString("packageName") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        id = null,
                        userId = userId
                    )
                }
                val dbWritable = writableDatabase
                dbWritable.execSQL("DELETE FROM $TABLE_NAME") // Clear existing
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
                onComplete()
            }
    }


    // Prevent duplicate inserts by checking for existing timestamp + package
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
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "notifications.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notifications"
        const val COLUMN_ID = "id"
        const val COLUMN_PACKAGE = "packageName"
        const val COLUMN_TIMESTAMP = "timestamp"

        const val FIREBASE_COLLECTION = "notifications"
    }
}
