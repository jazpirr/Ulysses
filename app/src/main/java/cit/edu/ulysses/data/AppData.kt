package cit.edu.ulysses.models

import android.graphics.drawable.Drawable

data class AppData(
    val name: String,
    val icon: Drawable,
    val packageName: String,
    var isChecked: Boolean = false
)
