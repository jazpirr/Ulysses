package cit.edu.ulysses.data

data class NotificationEntry(
    val packageName: String,
    val timestamp: Long,
    val id: String? = null,
    val userId: String = ""
)
