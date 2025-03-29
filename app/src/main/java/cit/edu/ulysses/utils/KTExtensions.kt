package cit.edu.ulysses.utils

import android.app.Activity
import android.widget.EditText
import android.widget.Toast

fun Activity.toast(msg: String){
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}

fun EditText.isNotValid(): Boolean {
    return this.text.toString().isNullOrBlank()
}

fun EditText.toText(): String {
    return this.text.toString()
}

fun EditText.clearOnFocus() {
    var originalText: String = text.toString()

    setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            setText("") // Clear when clicked
        } else if (text.isNullOrEmpty()) {
            setText(originalText)
        }
    }
}
