package cit.edu.ulysses.app

import android.app.Application
import android.util.Log

class UserApplication : Application() {
    var username: String = ""
    var password: String = ""
    var email: String =""
    var phone: String = "Not set"
    var dob: String = "Not set"


    override fun onCreate() {
        super.onCreate()
        Log.e("Test", "User application called")
    }
}