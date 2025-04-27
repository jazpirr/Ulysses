package cit.edu.ulysses.data

import android.graphics.drawable.Drawable

data class AppStats(
    val name: String,
    val icon: Drawable,
    val packageName: String,
    val statistic: Long
)