package cit.edu.ulysses.users

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import cit.edu.ulysses.db.User

class UserHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ulysses.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_DOB = "dob"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_DOB TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(
        username: String,
        email: String,
        password: String,
        phone: String = "Not set",
        dob: String = "Not set"
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PHONE, phone)
            put(COLUMN_DOB, dob)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getUserByUsername(username: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            val dob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB))

            user = User(id, username, email, password, phone, dob)
        }

        cursor.close()
        db.close()
        return user
    }
    fun updateUser(username: String, email: String, password: String, phone: String, dob: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PHONE, phone)
            put(COLUMN_DOB, dob)
        }
        db.update(TABLE_NAME, values, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
    }
    fun deleteUser(username: String) {
        val db = writableDatabase
        Log.d("UserHelper", "Deleting user with username: $username") // Log to check the username
        db.delete(TABLE_NAME, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
    }
}